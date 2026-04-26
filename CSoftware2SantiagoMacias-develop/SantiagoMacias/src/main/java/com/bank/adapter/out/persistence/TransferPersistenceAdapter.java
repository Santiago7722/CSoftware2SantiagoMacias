package com.bank.adapter.out.persistence;

import com.bank.adapter.out.persistence.mapper.TransferPersistenceMapper;
import com.bank.adapter.out.persistence.repository.TransferJpaRepository;
import com.bank.application.port.output.TransferRepositoryPort;
import com.bank.domain.model.aggregate.Transfer;
import com.bank.domain.model.valueobject.TransferStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * INFRASTRUCTURE ADAPTER - TransferPersistenceAdapter
 * Implements TransferRepositoryPort using Spring Data JPA.
 */
@Component
public class TransferPersistenceAdapter implements TransferRepositoryPort {

    private final TransferJpaRepository jpaRepository;
    private final TransferPersistenceMapper mapper;

    public TransferPersistenceAdapter(TransferJpaRepository jpaRepository, TransferPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transfer save(Transfer transfer) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(transfer)));
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Transfer> findByStatus(TransferStatus status) {
        return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Transfer> findBySourceAccountOrDestinationAccount(String accountNumber) {
        return jpaRepository.findBySourceOrDestination(accountNumber).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Transfer> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
