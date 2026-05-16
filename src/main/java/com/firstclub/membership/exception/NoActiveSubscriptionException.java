package com.firstclub.membership.exception;

public class NoActiveSubscriptionException extends RuntimeException {
    public NoActiveSubscriptionException(Long userId) {
        super("No active subscription found for user: " + userId);
    }
}
