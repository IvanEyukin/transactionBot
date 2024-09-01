package transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transaction.dto.database.AccountDto;

@Repository
public interface AccountRepository extends JpaRepository<AccountDto, Integer> {
}