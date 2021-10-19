package varanegar.com.discountcalculatorlib.entity.evc;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by m.aghajani on 3/27/2016.
 */
@Root(name = "G")
public class DiscountGoodsDetailXml {

    @Attribute(name = "I")
    public int Id;

    @Attribute(name = "G")
    public int GoodsGroupRef;

    @Attribute(name = "M")
    public int ManufacturerRef;

    @Attribute(name = "B")
    public int BrandRef;

    @Attribute(name = "C")
    public int CartonType;

    @Attribute(name = "W")
    public int Weight;

    @Attribute(name = "T")
    public int GoodsCtgrRef;

    public DiscountGoodsDetailXml()
    {}
}
