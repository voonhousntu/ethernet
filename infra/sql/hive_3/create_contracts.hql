CREATE TABLE ethernet.contracts
(
    `block_number`       bigint,
    `block_timestamp`    timestamp,
    `address`            string,
    `bytecode`           string,
    `function_sighashes` string,
    `is_erc20`           boolean,
    `is_erc721`          boolean,
    `block_hash`         string
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');