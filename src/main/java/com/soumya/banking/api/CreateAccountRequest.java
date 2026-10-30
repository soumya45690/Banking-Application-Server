package com.soumya.banking.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
    @NotBlank @Size(max = 100) String accountHolder,
    @NotBlank @Email @Size(max = 150) String email
) {}
