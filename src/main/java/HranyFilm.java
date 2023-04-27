import java.util.ArrayList;
import java.util.List;

public class HranyFilm extends Film {
    private List<HodnotenieHviezdy> hodnoteniaHviezdy = new ArrayList<>();

    public HranyFilm(String nazov, String reziser, int rok, List<Osoba> zoznamOsob) {
        super(nazov, reziser, rok, zoznamOsob);
    }

    public List<HodnotenieHviezdy> getHodnoteniaHviezdy() {
        return hodnoteniaHviezdy;
    }

    public void addHodnotenieHviezdy(HodnotenieHviezdy hodnotenieHviezdy) {
        this.hodnoteniaHviezdy.add(hodnotenieHviezdy);
    }
}
