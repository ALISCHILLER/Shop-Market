package varanegar.com.discountcalculatorlib.entity.evc;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by m.aghajani on 3/27/2016.
 */
@Root(name = "G")
public class DiscountGoodsGroupDetailXml {

    @Attribute(name = "I")
    public int Id;

    @Attribute(name = "P")
    public int Parent;

    @Attribute(name = "L")
    public long NLeft;

    @Attribute(name = "R")
    public long NRight;

    public DiscountGoodsGroupDetailXml()
    {}
}
