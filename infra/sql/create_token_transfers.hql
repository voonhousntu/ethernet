CREATE TABLE ethernet.token_transfers
(
    `token_adress`     string,
    `from_address`     string,
    `to_address`       string,
    `value`            string,
    `transaction_hash` string,
    `log_index`        int,
    `block_hash`       string

)
    PARTITIONED BY
        (
        `block_number` bigint,
        `block_timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');