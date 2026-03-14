package com.Imara.imara.controller;

import com.Imara.imara.controller.dto.JoinCompanyRequest;
import com.Imara.imara.controller.dto.LoginRequest;
import com.Imara.imara.controller.dto.RegisterRequest;
import com.Imara.imara.dto.LoginResponse;
import com.Imara.imara.dto.UserInfoResponse;
import com.Imara.imara.security.UserPrincipal;
import com.Imara.imara.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.email(), request.password()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(authService.getCurrentUserInfo(principal));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/register/join")
    public ResponseEntity<LoginResponse> joinCompany(@Valid @RequestBody JoinCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.joinCompany(request));
    }
}
