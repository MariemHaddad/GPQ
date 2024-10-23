package com.example.gpq.Services;

import com.example.gpq.Entities.AccountStatus;
import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import com.example.gpq.Repositories.UserRepository;
import com.example.gpq.Services.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .idU(1L)
                .nom("John")
                .prenom("Doe")
                .email("john.doe@example.com")
                .motDePasse("password")
                .role(Role.CHEFDEPROJET)
                .accountStatus(AccountStatus.APPROVED)
                .build();
    }

    @Test
    void registerUser_shouldSaveUser() {
        userService.registerUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenUserExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserByEmail("john.doe@example.com");
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getNom());
    }

    @Test
    void getUserByEmail_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findByEmail("unknown@example.com"));
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getNom());
    }

    @Test
    void blockUser_shouldChangeUserStatusToBlocked() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.blockUser(1L);
        assertEquals(AccountStatus.BLOCKED, user.getAccountStatus());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void blockUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.blockUser(1L));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        // New user data to update with
        User updatedUser = User.builder()
                .nom("Jane")
                .prenom("Doe")
                .email("jane.doe@example.com")
                .role(Role.ADMIN)
                .accountStatus(AccountStatus.APPROVED)
                .build();

        // Mock the repository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user); // Mock save behavior

        // Execute the update method
        User result = userService.updateUser(1L, updatedUser);

        // Verify the result
        assertEquals("Jane", result.getNom());
        assertEquals("jane.doe@example.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals(AccountStatus.APPROVED, result.getAccountStatus());

        // Verify interactions with the repository
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    void saveResetToken_shouldSetResetToken() {
        userService.saveResetToken("token123", user);
        assertEquals("token123", user.getResetToken());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserByResetToken_shouldReturnUser_whenTokenExists() {
        when(userRepository.findByResetToken("token123")).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserByResetToken("token123");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserByResetToken_shouldReturnEmpty_whenTokenDoesNotExist() {
        when(userRepository.findByResetToken("invalidToken")).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserByResetToken("invalidToken");
        assertFalse(result.isPresent());
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void getUserById_shouldReturnNull_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        User result = userService.getUserById(1L);
        assertNull(result);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.findById(1L);
        assertEquals(user, result);
    }

    @Test
    void findById_shouldReturnNull_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        User result = userService.findById(1L);
        assertNull(result);
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        User result = userService.findByEmail("john.doe@example.com");
        assertEquals(user, result);
    }

    @Test
    void findByEmail_shouldThrowException_whenEmailNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findByEmail("unknown@example.com"));
    }

    @Test
    void findByNom_shouldReturnUser_whenNomExists() {
        when(userRepository.findByNom("John")).thenReturn(user);
        User result = userService.findByNom("John");
        assertEquals(user, result);
    }

    @Test
    void findByRole_shouldReturnUsersWithSpecificRole() {
        List<User> users = List.of(user);
        when(userRepository.findByRole(Role.CHEFDEPROJET)).thenReturn(users);
        List<User> result = userService.findByRole(Role.CHEFDEPROJET);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getNom());
    }

    @Test
    void loadUserByUsername_shouldReturnUser_whenUserExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        User result = userService.loadUserByUsername("john.doe@example.com");
        assertEquals(user, result);
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown@example.com"));
    }
}
