CREATE TABLE IF NOT EXISTS ethernet.blocks
(
    `number`    bigint,
    `timestamp` timestamp
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');