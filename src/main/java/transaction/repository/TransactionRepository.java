package transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import transaction.dto.database.TransactionDto;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionDto, UUID> {
    boolean existsByUserSrc(long UserId);
}