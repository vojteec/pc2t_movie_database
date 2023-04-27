import java.util.ArrayList;
import java.util.List;

public class AnimovanyFilm extends Film {
    private int minVekDivaka;
    private List<HodnotenieCislo> hodnoteniaCislo = new ArrayList<>();

    public AnimovanyFilm(String nazov, String reziser, int rok, List<Osoba> zoznamOsob, int minVekDivaka) {
        super(nazov, reziser, rok, zoznamOsob);
        this.minVekDivaka = minVekDivaka;
    }

    public int getMinVekDivaka() {
        return minVekDivaka;
    }

    public List<HodnotenieCislo> getHodnoteniaCislo() {
        return hodnoteniaCislo;
    }

    public void addHodnotenieCislo(HodnotenieCislo hodnotenieCislo) {
        this.hodnoteniaCislo.add(hodnotenieCislo);
    }

    public void setMinVekDivaka(int minVekDivaka) {
        this.minVekDivaka = minVekDivaka;
    }
}
