package com.example.gpq.Repositories;

import com.example.gpq.Entities.AccountStatus;
import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByAccountStatus(AccountStatus status);
}
