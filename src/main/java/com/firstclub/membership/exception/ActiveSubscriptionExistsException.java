package com.firstclub.membership.exception;

public class ActiveSubscriptionExistsException extends RuntimeException {
    public ActiveSubscriptionExistsException(Long userId) {
        super("User already has an active subscription: " + userId);
    }
}
