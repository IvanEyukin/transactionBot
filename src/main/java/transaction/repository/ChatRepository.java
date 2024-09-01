package transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import transaction.dto.bot.Chat;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {
}