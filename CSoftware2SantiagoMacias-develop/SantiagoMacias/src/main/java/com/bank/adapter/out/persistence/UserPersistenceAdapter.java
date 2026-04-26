package com.bank.adapter.out.persistence;

import com.bank.adapter.out.persistence.entity.UserJpaEntity;
import com.bank.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.bank.adapter.out.persistence.repository.UserJpaRepository;
import com.bank.application.port.output.UserRepositoryPort;
import com.bank.domain.model.entity.User;
import com.bank.domain.model.valueobject.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * INFRASTRUCTURE ADAPTER (Driven Adapter) - UserPersistenceAdapter
 *
 * Implements the UserRepositoryPort (output port defined by Application layer).
 * Translates between Domain entities and JPA entities using the mapper.
 * This is where Spring Data JPA lives — completely outside the domain.
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository, UserPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByIdentificationNumber(String identificationNumber) {
        return jpaRepository.findByIdentificationNumber(identificationNumber).map(mapper::toDomain);
    }

    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        return jpaRepository.existsByIdentificationNumber(identificationNumber);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<User> findByCompanyId(String companyId) {
        return jpaRepository.findByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return jpaRepository.findByRole(role).stream().map(mapper::toDomain).toList();
    }
}
