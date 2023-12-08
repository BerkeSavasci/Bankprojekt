package bankprojekt.verwaltung;

/**
 * An exception thrown when trying to access a non-existent account.
 */

public class KontonummerNichtVorhandenException extends Exception {
    /**
     * Constructs a new KontonummerNichtVorhandenException with the specified Kontonummer.
     *
     * The KontonummerNichtVorhandenException is thrown when trying to access an account that does not exist.
     *
     * @param kontonummer the Kontonummer that does not exist
     */
    public KontonummerNichtVorhandenException(long kontonummer)
    {
        super("Zugriff auf eine nicht existierende Konto" + kontonummer);
    }

}
