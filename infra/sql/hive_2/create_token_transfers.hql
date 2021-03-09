CREATE TABLE ethernet.token_transfers
(
    `token_adress`     string,
    `from_address`     string,
    `to_address`       string,
    `value`            string,
    `transaction_hash` string,
    `log_index`        bigint,
    `block_timestamp`  timestamp,
    `block_number`     bigint,
    `block_hash`       string

)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');