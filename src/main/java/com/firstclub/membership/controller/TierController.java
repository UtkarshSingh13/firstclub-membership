package com.firstclub.membership.controller;

import com.firstclub.membership.dto.TierBenefitResponse;
import com.firstclub.membership.dto.TierDetailResponse;
import com.firstclub.membership.service.TierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiers")
@RequiredArgsConstructor
public class TierController {

    private final TierService tierService;

    @GetMapping
    public ResponseEntity<List<TierDetailResponse>> getAllTiers() {
        return ResponseEntity.ok(tierService.getAllTiers());
    }

    @GetMapping("/{tierId}/benefits")
    public ResponseEntity<List<TierBenefitResponse>> getTierBenefits(@PathVariable Long tierId) {
        return ResponseEntity.ok(tierService.getTierBenefits(tierId));
    }
}
