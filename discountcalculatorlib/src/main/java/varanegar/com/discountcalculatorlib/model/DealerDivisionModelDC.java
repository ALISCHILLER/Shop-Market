package varanegar.com.discountcalculatorlib.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Create by Mehrdad Latifi on 9/21/2022
 */

public class DealerDivisionModelDC{

    public UUID DivisionCenterKey;

    public String DivisionBackOfficeCode;

    public String DivisionSalesOrg;

    public String DivisionDisChanel;

    public String DivisionCode;


    public DealerDivisionModelDC() {
    }


    public DealerDivisionModelDC(UUID divisionCenterKey, String divisionBackOfficeCode, String divisionSalesOrg, String divisionDisChanel, String divisionCode) {
        DivisionCenterKey = divisionCenterKey;
        DivisionBackOfficeCode = divisionBackOfficeCode;
        DivisionSalesOrg = divisionSalesOrg;
        DivisionDisChanel = divisionDisChanel;
        DivisionCode = divisionCode;
    }
}
