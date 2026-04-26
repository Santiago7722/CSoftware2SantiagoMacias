package com.bank.adapter.out.persistence;

import com.bank.adapter.out.persistence.mapper.LoanPersistenceMapper;
import com.bank.adapter.out.persistence.repository.LoanJpaRepository;
import com.bank.application.port.output.LoanRepositoryPort;
import com.bank.domain.model.aggregate.Loan;
import com.bank.domain.model.valueobject.LoanStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * INFRASTRUCTURE ADAPTER - LoanPersistenceAdapter
 * Implements LoanRepositoryPort using Spring Data JPA.
 */
@Component
public class LoanPersistenceAdapter implements LoanRepositoryPort {

    private final LoanJpaRepository jpaRepository;
    private final LoanPersistenceMapper mapper;

    public LoanPersistenceAdapter(LoanJpaRepository jpaRepository, LoanPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Loan save(Loan loan) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(loan)));
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Loan> findByClientId(String clientId) {
        return jpaRepository.findByClientId(clientId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Loan> findByStatus(LoanStatus status) {
        return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Loan> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
