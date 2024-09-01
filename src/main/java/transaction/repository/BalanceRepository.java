package transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import transaction.dto.database.BalanceDto;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceDto, Long> {
    List<BalanceDto> findByUserIdOrderByAccount(long userId);

    BalanceDto findByUserIdAndAccount(long userId, int account);

    @Transactional
    @Modifying
    @Query("UPDATE BalanceDto SET balance =:balance")
    void updateAll(BigDecimal balance);

    @Transactional
    @Modifying
    @Query("UPDATE BalanceDto SET balance =:balance WHERE userId =:userId")
    void updateUser(long userId, BigDecimal balance);

    @Transactional
    @Modifying
    @Query("UPDATE BalanceDto SET balance =:balance WHERE userId =:userId and account = :account")
    void updateUser(long userId, int account, BigDecimal balance);
}