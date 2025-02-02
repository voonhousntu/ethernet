CREATE TABLE IF NOT EXISTS ethernet.block_timestamp_mapping
(
    `number` bigint,
    `timestamp` timestamp
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');