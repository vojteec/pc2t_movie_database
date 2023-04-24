public class HodnotenieHviezdy extends Hodnotenie {
    public static final int minValue = 1;
    public static final int maxValue = 5;

    public HodnotenieHviezdy(int bodoveHodnotenie, String slovnyKomantar) throws Exception {
        super(bodoveHodnotenie, slovnyKomantar);
        if (bodoveHodnotenie < HodnotenieHviezdy.minValue || bodoveHodnotenie > HodnotenieHviezdy.maxValue) {
            throw new Exception("Bodove hodnotenie mimo povoleneho rozsahu");
        }
    }
}
