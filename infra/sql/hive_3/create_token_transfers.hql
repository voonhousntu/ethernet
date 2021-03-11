CREATE TABLE ethernet.token_transfers
(
    `token_address`     string,
    `from_address`     string,
    `to_address`       string,
    `value`            string,
    `transaction_hash` string,
    `log_index`        bigint,
    `block_hash`       string,
    `block_number`     bigint,
    `block_timestamp`  timestamp
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');