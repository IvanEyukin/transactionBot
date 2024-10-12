--Поставка 05.10.2024

ALTER TABLE public.transactions ADD "comment" text NULL;

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



--Поставка 12.10.2024

DROP VIEW users_transactions

ALTER TABLE public.transactions DROP CONSTRAINT "transactions_users_src_fk"
ALTER TABLE public.transactions DROP CONSTRAINT "transactions_users_dst_fk"
ALTER TABLE public.balance DROP CONSTRAINT "balance_users_fk"

ALTER TABLE public.users ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.transactions ALTER COLUMN user_src TYPE BIGINT USING user_src::BIGINT;
ALTER TABLE public.transactions ALTER COLUMN user_dst TYPE BIGINT USING user_dst::BIGINT;
ALTER TABLE public.balance ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;

ALTER TABLE public.transactions ADD CONSTRAINT transactions_users_src_fk FOREIGN KEY (user_src) REFERENCES public.users(id) ON DELETE CASCADE
ALTER TABLE public.transactions ADD CONSTRAINT transactions_users_dst_fk FOREIGN KEY (user_dst) REFERENCES public.users(id) ON DELETE CASCADE
ALTER TABLE public.balance ADD CONSTRAINT balance_users_fk FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE

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