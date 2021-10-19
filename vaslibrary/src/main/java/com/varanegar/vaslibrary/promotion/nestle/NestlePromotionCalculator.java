package com.varanegar.vaslibrary.promotion.nestle;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/5/2018.
 */

public class NestlePromotionCalculator {
    public List<NestlePromotionModel> getPromotions(@NonNull UUID customerId, int customerRef) {
        String sql = "SELECT NestleDiscount.* , \n" +
                " Product.UniqueId as ProductUniqueId, ZPR0 - NestleDiscount.DisZ011 as Dis1\n" +
                "                FROM\n" +
                "                (\n" +
                "                SELECT\n" +
                "                CP.UniqueId AS UniqueId,\n" +
                "                CP.MaterialNumber,\n" +
                "                CP.ConditionAmount AS ZPR0,\n" +
                "                IFNULL(Z001.ConditionAmount,0) AS Z001,\n" +
                "                IFNULL(X007.ConditionAmount,0) AS X007,\n" +
                "                IFNULL(X359.ConditionAmount,0) AS X359,\n" +
                "                IFNULL(Z011.ConditionAmount,0) AS Z011,\n" +
                "                CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100) as DisZ001,\n" +
                "                \n" +
                "                (CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) + ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) * IFNULL(X007.ConditionAmount,0)*-1 / 100) as DisX007,\n" +
                "                \n" +
                "                ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) + ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) * IFNULL(X007.ConditionAmount,0)*-1 / 100))\n" +
                "                  +(((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) + ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) * IFNULL(X007.ConditionAmount,0)*-1 / 100)) * IFNULL(X359.ConditionAmount,0)*-1 / 100) as DisX359,\n" +
                "                \n" +
                "                (((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) + ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) * IFNULL(X007.ConditionAmount,0)*-1 / 100))\n" +
                "                  +(((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) + ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) * IFNULL(X007.ConditionAmount,0)*-1 / 100)) * IFNULL(X359.ConditionAmount,0)*-1 / 100))\n" +
                "                +((((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) + ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) * IFNULL(X007.ConditionAmount,0)*-1 / 100))\n" +
                "                  +(((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) + ((CP.ConditionAmount + (CP.ConditionAmount * IFNULL(Z001.ConditionAmount,0)*-1 / 100)) * IFNULL(X007.ConditionAmount,0)*-1 / 100)) * IFNULL(X359.ConditionAmount,0)*-1 / 100)) * IFNULL(Z011.ConditionAmount,0)*-1/ 100) as DisZ011\n" +
                "                \n" +
                "                FROM\n" +
                "                ContractPriceNestle CP \n" +
                "                LEFT JOIN (SELECT * FROM ContractPriceNestle WHERE ConditionType = 'Z001') Z001 \n" +
                "                ON CP.MaterialNumber = Z001.MaterialNumber and Z001.CustomerHierarchyNumber = '" + customerRef + "'\n" +
                "                LEFT JOIN (SELECT * FROM ContractPriceNestle WHERE ConditionType = 'X007') X007 \n" +
                "                ON CP.MaterialNumber = X007.MaterialNumber and X007.CustomerHierarchyNumber = '" + customerRef + "'\n" +
                "                LEFT JOIN (SELECT * FROM ContractPriceNestle WHERE ConditionType = 'X359') X359 \n" +
                "                ON CP.MaterialNumber = X359.MaterialNumber and X359.CustomerHierarchyNumber = '" + customerRef + "'\n" +
                "                LEFT JOIN (SELECT * FROM ContractPriceNestle WHERE ConditionType = 'Z011') Z011 \n" +
                "                ON CP.MaterialNumber = Z011.MaterialNumber and Z011.CustomerHierarchyNumber = '" + customerRef + "'\n" +
                "                WHERE\n" +
                "                CP.ConditionType = 'ZPR0'\n" +
                "                GROUP BY CP.MaterialNumber\n" +
                "                ) NestleDiscount\n" +
                "JOIN Product ON NestleDiscount.MaterialNumber = Product.BackOfficeId\n" +
                "JOIN CustomerCallOrderLines Lin ON Product.UniqueId = Lin.ProductUniqueId\n" +
                "JOIN CustomerCallOrder ON Lin.OrderUniqueId =  CustomerCallOrder.UniqueId AND CustomerCallOrder.CustomerUniqueId = '" + customerId.toString() + "'\n";
        List<NestlePromotionModel> items = new NestlePromotionModelRepository().getItems(sql, null);
        return items;
    }
}
