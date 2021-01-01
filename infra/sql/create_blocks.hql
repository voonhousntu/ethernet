CREATE TABLE ethernet.blocks
(
    `timestamp`         timestamp,
    `number`            bigint,
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
    `size`              int,
    `extra_data`        string,
    `gas_limit`         int,
    `gas_used`          int,
    `transaction_count` int

)
    PARTITIONED BY
        (
        `number` bigint,
        `timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');