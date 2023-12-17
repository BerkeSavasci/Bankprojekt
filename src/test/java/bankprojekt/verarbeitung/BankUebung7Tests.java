package bankprojekt.verarbeitung;

import bankprojekt.verwaltung.Bank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.times;

/**
 * @author berkesavasci
 * 22.11.23
 * project: Uebung_3_new
 */
public class BankUebung7Tests {
    private Bank bank;
    private Kunde kunde1, kunde2, kunde3;
    private Konto konto1, konto2, konto3;
    private long kontoNr1, kontoNr2, kontoNr3;

    @BeforeEach
    void setup() {
        bank = new Bank(124124L);

        kunde1 = Mockito.mock(Kunde.class);
        kunde2 = Mockito.mock(Kunde.class);
        kunde3 = Mockito.mock(Kunde.class);

        konto1 = Mockito.mock(Konto.class);
        konto2 = Mockito.mock(Konto.class);
        konto3 = Mockito.mock(Konto.class);

        kontoNr1 = bank.mockEinfuegen(konto1);
        kontoNr2 = bank.mockEinfuegen(konto2);
        kontoNr3 = bank.mockEinfuegen(konto3);

        Mockito.when(konto1.getKontonummer()).thenReturn(kontoNr1);
        Mockito.when(konto2.getKontonummer()).thenReturn(kontoNr2);
        Mockito.when(konto3.getKontonummer()).thenReturn(kontoNr3);

        Mockito.when(konto1.getInhaber()).thenReturn(kunde1);
        Mockito.when(konto2.getInhaber()).thenReturn(kunde2);
        Mockito.when(konto3.getInhaber()).thenReturn(kunde3);

        Mockito.when(kunde1.getName()).thenReturn("Mister, Mensch1");
        Mockito.when(kunde2.getName()).thenReturn("Misses, Mensch2");
        Mockito.when(kunde3.getName()).thenReturn("Mister, Mensch3");

        Mockito.when(kunde1.getAdresse()).thenReturn("Home1");
        Mockito.when(kunde2.getAdresse()).thenReturn("Home1");
        Mockito.when(kunde3.getAdresse()).thenReturn("Home3");
    }

    @Test
    void getAllAdressTest() {
        String expected = "Misses, Mensch2, Home1" + "\n" + "Mister, Mensch3, Home3" + "\n" + "Mister, Mensch1, Home1";

        Assertions.assertEquals(expected, bank.getKundenadressen());
    }

    @Test
    void sperrenTest() {
        Mockito.when(konto1.getKontostand()).thenReturn(-500.0);
        Mockito.when(konto2.getKontostand()).thenReturn(-500.0);
        Mockito.when(konto3.getKontostand()).thenReturn(-500.0);

        bank.pleitegeierSperren();

        Mockito.verify(konto1, times(1)).sperren();
        Mockito.verify(konto2, times(1)).sperren();
        Mockito.verify(konto3, times(1)).sperren();
    }

    @Test
    void getKundenMitVollemKontoTest() {
        Mockito.when(konto1.getKontostand()).thenReturn(1000.0);
        Mockito.when(konto2.getKontostand()).thenReturn(998.0);
        Mockito.when(konto3.getKontostand()).thenReturn(1000.0);

        List<Kunde> reicheKunden = bank.getKundenMitVollemKonto(999.0);

        Assertions.assertEquals(2, reicheKunden.size());
        Assertions.assertEquals(kunde1, reicheKunden.get(0));
        Assertions.assertEquals(kunde3, reicheKunden.get(1));
    }

}
