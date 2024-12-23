-- Table: transaction_pins
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'transaction_pins' AND xtype = 'U')
BEGIN
    CREATE TABLE transaction_pins (
        id NVARCHAR(255) PRIMARY KEY,
        user_id NVARCHAR(255) NOT NULL,
        terminal_id NVARCHAR(255),
        pin NVARCHAR(255) NOT NULL,
        created_at BIGINT NOT NULL,
        updated_at BIGINT NOT NULL
    );

    -- Indexes
    CREATE INDEX IDX_transaction_pins_user_id ON transaction_pins(user_id);
END;

-- Table: transactions
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE transactions (
        id NVARCHAR(255) PRIMARY KEY,
        reference NVARCHAR(255) NOT NULL UNIQUE,
        transaction_type NVARCHAR(255) NOT NULL,
        amount DECIMAL(19, 4) NOT NULL,
        fee FLOAT NOT NULL,
        status NVARCHAR(255) NOT NULL,
        status_description NVARCHAR(255),
        currency NVARCHAR(3) DEFAULT 'NGN',
        initiator_id NVARCHAR(255) NOT NULL,
        description NVARCHAR(255),
        created_at BIGINT NOT NULL,
        updated_at BIGINT NOT NULL
    );

    -- Indexes
    CREATE INDEX IDX_transactions_reference ON transactions(reference);
    CREATE INDEX IDX_transactions_initiator_id ON transactions(initiator_id);
    CREATE INDEX IDX_transactions_status ON transactions(status);
END;

-- Table: pos_transactions
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'pos_transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE pos_transactions (
        id NVARCHAR(255) PRIMARY KEY,
        transaction_id NVARCHAR(255) NOT NULL,
        transaction_date BIGINT NOT NULL,
        pan NVARCHAR(255),
        rrn NVARCHAR(255),
        stan NVARCHAR(255),
        account_agent_number NVARCHAR(255),
        card_expiry NVARCHAR(255),
        status_code NVARCHAR(255),
        customer_name NVARCHAR(255),
        terminal_id NVARCHAR(255),
        serial_number NVARCHAR(255),
        invoice_id NVARCHAR(255),
        created_at BIGINT NOT NULL,
        updated_at BIGINT NOT NULL,
        CONSTRAINT FK_pos_transactions_transaction FOREIGN KEY (transaction_id)
            REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- Indexes
    CREATE INDEX IDX_pos_transactions_transaction_id ON pos_transactions(transaction_id);
    CREATE INDEX IDX_pos_transactions_terminal_id ON pos_transactions(terminal_id);
END;

-- Table: debit_transactions
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'debit_transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE debit_transactions (
        id NVARCHAR(255) PRIMARY KEY,
        transaction_id NVARCHAR(255) NOT NULL,
        created_at BIGINT NOT NULL,
        updated_at BIGINT NOT NULL,
        CONSTRAINT FK_debit_transactions_transaction FOREIGN KEY (transaction_id)
            REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- Indexes
    CREATE INDEX IDX_debit_transactions_transaction_id ON debit_transactions(transaction_id);
END;

-- Table: credit_transactions
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'credit_transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE credit_transactions (
        id NVARCHAR(255) PRIMARY KEY,
        transaction_id NVARCHAR(255) NOT NULL,
        created_at BIGINT NOT NULL,
        updated_at BIGINT NOT NULL,
        CONSTRAINT FK_credit_transactions_transaction FOREIGN KEY (transaction_id)
            REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- Indexes
    CREATE INDEX IDX_credit_transactions_transaction_id ON credit_transactions(transaction_id);
END;
