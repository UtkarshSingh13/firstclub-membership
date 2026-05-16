package com.firstclub.membership.controller;

import com.firstclub.membership.dto.PlanChangeRequest;
import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.TierChangeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.enums.PlanName;
import com.firstclub.membership.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(
            @PathVariable Long userId,
            @Valid @RequestBody SubscribeRequest request) {
        PlanName planName = PlanName.valueOf(request.planName().toUpperCase());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.subscribe(userId, planName));
    }

    @GetMapping("/active")
    public ResponseEntity<SubscriptionResponse> getActiveSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscription(userId));
    }

    @PutMapping("/upgrade-tier")
    public ResponseEntity<SubscriptionResponse> upgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody TierChangeRequest request) {
        return ResponseEntity.ok(subscriptionService.upgradeTier(userId, request.tierName()));
    }

    @PutMapping("/downgrade-tier")
    public ResponseEntity<SubscriptionResponse> downgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody TierChangeRequest request) {
        return ResponseEntity.ok(subscriptionService.downgradeTier(userId, request.tierName()));
    }

    @PutMapping("/change-plan")
    public ResponseEntity<SubscriptionResponse> changePlan(
            @PathVariable Long userId,
            @Valid @RequestBody PlanChangeRequest request) {
        PlanName planName = PlanName.valueOf(request.planName().toUpperCase());
        return ResponseEntity.ok(subscriptionService.changePlan(userId, planName));
    }

    @PostMapping("/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.cancel(userId));
    }
}
