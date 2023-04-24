import java.util.ArrayList;
import java.util.List;

public class Osoba {
    private String meno;
    private List<Film> filmy = new ArrayList<>();

    public Osoba(String meno) {
        this.meno = meno;
    }

    public String getMeno() {
        return meno;
    }

    public List<Film> getFilmy() {
        return filmy;
    }

    public void setFilmy(List<Film> filmy) {
        this.filmy = filmy;
    }

    public void addFilm(Film film) {
        this.filmy.add(film);
    }

    public void removeFilm(Film film) {
        this.filmy.remove(film);
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }
}
