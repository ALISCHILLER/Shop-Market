package varanegar.com.discountcalculatorlib.entity.evc;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by m.aghajani on 3/27/2016.
 */
@Root(name = "MS")
public class DiscountGoodsMainSubTypeDetailXml {

    @Attribute(name = "I")
    public int Id;

    @Attribute(name = "G")
    public int GoodsRef;

    @Attribute(name = "M")
    public int MainGroupRef;

    @Attribute(name = "S")
    public int SubGroupRef;

    public DiscountGoodsMainSubTypeDetailXml()
    {}
}
