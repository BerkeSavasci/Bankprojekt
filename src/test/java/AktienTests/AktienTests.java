package AktienTests;

import bankprojekt.verarbeitung.Aktie;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author berkesavasci
 * 08.12.23
 * project: Uebung_3_new
 */
public class AktienTests {
    private Aktie aktie;
    private Konto berkesKonto;
    @BeforeEach
    void setup() {
        this.aktie = new Aktie("Berke GMBH Inc.", 123123, 50);

        berkesKonto = new Girokonto(Mockito.mock(Kunde.class),999123L,500,0);
        berkesKonto.einzahlen(5000);
    }
    @Test
    void kaufAuftragTest() {
        System.out.println(berkesKonto.kaufauftrag(this.aktie,200,30));

        Assertions.assertEquals(200, berkesKonto.getAktienStueckzahl());

        boolean abgezogen = berkesKonto.getKontostand() < 5000;
        Assertions.assertTrue(abgezogen);

    }
}
