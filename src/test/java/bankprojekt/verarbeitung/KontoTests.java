package bankprojekt.verarbeitung;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * @author berkesavasci
 * 21.10.23
 * project: BankProgramm_Maven
 */
public class KontoTests {
    private static Konto giroKonto;
    private static Konto sparrKonto;

    @BeforeEach
    void setUp() {
        Kunde kunde1 = new Kunde("Berke", "Savasci", "123 Street", LocalDate.parse("2001-10-29"));

        giroKonto = new Girokonto(kunde1, 91257120812123L, 250.90,0);
        sparrKonto = new Sparbuch(kunde1, 124619842141L,0);

        sparrKonto.einzahlen(1000, Waehrung.EUR);
        giroKonto.einzahlen(1000, Waehrung.EUR);
    }

    @Test
    void waehrungsewechselGiroTest() {
        giroKonto.waehrungswechsel(Waehrung.DKK);
        Assertions.assertEquals(Waehrung.DKK, giroKonto.getAktuelleWaehrung());
    }

    @Test
    void waehrungsewechselSparrbuchTest() {
        sparrKonto.waehrungswechsel(Waehrung.DKK);
        Assertions.assertEquals(Waehrung.DKK, sparrKonto.getAktuelleWaehrung());
    }

    @Test
    void abhebenSparrTest() throws GesperrtException {
        Assertions.assertTrue(sparrKonto.abheben(800,Waehrung.EUR));
        Assertions.assertEquals(200,sparrKonto.getKontostand());
    }
    @Test
    void abhebenGiroTest() throws GesperrtException {
        Assertions.assertTrue(giroKonto.abheben(800,Waehrung.EUR));
        Assertions.assertEquals(200,giroKonto.getKontostand());
    }
}
