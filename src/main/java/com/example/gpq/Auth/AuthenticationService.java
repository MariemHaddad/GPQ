package com.example.gpq.Auth;

import com.example.gpq.Configuration.JwtService;
import  static com.example.gpq.Entities.Role.*;
import com.example.gpq.Entities.User;
import com.example.gpq.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = null;
        if (request.getRole()==Directeur){
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(Directeur)
                    .build();
            repository.save(user);
        }
        else if (request.getRole()==Chef_de_projet){
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(Chef_de_projet)
                    .build();
            repository.save(user);
        }
        else if (request.getRole()==Admin){
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(Admin)
                    .build();
            repository.save(user);
        }
        else if (request.getRole()==R_Qualite){
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(R_Qualite)
                    .build();
            repository.save(user);
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail());
        var role=user.get().getRole();
        var userId=user.get().getIdU();
        var jwtToken = jwtService.generateToken(user.get());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(role.toString())
                .id(userId)
                .build();
    }
}
