CREATE TABLE ethernet.contracts
(
    `address`            string,
    `bytecode`           string,
    `function_sighashes` string,
    `is_erc20`           boolean,
    `is_erc721`          boolean,
    `block_hash`         string

)
    PARTITIONED BY
        (
        `block_number` bigint,
        `block_timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');