package spielereien;

import bankprojekt.verarbeitung.*;

import java.time.LocalDate;

/**
 * The Waehrungsspielereien class is a small program that demonstrates the use of different currencies in bank accounts.
 * It contains a main method that creates customer objects and bank accounts with different currencies, performs various operations such as deposits and withdrawals, and displays the account information after each operation.
 */
public class Waehrungsspielereien {

    /**
     * ein kleines Programm mit 2 Konten in verschiedenen Währungen
     *
     * @param args wird nicht verwendet
     */
    public static void main(String[] args) {
        try {
            Kunde ich = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));
            Konto meinKonto = new Girokonto(ich, 1234, 1000.0,0);

            meinKonto.waehrungswechsel(Waehrung.BGN);
            System.out.println("Nach Währungswechsel nach BGN: " + meinKonto);
            meinKonto.einzahlen(1000, Waehrung.EUR);
            System.out.println("1000 EUR eingezahlt: " + meinKonto);
            meinKonto.einzahlen(1000, Waehrung.DKK);
            System.out.println("1000 DKK eingezahlt: " + meinKonto);

            meinKonto.waehrungswechsel(Waehrung.MKD);
            System.out.println("Nach Währungswechsel nach MKD: " + meinKonto);
            boolean hatGeklappt;
            hatGeklappt = meinKonto.abheben(1000);
            System.out.println("1000 MKD abgehoben: " + hatGeklappt + System.lineSeparator() + meinKonto);
            hatGeklappt = meinKonto.abheben(1000, Waehrung.EUR);
            System.out.println("1000 EUR abgehoben: " + hatGeklappt + System.lineSeparator() + meinKonto);
            hatGeklappt = meinKonto.abheben(1000, Waehrung.DKK);
            System.out.println("1000 DKK abgehoben: " + hatGeklappt + System.lineSeparator() + meinKonto);

            meinKonto = new Sparbuch(ich, 9876,0);
            meinKonto.waehrungswechsel(Waehrung.BGN);
            System.out.println("Nach Währungswechsel nach BGN: " + meinKonto);
            meinKonto.einzahlen(1000, Waehrung.EUR);
            System.out.println("1000 EUR eingezahlt: " + meinKonto);
            meinKonto.einzahlen(1000, Waehrung.DKK);
            System.out.println("1000 DKK eingezahlt: " + meinKonto);

            meinKonto.waehrungswechsel(Waehrung.MKD);
            System.out.println("Nach Währungswechsel nach MKD: " + meinKonto);
            hatGeklappt = meinKonto.abheben(1000);
            System.out.println("1000 MKD abgehoben: " + hatGeklappt + System.lineSeparator() + meinKonto);
            hatGeklappt = meinKonto.abheben(1000, Waehrung.EUR);
            System.out.println("1000 EUR abgehoben: " + hatGeklappt + System.lineSeparator() + meinKonto);
            hatGeklappt = meinKonto.abheben(1000, Waehrung.DKK);
            System.out.println("1000 DKK abgehoben: " + hatGeklappt + System.lineSeparator() + meinKonto);
        } catch (GesperrtException e) {
        }  //nichts tun, tritt hier nicht auf
    }

}

