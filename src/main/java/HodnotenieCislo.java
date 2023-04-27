public class HodnotenieCislo extends Hodnotenie {
    public static final int minValue = 1;
    public static final int maxValue = 10;

    public HodnotenieCislo(int bodoveHodnotenie, String slovnyKomantar) throws Exception {
        super(bodoveHodnotenie, slovnyKomantar);
        if (bodoveHodnotenie < HodnotenieCislo.minValue || bodoveHodnotenie > HodnotenieCislo.maxValue) {
            throw new Exception("Bodove hodnotenie mimo povoleneho rozsahu");
        }
    }
}
