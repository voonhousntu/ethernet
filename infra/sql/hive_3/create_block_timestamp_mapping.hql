CREATE TABLE ethernet.blocks
(
    `number`    bigint,
    `timestamp` timestamp
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');