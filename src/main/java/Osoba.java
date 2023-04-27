import java.util.ArrayList;
import java.util.List;

public class Osoba {
    private final String meno;
    private final List<Film> filmy = new ArrayList<>();

    public Osoba(String meno) {
        this.meno = meno;
    }

    public String getMeno() {
        return meno;
    }

    public List<Film> getFilmy() {
        return filmy;
    }

    public void addFilm(Film film) {
        this.filmy.add(film);
    }

    public void removeFilm(Film film) {
        this.filmy.remove(film);
    }
}
