package com.firstclub.membership.exception;

public class PlanNotFoundException extends RuntimeException {
    public PlanNotFoundException(String planName) {
        super("Membership plan not found: " + planName);
    }
}
