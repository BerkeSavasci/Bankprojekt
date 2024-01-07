package bankprojekt.verarbeitung;

/**
 * The Kontofabrik class is an abstract class representing a factory for creating bank account objects.
 */
public abstract class Kontofabrik {
    /**
     * Creates a bank account object.
     *
     * @param inhaber The owner of the account.
     * @param kontoNr The account number to assign to the account.
     * @return The created Konto object.
     */
    public abstract Konto erstellenKonto(Kunde inhaber, long kontoNr);
}
