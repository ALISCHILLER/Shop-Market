package varanegar.com.discountcalculatorlib.handler.sds.v4_16;


import java.io.Console;

import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemStatutesSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempAcceptedDiscountAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempGoodsPackageItemSDSDBAdapter;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

public class PromotionApplyStatuteOnEVCItemPreviewSDSV3 {

	/*usp_ApplyStatuteOnEVCItem_Preview*/
	public static void applyStatuteOnEVCItemPreview(String evcId, String callId, int discountId, int orderId, int prizeType, int evcType, int saleId, int dcRef)
	{
        if(ApplyStatuteOnEVCItemCheckStatus(evcId) != 0) return;

		if(fillEVCAcceptedDiscountTemp(evcId, discountId) == 0) return;

		int prizePackageRef = EVCTempGoodsPackageItemSDSDBAdapter.getInstance().getPrizePackageRef(discountId);

		EVCTempGoodsPackageItemSDSDBAdapter.getInstance().fillEVCGoodPackageTemp(evcId, discountId, prizePackageRef, saleId, prizeType, evcType, orderId);
		int calcprizeType = 0;
		if (evcType == 2 || evcType == 10 ||evcType == 12 )
			calcprizeType = DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getPrizeCalcType(saleId);
		else
			calcprizeType = (GlobalVariables.getPrizeCalcType() ? 1 : 0);

		if(fillPrizeItemsInEVCItem(evcId, callId, discountId, calcprizeType, evcType, saleId, dcRef) == 0) return;

		if(ApplyStatuteOnEVCItem_CheckGoodsNoSale(evcId) != 0) return;

		deleteInvalidPrize(evcId);
		//updatePrizeIncludedItems(evcId);
		mergePrize(evcId, evcType);
		//updatePrizeMinUnit(evcId);
	}

	private static int fillEVCAcceptedDiscountTemp(String evcId, int discountId)
	{
        return EVCTempAcceptedDiscountAdapter.getInstance().fillEVCAcceptedDiscountTemp(evcId, discountId);
	}

	private static int ApplyStatuteOnEVCItemCheckStatus(String evcId)
	{
		int result = 0;

		result = checkCPrice(evcId);
		if(result != 0) return result;

		result = ApplyStatuteOnEVCItem_CheckGoodsNoSale(evcId);
		if(result != 0) return result;

		return checkPrice(evcId);
	}

	private static int fillPrizeItemsInEVCItem(String evcId, String callId, int discountId, int calcPrizeType,int evcType, int saleId, int dcRef)
	{
		return  EVCItemSDSDBAdapter.getInstance().fillByEVCItemPreview(evcId, callId, discountId , calcPrizeType, evcType, saleId, dcRef);
	}

	private static void deleteInvalidPrize(String evcId)
	{
        deleteInvalidStatute(evcId);

		EVCItemStatutesSDSDBAdapter.getInstance().deleteInvalidItemSatatutes(evcId);

		EVCItemSDSDBAdapter.getInstance().deleteInvalidPrizeItems(evcId);
	}

    private static void deleteInvalidStatute(String evcId)
    {
        //Invalid in tablet
    }

	private static void updatePrizeIncludedItems(String evcId)
	{
		EVCItemSDSDBAdapter.getInstance().updatePrizeIncludedItems(evcId);
	}
	
	private static void mergePrize(String evcId, int evcType)
	{
		EVCItemSDSDBAdapter.getInstance().mergePrize1(evcId);

		EVCItemSDSDBAdapter.getInstance().mergePrize2(evcId, evcType);
	}
	
	private static void updatePrizeMinUnit(String evcId)
	{
		EVCItemSDSDBAdapter.getInstance().updatePrizeMinUnit(evcId);
	}

    private static int checkCPrice(String evcId)
    {
        return 0;

//        if exists (select * from (
//                    SELECT distinct g.GoodsCode + ' - ' + g.GoodsName as good FROM(
//                            SELECT  D.PrizeRef AS GoodsRef
//                            FROM SLE.tblEVCItem EI
//                            INNER JOIN SLE.tblEVC E ON E.ID= EI.EVCRef
//                            INNER JOIN SLE.tblEVCItemStatutes ES ON EI.ID= ES.EVCItemRef
//                            INNER JOIN SLE.tblDiscount D ON D.ID= ES.DisRef
//                            LEFT JOIN SLE.tblPrice Pr ON Pr.GoodsRef= D.PrizeRef
//                            LEFT OUTER JOIN GNR.tblPackage P ON P.GoodsRef= D.PrizeRef AND P.UnitRef= D.PrizeUnit
//                            LEFT OUTER JOIN GNR.tblPackage P3 ON P3.GoodsRef= D.GoodsRef AND P3.UnitRef= D.QtyUnit
//
//                            INNER JOIN GNR.tblCust C ON C.ID= E.CustRef
//                            LEFT OUTER JOIN GNR.vwArea A ON A.ID= C.AreaRef
//                            INNER JOIN GNR.tblGoods G ON G.ID=EI.GoodsRef
//                            WHERE EI.EVCRef=@ID
//                                    AND EI.PrizeType = 1
//                            AND NOT EXISTS(SELECT Top 1 CP2.ID
//                                    FROM SLE.tblCPrice CP2
//                                    INNER JOIN GNR.tblPackage P2
//                                    ON P2.UnitRef = CP2.UnitRef
//                                    AND P2.GoodsRef = D.PrizeRef--EI.GoodsRef --=CP2.GoodsRef
//                                    INNER JOIN GNR.tblCust C ON C.ID= E.CustRef
//                                    WHERE 1 = 1 --CP2.GoodsRef = D.PrizeRef
//                                    AND (
//                                            ( CP2.GoodsRef Is Not Null
//                                            And CP2.GoodsRef = D.PrizeRef
//                                    )
//                                    Or
//                                            (  CP2.GoodsRef Is Null
//                                                    And
//                                                            (
//                                                                    CP2.GoodsGroupRef Is Null
//                                                                    Or
//                                                                    CP2.GoodsGroupRef IN (
//                                                                            Select GGP.ID
//                                                                            from
//                                                                            gnr.tblGoodsGroup GGP
//                                                                            Inner Join gnr.tblGoodsGroup GGC
//                                                                            On GGC.NLeft Between GGP.NLeft And GGP.NRight
//                                                                            Inner Join gnr.tblGoods G
//                                                                            On GGC.ID = G.GoodsGroupRef And D.PrizeRef = G.ID
//                                                                    )
//                                                            )
//
//                                                    And
//                                                            (
//                                                                    CP2.MainTypeRef Is Null
//                                                                    Or
//                                                                    CP2.MainTypeRef IN (
//                                                                            Select MainTypeRef
//                                                                            from
//                                                                            gnr.tblGoodsMainSubType GMST
//                                                                            Where GMST.GoodsRef = D.PrizeRef
//                                                                    )
//                                                            )
//
//                                                    And
//                                                            (
//                                                                    CP2.SubTypeRef Is Null
//                                                                    Or
//                                                                    CP2.SubTypeRef IN (
//                                                                            Select SubTypeRef
//                                                                            from
//                                                                            gnr.tblGoodsMainSubType GMST
//                                                                            Where GMST.GoodsRef = D.PrizeRef
//                                                                    )
//                                                            )
//
//                                            )
//                            )
//                            AND (E.DateOf BETWEEN CP2.StartDate AND ISNULL(CP2.EndDate,E.DateOf))
//                            AND (EI.TotalQty BETWEEN CP2.MinQty*P2.Qty AND ISNULL(CP2.MaxQty*P2.Qty,EI.TotalQty))
//                            AND ISNULL(CP2.CustRef, E.CustRef) = E.CustRef
//                            AND ISNULL(CP2.DCRef, E.DCRef) = E.DCRef
//                            AND ISNULL(CP2.CustCtgrRef, ISNULL(C.CustCtgrRef,0)) = ISNULL(C.CustCtgrRef,0)
//                            AND ISNULL(CP2.CustActRef, ISNULL(C.CustActRef,0)) = ISNULL(C.CustActRef,0)
//                            ORDER BY CP2.GoodsRef Desc , CP2.Priority --CP2.CustActRef DESC, CP2.CustCtgrRef DESC
//                    )
//
//                    GROUP BY ES.DisRef, D.PrizeRef, P.Qty, D.PrizeQty, D.MinQty, D.PrizeStep, E.AccYear, E.ID, PrizeUnit, Pr.ID
//            ) X
//            inner join gnr.tblgoods g on g.id=x.GoodsRef
//    ) as d )
//
//        begin
//        SELECT distinct 3000 As ErrNumber , 'براي کالاي جايزه '+ good + ' نرخ قراردادي تعريف نشده است'  As ErrMessage
//        from (
//                SELECT g.GoodsCode + ' - ' + g.GoodsName as good FROM(
//                        SELECT  D.PrizeRef AS GoodsRef
//                        FROM SLE.tblEVCItem EI
//                        INNER JOIN SLE.tblEVC E ON E.ID= EI.EVCRef
//                        INNER JOIN SLE.tblEVCItemStatutes ES ON EI.ID= ES.EVCItemRef
//                        INNER JOIN SLE.tblDiscount D ON D.ID= ES.DisRef
//                        LEFT JOIN SLE.tblPrice Pr ON Pr.GoodsRef= D.PrizeRef
//                        LEFT OUTER JOIN GNR.tblPackage P ON P.GoodsRef= D.PrizeRef AND P.UnitRef= D.PrizeUnit
//                        LEFT OUTER JOIN GNR.tblPackage P3 ON P3.GoodsRef= D.GoodsRef AND P3.UnitRef= D.QtyUnit
//
//                        INNER JOIN GNR.tblCust C ON C.ID= E.CustRef
//                        LEFT OUTER JOIN GNR.vwArea A ON A.ID= C.AreaRef
//                        INNER JOIN GNR.tblGoods G ON G.ID=EI.GoodsRef
//                        WHERE EI.EVCRef=@ID
//                                AND EI.PrizeType = 1
//                        AND NOT EXISTS(SELECT Top 1 CP2.ID
//                                FROM SLE.tblCPrice CP2
//                                INNER JOIN GNR.tblPackage P2
//                                ON P2.UnitRef = CP2.UnitRef
//                                AND P2.GoodsRef = D.PrizeRef--EI.GoodsRef --=CP2.GoodsRef
//                                INNER JOIN GNR.tblCust C ON C.ID= E.CustRef
//                                WHERE 1 = 1 --CP2.GoodsRef = D.PrizeRef
//                                AND (
//                                        ( CP2.GoodsRef Is Not Null
//                                        And CP2.GoodsRef = D.PrizeRef
//                                )
//                                Or
//                                        (  CP2.GoodsRef Is Null
//                                                And
//                                                        (
//                                                                CP2.GoodsGroupRef Is Null
//                                                                Or
//                                                                CP2.GoodsGroupRef IN (
//                                                                        Select GGP.ID
//                                                                        from
//                                                                        gnr.tblGoodsGroup GGP
//                                                                        Inner Join gnr.tblGoodsGroup GGC
//                                                                        On GGC.NLeft Between GGP.NLeft And GGP.NRight
//                                                                        Inner Join gnr.tblGoods G
//                                                                        On GGC.ID = G.GoodsGroupRef And D.PrizeRef = G.ID
//                                                                )
//                                                        )
//
//                                                And
//                                                        (
//                                                                CP2.MainTypeRef Is Null
//                                                                Or
//                                                                CP2.MainTypeRef IN (
//                                                                        Select MainTypeRef
//                                                                        from
//                                                                        gnr.tblGoodsMainSubType GMST
//                                                                        Where GMST.GoodsRef = D.PrizeRef
//                                                                )
//                                                        )
//
//                                                And
//                                                        (
//                                                                CP2.SubTypeRef Is Null
//                                                                Or
//                                                                CP2.SubTypeRef IN (
//                                                                        Select SubTypeRef
//                                                                        from
//                                                                        gnr.tblGoodsMainSubType GMST
//                                                                        Where GMST.GoodsRef = D.PrizeRef
//                                                                )
//                                                        )
//
//                                        )
//                        )
//                        AND (E.DateOf BETWEEN CP2.StartDate AND ISNULL(CP2.EndDate,E.DateOf))
//                        AND (EI.TotalQty BETWEEN CP2.MinQty*P2.Qty AND ISNULL(CP2.MaxQty*P2.Qty,EI.TotalQty))
//                        AND ISNULL(CP2.CustRef, E.CustRef) = E.CustRef
//                        AND ISNULL(CP2.DCRef, E.DCRef) = E.DCRef
//                        AND ISNULL(CP2.CustCtgrRef, ISNULL(C.CustCtgrRef,0)) = ISNULL(C.CustCtgrRef,0)
//                        AND ISNULL(CP2.CustActRef, ISNULL(C.CustActRef,0)) = ISNULL(C.CustActRef,0)
//                        ORDER BY CP2.GoodsRef Desc , CP2.Priority --CP2.CustActRef DESC, CP2.CustCtgrRef DESC
//                )
//
//                GROUP BY ES.DisRef, D.PrizeRef, P.Qty, D.PrizeQty, D.MinQty, D.PrizeStep, E.AccYear, E.ID, PrizeUnit, Pr.ID
//        ) X
//        inner join gnr.tblgoods g on g.id=x.GoodsRef
//        ) as d
//        return 3000
//        end
    }

    private static int ApplyStatuteOnEVCItem_CheckGoodsNoSale(String evcId)
    {
        return 0;

//        DECLARE @EVCType int,@CustRef INT=0,@CustCtgrRef INT=0,@CustActRef INT=0,@CustLevelRef INT=0,@AreaRef INT=0,@CountyRef INT=0,@StateRef INT=0,@DateOf varchar(8)=''
//        SELECT @EVCType=EVCType,@CustRef=CustRef,@DateOf=OprDate FROM SLE.tblEVC WHERE ID = @ID
//            SELECT @CustCtgrRef=ISNULL(CustCtgrRef,0),@CustActRef=ISNULL(CustActRef,0),@CustLevelRef=ISNULL(CustLevelRef,0),@AreaRef=ISNULL(AreaRef,0) FROM Gnr.tblCust WHERE id=@CustRef
//            SELECT @CountyRef=ISNULL(CountyRef,0),@StateRef=ISNULL(StateRef,0) FROM gnr.vwArea Where Id=@AreaRef
// /*-------------------------------------------------*/
//            CREATE TABLE #Tmp(Good nvarchar(2000))
//
//        Declare @GroupCPrice BIT=0,@SQL varchar(max)=''
//        IF EXISTS(SELECT * FROM SLE.tblCPrice WHERE GoodsRef is null AND @DateOf BETWEEN StartDate AND ISNULL(EndDate,@DateOf))
//        SET @GroupCPrice=1
//
//        SET @SQL='
//        INSERT INTO #Tmp(Good)
//        SELECT distinct g.GoodsCode + '' - '' + g.GoodsName as good FROM(
//            SELECT  D.PrizeRef AS GoodsRef
//            FROM SLE.tblEVC E
//            INNER JOIN SLE.tblEVCItem EI ON E.ID= EI.EVCRef AND EI.EVCRef='+Cast(@ID as varchar)+'
//            INNER JOIN SLE.tblEVCItemStatutes ES ON EI.ID= ES.EVCItemRef
//            INNER JOIN SLE.tblDiscount D ON D.ID= ES.DisRef
//            INNER JOIN SLE.tblCPrice CP ON
//            CP.ID= (SELECT Top 1 CP2.ID
//            FROM SLE.tblCPrice CP2
//            INNER JOIN GNR.tblPackage P2
//            ON P2.UnitRef = CP2.UnitRef
//            AND P2.GoodsRef = D.PrizeRef
//                    --INNER JOIN GNR.tblCust C ON C.ID= E.CustRef
//            WHERE 1 = 1
//            AND (
//                    ( CP2.GoodsRef Is Not Null
//                    And CP2.GoodsRef = D.PrizeRef
//            )
//        '
//        IF @GroupCPrice=0 SET @SQL=@SQL+')'
//        IF @GroupCPrice=1 SET @SQL=@SQL+'
//        Or
//                (  CP2.GoodsRef Is Null
//                        And
//                                (
//                                        CP2.GoodsGroupRef Is Null
//                                        Or
//                                        CP2.GoodsGroupRef IN (
//                                                Select GGP.ID
//                                                from
//                                                gnr.tblGoodsGroup GGP
//                                                Inner Join gnr.tblGoodsGroup GGC
//                                                On GGC.NLeft Between GGP.NLeft And GGP.NRight
//                                                Inner Join gnr.tblGoods G
//                                                On GGC.ID = G.GoodsGroupRef And D.PrizeRef = G.ID
//                                        )
//                                )
//
//                        And
//                                (
//                                        CP2.MainTypeRef Is Null
//                                        Or
//                                        CP2.MainTypeRef IN (
//                                                Select MainTypeRef
//                                                from
//                                                gnr.tblGoodsMainSubType GMST
//                                                Where GMST.GoodsRef = D.PrizeRef
//                                        )
//                                )
//
//                        And
//                                (
//                                        CP2.SubTypeRef Is Null
//                                        Or
//                                        CP2.SubTypeRef IN (
//                                                Select SubTypeRef
//                                                from
//                                                gnr.tblGoodsMainSubType GMST
//                                                Where GMST.GoodsRef = D.PrizeRef
//                                        )
//                                )
//
//                )
//        )'
//        SET @SQL=@SQL+'
//        AND (E.OprDate BETWEEN CP2.StartDate AND ISNULL(CP2.EndDate,E.OprDate))
//        AND (EI.TotalQty BETWEEN CP2.MinQty*P2.Qty AND ISNULL(CP2.MaxQty*P2.Qty,EI.TotalQty))
//        AND ISNULL(CP2.CustRef, E.CustRef) = E.CustRef
//        AND ISNULL(CP2.DCRef, E.DCRef) = E.DCRef
//        AND ISNULL(CP2.CustCtgrRef, '+Cast(@CustCtgrRef as varchar)+') = '+Cast(@CustCtgrRef as varchar)+'
//        AND ISNULL(CP2.CustActRef, '+Cast(@CustActRef as varchar)+') = '+CAST(@CustActRef as varchar)+'
//        ORDER BY CP2.GoodsRef Desc , CP2.Priority --CP2.CustActRef DESC, CP2.CustCtgrRef DESC
//        )
//
//
//        LEFT JOIN SLE.tblPrice Pr ON Pr.GoodsRef= D.PrizeRef
//        LEFT OUTER JOIN GNR.tblPackage P ON P.GoodsRef= D.PrizeRef AND P.UnitRef= D.PrizeUnit
//        LEFT OUTER JOIN GNR.tblPackage P3 ON P3.GoodsRef= D.GoodsRef AND P3.UnitRef= D.QtyUnit
//            --INNER JOIN GNR.tblCust C ON C.ID= E.CustRef
//            --LEFT OUTER JOIN GNR.vwArea A ON A.ID= C.AreaRef
//        WHERE EI.EVCRef='+CAST(@ID as varchar)+'
//        AND D.PrizeType = 1
//        AND ( '+CAST(@EVCType as varchar)+' <>2 and  EXISTS (SELECT Pr2.ID
//        FROM SLE.tblGoodsNoSale Pr2
//        WHERE Pr2.GoodsRef=D.PrizeRef
//        AND ISNULL(Pr2.DCRef ,E.DCRef) = E.DCRef
//        AND ISNULL(Pr2.CustCtgrRef, '+Cast(@CustCtgrRef as varchar)+')= '+Cast(@CustCtgrRef as varchar)+'
//        AND ISNULL(Pr2.CustActRef, '+Cast(@CustActRef as varchar)+')='+CAST(@CustActRef as varchar)+'
//        AND ISNULL(Pr2.CustLevelRef, '+Cast(@CustLevelRef as varchar)+')='+Cast(@CustLevelRef as varchar)+'
//        AND ISNULL(Pr2.StateRef, '+Cast(@StateRef as varchar)+')='+Cast(@StateRef as varchar)+'
//        AND ISNULL(pr2.CountyRef, '+Cast(@CountyRef as varchar)+')='+Cast(@CountyRef as varchar)+'
//        AND ISNULL(pr2.AreaRef, '+Cast(@AreaRef as varchar)+')='+Cast(@AreaRef as varchar)+'
//        AND (pr2.CustRef IS NULL OR pr2.CustRef='+Cast(@CustRef as varchar)+')
//        AND (E.OprDate BETWEEN Pr2.StartDate AND ISNULL(Pr2.EndDate,E.OprDate))
//        AND Pr2.Status & 1= 1
//        ))
//
//        GROUP BY ES.DisRef, D.PrizeRef, P.Qty, D.PrizeQty, D.MinQty, D.PrizeStep, E.AccYear, E.ID, PrizeUnit, Pr.ID,
//            CP.ID
//        ) X
//        inner join gnr.tblgoods g on g.id=x.GoodsRef'
//
//        EXEC(@SQL)
//
//
//
//        if exists (select * from #Tmp as e )
//        begin
//        select distinct 3000 As ErrNumber ,'براي کالاي جايزه '+ good + ' امکان گردش  کالا وجود ندارد' As ErrMessage from #Tmp as e
//
//        return 3000
//        end
    }

    private static int checkPrice(String evcId)
    {
        return 0;

//        SELECT DISTINCT  @ErrMessage+='براي کالاي جايزه '+ good + ' تاريخچه کالا تعريف نشده است'
//        FROM (
//                SELECT  g.GoodsCode + ' - ' + g.GoodsName as good
//                FROM(
//                        SELECT  D.PrizeRef AS GoodsRef
//                        FROM SLE.tblEVCItem EI
//                        INNER JOIN  SLE.tblEVC E ON E.ID= EI.EVCRef
//                        INNER JOIN  SLE.tblEVCItemStatutes ES ON EI.ID= ES.EVCItemRef
//                        INNER JOIN  SLE.tblDiscount D ON D.ID= ES.DisRef
//                        LEFT JOIN SLE.tblPrice Pr ON Pr.GoodsRef= D.PrizeRef
//                        LEFT OUTER JOIN GNR.tblPackage P ON P.GoodsRef= D.PrizeRef AND P.UnitRef= D.PrizeUnit
//                        LEFT OUTER JOIN GNR.tblPackage P3 ON P3.GoodsRef= D.GoodsRef AND P3.UnitRef= D.QtyUnit
//                        INNER JOIN  SLE.tblCPrice CP ON   CP.ID= (SELECT Top 1 CP2.ID
//                        FROM SLE.tblCPrice CP2
//                        INNER JOIN  GNR.tblPackage P2
//                        ON P2.UnitRef = CP2.UnitRef
//                        AND P2.GoodsRef = D.PrizeRef--EI.GoodsRef --=CP2.GoodsRef
//                        INNER JOIN  GNR.tblCust C ON C.ID= E.CustRef
//                        WHERE 1 = 1 --CP2.GoodsRef = D.PrizeRef
//                        AND (
//                                ( CP2.GoodsRef Is Not Null  AND CP2.GoodsRef = D.PrizeRef )
//                        OR
//                                (  CP2.GoodsRef Is Null  AND (CP2.GoodsGroupRef Is Null
//                                                OR  CP2.GoodsGroupRef IN ( SELECT GGP.ID
//                                                        FROM
//                                                        gnr.tblGoodsGroup GGP
//                                                        INNER JOIN  gnr.tblGoodsGroup GGC
//                                                        ON GGC.NLeft Between GGP.NLeft AND GGP.NRight
//                                                        INNER JOIN  gnr.tblGoods G
//                                                        ON GGC.ID = G.GoodsGroupRef AND D.PrizeRef = G.ID
//                                                )
//                                        )
//
//                                        AND ( CP2.MainTypeRef Is Null  OR  CP2.MainTypeRef IN ( SELECT MainTypeRef
//                                                        FROM  gnr.tblGoodsMainSubType GMST
//                                                        WHERE GMST.GoodsRef = D.PrizeRef
//                                                )
//                                        )
//
//                                        AND ( CP2.SubTypeRef Is Null  OR  CP2.SubTypeRef IN ( SELECT SubTypeRef
//                                                        FROM  gnr.tblGoodsMainSubType GMST
//                                                        Where GMST.GoodsRef = D.PrizeRef
//                                                )
//                                        )
//
//                                )
//                )
//                AND (E.DateOf BETWEEN CP2.StartDate AND ISNULL(CP2.EndDate,E.DateOf))
//                AND (EI.TotalQty BETWEEN CP2.MinQty*P2.Qty AND ISNULL(CP2.MaxQty*P2.Qty,EI.TotalQty))
//                AND ISNULL(CP2.CustRef, E.CustRef) = E.CustRef
//                AND ISNULL(CP2.DCRef, E.DCRef) = E.DCRef
//                AND ISNULL(CP2.CustCtgrRef, ISNULL(C.CustCtgrRef,0)) = ISNULL(C.CustCtgrRef,0)
//                AND ISNULL(CP2.CustActRef, ISNULL(C.CustActRef,0)) = ISNULL(C.CustActRef,0)
//                ORDER BY CP2.GoodsRef Desc , CP2.Priority --CP2.CustActRef DESC, CP2.CustCtgrRef DESC
//        )
//        INNER JOIN  GNR.tblCust C ON C.ID= E.CustRef
//        LEFT OUTER JOIN GNR.vwArea A ON A.ID= C.AreaRef
//        INNER JOIN  GNR.tblGoods G ON G.ID=EI.GoodsRef
//        WHERE EI.EVCRef=@ID
//            AND D.PrizeType = 1
//        AND NOT EXISTS (SELECT TOP 1 Pr2.ID
//            FROM SLE.tblPrice Pr2
//            WHERE Pr2.GoodsRef=D.PrizeRef
//            AND (E.DateOf BETWEEN Pr2.StartDate AND ISNULL(Pr2.EndDate,E.DateOf))
//            AND ISNULL(Pr2.DCRef ,E.DCRef) = E.DCRef
//            ORDER BY Pr2.DCRef DESC
//    )
//
//        GROUP BY ES.DisRef, D.PrizeRef, P.Qty, D.PrizeQty, D.MinQty, D.PrizeStep, E.AccYear, E.ID, PrizeUnit, Pr.ID, CP.ID
//        ) X
//        INNER JOIN  gnr.tblgoods g ON g.id=x.GoodsRef
//        ) AS f
//
//        IF @ErrMessage<>''
//        BEGIN
//        SELECT 3000 AS ErrNumber,@ErrMessage  AS ErrMessage
//        RETURN 3000
//        END
    }
}
