package com.firstclub.membership.service;

import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.dto.TierBenefitResponse;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.exception.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository benefitRepository;

    @Transactional
    public SubscriptionResponse subscribe(Long userId, PlanName planName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (subscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)) {
            throw new ActiveSubscriptionExistsException(userId);
        }

        MembershipPlan plan = planRepository.findByName(planName)
                .orElseThrow(() -> new PlanNotFoundException(planName.name()));

        MembershipTier silverTier = tierRepository.findByName("SILVER")
                .orElseThrow(() -> new TierNotFoundException("SILVER"));

        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .tier(silverTier)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(plan.getDurationInDays()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        subscription = subscriptionRepository.save(subscription);
        return toResponse(subscription);
    }

    public SubscriptionResponse getActiveSubscription(Long userId) {
        UserSubscription subscription = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NoActiveSubscriptionException(userId));
        return toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse upgradeTier(Long userId, String tierName) {
        UserSubscription subscription = findActiveSubscription(userId);
        MembershipTier newTier = tierRepository.findByName(tierName.toUpperCase())
                .orElseThrow(() -> new TierNotFoundException(tierName));

        if (newTier.getRank() <= subscription.getTier().getRank()) {
            throw new InvalidTierTransitionException(
                    "Cannot upgrade from " + subscription.getTier().getName() + " to " + tierName +
                    ". Target tier must be higher.");
        }

        subscription.setTier(newTier);
        subscription = subscriptionRepository.save(subscription);
        return toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse downgradeTier(Long userId, String tierName) {
        UserSubscription subscription = findActiveSubscription(userId);
        MembershipTier newTier = tierRepository.findByName(tierName.toUpperCase())
                .orElseThrow(() -> new TierNotFoundException(tierName));

        if (newTier.getRank() >= subscription.getTier().getRank()) {
            throw new InvalidTierTransitionException(
                    "Cannot downgrade from " + subscription.getTier().getName() + " to " + tierName +
                    ". Target tier must be lower.");
        }

        subscription.setTier(newTier);
        subscription = subscriptionRepository.save(subscription);
        return toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse changePlan(Long userId, PlanName planName) {
        UserSubscription subscription = findActiveSubscription(userId);
        MembershipPlan newPlan = planRepository.findByName(planName)
                .orElseThrow(() -> new PlanNotFoundException(planName.name()));

        subscription.setPlan(newPlan);
        subscription.setEndDate(LocalDate.now().plusDays(newPlan.getDurationInDays()));
        subscription = subscriptionRepository.save(subscription);
        return toResponse(subscription);
    }

    // TODO: consider sending a cancellation email/notification
    @Transactional
    public SubscriptionResponse cancel(Long userId) {
        UserSubscription subscription = findActiveSubscription(userId);

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new SubscriptionAlreadyCancelledException(userId);
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription = subscriptionRepository.save(subscription);
        return toResponse(subscription);
    }

    UserSubscription findActiveSubscription(Long userId) {
        return subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NoActiveSubscriptionException(userId));
    }

    private SubscriptionResponse toResponse(UserSubscription subscription) {
        List<TierBenefitResponse> benefits = benefitRepository
                .findByTierId(subscription.getTier().getId())
                .stream().map(TierBenefitResponse::from).toList();
        return SubscriptionResponse.from(subscription, benefits);
    }
}
