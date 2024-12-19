-- Table: transaction_pin
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'transaction_pins' AND xtype = 'U')
BEGIN
    CREATE TABLE transaction_pins (
        id NVARCHAR(255) PRIMARY KEY,
        business_id NVARCHAR(255) NOT NULL,
        pin NVARCHAR(255) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END;

-- Table: transaction
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE transactions (
        id NVARCHAR(255) PRIMARY KEY,
        reference NVARCHAR(255) NOT NULL,
        transaction_type NVARCHAR(255) NOT NULL,
        amount DECIMAL(19, 4) NOT NULL,
        fee FLOAT NOT NULL,
        status NVARCHAR(255) NOT NULL,
        status_description NVARCHAR(255),
        currency NVARCHAR(3) DEFAULT 'NGN',
        initiator_id NVARCHAR(255) NOT NULL,
        description NVARCHAR(255),
        transaction_date DATETIME NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END;

-- Table: pos_transaction
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'pos_transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE pos_transactions (
        id NVARCHAR(255) PRIMARY KEY,
        transaction_id NVARCHAR(255) NOT NULL,
        rrn NVARCHAR(255),
        card_number NVARCHAR(255),
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT FK_pos_transactions_transaction FOREIGN KEY (transaction_id)
            REFERENCES transactions(id) ON DELETE CASCADE
    );
END;

-- Table: debit_transaction
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'debit_transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE debit_transactions (
        id NVARCHAR(255) PRIMARY KEY,
        transaction_id NVARCHAR(255) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT FK_debit_transactions_transaction FOREIGN KEY (transaction_id)
            REFERENCES transactions(id) ON DELETE CASCADE
    );
END;

-- Table: credit_transaction
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name = 'credit_transactions' AND xtype = 'U')
BEGIN
    CREATE TABLE credit_transactions (
        id NVARCHAR(255) PRIMARY KEY,
        transaction_id NVARCHAR(255) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT FK_credit_transactions_transaction FOREIGN KEY (transaction_id)
            REFERENCES transactions(id) ON DELETE CASCADE
    );
END;
