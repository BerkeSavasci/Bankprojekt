package spielereien;

import bankprojekt.verarbeitung.Aktie;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The AktienSpielereien class is a demonstration class that showcases the usage of the Aktie and Konto classes.
 * It demonstrates how to create and manipulate instances of these classes to perform buying and selling of stocks.
 */
public class AktienSpielereien {
    /**
     * Main method to execute the programm
     * @param args not used
     * @throws ExecutionException shouldn't happen
     * @throws InterruptedException shouldn't happen
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Create Aktie instance
        Aktie aktie = new Aktie("Berke GMBH Inc.", 123123, 50);

        // Create Girokonto instance
        Girokonto berkesKonto = new Girokonto(Mockito.mock(Kunde.class), 999123L, 500, 0);
        berkesKonto.einzahlen(5000);

        // Call the Kaufauftrag method
        Future<Double> result = berkesKonto.kaufauftrag(aktie, 20, 49);

        try {
            System.out.println("The total price of the bought shares is: " + result.get());
            System.out.println("The new account balance is: " + berkesKonto.getKontostand());
            System.out.println("Number of Stocks in account: " + berkesKonto.getAktienStueckzahl());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Something went wrong");
            Thread.currentThread().interrupt();
        }

        Future<Double> saleResult = berkesKonto.verkaufauftrag(String.valueOf(aktie.getWertpapierkennummer()), 50);

        try {
            System.out.println("The total price of the sold shares is: " + saleResult.get());
            System.out.println("The new account balance after selling is: " + berkesKonto.getKontostand());
            System.out.println("Number of Stocks in account after selling: " + berkesKonto.getAktienStueckzahl());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Something went wrong during selling");
            Thread.currentThread().interrupt();
        }



    }

}
