-- ----------------------------
-- Table SupervisorFullCustomer for Customer
-- ----------------------------

CREATE TABLE "SupervisorFullCustomer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"customerUniqueId"  TEXT  COLLATE NOCASE ,
"CustomerName"  TEXT,
"CustomerCode"  TEXT,
"Latitude" NUMBER,
"Longitude" NUMBER,
"Address"  TEXT,
"Phone"  TEXT,
"StoreName"  TEXT,
"Mobile"  TEXT,
"NationalCode"  TEXT(2048),
"CustomerLevel"  TEXT COLLATE NOCASE ,
"CustomerActivity"  TEXT COLLATE NOCASE ,
"customerActivityUniqueId"  TEXT COLLATE NOCASE ,
"CustomerCategory"  TEXT COLLATE NOCASE ,
"dealerUniqueId"  TEXT COLLATE NOCASE ,
"DealerName"  TEXT COLLATE NOCASE ,
--"VisitTemplatePathTitle"  TEXT COLLATE NOCASE ,
--"VisitTemplatePathUniqueId"TEXT COLLATE NOCASE,
"customerCategoryUniqueId"TEXT COLLATE NOCASE,
"customerLevelUniqueId"TEXT COLLATE NOCASE,
"CityId"  TEXT COLLATE NOCASE ,
"StateId"  TEXT COLLATE NOCASE ,
"CenterId"  TEXT COLLATE NOCASE ,
PRIMARY KEY ("customerUniqueId" ASC)
);