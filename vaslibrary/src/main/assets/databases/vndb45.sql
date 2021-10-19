-- ----------------------------
-- Alter Table structure for VisitTemplatePathCustomer
-- ----------------------------
alter table VisitTemplatePathCustomer add column PathRowId INTEGER;

-- ----------------------------
-- Alter Table structure for Customer
-- ----------------------------
alter table Customer add column CustomerPostalCode TEXT;

-- ----------------------------
--  CustomerPathView
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerPathView";
CREATE VIEW CustomerPathView AS
SELECT
	c.*, VisitTemplatePathCustomer.PathRowId, VisitTemplatePathCustomer.VisitTemplatePathId AS VisitTemplatePathId
FROM
	(
		SELECT
			Customer.*, SUM(
				CustomerCallOrderPreview.TotalPrice
			) AS TotalOrderAmount
		FROM
			Customer
		LEFT JOIN CustomerCallOrderPreview ON CustomerCallOrderPreview.CustomerUniqueId = Customer.UniqueId
		GROUP BY
			Customer.UniqueId
	) c
JOIN VisitTemplatePathCustomer ON c.UniqueId = VisitTemplatePathCustomer.CustomerId;


-- ----------------------------
-- Table structure for CustomerOwnerType
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerOwnerType";
CREATE TABLE "CustomerOwnerType" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"CustomerOwnerTypeName"  TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for State
-- ----------------------------
DROP TABLE IF EXISTS "main"."State";
CREATE TABLE "State" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId" INTEGER,
"StateName"  TEXT,
"StateCode" TEXT,
PRIMARY KEY ("UniqueId")
);

-- ----------------------------
-- Table structure for County
-- ----------------------------
DROP TABLE IF EXISTS "main"."County";
CREATE TABLE "County" (
"UniqueId"  TEXT NOT NULL,
"BackOfficeId"  INTEGER,
"CountyName"  TEXT,
"CountyCode"  TEXT,
PRIMARY KEY ("UniqueId")
);
-- ----------------------------
-- Table structure for Dealer
-- ----------------------------
CREATE TABLE IF NOT EXISTS "Dealer" (
"UniqueId"  TEXT NOT NULL COLLATE NOCASE ,
"Name"  TEXT DEFAULT 0,
PRIMARY KEY ("UniqueId" ASC)
);


alter table CustomerCallReturn add column DealerUniqueId Text;
-- ----------------------------
-- َAlter View structure for RequestReportView To fix bug DMC-32977
-- ----------------------------
DROP VIEW IF EXISTS "RequestReportView";
CREATE VIEW "RequestReportView" AS
SELECT
	Customer.UniqueId,
	Customer.CustomerName,
	Customer.CustomerCode,
	Customer.StoreName,
	CustomerCallOrder.UniqueId AS OrderUniqueId,
	PaymentTypeOrder.PaymentTypeOrderName AS PaymentTypeBaseName,
	SUM(case when CustomerCall.ExtraField1 = CustomerCallOrder.UniqueId then CustomerCallOrderOrderView.RequestAmount else 0 end) AS TotalOrderNetAmount,
    CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
	group_concat(CustomerCall.CallType , ':') as CallType,
	group_concat(CustomerCall.ConfirmStatus , ':') as ConfirmStatus,
	(
		NOT CustomerCallReturn.UniqueId IS NULL
	) AS HasReturn,
ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) as Discount
FROM
	Customer
JOIN CustomerCallOrder ON CustomerCallOrder.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCallOrderOrderView ON CustomerCallOrderOrderView.OrderUniqueId = CustomerCallOrder.UniqueId
JOIN PaymentTypeOrder ON CustomerCallOrder.OrderPaymentTypeUniqueId = PaymentTypeOrder.UniqueId
LEFT JOIN CustomerCallReturn ON CustomerCallReturn.CustomerUniqueId = Customer.UniqueId
LEFT JOIN CustomerCall ON CustomerCall.CustomerId = Customer.UniqueId
GROUP BY
	Customer.UniqueId, CustomerCallOrder.UniqueId;


-- ----------------------------
-- َAlter View structure for WarehouseProductQtyView To add total return  qty. Related to issue: DMC-33990
-- ----------------------------
DROP VIEW IF EXISTS "WarehouseProductQtyView";
CREATE VIEW "WarehouseProductQtyView" AS
SELECT
Product.UniqueId AS UniqueId,
Product.ProductCode,
Product.ProductName,
Product.ProductTypeId,
ProductUnitsView.UnitName AS UnitName,
ProductUnitsView.ConvertFactor as ConvertFactor,
ProductUnitsView.ProductUnitId as ProductUnitId,
group_concat(ProductBatchOnHandQty.BatchNo, ':') as BatchNo,
CAST(OnHandQty.OnHandQty as REAL) as OnHandQty,
CAST(IFNULL(OnHandQty.RenewQty,0) as REAL) as RenewQty,
CAST(IFNULL(OnHandQty.OnHandQty,0) - IFNULL(TotalProductOrderQtyView.TotalQty,0) as REAL ) as RemainedQty,
CAST(IFNULL(TotalProductOrderQtyView.TotalQty,0) as REAL) as TotalQty,
CAST (PR.SalePrice as REAL) as SalePrice,
CAST(ifnull(PR.SalePrice,0) * (IFNULL(OnHandQty.OnHandQty,0) - IFNULL(TotalProductOrderQtyView.TotalQty,0)) as REAL) as RemainedPriceQty,
ProductReturn.TotalReturnQty
FROM
OnHandQty
JOIN ProductUnitsView ON ProductUnitsView.UniqueId = OnHandQty.ProductId
JOIN Product On Product.UniqueId = OnHandQty.ProductId
JOIN PriceHistory PR
     on PR.GoodsRef= Product.BackOfficeId
      AND pr.BackOfficeId = (
          Select tP2.BackOfficeId
          From PriceHistory tP2
         Where tP2.GoodsRef=Product.BackOfficeId
         AND ((SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66') BETWEEN tP2.StartDate AND IFNULL(tP2.EndDate, (SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66')))
         LIMIT 1
      )
LEFT JOIN TotalProductOrderQtyView ON TotalProductOrderQtyView.ProductId = OnHandQty.ProductId
LEFT JOIN ProductBatchOnHandQty ON ProductBatchOnHandQty.ProductId = OnHandQty.ProductId
LEFT JOIN
(
Select SUM(TotalReturnQty) as TotalReturnQty , ProductId FROM
CustomerCallReturnView
Group BY CustomerCallReturnView.ProductId
) as ProductReturn ON ProductReturn.ProductId = OnHandQty.ProductId
GROUP BY Product.UniqueId;



alter table City add column BackOfficeId INTEGER;

