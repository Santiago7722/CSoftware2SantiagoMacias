package com.bank.adapter.out.persistence;

import com.bank.adapter.out.persistence.mapper.BankAccountPersistenceMapper;
import com.bank.adapter.out.persistence.repository.BankAccountJpaRepository;
import com.bank.application.port.output.BankAccountRepositoryPort;
import com.bank.domain.model.aggregate.BankAccount;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * INFRASTRUCTURE ADAPTER - BankAccountPersistenceAdapter
 * Implements BankAccountRepositoryPort using Spring Data JPA.
 */
@Component
public class BankAccountPersistenceAdapter implements BankAccountRepositoryPort {

    private final BankAccountJpaRepository jpaRepository;
    private final BankAccountPersistenceMapper mapper;

    public BankAccountPersistenceAdapter(BankAccountJpaRepository jpaRepository, BankAccountPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public BankAccount save(BankAccount account) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(account)));
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber).map(mapper::toDomain);
    }

    @Override
    public List<BankAccount> findByOwnerId(String ownerId) {
        return jpaRepository.findByOwnerId(ownerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<BankAccount> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }
}
