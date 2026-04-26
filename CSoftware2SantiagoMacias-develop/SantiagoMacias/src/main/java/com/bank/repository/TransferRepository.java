package com.bank.repository;
import com.bank.model.Transfer;
import com.bank.model.TransferStatus;
import java.util.List;
import java.util.Optional;
public interface TransferRepository {
    void save(Transfer transfer);
    Optional<Transfer> findById(int transferId);
    List<Transfer> findBySourceOrDestinationAccount(String accountNumber);
    List<Transfer> findByStatus(TransferStatus status);
    List<Transfer> findAll();
}
