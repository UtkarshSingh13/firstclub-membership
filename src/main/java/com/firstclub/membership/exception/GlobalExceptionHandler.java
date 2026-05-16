package com.firstclub.membership.exception;

import com.firstclub.membership.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("USER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanNotFound(PlanNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("PLAN_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(TierNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTierNotFound(TierNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("TIER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(NoActiveSubscriptionException.class)
    public ResponseEntity<ErrorResponse> handleNoActiveSubscription(NoActiveSubscriptionException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NO_ACTIVE_SUBSCRIPTION", ex.getMessage()));
    }

    @ExceptionHandler(ActiveSubscriptionExistsException.class)
    public ResponseEntity<ErrorResponse> handleActiveSubscriptionExists(ActiveSubscriptionExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("ACTIVE_SUBSCRIPTION_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(InvalidTierTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTierTransition(InvalidTierTransitionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("INVALID_TIER_TRANSITION", ex.getMessage()));
    }

    @ExceptionHandler(SubscriptionAlreadyCancelledException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyCancelled(SubscriptionAlreadyCancelledException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("SUBSCRIPTION_ALREADY_CANCELLED", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("VALIDATION_ERROR", message));
    }
}
