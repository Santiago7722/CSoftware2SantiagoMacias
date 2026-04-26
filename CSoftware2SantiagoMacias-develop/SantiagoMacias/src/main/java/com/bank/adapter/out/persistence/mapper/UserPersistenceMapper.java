package com.bank.adapter.out.persistence.mapper;

import com.bank.adapter.out.persistence.entity.UserJpaEntity;
import com.bank.domain.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE MAPPER - UserPersistenceMapper
 * Uses User.reconstitute() for safe loading from DB.
 * Uses User.create() only for brand-new registrations (via UserUseCase).
 */
@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity e) {
        if (e == null) return null;
        return User.reconstitute(
            e.getId(),
            e.getRelatedEntityId(),
            e.getFullName(),
            e.getIdentificationNumber(),
            e.getEmail(),
            e.getPhone(),
            e.getBirthDate(),
            e.getAddress(),
            e.getRole(),
            e.getStatus(),
            e.getPasswordHash(),
            e.getCompanyId()
        );
    }

    public UserJpaEntity toJpaEntity(User u) {
        if (u == null) return null;
        return UserJpaEntity.builder()
            .id(u.getId())
            .relatedEntityId(u.getRelatedEntityId())
            .fullName(u.getFullName())
            .identificationNumber(u.getIdentificationNumber())
            .email(u.getEmail().getValue())
            .phone(u.getPhone().getValue())
            .birthDate(u.getBirthDate())
            .address(u.getAddress())
            .role(u.getRole())
            .status(u.getStatus())
            .passwordHash(u.getPasswordHash())
            .companyId(u.getCompanyId())
            .build();
    }
}
