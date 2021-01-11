CREATE TABLE ethernet.block_timestamp_mapping
(
    `timestamp` timestamp
)
    PARTITIONED BY
        (
        `number` bigint
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');