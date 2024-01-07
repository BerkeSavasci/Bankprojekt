package bankprojekt.verarbeitung;

/**
 * This class extends the abstract class Kontofabrik and represents a factory for creating Girokonto objects.
 */
public class GirokontoFabrik extends Kontofabrik{
    /**
     * The DEFAULT_DISPO variable represents the default value for the Disposition
     * The initial value is 500.
     */
    private static final int DEFAULT_DISPO = 500;

    @Override
    public Konto erstellenKonto(Kunde inhaber, long kontoNr) {
        return new Girokonto(inhaber, kontoNr, DEFAULT_DISPO,0 );
    }
}
