package com.firstclub.membership.service;

import com.firstclub.membership.dto.OrderResponse;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.exception.UserNotFoundException;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final TierEvaluator tierEvaluator;

    @Transactional
    public OrderResponse placeOrder(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Order order = Order.builder()
                .user(user)
                .amount(amount)
                .build();
        order = orderRepository.save(order);

        // TODO: move tier evaluation to async event listener for better decoupling
        // Trigger tier evaluation if user has an active subscription
        subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(subscription -> {
                    MembershipTier evaluatedTier = tierEvaluator.evaluateHighestTier(userId);
                    if (evaluatedTier.getRank() > subscription.getTier().getRank()) {
                        log.info("Auto-upgrading user {} from {} to {}",
                                userId, subscription.getTier().getName(), evaluatedTier.getName());
                        subscription.setTier(evaluatedTier);
                        subscriptionRepository.save(subscription);
                    }
                });

        return OrderResponse.from(order);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId)
                .stream().map(OrderResponse::from).toList();
    }
}
