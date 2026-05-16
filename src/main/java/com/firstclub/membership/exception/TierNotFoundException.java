package com.firstclub.membership.exception;

public class TierNotFoundException extends RuntimeException {
    public TierNotFoundException(String tierName) {
        super("Membership tier not found: " + tierName);
    }
}
