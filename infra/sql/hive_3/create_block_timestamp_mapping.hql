CREATE TABLE ethernet.blocks
    PARTITIONED BY
        (
        `number` bigint,
        `timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');