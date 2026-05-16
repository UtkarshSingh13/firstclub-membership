package com.firstclub.membership.exception;

public class SubscriptionAlreadyCancelledException extends RuntimeException {
    public SubscriptionAlreadyCancelledException(Long userId) {
        super("Subscription already cancelled for user: " + userId);
    }
}
