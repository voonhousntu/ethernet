CREATE TABLE ethernet.traces
(
    `transaction_hash`  string,
    `transaction_index` bigint,
    `from_address`      string,
    `to_address`        string,
    `value`             numeric,
    `input`             string,
    `output`            string,
    `trace_type`        string,
    `call_type`         string,
    `reward_type`       string,
    `gas`               bigint,
    `gas_used`          bigint,
    `subtraces`         bigint,
    `trace_address`     string,
    `error`             string,
    `status`            bigint,
    `block_hash`        string,
    `trace_id`          string
)
    PARTITIONED BY
        (
        `block_number` bigint,
        `block_timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');