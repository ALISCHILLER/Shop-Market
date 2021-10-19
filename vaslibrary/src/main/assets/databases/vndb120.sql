-- ----------------------------
--  Alter view CustomerCallReturnView related to issue: NGT-3484
-- ----------------------------
DROP VIEW IF EXISTS "main"."CustomerCallReturnView";
CREATE VIEW "CustomerCallReturnView" AS
SELECT
	CustomerCallReturnLinesView.UniqueId,
	CustomerCallReturnLinesView.IsPromoLine,
	CustomerCallReturnLinesView.StockId,
	CustomerCallReturnLinesView.CustomerUniqueId AS CustomerUniqueId,
	CustomerCallReturnLinesView.ReturnUniqueId AS ReturnUniqueId,
	CustomerCallReturnLinesView.InvoiceId AS InvoiceId,
	sum(CustomerCallReturnLinesView.TotalRequestAmount) AS TotalRequestAmount,
	CustomerCallReturnLinesView.IsFromRequest AS IsFromRequest,
	CustomerCallReturnLinesView.OriginalTotalReturnQty as OriginalTotalReturnQty,
	CustomerCallReturnLinesView.Comment as Comment,
	CustomerCallReturnLinesView.DealerUniqueId as DealerUniqueId,
	group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
	group_concat(ReturnReasonId, ':') AS ReturnReasonId,
	CustomerCallReturnLinesView.ProductName AS ProductName,
	CustomerCallReturnLinesView.ProductCode AS ProductCode,
	CustomerCallReturnLinesView.ProductId AS ProductId,
	group_concat(CustomerCallReturnLinesView.ConvertFactor,'|') AS ConvertFactor,
	group_concat(CustomerCallReturnLinesView.ProductUnitId,'|') AS ProductUnitId,
	group_concat(CustomerCallReturnLinesView.Qty,'|') AS Qty,
	group_concat(CustomerCallReturnLinesView.UnitName,'|') AS UnitName,
	sum(CustomerCallReturnLinesView.TotalReturnQty) AS TotalReturnQty,
	CustomerOldInvoiceDetail.TotalQty AS InvoiceQty,
	CustomerOldInvoiceHeader.SaleNo AS SaleNo,
	CustomerCallReturnLinesView.RequestUnitPrice,
	sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN
		ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount,0)
	ELSE
		ifnull(CustomerCallReturnLinesView.TotalRequestAmount,0) -
		ifnull(CustomerCallReturnLinesView.TotalRequestDiscount,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestTax,0) +
		ifnull(CustomerCallReturnLinesView.TotalRequestCharge,0)
	END)  AS TotalRequestNetAmount
FROM CustomerCallReturnLinesView
JOIN ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
LEFT JOIN CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId
and CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
LEFT JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId
AND CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
GROUP BY CustomerCallReturnLinesView.ProductId, CustomerCallReturnLinesView.IsPromoLine, ReturnUniqueId;

-- ----------------------------
--  Create view PaymentReportView related to issue: NGT-1298
-- ----------------------------
DROP VIEW IF EXISTS "main"."PaymentReportView";
CREATE VIEW "PaymentReportView" AS
SELECT
	Customer.UniqueId AS CustomerId,
	Customer.CustomerCode AS CustomerCode,
	Customer.CustomerName AS CustomerName,
	a.PaymentId AS PaymentId,
	a.InvoiceId AS InvoiceId,
	a.CashAmount AS CashAmount,
	a.ChequeAmount AS ChequeAmount,
	a.CardAmount AS CardAmount,
	a.SettlementDiscountAmount AS SettlementDiscountAmount,
	a.CreditAmount AS CreditAmount,
	Payment.CheckNumber AS ChqNo,
	Payment.ChqDate AS ChqDate,
	Bank.BankName AS BankName,
	Payment.ChqBranchName AS ChqBranchName,
	Payment.ChqBranchCode AS ChqBranchCode,
	Payment.CheckAccountNumber AS ChqAccountNo,
	Payment.ChqAccountName AS ChqAccountName,
	Payment.Ref AS FollowNo,
	City.CityName AS CityName,
	a.PaidAmount AS PaidAmount,
	CustomerCallInvoice.DealerName AS DealerName,
	payment.PaymentType
FROM
	(
	SELECT
		Payment.UniqueId AS PaymentId,
		Payment.CustomerId,
		InvoicePaymentInfo.InvoiceId,
		CASE
			WHEN Payment.PaymentType = '837689e8-2115-4085-bf7f-0d0da86f3d71' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS CashAmount,
		CASE
			WHEN Payment.PaymentType = 'e3a93634-ae20-4d57-8e27-eee7b768adfc' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS ChequeAmount,
		CASE
			WHEN Payment.PaymentType = 'f1b06da6-122d-4427-abd0-84a7cf72b29c' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS CardAmount,
		CASE
			WHEN Payment.PaymentType = 'df7e99c9-2ed9-436a-b9a3-8ec0f4e86651' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS SettlementDiscountAmount,
		CASE
			WHEN Payment.PaymentType = '56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862' THEN
			InvoicePaymentInfo.Amount ELSE 0
		END AS CreditAmount,
		InvoicePaymentInfo.Amount AS PaidAmount
	FROM
		Payment
		INNER JOIN InvoicePaymentInfo ON InvoicePaymentInfo.PaymentId = Payment.UniqueId
	UNION ALL
	SELECT
		Payment.UniqueId AS PaymentId,
		Payment.CustomerId,
		NULL AS InvoiceId,
		CASE
			WHEN Payment.PaymentType = '837689e8-2115-4085-bf7f-0d0da86f3d71' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS CashAmount,
		CASE
			WHEN Payment.PaymentType = 'e3a93634-ae20-4d57-8e27-eee7b768adfc' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS ChequeAmount,
		CASE
			WHEN Payment.PaymentType = 'f1b06da6-122d-4427-abd0-84a7cf72b29c' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS CardAmount,
		CASE
			WHEN Payment.PaymentType = 'df7e99c9-2ed9-436a-b9a3-8ec0f4e86651' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS SettlementDiscountAmount,
		CASE
			WHEN Payment.PaymentType = '56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862' THEN
			( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) ELSE 0
		END AS CreditAmount,
		( Payment.Amount - IFNULL(sum( InvoicePaymentInfo.Amount ), 0) ) AS PaidAmount
	FROM
		Payment
		LEFT JOIN InvoicePaymentInfo ON InvoicePaymentInfo.PaymentId = Payment.UniqueId
	GROUP BY
		PaymentId
	) AS a
	INNER JOIN customer ON Customer.UniqueId = a.CustomerId
	LEFT JOIN Payment ON Payment.UniqueId = a.PaymentId
	LEFT JOIN Bank ON Bank.UniqueId = Payment.BankId
	LEFT JOIN City ON City.UniqueId = Payment.CityId
	LEFT JOIN CustomerCallInvoice ON CustomerCallInvoice.UniqueId = a.InvoiceId
WHERE
	PaidAmount >0;

-- ----------------------------
--  Create view DeliveryReportView related to issue: NGT-1297
-- ----------------------------
DROP VIEW IF EXISTS "main"."DeliveryReportView";
CREATE VIEW "DeliveryReportView" AS
SELECT
	c.CustomerId AS CustomerId,
	h.CustomerCode AS CustomerCode,
	h.CustomerName AS CustomerName,
	c.OrderUniqueId AS OrderUniqueId,
	c.InvoiceNetAmount AS InvoiceNetAmount,
	c.InvoiceNetAmount - c.OrderNetAmount AS InvoiceReturnNetAmount,
	h.TotalReturnNetAmount AS TotalReturnNetAmount,
	h.TotalOldInvoiceAmount AS TotalOldInvoiceAmount,
	h.TotalPayAbleAmount AS TotalPayAbleAmount,
	h.ReceiptAmount AS ReceiptAmount,
	c.CashAmount AS CashAmount,
	c.ChequeAmount AS ChequeAmount,
	c.CardAmount AS CardAmount,
	c.SettlementDiscountAmount AS SettlementDiscountAmount,
	c.CreditAmount AS CreditAmount,
	c.DealerName AS DealerName
FROM
	(
	SELECT
		a.CustomerId AS CustomerId,
		a.OrderUniqueId AS OrderUniqueId,
		a.OrderNetAmount AS OrderNetAmount,
		a.InvoiceNetAmount AS InvoiceNetAmount,
		IFNULL( b.CashAmount, 0 ) AS CashAmount,
		IFNULL( b.ChequeAmount, 0 ) AS ChequeAmount,
		IFNULL( b.CardAmount, 0 ) AS CardAmount,
		IFNULL( b.SettlementDiscountAmount, 0 ) AS SettlementDiscountAmount,
		IFNULL( b.CreditAmount, 0 ) AS CreditAmount,
		CustomerCallInvoice.DealerName AS DealerName
	FROM
		(
		SELECT
			customerCallOrderOrderView.OrderUniqueId AS OrderUniqueId,
			customerCallOrderOrderView.CustomerUniqueId AS CustomerId,
			sum(
				i.TotalPrice - i.InvoiceDis1Amount - i.InvoiceDis2Amount - i.InvoiceDis3Amount + i.InvoiceAdd1Amount + i.InvoiceAdd2Amount + i.InvoiceTaxAmount + i.InvoiceChargeAmount
			) AS InvoiceNetAmount,
			sum(CASE
			WHEN EXISTS ( SELECT 1 FROM CustomerCall WHERE customerCallOrderOrderView.OrderUniqueId = CustomerCall.ExtraField1 AND ( CustomerCall.CallType = 15 OR CustomerCall.CallType = 16 ) )
			THEN
			CASE
				WHEN customerCallOrderOrderView.IsRequestFreeItem = 1 THEN (0)
				WHEN customerCallOrderOrderView.IsPromoLine = 1
				THEN IFNULL( customerCallOrderOrderView.PromotionPrice, 0 ) - IFNULL( customerCallOrderOrderView.RequestDis1Amount, 0 ) - IFNULL( customerCallOrderOrderView.RequestDis2Amount, 0 ) - IFNULL( customerCallOrderOrderView.RequestDis3Amount, 0 ) + IFNULL( customerCallOrderOrderView.RequestAdd1Amount, 0 ) + IFNULL( customerCallOrderOrderView.RequestAdd2Amount, 0 ) + IFNULL( customerCallOrderOrderView.RequestTaxAmount, 0 ) + IFNULL( customerCallOrderOrderView.RequestChargeAmount, 0 )
				ELSE IFNULL( customerCallOrderOrderView.RequestAmount, 0 ) - IFNULL( customerCallOrderOrderView.RequestDis1Amount, 0 ) - IFNULL( customerCallOrderOrderView.RequestDis2Amount, 0 ) - IFNULL( customerCallOrderOrderView.RequestDis3Amount, 0 ) + IFNULL( customerCallOrderOrderView.RequestAdd1Amount, 0 ) + IFNULL( customerCallOrderOrderView.RequestAdd2Amount, 0 ) + IFNULL( customerCallOrderOrderView.RequestTaxAmount, 0 ) + IFNULL( customerCallOrderOrderView.RequestChargeAmount, 0 )
			END
			ELSE 0
			END) AS OrderNetAmount
		FROM
			customerCallOrderOrderView
			LEFT JOIN (
			SELECT
				CustomerCallInvoiceLines.UniqueId AS LineUniqueId,
				CustomerCallInvoiceLines.OrderUniqueId AS OrderUniqueId,
				CASE
					WHEN IsRequestFreeItem = 1 THEN
					( 0 )
					WHEN IsPromoLine = 1 THEN
					( PromotionPrice )
					WHEN RequestBulkQtyUnitUniqueId IS NULL THEN
					(
						sum( Qty * ConvertFactor ) * IFNULL(( SELECT price FROM CustomerPrice WHERE CustomerPrice.CallOrderId = CustomerCallInvoice.UniqueId AND CustomerPrice.ProductUniqueId = CustomerCallInvoiceLines.ProductUniqueId LIMIT 1 ),0 ))
					ELSE ( RequestBulkQty * IFNULL(( SELECT price FROM CustomerPrice WHERE CustomerPrice.CallOrderId = CustomerCallInvoice.UniqueId AND CustomerPrice.ProductUniqueId = CustomerCallInvoiceLines.ProductUniqueId LIMIT 1 ),0 ))
				END AS TotalPrice,
				IFNULL( CustomerCallInvoiceLines.RequestDis1Amount, 0 ) AS InvoiceDis1Amount,
				IFNULL( CustomerCallInvoiceLines.RequestDis2Amount, 0 ) AS InvoiceDis2Amount,
				IFNULL( CustomerCallInvoiceLines.RequestDis3Amount, 0 ) AS InvoiceDis3Amount,
				IFNULL( CustomerCallInvoiceLines.RequestAdd1Amount, 0 ) AS InvoiceAdd1Amount,
				IFNULL( CustomerCallInvoiceLines.RequestAdd2Amount, 0 ) AS InvoiceAdd2Amount,
				IFNULL( CustomerCallInvoiceLines.RequestTaxAmount, 0 ) AS InvoiceTaxAmount,
				IFNULL( CustomerCallInvoiceLines.RequestChargeAmount, 0 ) AS InvoiceChargeAmount
			FROM
				CustomerCallInvoiceLines,
				ProductUnit ON CustomerCallOrderLinesInvoiceQtyDetail.ProductUnitId = ProductUnit.UniqueId,
				CustomerCallOrderLinesInvoiceQtyDetail ON CustomerCallOrderLinesInvoiceQtyDetail.OrderLineUniqueId = CustomerCallInvoiceLines.UniqueId,
				Product ON Product.UniqueId = CustomerCallInvoiceLines.ProductUniqueId,
				CustomerCallInvoice ON CustomerCallInvoice.UniqueId = CustomerCallInvoiceLines.OrderUniqueId
			GROUP BY
				OrderLineUniqueId
				) AS i ON customerCallOrderOrderView.UniqueId = i.LineUniqueId
					GROUP BY
						customerCallOrderOrderView.OrderUniqueId
		) AS a
		LEFT JOIN CustomerCallInvoice ON CustomerCallInvoice.UniqueId = a.OrderUniqueId
		LEFT JOIN (
		SELECT
			PaymentReportView.CustomerId,
			PaymentReportView.InvoiceId,
			sum( IFNULL( PaymentReportView.CashAmount, 0 ) ) AS CashAmount,
			sum( IFNULL( PaymentReportView.ChequeAmount, 0 ) ) AS ChequeAmount,
			sum( IFNULL( PaymentReportView.CardAmount, 0 ) ) AS CardAmount,
			sum( IFNULL( PaymentReportView.SettlementDiscountAmount, 0 ) ) AS SettlementDiscountAmount,
			sum( IFNULL( PaymentReportView.CreditAmount, 0 ) ) AS CreditAmount
		FROM
			PaymentReportView
		GROUP BY
			PaymentReportView.CustomerId,
			PaymentReportView.InvoiceId
		) AS b ON b.CustomerId = a.CustomerId
		AND b.InvoiceId = a.OrderUniqueId
	GROUP BY
		a.OrderUniqueId UNION ALL
	SELECT
		PaymentReportView.CustomerId,
		PaymentReportView.InvoiceId,
		0 AS OrderNetAmount,
		0 AS InvoiceNetAmount,
		sum( IFNULL( PaymentReportView.CashAmount, 0 ) ) AS CashAmount,
		sum( IFNULL( PaymentReportView.ChequeAmount, 0 ) ) AS ChequeAmount,
		sum( IFNULL( PaymentReportView.CardAmount, 0 ) ) AS CardAmount,
		sum( IFNULL( PaymentReportView.SettlementDiscountAmount, 0 ) ) AS SettlementDiscountAmount,
		sum( IFNULL( PaymentReportView.CreditAmount, 0 ) ) AS CreditAmount,
		NULL AS DealerName
	FROM
		PaymentReportView
	WHERE
		(PaymentReportView.InvoiceId IS NULL OR PaymentReportView.InvoiceId NOT IN (SELECT DISTINCT customerCallOrderOrderView.OrderUniqueId FROM customerCallOrderOrderView))
	GROUP BY
		PaymentReportView.CustomerId
	) AS c
	LEFT JOIN (
	SELECT
		Customer.UniqueId AS CustomerId,
		Customer.CustomerCode AS CustomerCode,
		Customer.CustomerName AS CustomerName,
		IFNULL( d.TotalReturnNetAmount, 0 ) AS TotalReturnNetAmount,
		IFNULL( e.selectedOldInvoiceAmount, 0 ) AS TotalOldInvoiceAmount,
		IFNULL( g.TotalOrderNetAmount, 0 ) + IFNULL( e.selectedOldInvoiceAmount, 0 ) - IFNULL( d.TotalReturnNetAmount, 0 ) AS TotalPayAbleAmount,
		IFNULL( g.TotalOrderNetAmount, 0 ) + IFNULL( e.selectedOldInvoiceAmount, 0 ) - IFNULL( d.TotalReturnNetAmount, 0 ) - IFNULL( f.TotalPaidAmount, 0 ) AS ReceiptAmount
	FROM
		Customer
		LEFT JOIN ( SELECT CustomerUniqueId, sum( IFNULL( TotalRequestNetAmount, 0 ) ) AS TotalReturnNetAmount FROM CustomerCallReturnView GROUP BY CustomerUniqueId ) AS d ON d.CustomerUniqueId = Customer.UniqueId
		LEFT JOIN ( SELECT CustomerUniqueId, sum( IFNULL( RemAmount, 0 ) ) AS selectedOldInvoiceAmount FROM OldInvoiceHeaderView WHERE RemAmount > 0 AND HasPayment GROUP BY CustomerUniqueId ) AS e ON e.CustomerUniqueId = Customer.UniqueId
		LEFT JOIN ( SELECT CustomerId, sum( IFNULL( Amount, 0 ) ) AS TotalPaidAmount FROM Payment GROUP BY CustomerId ) AS f ON f.CustomerId = Customer.UniqueId
		LEFT JOIN (
		SELECT
			CustomerUniqueId,
			sum(CASE
			WHEN EXISTS ( SELECT 1 FROM CustomerCall WHERE customerCallOrderOrderView.OrderUniqueId = CustomerCall.ExtraField1 AND ( CustomerCall.CallType = 15 OR CustomerCall.CallType = 16 ) )
			THEN
				CASE
				WHEN IsRequestFreeItem = 1 THEN (0)
				WHEN IsPromoLine = 1
				THEN IFNULL( PromotionPrice, 0 ) - IFNULL( RequestDis1Amount, 0 ) - IFNULL( RequestDis2Amount, 0 ) - IFNULL( RequestDis3Amount, 0 ) + IFNULL( RequestAdd1Amount, 0 ) + IFNULL( RequestAdd2Amount, 0 ) + IFNULL( RequestChargeAmount, 0 ) + IFNULL( RequestTaxAmount, 0 )
				ELSE IFNULL( RequestAmount, 0 ) - IFNULL( RequestDis1Amount, 0 ) - IFNULL( RequestDis2Amount, 0 ) - IFNULL( RequestDis3Amount, 0 ) + IFNULL( RequestAdd1Amount, 0 ) + IFNULL( RequestAdd2Amount, 0 ) + IFNULL( RequestChargeAmount, 0 ) + IFNULL( RequestTaxAmount, 0 )
				END
			ELSE 0
			END) AS TotalOrderNetAmount
		FROM
			customerCallOrderOrderView
		GROUP BY
			CustomerUniqueId
		) AS g ON g.CustomerUniqueId = Customer.UniqueId
	) AS h ON c.CustomerId = h.CustomerId;

-- ----------------------------
--  Alter view RequestReportView related to issue: NGT-2764 and DMC-53808
-- ----------------------------
DROP VIEW IF EXISTS "main"."RequestReportView";
CREATE VIEW "RequestReportView" AS
SELECT
	Customer.UniqueId,
	Customer.CustomerName,
	Customer.CustomerCode,
	Customer.StoreName,
	CustomerCallOrder.UniqueId AS OrderUniqueId,
	PaymentTypeOrder.PaymentTypeOrderName AS PaymentTypeBaseName,
	SUM(
	CASE
			WHEN CustomerCall.ExtraField1 = CustomerCallOrder.UniqueId THEN
			(IFNULL( CustomerCallOrderOrderView.RequestAmount, 0 ) - IFNULL( CustomerCallOrderOrderView.RequestDis1Amount, 0 ) - IFNULL( CustomerCallOrderOrderView.RequestDis2Amount, 0 ) - IFNULL( CustomerCallOrderOrderView.RequestDis3Amount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestAdd1Amount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestAdd2Amount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestChargeAmount, 0 ) + IFNULL( CustomerCallOrderOrderView.RequestTaxAmount, 0 )
			) ELSE 0
		END
		) AS TotalOrderNetAmount,
    CustomerCallOrder.LocalPaperNo AS LocalPaperNo,
	group_concat(CustomerCall.CallType , ':') as CallType,
	group_concat(CustomerCall.ConfirmStatus , ':') as ConfirmStatus,
	(
 EXISTS (select 1 from CustomerCallReturn WHERE CustomerCallReturn.CustomerUniqueId = Customer.UniqueId)
	) AS HasReturn,
ifnull(CustomerCallOrderOrderView.RequestDis1Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis2Amount,0) + ifnull(CustomerCallOrderOrderView.RequestDis3Amount,0) as Discount
FROM
	Customer
JOIN CustomerCallOrder ON CustomerCallOrder.CustomerUniqueId = Customer.UniqueId
JOIN CustomerCallOrderOrderView ON CustomerCallOrderOrderView.OrderUniqueId = CustomerCallOrder.UniqueId
JOIN PaymentTypeOrder ON CustomerCallOrder.OrderPaymentTypeUniqueId = PaymentTypeOrder.UniqueId
LEFT JOIN CustomerCall ON CustomerCall.CustomerId = Customer.UniqueId
GROUP BY
	Customer.UniqueId, CustomerCallOrder.UniqueId;

-- ----------------------------
--  Create view WellReturnQtyView related to issue: NGT-1296
-- ----------------------------
DROP VIEW IF EXISTS "main"."WellReturnQtyView";
CREATE VIEW "WellReturnQtyView" AS
SELECT
    CustomerCallReturnLines.ProductUniqueId AS ProductUniqueId,
    sum(Qty * ConvertFactor) AS WellReturnQty
FROM
    CustomerCallReturnLines
JOIN CustomerCallReturnLinesQtyDetail ON CustomerCallReturnLines.UniqueId = CustomerCallReturnLinesQtyDetail.ReturnLineUniqueId
INNER JOIN ProductUnit ON ProductUnit.UniqueId = CustomerCallReturnLinesQtyDetail.ProductUnitId
WHERE CustomerCallReturnLines.ReturnProductTypeId = 'cfd30f1c-69b6-47c3-8e01-8ab5768b6907'
GROUP BY ProductUniqueId;

-- ----------------------------
--  Create view WasteReturnQtyView related to issue: NGT-1296
-- ----------------------------
DROP VIEW IF EXISTS "main"."WasteReturnQtyView";
CREATE VIEW "WasteReturnQtyView" AS
SELECT
    CustomerCallReturnLines.ProductUniqueId AS ProductUniqueId,
    sum(Qty * ConvertFactor) AS WasteReturnQty
FROM
    CustomerCallReturnLines
JOIN CustomerCallReturnLinesQtyDetail ON CustomerCallReturnLines.UniqueId = CustomerCallReturnLinesQtyDetail.ReturnLineUniqueId
INNER JOIN ProductUnit ON ProductUnit.UniqueId = CustomerCallReturnLinesQtyDetail.ProductUnitId
WHERE CustomerCallReturnLines.ReturnProductTypeId = '73b8ae91-ea32-407e-b7c6-57ac185a3b8b'
GROUP BY ProductUniqueId;

-- ----------------------------
--  Create view ReturnFromDistView related to issue: NGT-1296
-- ----------------------------
DROP VIEW IF EXISTS "main"."ReturnFromDistView";
CREATE VIEW "ReturnFromDistView" AS
SELECT
    ProductUnit.ProductId,
    sum(((ifnull(CustomerCallOrderLinesInvoiceQtyDetail.Qty,0) * ConvertFactor) - (ifnull(CustomerCallOrderLinesOrderQtyDetail.Qty,0)) * ConvertFactor)) as TotalQty
FROM
    CustomerCallOrderLinesOrderQtyDetail
LEFT JOIN CustomerCallOrderLinesInvoiceQtyDetail ON CustomerCallOrderLinesOrderQtyDetail.OrderLineUniqueId = CustomerCallOrderLinesInvoiceQtyDetail.OrderLineUniqueId
AND CustomerCallOrderLinesOrderQtyDetail.ProductUnitId = CustomerCallOrderLinesInvoiceQtyDetail.ProductUnitId
INNER JOIN ProductUnit ON ProductUnit.UniqueId = CustomerCallOrderLinesOrderQtyDetail.ProductUnitId
GROUP BY ProductUnit.ProductId;

-- ----------------------------
--  Create view DistWarehouseProductQtyView related to issue: NGT-1296
-- ----------------------------
DROP VIEW IF EXISTS "main"."DistWarehouseProductQtyView";
CREATE VIEW "DistWarehouseProductQtyView" AS
SELECT
Product.UniqueId AS UniqueId,
Product.ProductCode,
Product.ProductName,
Product.ProductTypeId,
ProductUnitsView.UnitName AS UnitName,
ProductUnitsView.ConvertFactor as ConvertFactor,
ProductUnitsView.ProductUnitId as ProductUnitId,
CAST(ifnull(OnHandQty.OnHandQty,0) as REAL) as OnHandQty,
CAST(IFNULL(ReturnFromDistView.TotalQty,0) as REAL) as TotalReturnedQty,
CAST(ifnull(WellReturnQtyView.WellReturnQty,0)as REAL) AS WellReturnQty,
CAST(ifnull(WasteReturnQtyView.WasteReturnQty,0) as REAL) AS WasteReturnQty
FROM
Product
JOIN ProductUnitsView ON Product.UniqueId = ProductUnitsView.UniqueId
LEFT JOIN OnHandQty On Product.UniqueId = OnHandQty.ProductId
LEFT JOIN WellReturnQtyView ON WellReturnQtyView.ProductUniqueId = Product.UniqueId
LEFT JOIN WasteReturnQtyView ON WasteReturnQtyView.ProductUniqueId = Product.UniqueId
LEFT JOIN ReturnFromDistView ON ReturnFromDistView.ProductId = Product.UniqueId
GROUP BY Product.UniqueId;


-- ----------------------------
--  Recreate tables CustomerCallReturnLines and CustomerCallReturnLinesRequest ==> NGT-3573
--  Alter view
-- ----------------------------
DROP TABLE IF EXISTS CustomerCallReturnLines;
CREATE TABLE CustomerCallReturnLines (
    UniqueId               TEXT        NOT NULL
                                       COLLATE NOCASE,
    ReturnUniqueId         TEXT (2048) COLLATE NOCASE,
    ProductUniqueId        TEXT        COLLATE NOCASE,
    IsFreeItem             INTEGER,
    RequestUnitPrice       REAL,
    TotalRequestAdd1Amount REAL,
    TotalRequestAdd2Amount REAL,
    TotalRequestAddOtherAmount REAL,
    TotalRequestTax        REAL,
    TotalRequestCharge     REAL,
    TotalRequestNetAmount  REAL,
    TotalRequestDis1Amount REAL,
    TotalRequestDis2Amount REAL,
    TotalRequestDis3Amount REAL,
    TotalRequestDisOtherAmount REAL,
    SortId                 INTEGER,
    IndexInfo              INTEGER,
    Weight                 REAL,
    ReturnProductTypeId    TEXT,
    ReferenceId            TEXT,
    ReferenceNo            INTEGER,
    ReturnReasonId         TEXT,
    RequestBulkQty         REAL,
    RequestBulkUnitId      TEXT,
    IsPromoLine            INTEGER,
    StockId                TEXT,
    ReferenceDate          INTEGER,
    PRIMARY KEY (
        UniqueId ASC
    ),
    CONSTRAINT fkey0 FOREIGN KEY (
        RequestBulkUnitId
    )
    REFERENCES ProductUnit (UniqueId) ON DELETE CASCADE
                                      ON UPDATE CASCADE,

    CONSTRAINT fkey3 FOREIGN KEY (
        ProductUniqueId
    )
    REFERENCES Product (UniqueId) ON DELETE CASCADE
                                  ON UPDATE CASCADE,
    CONSTRAINT fkey4 FOREIGN KEY (
        ReturnUniqueId
    )
    REFERENCES CustomerCallReturn (UniqueId) ON DELETE CASCADE
                                             ON UPDATE CASCADE
);

DROP TABLE IF EXISTS CustomerCallReturnLinesRequest;
CREATE TABLE CustomerCallReturnLinesRequest (
    UniqueId               TEXT        NOT NULL
                                       COLLATE NOCASE,
    ReturnUniqueId         TEXT (2048) COLLATE NOCASE,
    ProductUniqueId        TEXT        COLLATE NOCASE,
    IsFreeItem             INTEGER,
    RequestUnitPrice       REAL,
    TotalRequestAdd1Amount REAL,
    TotalRequestAdd2Amount REAL,
    TotalRequestAddOtherAmount REAL,
    TotalRequestTax        REAL,
    TotalRequestCharge     REAL,
    TotalRequestNetAmount  REAL,
    TotalRequestDis1Amount REAL,
    TotalRequestDis2Amount REAL,
    TotalRequestDis3Amount REAL,
    TotalRequestDisOtherAmount REAL,
    SortId                 INTEGER,
    IndexInfo              INTEGER,
    Weight                 REAL,
    ReturnProductTypeId    TEXT,
    ReferenceId            TEXT,
    ReferenceNo            INTEGER,
    ReturnReasonId         TEXT,
    RequestBulkQty         REAL,
    RequestBulkUnitId      TEXT,
    IsPromoLine            INTEGER,
    StockId                TEXT,
    ReferenceDate          INTEGER,
    PRIMARY KEY (
        UniqueId ASC
    ),
    CONSTRAINT fkey0 FOREIGN KEY (
        RequestBulkUnitId
    )
    REFERENCES ProductUnit (UniqueId) ON DELETE CASCADE
                                      ON UPDATE CASCADE,

    CONSTRAINT fkey3 FOREIGN KEY (
        ProductUniqueId
    )
    REFERENCES Product (UniqueId) ON DELETE CASCADE
                                  ON UPDATE CASCADE,
    CONSTRAINT fkey4 FOREIGN KEY (
        ReturnUniqueId
    )
    REFERENCES CustomerCallReturnRequest (UniqueId) ON DELETE CASCADE
                                             ON UPDATE CASCADE
);

DROP VIEW IF EXISTS "main"."CustomerCallReturnView";
CREATE VIEW CustomerCallReturnView AS
    SELECT CustomerCallReturnLinesView.UniqueId,
           CustomerCallReturnLinesView.IsPromoLine,
           CustomerCallReturnLinesView.StockId,
           CustomerCallReturnLinesView.CustomerUniqueId AS CustomerUniqueId,
           CustomerCallReturnLinesView.ReturnUniqueId AS ReturnUniqueId,
           CustomerCallReturnLinesView.InvoiceId AS InvoiceId,
           sum(CustomerCallReturnLinesView.TotalRequestAmount) AS TotalRequestAmount,
           CustomerCallReturnLinesView.IsFromRequest AS IsFromRequest,
           CustomerCallReturnLinesView.OriginalTotalReturnQty AS OriginalTotalReturnQty,
           CustomerCallReturnLinesView.Comment AS Comment,
           CustomerCallReturnLinesView.DealerUniqueId AS DealerUniqueId,
           group_concat(ReturnProductTypeId, ':') AS ReturnProductTypeId,
           group_concat(ReturnReasonId, ':') AS ReturnReasonId,
           CustomerCallReturnLinesView.ProductName AS ProductName,
           CustomerCallReturnLinesView.ProductCode AS ProductCode,
           CustomerCallReturnLinesView.ProductId AS ProductId,
           group_concat(CustomerCallReturnLinesView.ConvertFactor, '|') AS ConvertFactor,
           group_concat(CustomerCallReturnLinesView.ProductUnitId, '|') AS ProductUnitId,
           group_concat(CustomerCallReturnLinesView.Qty, '|') AS Qty,
           group_concat(CustomerCallReturnLinesView.UnitName, '|') AS UnitName,
           sum(CustomerCallReturnLinesView.TotalReturnQty) AS TotalReturnQty,
           CustomerOldInvoiceDetail.TotalQty AS InvoiceQty,
           CustomerOldInvoiceHeader.SaleNo AS SaleNo,
           CustomerCallReturnLinesView.RequestUnitPrice,
           sum(CASE WHEN CustomerCallReturnLinesView.IsPromoLine THEN ifnull(CustomerCallReturnLinesView.TotalRequestNetAmount, 0) ELSE ifnull(CustomerCallReturnLinesView.TotalRequestAmount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis1Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis2Amount, 0) - ifnull(CustomerCallReturnLinesView.TotalRequestDis3Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestDisOtherAmount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd1Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestAdd2Amount, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestTax, 0) + ifnull(CustomerCallReturnLinesView.TotalRequestCharge, 0) END) AS TotalRequestNetAmount
      FROM CustomerCallReturnLinesView
           JOIN
           ReturnReason ON ReturnReason.UniqueId = CustomerCallReturnLinesView.ReturnReasonId
           LEFT JOIN
           CustomerOldInvoiceDetail ON CustomerOldInvoiceDetail.ProductId = CustomerCallReturnLinesView.ProductId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
           LEFT JOIN
           CustomerOldInvoiceHeader ON CustomerOldInvoiceHeader.UniqueId = CustomerOldInvoiceDetail.SaleId AND
                                       CustomerOldInvoiceDetail.SaleId = CustomerCallReturnLinesView.InvoiceId
     GROUP BY CustomerCallReturnLinesView.ProductId,
              CustomerCallReturnLinesView.IsPromoLine,
              ReturnUniqueId;

-- ----------------------------
--  Create table CustomerBarcode related to issue: NGT-3445
-- ----------------------------
DROP TABLE IF EXISTS "main"."CustomerBarcode";
CREATE TABLE "CustomerBarcode" (
"uniqueId"  TEXT NOT NULL,
"customerUniqueId"  TEXT,
"barcode"  TEXT,
PRIMARY KEY ("UniqueId"),
CONSTRAINT "fkey1" FOREIGN KEY ("CustomerUniqueId") REFERENCES "Customer" ("UniqueId") ON DELETE CASCADE ON UPDATE CASCADE
);