package serializeTests;

import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;
import bankprojekt.verwaltung.Bank;
import bankprojekt.verwaltung.KontonummerNichtVorhandenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;


/**
 * @author berkesavasci
 * 13.12.23
 * project: Uebung_3_new
 */
public class SerializableTests {

    @Test
    void testCopy() throws KontonummerNichtVorhandenException {
        Kunde ich = Mockito.mock(Kunde.class);

        // Girokonto k = new Girokonto(ich, 1234, 500.0, 0);
        //k.einzahlen(13123);
        Bank b = new Bank(21841L);
        long kontoNo = b.girokontoErstellen(ich);
        b.geldEinzahlen(kontoNo, 123123);

        try {
            Bank copy = b.clone();

            Assertions.assertNotSame(copy, b);
            Assertions.assertEquals(b.getBankleitzahl(), copy.getBankleitzahl());
            Assertions.assertEquals(b.getKontostand(kontoNo), copy.getKontostand(kontoNo));

        } catch (KontonummerNichtVorhandenException | CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSerialize() {
        Kunde ich = Mockito.mock(Kunde.class);
        Girokonto k = new Girokonto(ich, 1234, 500.0, 0);
        k.einzahlen(13123);

        try (FileOutputStream fo = new FileOutputStream("konto.dat");
             ObjectOutputStream oo = new ObjectOutputStream(fo);) {

            oo.writeObject(k);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fo = new FileInputStream("konto.dat");
             ObjectInputStream oo = new ObjectInputStream(fo);) {

            Konto kopie = (Konto) oo.readObject();
            System.out.println(kopie);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
