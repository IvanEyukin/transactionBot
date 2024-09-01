package transaction.sender;

public class Sender {
    public final static String START = """
            Приветствую %s!
            Чем я могу тебе помочь?
            """;

    public final static String ERROR = """
            Прости, но я тебя не понимаю.
            Выбери что ты хочешь из меню.
            """;

    public final static String START_NO_USER = """
            Приветствую Путешественник!
            Мы еще не знакомы, представься мне.
            """;

    public final static String USER_NAME_SAVE = """
            Рад с тобой познакомится, %s!
            Чем я могу тебе помочь?
            """;

    public final static String USER_NAME_UPDATE = """
            О, ты решил изменить имя?
            И как же мне теперь тебя называть?
            """;

    public final static String BALANCE = """
            %s, ты располагаешь следующими средствами:
            """;

    public final static String BALANCE_ACCOUNT = """
            Баланс счета %s составляет %s.
            """;

    public final static String TRANSACTION_USER = """
            Укажи имя, кому перевести средства.
            Но помни, я могу перевести только знакомым мне людям!
            """;

    public final static String TRANSACTION_USER_ERROR = """
            К сожалению, я не знаю этого человека.
            Укажи другое имя.
            """;

    public final static String TRANSACTION_ACCOUNT = """
            Выбери счет, с которого нужно списать средства.
            """;

    public final static String TRANSACTION_ACCOUNT_ZERO = """
            К сожалению на твоих счетах недостаточно средств для перевода.
            Обратись к администратору.
            """;

    public final static String TRANSACTION_SUM = """
            Укажи сумму к переводу.
            """;

    public final static String TRANSACTION_SUM_ERROR_ZERO = """
            Сумма к переводу должна быть больше нуля.
            """;

    public final static String TRANSACTION_SUM_ERROR_INT = """
            Кажется ты ввел не число. Будь добр, введи сумму к переводу числом.
            """;

    public final static String TRANSACTION_SUM_ERROR_DEFICIENCY = """
            Твоих средств недостаточно для перевода.
            Укажи другую сумму.
            """;

    public final static String TRANSACTION_CONFIRM = """
            Выполнить перевод %s, со счета %s, на сумму %s?
            """;

    public final static String TRANSACTION_CANCEL= """
            Перевод отменен.
            """;

    public final static String NOTIFICATION_USER_SRC = """
            Перевод %s, со счета %s, на сумму %s был доставлен.
            %s
            #Перевод
            """;

    public final static String NOTIFICATION_USER_SRC_CANCELLATION = """
            Перевод %s, со счета %s, на сумму %s был отменен администратором.
            %s
            #ПереводОтменен
            """;

    public final static String NOTIFICATION_USER_DST = """
            Вам поступили средства в размере %s на счет %s, от %s.
            %s
            #Поступление
            """;

    public final static String NOTIFICATION_USER_DST_CANCELLATION = """
            Поступление средств в размере %s на счет %s, от %s было отменено администратором.
            %s
            #ПоступлениеОтменено
            """;

    public final static String NOTIFICATION_USER_ADMIN = """
            Совершен перевод
            От кого: %s
            Кому: %s
            Счет: %s
            Сумма: %s
            """;

    public final static String NOTIFICATION_USER_ADMIN_CANCELLATION = """
            Выполнена отмена перевода
            От кого: %s
            Кому: %s
            Счет: %s
            Сумма: %s
            """;

    public final static String ADMIN_ANSWER = """
            Что нужно сделать?
            """;

    public final static String ADMIN_USERS = """
            Список всех пользователей бота:
            """;

    public final static String ADMIN_ANSWER_USER = """
            Укажи имя пользователя.
            """;

    public final static String ADMIN_ANSWER_ACCOUNT = """
            Укажи, на какой аккаунт пользователя зачислить средства.
            """;

    public final static String ADMIN_ANSWER_BALANCE = """
            Укажи сумму для обновления пользовательского баланса.
            """;

    public final static String ADMIN_NOTIFICATION_UPDATE_BALANCE = """
            Администратор обновил ваш баланс.
            """;
}