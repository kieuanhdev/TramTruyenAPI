package com.tramtruyen.api.presentation.controllers;

import com.tramtruyen.api.application.services.UserService;
import com.tramtruyen.api.presentation.payloads.request.UserCreateRequest;
import com.tramtruyen.api.presentation.payloads.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        // Trả về HTTP Status 201 (Created) khi tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}