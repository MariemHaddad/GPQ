package com.example.gpq.Auth;

import com.example.gpq.Entities.AccountStatus;
import com.example.gpq.Entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private Role role;
    private AccountStatus accountStatus;
}
