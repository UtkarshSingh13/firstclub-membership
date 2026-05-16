package com.firstclub.membership.service;

import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final MembershipPlanRepository planRepository;

    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(PlanResponse::from)
                .toList();
    }
}
