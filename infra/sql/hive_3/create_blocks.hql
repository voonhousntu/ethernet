CREATE TABLE IF NOT EXISTS ethernet.blocks
(
    `number`            bigint,
    `timestamp`         timestamp,
    `hash`              string,
    `parent_hash`       string,
    `nonce`             string,
    `sha3_uncles`       string,
    `logs_bloom`        string,
    `transactions_root` string,
    `state_root`        string,
    `receipts_root`     string,
    `miner`             string,
    `difficulty`        numeric,
    `total_difficulty`  numeric,
    `size`              bigint,
    `extra_data`        string,
    `gas_limit`         bigint,
    `gas_used`          bigint,
    `transaction_count` bigint

)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');