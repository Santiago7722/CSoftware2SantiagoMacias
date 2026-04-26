package com.bank.repository;
import com.bank.model.User;
import java.util.List;
import java.util.Optional;
public interface UserRepository {
    User save(User user);
    Optional<User> findById(int userId);
    Optional<User> findByIdentification(String identificationNumber);
    List<User> findAll();
    List<User> findByCompanyId(String companyId);
    boolean existsByIdentification(String identificationNumber);
}
