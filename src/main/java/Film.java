import java.util.List;

public abstract class Film {
    private String nazov;
    private String reziser;
    private int rok;
    private List<Osoba> zoznamOsob;

    public Film(String nazov, String reziser, int rok, List<Osoba> zoznamOsob) {
        this.nazov = nazov;
        this.reziser = reziser;
        this.rok = rok;
        this.zoznamOsob = zoznamOsob;
    }

    public int getRok() {
        return rok;
    }

    public String getNazov() {
        return nazov;
    }

    public String getReziser() {
        return reziser;
    }

    public List<Osoba> getZoznamOsob() {
        return zoznamOsob;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public void setReziser(String reziser) {
        this.reziser = reziser;
    }

    public void setRok(int rok) {
        this.rok = rok;
    }

    public void setZoznamOsob(List<Osoba> zoznamOsob) {
        this.zoznamOsob = zoznamOsob;
    }

    public void addOsoba(Osoba osoba) {
        this.zoznamOsob.add(osoba);
    }
}
