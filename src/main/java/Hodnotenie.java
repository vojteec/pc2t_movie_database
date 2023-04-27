public class Hodnotenie {
    private int bodoveHodnotenie;
    private String slovnyKomantar;

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

    public void setBodoveHodnotenie(int bodoveHodnotenie) {
        this.bodoveHodnotenie = bodoveHodnotenie;
    }

    public void setSlovnyKomantar(String slovnyKomantar) {
        this.slovnyKomantar = slovnyKomantar;
    }
}
