package transaction.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transaction.dto.database.UserTransactionView;

import java.util.List;

@Repository
public interface UserTransactionViewRepository extends JpaRepository<UserTransactionView, Long> {
    List<UserTransactionView> findByUserSrc(long userId, Limit limit);
}