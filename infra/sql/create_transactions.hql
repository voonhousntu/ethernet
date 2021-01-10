CREATE TABLE ethernet.transactions
(
    `hash`                        string,
    `nonce`                       int,
    `transaction_index`           int,
    `from_address`                string,
    `to_address`                  string,
    `value`                       numeric,
    `gas`                         int,
    `gas_price`                   int,
    `input`                       string,
    `receipt_cumulative_gas_used` int,
    `receipt_gas_used`            int,
    `receipt_contract_address`    string,
    `receipt_root`                string,
    `receipt_status`              int,
    `block_hash`                  string

)
    PARTITIONED BY
        (
        `block_number` bigint,
        `block_timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');