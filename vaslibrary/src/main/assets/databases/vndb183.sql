---CREATE TABLE CustomerLastBill
CREATE TABLE "CustomerLastBill" (
"uniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"ProductName"  TEXT ,
"CustomerCode"  TEXT ,
"ProductCode"  TEXT ,
"CustomerName"  TEXT ,
"ProductUnit_EA"  TEXT ,
"ProductQTY_EA"  TEXT ,
"ProductUnit_KAR"  TEXT ,
"ProductQTY_KAR"  TEXT ,
"Type"  TEXT ,
"TypeSum"  TEXT ,
"ProductUnit_SUM_EA"  TEXT ,
"ProductQTY_SUM_EA"  TEXT ,
"ProductQTY_SUM_KAR"  TEXT ,
"ProductUnit_SUM_KAR"  TEXT ,
PRIMARY KEY ("uniqueId" ASC));
