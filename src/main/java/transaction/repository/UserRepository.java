package transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transaction.dto.database.UserDto;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserDto, Long> {
    UserDto findByUserNameIgnoreCase(String userName);
    List<UserDto> findByIsAdmin(boolean isAdmin);
}