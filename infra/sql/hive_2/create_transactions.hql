CREATE TABLE IF NOT EXISTS ethernet.transactions
(
    `hash`                        string,
    `nonce`                       bigint,
    `transaction_index`           bigint,
    `from_address`                string,
    `to_address`                  string,
    `value`                       string,
    `gas`                         bigint,
    `gas_price`                   bigint,
    `input`                       string,
    `receipt_cumulative_gas_used` bigint,
    `receipt_gas_used`            bigint,
    `receipt_contract_address`    string,
    `receipt_root`                string,
    `receipt_status`              bigint,
    `block_timestamp`             timestamp,
    `block_number`                bigint,
    `block_hash`                  string
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');