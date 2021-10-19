package com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes;

import java.util.UUID;

/**
 * Created by A.Torabi on 12/9/2017.
 */

public final class PaymentType {
    public static final UUID Check = UUID.fromString("e3a93634-ae20-4d57-8e27-eee7b768adfc");
    public static final UUID Card = UUID.fromString("f1b06da6-122d-4427-abd0-84a7cf72b29c");
    public static final UUID Cash = UUID.fromString("837689e8-2115-4085-bf7f-0d0da86f3d71");
    public static final UUID Discount = UUID.fromString("df7e99c9-2ed9-436a-b9a3-8ec0f4e86651");
    public static final UUID Credit = UUID.fromString("56c7d3ee-4d18-4c5c-bbbd-aacc6bebd862");
    public static final UUID Recipt = UUID.fromString("da575ef0-da25-46a6-a07b-74625a818071");
}
