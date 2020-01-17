package ba.unsa.etf.rpr;

public class Grad {
    private int id;
    private String naziv;
    private int brojStanovnika;
    private Drzava drzava;
    private int zagadjenost;

    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava, int zagadjenost) {
        this.id = id;
        this.naziv = naziv;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
        setZagadjenost(zagadjenost);
    }

    public Grad() {
        zagadjenost = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    public int getZagadjenost() {
        return zagadjenost;
    }

    public void setZagadjenost(int zagadjenost) {
        if (zagadjenost < 1 || zagadjenost > 10)
            throw new IllegalArgumentException("Neispravna zagadjenost " + zagadjenost);
        this.zagadjenost = zagadjenost;
    }

    @Override
    public String toString() { return naziv; }
}
