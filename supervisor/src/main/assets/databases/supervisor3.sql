-- ----------------------------
-- Related to NGT-3510
-- ----------------------------
DROP TABLE IF EXISTS Location;
CREATE TABLE Location (
    UniqueId             TEXT    NOT NULL COLLATE NOCASE,
    Longitude            REAL,
    Latitude             REAL,
    Accuracy             REAL,
    Speed                REAL,
    Date                 INTEGER,
    EventType            TEXT,
    Event                TEXT,
    IsSend               INTEGER,
    LastRetryTime        INTEGER,
    Tracking             INTEGER,
    Address              TEXT,
    ActivityType         INTEGER,
    IsImportant          INTEGER,
    TourId               TEXT,
    TourRef              INTEGER,
    CompanyPersonnelId   TEXT,
    CompanyPersonnelName TEXT,
    CustomerId           TEXT,
    TimeOffset           INTEGER,
    Provider             TEXT,
    LicensePolicy        INTEGER,
    DateAndTime          TEXT,
    UpdateTime           INTEGER,
    DeviceTime           INTEGER,
    PRIMARY KEY (
        UniqueId ASC
    )
);
