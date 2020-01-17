package ba.unsa.etf.rpr;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class IspitDAOTest {

    @Test
    void testGrad() {
        Grad grad = new Grad();
        grad.setNaziv("Sarajevo");
        grad.setZagadjenost(6);
        assertEquals(6, grad.getZagadjenost());
    }

    @Test
    void testGradIzuzetak() {
        Grad grad = new Grad();
        grad.setNaziv("Sarajevo");
        grad.setZagadjenost(8);
        assertThrows(IllegalArgumentException.class, () -> grad.setZagadjenost(11));
        assertEquals(8, grad.getZagadjenost());
    }

    @Test
    void testGradCtor() {
        Drzava drzava = new Drzava();
        Grad grad = new Grad(0, "Sarajevo", 350000, drzava, 4); // Zagađenost je posljednji parametar
        assertEquals(4, grad.getZagadjenost());
    }

    @Test
    void testGradCtorIzuzetak() {
        Drzava drzava = new Drzava();
        assertThrows(IllegalArgumentException.class, () -> { Grad grad= new Grad(0, "Sarajevo", 350000, drzava, -3); });
    }

    @Test
    void testIzmijeniGrad() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad bech = dao.glavniGrad("Austrija");
        int zagadjenost = bech.getZagadjenost()+1;
        if (zagadjenost == 11) zagadjenost=1;
        bech.setZagadjenost(zagadjenost);
        dao.izmijeniGrad(bech);

        Grad b2 = dao.glavniGrad("Austrija");
        assertEquals(zagadjenost, b2.getZagadjenost());
        b2.setZagadjenost(1);
        dao.izmijeniGrad(b2);

        Grad b3 = dao.glavniGrad("Austrija");
        assertEquals(1, b2.getZagadjenost());
    }

    @Test
    void testDodajGrad() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava francuska = dao.nadjiDrzavu("Francuska");
        Grad sarajevo = new Grad(0, "Sarajevo", 350000, francuska, 5);

        dao.dodajGrad(sarajevo);

        Grad s2 = null;
        for(Grad grad : dao.gradovi()) {
            if (grad.getNaziv().equals("Sarajevo"))
                s2 = grad;
        }
        assertNotNull(s2);

        assertEquals(5, s2.getZagadjenost());
    }

    @Test
    void testNadjiGrad() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava francuska = dao.nadjiDrzavu("Francuska");

        Grad sarajevo = new Grad(0, "Sarajevo", 350000, francuska, 3);
        dao.dodajGrad(sarajevo);

        Grad s2 = dao.nadjiGrad("Sarajevo");
        assertNotNull(s2);

        assertEquals(3, s2.getZagadjenost());
    }


    @Test
    void testNadjiDrzavu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava francuska = dao.nadjiDrzavu("Francuska");

        Grad sarajevo = new Grad(0, "Sarajevo", 350000, francuska, 4); // Zagađenost je pozljednji parametar
        dao.dodajGrad(sarajevo);

        Grad s2 = dao.nadjiGrad("Sarajevo");
        assertNotNull(s2);

        Drzava bih = new Drzava(0, "Bosna i Hercegovina", s2);
        dao.dodajDrzavu(bih);

        Drzava d2 = dao.nadjiDrzavu("Bosna i Hercegovina");

        assertNotNull(d2);
        assertEquals(4, d2.getGlavniGrad().getZagadjenost());
    }

    @Test
    void testBazaDirekt() {
        // Test koji direktno pristupa bazi zaobilazeći DAO klasu

        // Regenerišemo bazu ako je promijenjena prethodnim testovima
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Sad ćemo se opet diskonektovati jer radimo sa bazom direktno
        GeografijaDAO.removeInstance();

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
            try {
                PreparedStatement nadmorskaUpit = conn.prepareStatement("SELECT zagadjenost FROM grad WHERE id=1");
                nadmorskaUpit.execute();
                conn.close();
            } catch (SQLException e) {
                fail("Tabela grad ne sadrži kolonu zagadjenost");
            }
        } catch (SQLException e) {
            fail("Datoteka sa bazom ne postoji ili je nedostupna");
        }

    }
}