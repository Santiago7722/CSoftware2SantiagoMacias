package com.bank.application.port.output;

import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.UserRole;
import java.util.List;
import java.util.Optional;

/**
 * APPLICATION OUTPUT PORT - UserRepositoryPort
 *
 * The application layer communicates with persistence through this interface.
 * The Infrastructure layer provides the concrete implementation (JPA adapter).
 * This keeps the application independent of database technology.
 */
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByIdentificationNumber(String identificationNumber);
    boolean existsByIdentificationNumber(String identificationNumber);
    List<User> findAll();
    List<User> findByCompanyId(String companyId);
    List<User> findByRole(UserRole role);
}
