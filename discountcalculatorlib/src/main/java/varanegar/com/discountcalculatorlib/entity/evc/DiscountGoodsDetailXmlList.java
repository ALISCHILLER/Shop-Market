package varanegar.com.discountcalculatorlib.entity.evc;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by m.aghajani on 3/27/2016.
 */
@Root(name = "GS")
public class DiscountGoodsDetailXmlList {

    @ElementList(entry="G", inline=true)
    public List<DiscountGoodsDetailXml> recordList;
}
