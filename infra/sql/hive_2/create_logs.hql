CREATE TABLE IF NOT EXISTS ethernet.logs
(
    `log_index`         bigint,
    `transaction_hash`  string,
    `transaction_index` bigint,
    `address`           string,
    `data`              string,
    `topics`            string,
    `block_timestamp`   timestamp,
    `block_number`      bigint,
    `block_hash`        string

)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');