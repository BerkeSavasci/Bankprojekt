package bankprojekt.verarbeitung;

import bankprojekt.verwaltung.Bank;
import bankprojekt.verwaltung.KontonummerNichtVorhandenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * Performs unit tests for the Bank class.
 * The BankTests class initializes a Bank object and an array of Kunde objects.
 * It contains several test methods to test the functionality of the Bank class methods.
 */
class BankTests {
    Bank b = new Bank(12312L);
    long DEFAULT_KONTO;
    Kunde k1;
    Kunde k2;
    long k1No;
    long k2No;
    Kunde[] kundenArray = {
            new Kunde("Max", "Mustermann", "Home", LocalDate.parse("2001-10-29")),
            new Kunde("John", "Doe", "Work", LocalDate.parse("1985-07-12")),
            new Kunde("Jane", "Doe", "Home", LocalDate.parse("1986-08-13")),
            new Kunde("Alice", "Johnson", "School", LocalDate.parse("1999-11-14")),
            new Kunde("Bob", "Smith", "Home", LocalDate.parse("1978-04-25")),
            new Kunde("Charlie", "Brown", "Work", LocalDate.parse("1990-01-01")),
            new Kunde("Lucy", "Van Pelt", "Home", LocalDate.parse("1990-02-02")),
            new Kunde("Linus", "Van Pelt", "Work", LocalDate.parse("1990-03-03")),
            new Kunde("Peppermint", "Patty", "Home", LocalDate.parse("1990-04-04")),
            new Kunde("Snoopy", "Dog", "Work", LocalDate.parse("1990-05-05"))
    };

    /**
     * This method is executed before each test case in order to set up the necessary objects and data. It performs the following steps:
     * 1. Iterates over the kundenArray and calls the girokontoErstellen and sparbuchErstellen methods on each Kunde object.
     * 2. Creates a new Kunde object named "test" with the attributes "ABC", "ABC", "Home", and "1999-01-01".
     * 3. Calls the girokontoErstellen method on the "test" Kunde object and assigns the returned value to DEFAULT_KONTO.
     * <p>
     * The purpose of this setup method is to prepare the system for each test by creating required account objects and initializing necessary variables.
     */
    @BeforeEach
    void setup() {
        for (int i = 0; i < kundenArray.length; i++) {
            b.girokontoErstellen(kundenArray[i]);
            b.sparbuchErstellen(kundenArray[i]);

            Kunde test = new Kunde("ABC", "ABC", "Home", LocalDate.parse("1999-01-01"));
            DEFAULT_KONTO = b.girokontoErstellen(test);
        }
    }

    /**
     * This test case verifies that the girokontoErstellen method throws a NullPointerException when called with a null parameter.
     * It uses the assertThrows assertion to expect a NullPointerException and executes the girokontoErstellen method with a null parameter using a lambda expression.
     */
    @Test
    void nullGirokontoErstellen() {
        Assertions.assertThrows(NullPointerException.class, () -> b.girokontoErstellen(null));
    }

    /**
     * This test case verifies that the sparbuchErstellen method throws a NullPointerException when called with a null parameter.
     * It uses the assertThrows assertion to expect a NullPointerException and executes the sparbuchErstellen method with a null parameter using a lambda expression.
     */
    @Test
    void nullSparbuchkontoErstellen() {
        Assertions.assertThrows(NullPointerException.class, () -> b.sparbuchErstellen(null));
    }

    /**
     * This test case verifies that the einzahlenTest method successfully adds the specified amount of money to a given account.
     * It calls the geldEinzahlen method with the DEFAULT_KONTO identifier and a deposit of 100.
     * Then it retrieves the account balance using the getKontostand method and prints it to the console.
     * Finally, it uses the assertEquals assertion to verify that the account balance is equal to 100.
     *
     * @throws KontonummerNichtVorhandenException if the account number is not found
     */
    @Test
    void einzahlenTest() throws KontonummerNichtVorhandenException {
        b.geldEinzahlen(DEFAULT_KONTO, 100);
        Assertions.assertEquals(100, b.getKontostand(DEFAULT_KONTO));
    }

    /**
     * This test case verifies that the einzahlenFalseKonto method throws a KontonummerNichtVorhandenException when attempting to deposit money into a non-existent account.
     * It uses the assertThrows assertion to verify that calling the geldEinzahlen method with a non-existent account number (1000000000L) and a deposit of 100 throws a KontonummerNichtVorhandenException.
     */
    @Test
    void einzahlenFalseKonto() {
        Assertions.assertThrows(KontonummerNichtVorhandenException.class, () -> b.geldEinzahlen(1000000000L, 100));
    }

    /**
     * This test case verifies that the einzahlenFalseBetrag method throws an IllegalArgumentException when attempting to deposit an invalid amount of money into an account.
     * It uses the assertThrows assertion to verify that calling the geldEinzahlen method with a valid account number (DEFAULT_KONTO) and an invalid deposit amount (0 and -100) throws an IllegalArgumentException.
     */
    @Test
    void einzahlenFalseBetrag() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.geldEinzahlen(DEFAULT_KONTO, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.geldEinzahlen(DEFAULT_KONTO, -100));
    }

    /**
     * This test case verifies that the geldAbhebenTest method correctly deducts the specified amount of money from an account.
     * It uses the geldAbheben method to withdraw 100 units of currency from the account with the specified account number (DEFAULT_KONTO).
     * It then uses the assertEquals assertion to verify that the account balance is updated correctly to -100.
     *
     * @throws KontonummerNichtVorhandenException if the account number is not found in the system
     * @throws GesperrtException                  if the account is locked and cannot perform any transactions
     */
    @Test
    void geldAbhebenTest() throws KontonummerNichtVorhandenException, GesperrtException {
        b.geldAbheben(DEFAULT_KONTO, 100);
        Assertions.assertEquals(-100, b.getKontostand(DEFAULT_KONTO));
    }

    /**
     * This test case verifies that the geldAbhebenFalseKonto method correctly throws a KontonummerNichtVorhandenException
     * when attempting to withdraw money from a nonexistent account.
     * It uses the geldAbheben method to attempt to withdraw 100 units of currency from an account with the specified
     * non-existent account number (1000000000L).
     * It uses the assertThrows assertion to verify that a KontonummerNichtVorhandenException is thrown.
     */
    @Test
    void geldAbhebenFalseKonto() {
        Assertions.assertThrows(KontonummerNichtVorhandenException.class, () -> b.geldAbheben(1000000000L, 100));
    }

    /**
     * This test case verifies that the geldAbhebenFalseBetrag method correctly throws an IllegalArgumentException
     * when attempting to withdraw an invalid amount of money from an account.
     * <p>
     * It uses the geldAbheben method to attempt to withdraw 0 units of currency from the account with the specified
     * default account number (DEFAULT_KONTO). It then uses the assertThrows assertion to verify that an
     * IllegalArgumentException is thrown.
     * <p>
     * It also uses the geldAbheben method to attempt to withdraw -100 units of currency from the account with the
     * specified default account number (DEFAULT_KONTO). Again, the assertThrows assertion is used to verify that an
     * IllegalArgumentException is thrown.
     */
    @Test
    void geldAbhebenFalseBetrag() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.geldAbheben(DEFAULT_KONTO, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.geldAbheben(DEFAULT_KONTO, -100));
    }

    /**
     * This test case verifies the behavior of the getKontostand method by checking that it correctly returns the account balance
     * for the account with the specified default account number (DEFAULT_KONTO).
     * <p>
     * It uses the getKontostand method to retrieve the account balance for the account with the default account number.
     * The retrieved balance is then compared to an expected value of 0 using the assertEquals assertion. If the retrieved balance
     * matches the expected value, the test case passes.
     *
     * @throws KontonummerNichtVorhandenException if the specified account number (DEFAULT_KONTO) does not exist
     */
    @Test
    void getKontostandTest() throws KontonummerNichtVorhandenException {
        Assertions.assertEquals(0, b.getKontostand(DEFAULT_KONTO));
    }

    /**
     * This test case verifies the behavior of the getKontostand method when an invalid account number is provided.
     * The test case expects the method to throw a KontonummerNichtVorhandenException.
     * <p>
     * It uses the assertThrows method to assert that a KontonummerNichtVorhandenException is thrown when calling
     * getKontostand with an invalid account number (1000000000L).
     * <p>
     * If the method throws the expected exception, the test case passes. Otherwise, it fails.
     */
    @Test
    void getKontostandFalseNummer() {
        Assertions.assertThrows(KontonummerNichtVorhandenException.class, () -> b.getKontostand(1000000000L));
    }

    /**
     * This test case verifies the behavior of the kontoLoeschen method when a valid account number is provided.
     * The test case expects the method to return true, indicating that the account was successfully deleted.
     * <p>
     * It uses the assertTrue method to assert that the kontoLoeschen method returns true when called with
     * a valid account number (DEFAULT_KONTO).
     * <p>
     * If the method returns true, the test case passes. Otherwise, it fails.
     */
    @Test
    void kontoLoeschenTest() {
        Assertions.assertTrue(b.kontoLoeschen(DEFAULT_KONTO));
    }

    /**
     * This test case verifies the behavior of the kontoLoeschen method when an invalid account number is provided.
     * The test case expects the method to return false, indicating that the account was not deleted.
     * <p>
     * It uses the assertFalse method to assert that the kontoLoeschen method returns false when called with
     * an invalid account number (1000000000L).
     * <p>
     * If the method returns false, the test case passes. Otherwise, it fails.
     */
    @Test
    void kontoLoeschenFalseTest() {
        Assertions.assertFalse(b.kontoLoeschen(1000000000L));
    }

    private void setUpUeberweisenGiro() throws KontonummerNichtVorhandenException {
        k1 = new Kunde("Kunde1", "Kunde1", "Kunde1", LocalDate.parse("2000-01-01"));
        k2 = new Kunde("Kunde2", "Kunde2", "Kunde2", LocalDate.parse("2000-01-01"));

        k1No = b.girokontoErstellen(k1);
        k2No = b.girokontoErstellen(k2);

        b.geldEinzahlen(k1No, 1000);
        b.geldEinzahlen(k2No, 1000);
    }

    private void setUpUeberweisenSpar() throws KontonummerNichtVorhandenException {
        k1 = new Kunde("Kunde1", "Kunde1", "Kunde1", LocalDate.parse("2000-01-01"));
        k2 = new Kunde("Kunde2", "Kunde2", "Kunde2", LocalDate.parse("2000-01-01"));

        k1No = b.sparbuchErstellen(k1);
        k2No = b.sparbuchErstellen(k2);

        b.geldEinzahlen(k1No, 1000);
        b.geldEinzahlen(k2No, 1000);
    }

    @Test
    void geldUeberweisenGiroTest() throws KontonummerNichtVorhandenException, GesperrtException {
        setUpUeberweisenGiro();
        b.geldUeberweisen(k1No, k2No, 500, "");
        Assertions.assertEquals(500, b.getKontostand(k1No));
        Assertions.assertEquals(1500, b.getKontostand(k2No));
    }

    @Test
    void geldUeberweisenGiroZuViel() throws KontonummerNichtVorhandenException, GesperrtException {
        setUpUeberweisenGiro();
        Assertions.assertFalse(b.geldUeberweisen(k1No, k2No, 5000, ""));
    }

    @Test
    void geldUeberweisenSparTest() throws KontonummerNichtVorhandenException, GesperrtException {
        setUpUeberweisenSpar();
        Assertions.assertFalse(b.geldUeberweisen(k1No, k2No, 500, ""));

    }

    @Test
    void geldUeberweisenSparZuViel() throws KontonummerNichtVorhandenException, GesperrtException {
        setUpUeberweisenSpar();
        Assertions.assertFalse(b.geldUeberweisen(k1No, k2No, 5000, ""));
    }

    @Test
    void geldUeberweisenNonKontoTest() {
        Assertions.assertFalse(b.geldUeberweisen(1000000000L, DEFAULT_KONTO, 100, ""));
        Assertions.assertFalse(b.geldUeberweisen(DEFAULT_KONTO, 1000000000L, 100, ""));
    }

    @Test
    void geldUeberweisenKontoNegativeBetragTest() throws KontonummerNichtVorhandenException {
        setUpUeberweisenGiro();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> b.geldUeberweisen(k1No, k2No, -100, ""));

    }
}
