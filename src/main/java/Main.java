import dbconn.DBConnection;

import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.sql.*;
import java.util.*;

public class Main {

    public static int pouzeCelaCisla(Scanner sc) {
        int cislo = 0;
        try {
            cislo = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("Nastala vyjimka typu " + e.toString());
            System.out.println("zadejte prosim cele cislo ");
            sc.nextLine();
            cislo = pouzeCelaCisla(sc);
        }
        return cislo;
    }

    private static void vypisFilm(Film film) {
        System.out.println("Nazov: " + film.getNazov());
        System.out.println("Reziser: " + film.getReziser());
        System.out.println("Rok vydania: " + film.getRok());
        List<String> menaOsob = new ArrayList<>();
        for (Osoba o : film.getZoznamOsob()) {
            menaOsob.add(o.getMeno());
        }

        if (film instanceof HranyFilm) {
            System.out.print("Zoznam hercov: ");
        } else {
            System.out.print("Zoznam animatorov: ");
        }

        System.out.println(String.join(", ", menaOsob));

        if (film instanceof AnimovanyFilm) {
            System.out.println("Minimalny doporuceny vek divaka: " + ((AnimovanyFilm) film).getMinVekDivaka());
        }
    }

    public static void main(String[] args) {
        Connection conn = DBConnection.getDBConnection();
        Map<String, Film> filmyMapa = new HashMap<>();
        Map<String, Osoba> osobyMapa = new HashMap<>();

        try {
            PreparedStatement prStmt = conn.prepareStatement("SELECT * FROM Osoba");
            ResultSet rs = prStmt.executeQuery();
            while (rs.next()) {
                String meno = rs.getString("meno");
                Osoba o = new Osoba(meno);
                osobyMapa.put(meno, o);
            }

            PreparedStatement prStmt2 = conn.prepareStatement("SELECT * FROM Film");
            ResultSet rs2 = prStmt2.executeQuery();
            while (rs2.next()) {
                String nazov = rs2.getString("nazov");
                String reziser = rs2.getString("reziser");
                String typFilmu = rs2.getString("typFilmu");
                int rok = rs2.getInt("rok");
                int minVekDivaka = rs2.getInt("minVekDivaka");
                Film f = null;
                if (Objects.equals(typFilmu, "a")) {
                    f = new AnimovanyFilm(nazov, reziser, rok, new ArrayList<>(), minVekDivaka);
                }
                else if (Objects.equals(typFilmu, "h")) {
                    f = new HranyFilm(nazov, reziser, rok, new ArrayList<>());
                }
                filmyMapa.put(nazov, f);
            }

            PreparedStatement prStmt3 = conn.prepareStatement("SELECT * FROM FilmOsoba");
            ResultSet rs3 = prStmt3.executeQuery();
            while (rs3.next()) {
                String meno = rs3.getString("meno");
                String nazov = rs3.getString("nazov");
                if (osobyMapa.containsKey(meno) && filmyMapa.containsKey(nazov)) {
                    Osoba o = osobyMapa.get(meno);
                    Film f = filmyMapa.get(nazov);
                    o.addFilm(f);
                    f.addOsoba(o);
                }
            }

            PreparedStatement prStmt4 = conn.prepareStatement("SELECT * FROM Hodnotenie");
            ResultSet rs4 = prStmt4.executeQuery();
            while (rs4.next()) {
                String slovnyKomentar = rs4.getString("slovnyKomentar");
                String nazov = rs4.getString("nazov");
                int bodoveHodnotenie = rs4.getInt("bodoveHodnotenie");
                if (filmyMapa.containsKey(nazov)) {
                    Film f = filmyMapa.get(nazov);
                    if (f instanceof AnimovanyFilm) {
                        HodnotenieCislo h = new HodnotenieCislo(bodoveHodnotenie, slovnyKomentar);
                        ((AnimovanyFilm) f).addHodnotenieCislo(h);
                    }
                    else {
                        HodnotenieHviezdy h = new HodnotenieHviezdy(bodoveHodnotenie, slovnyKomentar);
                        ((HranyFilm) f).addHodnotenieHviezdy(h);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Scanner sc = new Scanner(System.in);
        int volba;
        boolean run = true;
        while (run) {
            System.out.println("Vyberte pozadovanu cinnost:");
            System.out.println("1 .. pridanie noveho filmu");
            System.out.println("2 .. upravenie filmu");
            System.out.println("3 .. zmazanie filmu");
            System.out.println("4 .. pridanie hodnotenia filmu");
            System.out.println("5 .. vypis filmov");
            System.out.println("6 .. vyhladanie filmu");
            System.out.println("7 .. vypis hercov alebo animatorov, ktori sa podielali na viac ako jednom filme");
            System.out.println("8 .. vypis vsetkych filmov, ktore obsahuju konkretneho herca alebo animatora");
            System.out.println("9 .. ulozenie informacie o vybranom filme do suboru");
            System.out.println("10 .. nacitanie vsetkych informacii o danom filme zo suboru");
            System.out.println("11 .. ukoncenie programu");
            volba = pouzeCelaCisla(sc);
            switch (volba) {
                case 1:
                    System.out.println("Stlac 1 pre pridanie Hraneho filmu; Stlac 2 pre pridanie Animovaneho filmu");
                    int volbaFilmu = pouzeCelaCisla(sc);

                    if (volbaFilmu != 1 && volbaFilmu != 2) {
                        System.out.println("Zadal si zle cislo");
                        break;
                    }
                    System.out.println("Zadaj nazov filmu");
                    String nazovFilmu = sc.nextLine();
                    System.out.println("Zadaj rezisera filmu");
                    String reziserFilmu = sc.nextLine();
                    System.out.println("Zadaj rok vydania filmu");
                    int rokVydania = pouzeCelaCisla(sc);

                    if (volbaFilmu == 1) {
                        System.out.println("Zadaj mena hercov (oddelenych ciarkami)");
                    } else if (volbaFilmu == 2) {
                        System.out.println("Zadaj mena animatorov (oddelenych ciarkami)");
                    }

                    String herci = sc.nextLine();
                    List<String> herciList = Arrays.asList(herci.split(","));
                    List<String> trimmedArray = new ArrayList<>(); //očisti o medzery
                    for (String s : herciList) {
                        if (s.trim().length() == 0) {
                            continue;
                        }
                        trimmedArray.add(s.trim());
                    }
                    List<Osoba> zoznamOsob = new ArrayList<>();
                    for (int i = 0; i < trimmedArray.size(); i++) {
                        Osoba osoba;
                        if (osobyMapa.containsKey(trimmedArray.get(i))) {    //ked uz je v mape
                            osoba = osobyMapa.get(trimmedArray.get(i));
                        } else {
                            osoba = new Osoba(trimmedArray.get(i)); //ked este neexistuje, vytvor novu
                            osobyMapa.put(trimmedArray.get(i), osoba);
                        }
                        zoznamOsob.add(osoba);
                    }

                    Film film = null;
                    if (volbaFilmu == 1) {
                        film = new HranyFilm(nazovFilmu, reziserFilmu, rokVydania, zoznamOsob);
                    } else if (volbaFilmu == 2) {
                        System.out.println("Zadaj doporuceny vek divaka");
                        int vekDivaka = pouzeCelaCisla(sc);
                        film = new AnimovanyFilm(nazovFilmu, reziserFilmu, rokVydania, zoznamOsob, vekDivaka);
                    }

                    filmyMapa.put(nazovFilmu, film);
                    for (Osoba o : zoznamOsob) {
                        o.addFilm(film);
                    }

                    break;
                case 2:
                    System.out.println("Zadaj nazov filmu, ktory chces upravit");
                    String filmUprava = sc.nextLine();
                    if (filmyMapa.containsKey(filmUprava)) {
                        Film foundFilm = filmyMapa.get(filmUprava);
                        System.out.println("Vyber pozadovanu zmenu:");
                        System.out.println("1 .. nazov filmu");
                        System.out.println("2 .. reziser");
                        System.out.println("3 .. rok vydania");
                        if (foundFilm instanceof AnimovanyFilm) {
                            System.out.println("4 .. zoznam animatorov");
                            System.out.println("5 .. doporuceny vek divaka");
                        } else {
                            System.out.println("4 .. zoznam hercov");
                        }
                        int volbaZmeny = pouzeCelaCisla(sc);
                        switch (volbaZmeny) {
                            case 1:
                                System.out.println("Zadaj novy nazov filmu");
                                String staryNazov = foundFilm.getNazov();
                                String novyNazov = sc.nextLine();
                                foundFilm.setNazov(novyNazov);
                                filmyMapa.remove(staryNazov);
                                filmyMapa.put(novyNazov, foundFilm);
                                break;
                            case 2:
                                System.out.println("Zadaj noveho rezisera");
                                String novyReziser = sc.nextLine();
                                foundFilm.setReziser(novyReziser);
                                break;
                            case 3:
                                System.out.println("Zadaj novy rok vydania");
                                int novyRok = pouzeCelaCisla(sc);
                                foundFilm.setRok(novyRok);
                                break;
                            case 4:
                                if (foundFilm instanceof AnimovanyFilm) {
                                    System.out.println("Zadaj novy zoznam animatorov");
                                } else {
                                    System.out.println("Zadaj novy zoznam hercov");
                                }
                                String noveOsoby = sc.nextLine();

                                for (Osoba o : foundFilm.getZoznamOsob()) {
                                    o.removeFilm(foundFilm);
                                    if (o.getFilmy().size() == 0) {
                                        osobyMapa.remove(o.getMeno());
                                    }
                                }

                                List<String> noveOsobyList = Arrays.asList(noveOsoby.split(","));
                                List<String> noveOsobyTrimmed = new ArrayList<>();
                                for (String s : noveOsobyList) {
                                    noveOsobyTrimmed.add(s.trim());
                                }
                                List<Osoba> novyZoznamOsob = new ArrayList<>();
                                for (int i = 0; i < noveOsobyTrimmed.size(); i++) {
                                    Osoba osoba;
                                    if (osobyMapa.containsKey(noveOsobyTrimmed.get(i))) {
                                        osoba = osobyMapa.get(noveOsobyTrimmed.get(i));
                                    } else {
                                        osoba = new Osoba(noveOsobyTrimmed.get(i));
                                        osobyMapa.put(noveOsobyTrimmed.get(i), osoba);
                                    }
                                    osoba.addFilm(foundFilm);
                                    novyZoznamOsob.add(osoba);
                                }

                                foundFilm.setZoznamOsob(novyZoznamOsob);
                                break;
                            case 5:
                                if (foundFilm instanceof AnimovanyFilm) {
                                    System.out.println("Zadaj novy doporuceny vek divaka");
                                    int novyVek = pouzeCelaCisla(sc);
                                    ((AnimovanyFilm) foundFilm).setMinVekDivaka(novyVek);
                                } else {
                                    System.out.println("Zadal si zle cislo");
                                }
                                break;
                            default:
                                System.out.println("Zadal si zle cislo");
                                break;
                        }
                    } else {
                        System.out.println("Zadany film neexistuje");
                    }
                    break;
                case 3:
                    System.out.println("Zadaj nazov filmu, ktory chces vymazat");
                    String filmZmazanie = sc.nextLine();
                    if (filmyMapa.containsKey(filmZmazanie)) {
                        Film foundFilm = filmyMapa.get(filmZmazanie);
                        for (Osoba o : foundFilm.getZoznamOsob()) {
                            o.removeFilm(foundFilm);
                            if (o.getFilmy().size() == 0) {
                                osobyMapa.remove(o.getMeno());
                            }
                        }
                        filmyMapa.remove(filmZmazanie);
                        System.out.println("Film bol uspesne vymazany");
                    } else {
                        System.out.println("Zadany film neexistuje");
                    }
                    break;
                case 4:
                    System.out.println("Zadaj nazov filmu");
                    String nazovHodnotenie = sc.nextLine();
                    if (filmyMapa.containsKey(nazovHodnotenie)) {
                        Film foundFilm = filmyMapa.get(nazovHodnotenie);
                        int min;
                        int max;
                        if (foundFilm instanceof HranyFilm) {
                            min = HodnotenieHviezdy.minValue;
                            max = HodnotenieHviezdy.maxValue;
                        } else {
                            min = HodnotenieCislo.minValue;
                            max = HodnotenieCislo.maxValue;
                        }
                        System.out.println("Zadaj hodnotenie medzi " + min + " a " + max);
                        int hodnotenieInt = pouzeCelaCisla(sc);

                        System.out.println("Zadaj slovny komentar alebo enter");
                        String slovnyKomentar = sc.nextLine();

                        try {
                            if (foundFilm instanceof HranyFilm) {
                                HodnotenieHviezdy hodnotenie = new HodnotenieHviezdy(hodnotenieInt, slovnyKomentar);
                                ((HranyFilm) foundFilm).addHodnotenieHviezdy(hodnotenie);
                            } else {
                                HodnotenieCislo hodnotenie = new HodnotenieCislo(hodnotenieInt, slovnyKomentar);
                                ((AnimovanyFilm) foundFilm).addHodnotenieCislo(hodnotenie);
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Zadany film neexistuje");
                    }
                    break;
                case 5:
                    for (Film value : filmyMapa.values()) {
                        vypisFilm(value);
                        System.out.print("\n");
                    }
                    break;
                case 6:
                    System.out.println("Zadaj nazov filmu");
                    String nazovVyhladavanie = sc.nextLine();
                    if (filmyMapa.containsKey(nazovVyhladavanie)) {
                        Film foundFilm = filmyMapa.get(nazovVyhladavanie);
                        vypisFilm(foundFilm);
                        if (foundFilm instanceof HranyFilm) {
                            List<HodnotenieHviezdy> hodnotenia = ((HranyFilm) foundFilm).getHodnoteniaHviezdy();
                            hodnotenia.sort((e1, e2) -> e2.getBodoveHodnotenie() - e1.getBodoveHodnotenie());
                            for (Hodnotenie h : hodnotenia) {
                                System.out.println("Hodnotenie: " + h.getBodoveHodnotenie());
                                if (h.getSlovnyKomantar().length() > 0) {
                                    System.out.println("Slovny komentar: " + h.getSlovnyKomantar());
                                }
                            }
                        } else {
                            List<HodnotenieCislo> hodnotenia = ((AnimovanyFilm) foundFilm).getHodnoteniaCislo();
                            hodnotenia.sort((e1, e2) -> e2.getBodoveHodnotenie() - e1.getBodoveHodnotenie());
                            for (Hodnotenie h : hodnotenia) {
                                System.out.print("Hodnotenie: " + h.getBodoveHodnotenie());
                                if (h.getSlovnyKomantar().length() > 0) {
                                    System.out.print(" Slovny komentar: " + h.getSlovnyKomantar());
                                }
                                System.out.print("\n");
                            }
                        }
                    } else {
                        System.out.println("Zadany film neexistuje");
                    }
                    break;
                case 7:
                    for (Osoba osoba : osobyMapa.values()) {
                        List<Film> filmy = osoba.getFilmy();
                        if (filmy.size() > 1) {
                            System.out.print(osoba.getMeno() + " - ");
                            List<String> nazvy = new ArrayList<>();
                            for (Film f : filmy) {
                                nazvy.add(f.getNazov());
                            }
                            System.out.print(String.join(", ", nazvy));
                            System.out.print("\n");
                        }

                    }
                    break;
                case 8:
                    System.out.println("Zadaj meno herca alebo animatora");
                    String hladaneMeno = sc.nextLine();
                    if (osobyMapa.containsKey(hladaneMeno)) {
                        Osoba o = osobyMapa.get(hladaneMeno);
                        List<String> filmyOsoby = new ArrayList<>();
                        for (Film f : o.getFilmy()) {
                            filmyOsoby.add(f.getNazov());
                        }
                        System.out.println("Filmy hladaneho herca alebo animatora: " + String.join(", ", filmyOsoby));
                    } else {
                        System.out.println("Zadany herec alebo animator neexistuje");
                    }
                    break;
                case 9:
                    System.out.println("Zadaj nazov filmu, ktory chces ulozit do suboru");
                    String filmUlozenie = sc.nextLine();
                    if (filmyMapa.containsKey(filmUlozenie)) {
                        Film foundFilm = filmyMapa.get(filmUlozenie);
                        System.out.println("Zadaj cestu suboru");
                        String cestaSuboru = sc.nextLine();

                        File subor = new File(cestaSuboru);
                        FileWriter fw;
                        try {
                            fw = new FileWriter(subor);

                            if (foundFilm instanceof HranyFilm) {
                                fw.write("hrany\n");
                            } else {
                                fw.write("animovany\n");
                            }

                            fw.write(foundFilm.getNazov() + "\n");
                            fw.write(foundFilm.getReziser() + "\n");
                            fw.write(foundFilm.getRok() + "\n");

                            List<String> menaOsob = new ArrayList<>();
                            for (Osoba o : foundFilm.getZoznamOsob()) {
                                menaOsob.add(o.getMeno());
                            }
                            fw.write(String.join(",", menaOsob) + "\n");

                            if (foundFilm instanceof AnimovanyFilm) {
                                fw.write(((AnimovanyFilm) foundFilm).getMinVekDivaka() + "\n");
                                for (Hodnotenie h : ((AnimovanyFilm) foundFilm).getHodnoteniaCislo()) {
                                    fw.write(h.getBodoveHodnotenie() + "," + h.getSlovnyKomantar() + ";");
                                }
                            } else if (foundFilm instanceof HranyFilm) {
                                for (Hodnotenie h : ((HranyFilm) foundFilm).getHodnoteniaHviezdy()) {
                                    fw.write(h.getBodoveHodnotenie() + "," + h.getSlovnyKomantar() + ";");
                                }
                            }

                            fw.close();
                        } catch (IOException e) {
                            System.out.println("Subor " + subor + " sa neda otvorit");
                        }
                    } else {
                        System.out.println("Zadany film neexistuje");
                    }
                    break;
                case 10:
                    System.out.println("Zadaj cestu suboru, ktory chcete nacitat");
                    String cestaSuboru = sc.nextLine();

                    File subor = new File(cestaSuboru);
                    BufferedReader reader;
                    try {
                        reader = new BufferedReader(new FileReader(subor));

                        String typFilmu = reader.readLine().trim();
                        if (!typFilmu.equals("animovany") && !typFilmu.equals("hrany")) {
                            throw new Exception("Subor ma nespravny tvar");
                        }

                        String nazov = reader.readLine().trim();
                        if (nazov.length() == 0) {
                            throw new Exception("Subor ma nespravny tvar");
                        }
                        if (filmyMapa.containsKey(nazov)) {
                            throw new Exception("Film s tymto nazov uz existuje");
                        }

                        String reziser = reader.readLine().trim();
                        if (reziser.length() == 0) {
                            throw new Exception("Subor ma nespravny tvar");
                        }

                        String rokString = reader.readLine().trim();
                        if (rokString.length() == 0) {
                            throw new Exception("Subor ma nespravny tvar");
                        }
                        int rok = Integer.parseInt(rokString);

                        String herciString = reader.readLine().trim();
                        List<String> herciMena = new ArrayList<>();
                        for (String s : herciString.split(",")) {
                            if (s.trim().length() > 0) {
                                herciMena.add(s.trim());
                            }
                        }

                        int vek = 0;
                        if (typFilmu.equals("animovany")) {
                            String vekString = reader.readLine().trim();
                            if (vekString.length() == 0) {
                                throw new Exception("Subor ma nespravny tvar");
                            }
                            vek = Integer.parseInt(vekString);
                        }

                        String hodnotenieString = reader.readLine();

                        List<Osoba> zoznamOsobLoad = new ArrayList<>();
                        for (String meno : herciMena) {
                            Osoba o;
                            if (osobyMapa.containsKey(meno)) {
                                o = osobyMapa.get(meno);
                            } else {
                                o = new Osoba(meno);
                                osobyMapa.put(meno, o);
                            }
                            zoznamOsobLoad.add(o);
                        }

                        Film f;
                        if (typFilmu.equals("hrany")) {
                            f = new HranyFilm(nazov, reziser, rok, zoznamOsobLoad);
                        } else {
                            f = new AnimovanyFilm(nazov, reziser, rok, zoznamOsobLoad, vek);
                        }

                        for (Osoba osoba : zoznamOsobLoad) {
                            osoba.addFilm(f);
                        }

                        filmyMapa.put(nazov, f);

                        if (hodnotenieString != null && hodnotenieString.trim().length() > 0) {
                            for (String s : hodnotenieString.trim().split(";")) {
                                s = s.trim();
                                if (s.length() == 0) {
                                    throw new Exception("Hodnotenia maju nespravny tvar");
                                }
                                String[] elements = s.split(",", 2);
                                if (elements.length != 2) {
                                    throw new Exception("Hodnotenia maju nespravny tvar");
                                }

                                if (typFilmu.equals("hrany")) {
                                    int hCislo = Integer.parseInt(elements[0]);
                                    HodnotenieHviezdy h = new HodnotenieHviezdy(hCislo, elements[1]);
                                    ((HranyFilm) f).addHodnotenieHviezdy(h);
                                } else {
                                    int hCislo = Integer.parseInt(elements[0]);
                                    HodnotenieCislo h = new HodnotenieCislo(hCislo, elements[1]);
                                    ((AnimovanyFilm) f).addHodnotenieCislo(h);
                                }
                            }
                        }

                        reader.close();
                    } catch (IOException e) {
                        System.out.println("Subor " + subor + " sa neda otvorit");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 11:
                    run = false;
                    break;
            }

        }

        try {
            Statement stmnt = conn.createStatement();
            stmnt.execute("DROP TABLE IF EXISTS Osoba");
            stmnt.execute("DROP TABLE IF EXISTS Film");
            stmnt.execute("DROP TABLE IF EXISTS Hodnotenie");
            stmnt.execute("DROP TABLE IF EXISTS FilmOsoba");
            stmnt.execute(
            "CREATE TABLE IF NOT EXISTS Osoba(\n" +
                "   meno VARCHAR(100) NOT NULL,\n" +
                "   PRIMARY KEY (meno)\n" +
                ");"
            );
            stmnt.execute(
            "CREATE TABLE IF NOT EXISTS Film(\n" +
                "   reziser VARCHAR(100) NOT NULL,\n" +
                "   nazov VARCHAR(100) NOT NULL,\n" +
                "   rok INT NOT NULL,\n" +
                "   minVekDivaka INT,\n" +
                "   typFilmu CHAR(1) NOT NULL,\n" +
                "   PRIMARY KEY (nazov)\n" +
                ");"
            );
            stmnt.execute(
            "CREATE TABLE IF NOT EXISTS Hodnotenie(\n" +
                "   bodoveHodnotenie INT NOT NULL,\n" +
                "   slovnyKomentar VARCHAR(200),\n" +
                "   nazov VARCHAR(100) NOT NULL,\n" +
                "   FOREIGN KEY (nazov) REFERENCES Film(nazov)\n" +
                ");"
            );
            stmnt.execute(
            "CREATE TABLE IF NOT EXISTS FilmOsoba(\n" +
                "   meno VARCHAR(100) NOT NULL,\n" +
                "   nazov VARCHAR(100) NOT NULL,\n" +
                "   PRIMARY KEY (meno, nazov),\n" +
                "   FOREIGN KEY (meno) REFERENCES Osoba(meno),\n" +
                "   FOREIGN KEY (nazov) REFERENCES Film(nazov)\n" +
                ");"
            );

            String insertOsoba = "INSERT INTO Osoba (meno) VALUES (?)";
            for (Osoba osoba : osobyMapa.values()) {
                PreparedStatement prStmt = conn.prepareStatement(insertOsoba);
                prStmt.setString(1, osoba.getMeno());
                prStmt.executeUpdate();
            }

            String insertFilm = "INSERT INTO Film (typFilmu, nazov, reziser, rok, minVekDivaka) VALUES (?,?,?,?,?)";
            String insertFilmOsoba = "INSERT INTO FilmOsoba (meno, nazov) VALUES (?,?)";
            String insertHodnotenie = "INSERT INTO Hodnotenie (bodoveHodnotenie, slovnyKomentar, nazov) VALUES (?,?,?)";
            for (Film film : filmyMapa.values()) {
                PreparedStatement prStmt = conn.prepareStatement(insertFilm);
                String typFilmu = film instanceof AnimovanyFilm ? "a" : "h";
                prStmt.setString(1, typFilmu);
                prStmt.setString(2, film.getNazov());
                prStmt.setString(3, film.getReziser());
                prStmt.setInt(4, film.getRok());
                Integer vek = null;
                if (film instanceof AnimovanyFilm) {
                    vek = ((AnimovanyFilm) film).getMinVekDivaka();
                }
                prStmt.setObject(5, vek);
                prStmt.executeUpdate();

                for (Osoba osoba : film.getZoznamOsob()) {
                    PreparedStatement prStmt2 = conn.prepareStatement(insertFilmOsoba);
                    prStmt2.setString(1, osoba.getMeno());
                    prStmt2.setString(2, film.getNazov());
                    prStmt2.executeUpdate();
                }

                if (film instanceof AnimovanyFilm) {
                    for (HodnotenieCislo h : ((AnimovanyFilm) film).getHodnoteniaCislo()) {
                        PreparedStatement prStmt3 = conn.prepareStatement(insertHodnotenie);
                        prStmt3.setInt(1, h.getBodoveHodnotenie());
                        prStmt3.setString(2, h.getSlovnyKomantar());
                        prStmt3.setString(3, film.getNazov());
                        prStmt3.executeUpdate();
                    }
                }
                else {
                    for (HodnotenieHviezdy h : ((HranyFilm) film).getHodnoteniaHviezdy()) {
                        PreparedStatement prStmt3 = conn.prepareStatement(insertHodnotenie);
                        prStmt3.setInt(1, h.getBodoveHodnotenie());
                        prStmt3.setString(2, h.getSlovnyKomantar());
                        prStmt3.setString(3, film.getNazov());
                        prStmt3.executeUpdate();
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        DBConnection.closeConnection();
    }
}
