CREATE TABLE public.users (
	id BIGINT NOT NULL,
	time_create timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	first_name text NULL,
	last_name text NULL,
	user_name text NULL,
	is_admin boolean DEFAULT false NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE public.accounts (
	id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
	name text NOT NULL,
	translate text NOT NULL,
	CONSTRAINT accounts_pk PRIMARY KEY (id)
);

CREATE TABLE public.transactions (
	guid uuid NOT NULL,
	time_create timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	user_src BIGINT NOT NULL,
	user_dst BIGINT NOT NULL,
	account integer NOT NULL,
	sum decimal NOT NULL,
	comment text NULL,
	CONSTRAINT transactions_pk PRIMARY KEY (guid),
	CONSTRAINT transactions_users_src_fk FOREIGN KEY (user_src) REFERENCES public.users(id) ON DELETE CASCADE,
	CONSTRAINT transactions_users_dst_fk FOREIGN KEY (user_dst) REFERENCES public.users(id) ON DELETE CASCADE,
	CONSTRAINT transactions_accounts_fk FOREIGN KEY (account) REFERENCES public.accounts(id) ON DELETE CASCADE
);

CREATE TABLE public.balance (
	user_id BIGINT NOT NULL,
	account integer NOT NULL,
	balance decimal NOT NULL,
	CONSTRAINT balance_users_fk FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
	CONSTRAINT balance_accounts_fk FOREIGN KEY (account) REFERENCES public.accounts(id) ON DELETE CASCADE
);

CREATE FUNCTION insert_user_in_balance() RETURNS trigger AS $$
    DECLARE
        account RECORD;
	BEGIN
	    FOR account IN
	        SELECT
	            id
	        FROM
	            accounts
        LOOP
            INSERT INTO balance (user_id, account, balance)
            VALUES (NEW.id, account.id, 0);
        END LOOP;
	RETURN NEW;
	END;
$$ LANGUAGE 'plpgsql';

CREATE FUNCTION update_user_balance() RETURNS trigger AS $$
    BEGIN
		UPDATE balance
		SET balance = (SELECT balance FROM balance WHERE user_id = NEW.user_src AND account = NEW.account) - NEW.sum
		WHERE
		    user_id = NEW.user_src
		    AND account = NEW.account;

		UPDATE balance
		SET balance = (SELECT balance FROM balance WHERE user_id = NEW.user_dst AND account = NEW.account) + NEW.sum
		WHERE
		    user_id = NEW.user_dst
		    AND account = NEW.account;
	RETURN NEW;
	END;
$$ LANGUAGE 'plpgsql';

CREATE FUNCTION insert_account_in_balance() RETURNS trigger AS $$
    DECLARE
        users RECORD;
	BEGIN
	    FOR users IN
	        SELECT
	            id
	        FROM
	            users
        LOOP
            INSERT INTO balance (user_id, account, balance)
            VALUES (users.id, NEW.id, 0);
        END LOOP;
	RETURN NEW;
	END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER accounts_insert
	AFTER INSERT
	ON public.accounts
	FOR EACH ROW
	EXECUTE PROCEDURE public.insert_account_in_balance();

CREATE TRIGGER users_insert
	AFTER INSERT
	ON public.users
	FOR EACH ROW
	EXECUTE PROCEDURE public.insert_user_in_balance();

CREATE TRIGGER transactions_insert
	AFTER INSERT
	ON public.transactions
	FOR EACH ROW
	EXECUTE PROCEDURE public.update_user_balance();

INSERT INTO accounts (name, translate) VALUES
('first', 'первый'),
('second', 'второй'),
('third', 'третий');

CREATE OR REPLACE VIEW users_transactions AS (
    SELECT DISTINCT
        u.id,
        u.user_name,
        t.user_src
    FROM
        transactions t
        JOIN users u ON u.id = t.user_dst
    WHERE
        u.user_name IS NOT null
    ORDER BY
        u.user_name
)

--Тестовые данные

INSERT INTO users (id, first_name, last_name, user_name) VALUES
(1, '1', '1', '1'),
(2, '2', '2', '2'),
(3, '3', '3', '3'),
(4, '4', '4', '4'),
(5, '5', '5', '5'),
(6, '6', '6', '6'),
(7, '7', '7', '7'),
(8, '8', '8', '8'),
(9, '9', '9', '9')