package com.firstclub.membership.config;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository benefitRepository;
    private final TierCriteriaRepository criteriaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (planRepository.count() > 0) {
            log.info("Data already seeded, skipping.");
            return;
        }
        log.info("Seeding membership data...");
        seedPlans();
        seedTiersWithBenefitsAndCriteria();
        log.info("Data seeding complete.");
    }

    private void seedPlans() {
        planRepository.save(MembershipPlan.builder()
                .name(PlanName.MONTHLY).durationInDays(30)
                .price(new BigDecimal("99")).active(true).build());
        planRepository.save(MembershipPlan.builder()
                .name(PlanName.QUARTERLY).durationInDays(90)
                .price(new BigDecimal("249")).active(true).build());
        planRepository.save(MembershipPlan.builder()
                .name(PlanName.YEARLY).durationInDays(365)
                .price(new BigDecimal("799")).active(true).build());
    }

    private void seedTiersWithBenefitsAndCriteria() {
        MembershipTier silver = tierRepository.save(MembershipTier.builder()
                .name("SILVER").rank(1).description("Silver tier - entry level membership").build());
        MembershipTier gold = tierRepository.save(MembershipTier.builder()
                .name("GOLD").rank(2).description("Gold tier - enhanced benefits").build());
        MembershipTier platinum = tierRepository.save(MembershipTier.builder()
                .name("PLATINUM").rank(3).description("Platinum tier - premium benefits").build());

        // Silver benefits
        benefitRepository.save(TierBenefit.builder().tier(silver)
                .benefitType(BenefitType.CASHBACK_PERCENTAGE).benefitValue("2")
                .description("2% cashback on every order").build());
        benefitRepository.save(TierBenefit.builder().tier(silver)
                .benefitType(BenefitType.FREE_DELIVERY_ABOVE).benefitValue("2499")
                .description("Free delivery on orders above Rs.2499").build());

        // Gold benefits
        benefitRepository.save(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.CASHBACK_PERCENTAGE).benefitValue("5")
                .description("5% instant cashback on every order").build());
        benefitRepository.save(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.FREE_DELIVERY_ABOVE).benefitValue("999")
                .description("Free delivery on orders above Rs.999").build());
        benefitRepository.save(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.EXCLUSIVE_DEALS_ACCESS).benefitValue("true")
                .description("Access to exclusive deals and early sales").build());

        // Platinum benefits
        benefitRepository.save(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.CASHBACK_PERCENTAGE).benefitValue("10")
                .description("10% cashback on every order").build());
        benefitRepository.save(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.FREE_DELIVERY_ABOVE).benefitValue("0")
                .description("Always free delivery").build());
        benefitRepository.save(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.PRIORITY_SUPPORT).benefitValue("true")
                .description("Priority customer support").build());
        benefitRepository.save(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.EXCLUSIVE_DEALS_ACCESS).benefitValue("true")
                .description("Access to exclusive deals and early sales").build());
        benefitRepository.save(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.REVIEW_REWARD_MULTIPLIER).benefitValue("2")
                .description("2x review reward coins").build());

        // Tier criteria (Silver has none - it's the default)
        criteriaRepository.save(TierCriteria.builder().tier(gold)
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("5")
                .evaluationPeriodDays(30).build());
        criteriaRepository.save(TierCriteria.builder().tier(gold)
                .criteriaType(CriteriaType.MIN_ORDER_VALUE).thresholdValue("10000")
                .evaluationPeriodDays(30).build());
        criteriaRepository.save(TierCriteria.builder().tier(platinum)
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("15")
                .evaluationPeriodDays(30).build());
        criteriaRepository.save(TierCriteria.builder().tier(platinum)
                .criteriaType(CriteriaType.MIN_ORDER_VALUE).thresholdValue("50000")
                .evaluationPeriodDays(30).build());
    }
}
