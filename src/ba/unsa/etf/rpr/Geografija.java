package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.List;

public class Geografija {
    private List<Grad> gradovi = new ArrayList<>();
    private List<Drzava> drzave = new ArrayList<>();

    public Geografija(List<Grad> gradovi, List<Drzava> drzave) {
        this.gradovi = gradovi;
        this.drzave = drzave;
    }

    public Geografija() {
    }

    public List<Grad> getGradovi() {
        return gradovi;
    }

    public void setGradovi(List<Grad> gradovi) {
        this.gradovi = gradovi;
    }

    public List<Drzava> getDrzave() {
        return drzave;
    }

    public void setDrzave(List<Drzava> drzave) {
        this.drzave = drzave;
    }
}
