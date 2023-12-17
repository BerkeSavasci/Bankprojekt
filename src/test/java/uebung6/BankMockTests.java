package uebung6;

import bankprojekt.verarbeitung.*;
import bankprojekt.verwaltung.Bank;
import bankprojekt.verwaltung.KontonummerNichtVorhandenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BankMockTests {

    Bank bank;
    Girokonto mockKonto1;
    Konto mockKonto2;
    long kontoNr1;
    long kontoNr2;

    @BeforeEach
    void setUp() {
        bank = new Bank(12345678);

        mockKonto1 = mock(Girokonto.class);
        mockKonto2 = mock(Konto.class, withSettings().extraInterfaces(Ueberweisungsfaehig.class));

        kontoNr1 = bank.mockEinfuegen(mockKonto1);
        kontoNr2 = bank.mockEinfuegen(mockKonto2);

        when(mockKonto1.getKontonummer()).thenReturn(kontoNr1);
        when(mockKonto2.getKontonummer()).thenReturn(kontoNr2);

        when(mockKonto1.getInhaber()).thenReturn(new Kunde());
        when(mockKonto2.getInhaber()).thenReturn(new Kunde());
    }

    @Test
    void geldUeberweisenHappyTest() throws GesperrtException {
        when(mockKonto1.ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString()))
                .thenReturn(true);

        boolean result = bank.geldUeberweisen(kontoNr1, kontoNr2, 100, "Test");

        assertTrue(result);
        verify(mockKonto1, times(1))
                .ueberweisungAbsenden(100, "Mustermann, Max", kontoNr2, bank.getBankleitzahl(), "Test");
    }

    @Test
    void geldUeberweisenFalschTest() throws GesperrtException {
        when(mockKonto1.ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString()))
                .thenReturn(false);

        boolean result = bank.geldUeberweisen(kontoNr1, kontoNr2, 100, "Test");

        assertFalse(result);
        verify(mockKonto1, times(1))
                .ueberweisungAbsenden(100, "Mustermann, Max", kontoNr2, bank.getBankleitzahl(), "Test");
    }

    @Test
    void geldUeberweisenNegativeBetragTest() throws GesperrtException {
        when(mockKonto1.ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString())).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> bank.geldUeberweisen(kontoNr1, kontoNr2, -100, "Test"));
        verify(mockKonto1, times(1))
                .ueberweisungAbsenden(-100, "Mustermann, Max", kontoNr2, bank.getBankleitzahl(), "Test");
    }

    @Test
    void geldUeberweisenNichtGenugGeldTest() throws GesperrtException {
        when(mockKonto1.ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString())).thenReturn(false);

        assertFalse(bank.geldUeberweisen(kontoNr1, kontoNr2, 10000, "Test"));
        verify(mockKonto1, times(1))
                .ueberweisungAbsenden(10000, "Mustermann, Max", kontoNr2, bank.getBankleitzahl(), "Test");
    }

    @Test
    void geldUeberweisenKeinReferenzTest() throws GesperrtException {
        when(mockKonto1.ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString())).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> bank.geldUeberweisen(kontoNr1, kontoNr2, 100, ""));
        verify(mockKonto1, times(1))
                .ueberweisungAbsenden(100, "Mustermann, Max", kontoNr2, bank.getBankleitzahl(), "");
    }

    @Test
    void geldUeberweisenKeinNachKontoTest() throws GesperrtException {
        boolean result = bank.geldUeberweisen(kontoNr1, 999999L, 100, "Test");

        assertFalse(result);
        verify(mockKonto1, times(0))
                .ueberweisungAbsenden(100, "Mustermann, Max", 999999L, bank.getBankleitzahl(), "Test");

    }

    @Test
    void geldUeberweisenKeinVonKontoTest() throws GesperrtException {
        boolean result = bank.geldUeberweisen(999999L, kontoNr2, 100, "Test");

        assertFalse(result);
        verify(mockKonto1, times(0))
                .ueberweisungAbsenden(100, "Mustermann, Max", kontoNr2, bank.getBankleitzahl(), "Test");
    }

    @Test
    void geldUeberweisenGleicheKontoTest() throws GesperrtException {
        when(mockKonto1.ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString())).thenReturn(false);

        boolean result = bank.geldUeberweisen(kontoNr1, kontoNr1, 100, "Test");

        assertFalse(result);
        verify(mockKonto1, times(1))
                .ueberweisungAbsenden(100, "Mustermann, Max", kontoNr1, bank.getBankleitzahl(), "Test");
    }

    @Test
    void kontoLoeschenTest() {
        assertTrue(bank.kontoLoeschen(kontoNr1));
        assertFalse(bank.kontoLoeschen(kontoNr1));
    }

    @Test
    void geloeschteKontoGeldAbheben(){
        assertTrue(bank.kontoLoeschen(kontoNr1));

        Assertions.assertThrowsExactly(KontonummerNichtVorhandenException.class, () -> bank.geldAbheben(kontoNr1, 20.0));
        assertFalse(bank.getAlleKontonummern().contains(kontoNr1));
    }
}
