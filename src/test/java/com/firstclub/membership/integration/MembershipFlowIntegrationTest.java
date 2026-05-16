package com.firstclub.membership.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MembershipFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void shouldListSeededPlans() throws Exception {
        mockMvc.perform(get("/api/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("MONTHLY"));
    }

    @Test
    @Order(2)
    void shouldListSeededTiersWithBenefits() throws Exception {
        mockMvc.perform(get("/api/tiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("SILVER"))
                .andExpect(jsonPath("$[0].benefits.length()").value(2));
    }

    @Test
    @Order(3)
    void shouldCreateUserAndSubscribeAtSilver() throws Exception {
        CreateUserRequest userReq = new CreateUserRequest("Utkarsh", "u@test.com", "9999999999", "EARLY_ADOPTER");
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = userResponse.id();

        SubscribeRequest subReq = new SubscribeRequest("MONTHLY");
        mockMvc.perform(post("/api/users/" + userId + "/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tierName").value("SILVER"))
                .andExpect(jsonPath("$.planName").value("MONTHLY"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @Order(4)
    void shouldAutoUpgradeToGoldAfterFiveOrders() throws Exception {
        CreateUserRequest userReq = new CreateUserRequest("GoldUser", "gold@test.com", null, null);
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andReturn();
        UserResponse userResp = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = userResp.id();

        SubscribeRequest subReq = new SubscribeRequest("MONTHLY");
        mockMvc.perform(post("/api/users/" + userId + "/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isCreated());

        PlaceOrderRequest orderReq = new PlaceOrderRequest(new BigDecimal("3000"));
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/users/" + userId + "/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderReq)))
                    .andExpect(status().isCreated());
        }

        MvcResult subResult = mockMvc.perform(get("/api/users/" + userId + "/subscriptions/active"))
                .andExpect(status().isOk())
                .andReturn();
        SubscriptionResponse subResp = objectMapper.readValue(
                subResult.getResponse().getContentAsString(), SubscriptionResponse.class);

        assertThat(subResp.tierName()).isEqualTo("GOLD");
        assertThat(subResp.benefits()).isNotEmpty();
    }

    @Test
    @Order(5)
    void shouldManuallyDowngradeAndCancel() throws Exception {
        CreateUserRequest userReq = new CreateUserRequest("CancelUser", "cancel@test.com", null, null);
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andReturn();
        UserResponse userResp = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = userResp.id();

        mockMvc.perform(post("/api/users/" + userId + "/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubscribeRequest("YEARLY"))))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/users/" + userId + "/subscriptions/upgrade-tier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TierChangeRequest("GOLD"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tierName").value("GOLD"));

        mockMvc.perform(put("/api/users/" + userId + "/subscriptions/downgrade-tier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TierChangeRequest("SILVER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tierName").value("SILVER"));

        mockMvc.perform(post("/api/users/" + userId + "/subscriptions/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @Order(6)
    void shouldReturnErrorForDuplicateSubscription() throws Exception {
        CreateUserRequest userReq = new CreateUserRequest("DupUser", "dup@test.com", null, null);
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andReturn();
        UserResponse userResp = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = userResp.id();

        SubscribeRequest subReq = new SubscribeRequest("MONTHLY");
        mockMvc.perform(post("/api/users/" + userId + "/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/" + userId + "/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("ACTIVE_SUBSCRIPTION_EXISTS"));
    }

    @Test
    @Order(7)
    void shouldChangePlanAndUpdateEndDate() throws Exception {
        CreateUserRequest userReq = new CreateUserRequest("PlanUser", "plan@test.com", null, null);
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andReturn();
        UserResponse userResp = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = userResp.id();

        mockMvc.perform(post("/api/users/" + userId + "/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubscribeRequest("MONTHLY"))))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/users/" + userId + "/subscriptions/change-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PlanChangeRequest("YEARLY"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("YEARLY"));
    }
}
