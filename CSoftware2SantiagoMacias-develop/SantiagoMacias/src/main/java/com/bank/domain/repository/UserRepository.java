package com.bank.domain.repository;

import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * DOMAIN REPOSITORY INTERFACE (Output Port at Domain Level)
 *
 * Defines what persistence operations the domain needs.
 * The implementation lives in the Infrastructure layer.
 * This interface has NO dependency on JPA, SQL, or any framework.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByIdentificationNumber(String identificationNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    List<User> findAll();

    List<User> findByCompanyId(String companyId);

    List<User> findByRole(UserRole role);
}
