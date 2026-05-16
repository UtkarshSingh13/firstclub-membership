package com.firstclub.membership.service;

import com.firstclub.membership.dto.CreateUserRequest;
import com.firstclub.membership.dto.UserResponse;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.exception.UserNotFoundException;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .cohort(request.cohort())
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse getUser(Long userId) {
        return UserResponse.from(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));
    }
}
