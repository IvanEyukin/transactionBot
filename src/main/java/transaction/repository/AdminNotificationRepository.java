package transaction.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import transaction.dto.database.AdminNotification;

import java.util.List;

@Repository
public interface AdminNotificationRepository extends CrudRepository<AdminNotification, Long> {
    @NotNull
    List<AdminNotification> findAll();
}