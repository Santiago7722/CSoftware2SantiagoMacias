package com.bank.application.port.input;

import com.bank.application.dto.UserDto.*;
import java.util.List;

/**
 * APPLICATION INPUT PORT - UserInputPort
 *
 * Defines the operations that external adapters (REST controllers, CLI, etc.)
 * can invoke on the application related to Users.
 * Controllers depend on this interface, NOT on concrete use case implementations.
 */
public interface UserInputPort {
    LoginResponse login(LoginCommand command);
    UserResponse registerUser(RegisterUserCommand command);
    UserResponse getUserById(Long id);
    UserResponse getUserByIdentification(String identificationNumber);
    List<UserResponse> getAllUsers();
    UserResponse updateUserStatus(Long userId, UpdateUserStatusCommand command);
}
