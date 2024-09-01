package transaction.service.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transaction.dto.database.*;
import transaction.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostgresService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserTransactionViewRepository userTransactionViewRepository;
    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;

    public void saveUser(UserDto userDto) {
        userRepository.save(userDto);
        log.debug("Request Postgres: {}", userDto);
    }

    public UserDto getUser(long userId) {
        log.debug("Request Postgres: {}", userId);
        boolean hasUserInDb = userRepository.existsById(userId);
        log.debug("Response Postgres: {}", hasUserInDb);

        if (hasUserInDb) {
            log.debug("Request Postgres: {}", userId);
            UserDto userBd = userRepository.getReferenceById(userId);
            log.debug("Response Postgres: {}", userBd);
            return userBd;
        } else {
            return null;
        }
    }

    public UserDto getUser(@NotNull UserDto userDto) {
        return getUser(userDto.getId());
    }

    public UserDto searchUser(String userName) {
        log.debug("Request Postgres: {}", userName);
        UserDto userDto = userRepository.findByUserNameIgnoreCase(userName);
        log.debug("Response Postgres: {}", userDto);
        return userDto;
    }

    public List<UserDto> getUserList() {
        List<UserDto> userDtoList = userRepository.findAll();
        log.debug("Request Postgres: {}", userDtoList);
        return userDtoList;
    }

    public List<UserDto> getAdminList() {
        List<UserDto> userDtoList = userRepository.findByIsAdmin(true);
        log.debug("Request Postgres: {}", userDtoList);
        return userDtoList;
    }

    public void saveTransaction(TransactionDto transactionDto) {
        transactionRepository.save(transactionDto);
        log.debug("Request Postgres: {}", transactionDto);
    }

    public TransactionDto getTransaction(UUID transactionGuid) {
        log.debug("Request Postgres: {}", transactionGuid);
        TransactionDto transactionDto = transactionRepository.getReferenceById(transactionGuid);
        log.debug("Response Postgres: {}", transactionDto);
        return transactionDto;
    }

    public List<UserTransactionView> getUserTransactionList(long userId) {
        log.debug("Request Postgres: {}", userId);
        boolean hasUserInDb = transactionRepository.existsByUserSrc(userId);
        log.debug("Response Postgres: {}", hasUserInDb);

        if(hasUserInDb) {
            log.debug("Request Postgres: {}", userId);
            List<UserTransactionView> userTransactionViews = userTransactionViewRepository.findByUserSrc(userId, Limit.of(10));
            log.debug("Response Postgres: {}", userTransactionViews);
            return userTransactionViews;
        } else {
            return null;
        }
    }

    public BalanceDto getBalance(long userId, int account) {
        log.debug("Request Postgres: {}, {}", userId, account);
        BalanceDto balanceDto = balanceRepository.findByUserIdAndAccount(userId, account);
        log.debug("Response Postgres: {}", balanceDto);
        return balanceDto;
    }

    public List<BalanceDto> getBalanceList(long userId) {
        log.debug("Request Postgres: {}", userId);
        List<BalanceDto> balanceDtoList = balanceRepository.findByUserIdOrderByAccount(userId);
        log.debug("Response Postgres: {}", balanceDtoList);
        return balanceDtoList;
    }

    public void updateBalance(BigDecimal balance) {
        log.debug("Request Postgres: {}", balance);
        balanceRepository.updateAll(balance);
    }

    public void updateUserBalance(long userId, int account, BigDecimal balance) {
        if (account != 0) {
            log.debug("Request Postgres: {}, {}, {}", userId, account, balance);
            balanceRepository.updateUser(userId, account, balance);
        } else {
            log.debug("Request Postgres: {}, {}", userId, balance);
            balanceRepository.updateUser(userId, balance);
        }
    }

    public AccountDto getAccount(int id) {
        log.debug("Request Postgres: {}", id);
        AccountDto accountDto = accountRepository.getReferenceById(id);
        log.debug("Response Postgres: {}", accountDto);
        return accountDto;
    }

    public List<AccountDto> getAccountList() {
        List<AccountDto> accountDtoList = accountRepository.findAll();
        log.debug("Response Postgres: {}", accountDtoList);
        return accountDtoList;
    }
}