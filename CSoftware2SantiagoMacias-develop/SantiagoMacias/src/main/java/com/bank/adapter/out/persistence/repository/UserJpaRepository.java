package com.bank.adapter.out.persistence.repository;

import com.bank.adapter.out.persistence.entity.UserJpaEntity;
import com.bank.domain.model.valueobject.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByIdentificationNumber(String identificationNumber);
    boolean existsByIdentificationNumber(String identificationNumber);
    List<UserJpaEntity> findByCompanyId(String companyId);
    List<UserJpaEntity> findByRole(UserRole role);
}
