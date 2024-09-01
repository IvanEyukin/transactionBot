package transaction.entitie;

public enum Status {
    Start,
    Transaction,
    Balance,
    Rename,
    WaitingName,
    WaitingTransactionUser,
    WaitingTransactionAccount,
    WaitingTransactionBalance,
    WaitingTransactionConfirm,
    WaitingUserCommand,
    AdminMenu,
    WaitingAdminCommand,
    WaitingAdminBalanceUser,
    WaitingAdminBalanceAccount,
    WaitingAdminBalance,
    Error;
}