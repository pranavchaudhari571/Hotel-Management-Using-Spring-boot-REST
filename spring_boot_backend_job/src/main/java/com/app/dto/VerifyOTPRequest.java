package com.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOTPRequest {
    private String email;
    private String otp;
    private String newPassword;
}
