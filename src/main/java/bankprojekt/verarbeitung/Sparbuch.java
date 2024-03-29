package bankprojekt.verarbeitung;

import java.time.LocalDate;

/**
 * ein Sparbuch, d.h. ein Konto, das nur recht eingeschränkt genutzt
 * werden kann. Insbesondere darf man monatlich nur höchstens 2000€
 * abheben, wobei der Kontostand nie unter 0,50€ fallen darf.
 *
 * @author Doro
 */
public class Sparbuch extends Konto {
    /**
     * Zinssatz, mit dem das Sparbuch verzinst wird. 0,03 entspricht 3%
     */
    private double zinssatz;

    /**
     * Monatlich erlaubter Gesamtbetrag für Abhebungen
     */
    public static final double ABHEBESUMME = 2000;

    /**
     * Betrag, der im aktuellen Monat bereits abgehoben wurde
     */
    private double bereitsAbgehoben = 0;

    /**
     * Monat und Jahr der letzten Abhebung
     */
    private LocalDate zeitpunkt = LocalDate.now();

    /**
     * ein Standard-Sparbuch
     */
    public Sparbuch() {
        zinssatz = 0.03;
    }

    /**
     * ein Standard-Sparbuch, das inhaber gehört und die angegebene Kontonummer hat
     *
     * @param inhaber          der Kontoinhaber
     * @param kontonummer      die Wunsch-Kontonummer
     * @param aktienStueckzahl die Anzahl von Aktien
     * @throws IllegalArgumentException wenn inhaber null ist
     */
    public Sparbuch(Kunde inhaber, long kontonummer, int aktienStueckzahl) {
        super(inhaber, kontonummer, aktienStueckzahl);
        zinssatz = 0.03;
    }

    /**
     * Aktualisiert die Währung des Sparbuchs auf die angegebene neue Währung.
     *
     * @param neu die neue Währung, auf die das Sparbuch umgestellt werden soll
     */
    @Override
    public void waehrungswechsel(Waehrung neu) {
        double kontoInEuro = getAktuelleWaehrung().waehrungInEuroUmrechnen(getKontostand());
        double dispoInEuro = getAktuelleWaehrung().waehrungInEuroUmrechnen(bereitsAbgehoben);

        setKontostand(neu.euroInWaehrungUmrechnen(kontoInEuro));
        bereitsAbgehoben = neu.euroInWaehrungUmrechnen(dispoInEuro);

        super.waehrungswechsel(neu);
    }

    @Override
    protected boolean validateBetrag(double betrag) {
        LocalDate heute = LocalDate.now();
        if (heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear()) {
            this.bereitsAbgehoben = 0;
        }
        return getKontostand() - betrag >= this.getAktuelleWaehrung().euroInWaehrungUmrechnen(0.50) &&
                bereitsAbgehoben + betrag <= getAktuelleWaehrung().euroInWaehrungUmrechnen(Sparbuch.ABHEBESUMME);
    }
    protected boolean executeAbheben(double betrag) {
        setKontostand(getKontostand() - betrag);
        this.bereitsAbgehoben += betrag;
        this.zeitpunkt = LocalDate.now();
        return true;
    }
    @Override
    public boolean abheben(double betrag, Waehrung w) throws GesperrtException {
        if (isGesperrt())
            throw new GesperrtException(getKontonummer());

        double betragInEuro = w.waehrungInEuroUmrechnen(betrag);
        double betragInKontoWaehrung = getAktuelleWaehrung().euroInWaehrungUmrechnen(betragInEuro);

        return abheben(betragInKontoWaehrung);
    }

    @Override
    public String toString() {
        return "-- SPARBUCH --" + System.lineSeparator() +
                super.toString()
                + "Zinssatz: " + this.zinssatz * 100 + "%" + System.lineSeparator();
    }
}
