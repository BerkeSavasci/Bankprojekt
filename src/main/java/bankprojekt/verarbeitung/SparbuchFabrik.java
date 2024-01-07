package bankprojekt.verarbeitung;

/**
 * @author berkesavasci
 * 03.01.24
 * project: Uebung_3_new
 */
public class SparbuchFabrik extends Kontofabrik {
    @Override
    public Konto erstellenKonto(Kunde inhaber, long kontoNr) {
        return new Sparbuch(inhaber, kontoNr, 0);
    }
}
