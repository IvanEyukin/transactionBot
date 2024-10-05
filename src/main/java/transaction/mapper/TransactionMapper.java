package transaction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import transaction.dto.bot.Transaction;
import transaction.dto.database.TransactionDto;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "timeCreate", ignore = true)
    TransactionDto transactionMapper(Transaction transaction);

    Transaction transactionDtoMapper(TransactionDto transactionDto);

    @Mapping(target = "guid", qualifiedByName = "mapGuid", source = "transactionDto.guid")
    @Mapping(target = "timeCreate", ignore = true)
    @Mapping(target = "userSrc", source = "transactionDto.userDst")
    @Mapping(target = "userDst", source = "transactionDto.userSrc")
    @Mapping(target = "comment", constant = "Транзакция отменена администратором")
    TransactionDto transactionNewMapper(TransactionDto transactionDto);

    @Named("mapGuid")
    default UUID mapGuid(UUID uuid) {
        return UUID.randomUUID();
    }
}