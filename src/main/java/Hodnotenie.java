public class Hodnotenie {
    private final int bodoveHodnotenie;
    private final String slovnyKomantar;

    public Hodnotenie(int bodoveHodnotenie, String slovnyKomantar) {
        this.bodoveHodnotenie = bodoveHodnotenie;
        this.slovnyKomantar = slovnyKomantar;
    }

    public int getBodoveHodnotenie() {
        return bodoveHodnotenie;
    }

    public String getSlovnyKomantar() {
        return slovnyKomantar;
    }
}
