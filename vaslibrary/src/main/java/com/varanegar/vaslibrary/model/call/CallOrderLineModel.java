package com.varanegar.vaslibrary.model.call;

import androidx.annotation.Nullable;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table(name = "CustomerCallOrderLines")
public class CallOrderLineModel extends OrderLineBaseModel {
    @Column
    public Currency PromotionPrice;
    @Column
    public Currency PresalesAdd1Amount = Currency.ZERO;
    @Column
    public Currency PresalesAdd2Amount = Currency.ZERO;
    @Column
    public Currency PresalesTaxAmount = Currency.ZERO;
    @Column
    public Currency PresalesChargeAmount = Currency.ZERO;
    @Column
    public Currency PresalesDis1Amount = Currency.ZERO;
    @Column
    public Currency PresalesDis2Amount = Currency.ZERO;
    @Column
    public Currency PresalesDis3Amount = Currency.ZERO;
    @Column
    public Currency PresalesOtherDiscountAmount = Currency.ZERO;
    @Column
    public Currency PresalesOtherAddAmount = Currency.ZERO;
    @Nullable
    @Column
    public UUID EditReasonId;
}
