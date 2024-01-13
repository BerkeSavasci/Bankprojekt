package bankprojekt.verarbeitung;

/**
 * Ein Girokonto, d.h. ein Konto mit einem Dispo und der Fähigkeit,
 * Überweisungen zu senden und zu empfangen.
 * Grundsätzlich sind Überweisungen und Abhebungen möglich bis
 * zu einem Kontostand von -this.dispo
 *
 * @author Doro
 */
public class Girokonto extends Konto implements Ueberweisungsfaehig {
    /**
     * Wert, bis zu dem das Konto überzogen werden darf.
     */
    private double dispo;

    /**
     * erzeugt ein leeres, nicht gesperrtes Standard-Girokonto
     * von Max MUSTERMANN
     */
    public Girokonto() {
        super(Kunde.MUSTERMANN, 99887766, 0);
        this.dispo = 500;
    }

    /**
     * erzeugt ein Girokonto mit den angegebenen Werten
     *
     * @param inhaber          Kontoinhaber
     * @param nummer           Kontonummer
     * @param dispo            Dispo
     * @param aktienStueckzahl Aktien Stückzahl
     * @throws IllegalArgumentException wenn der inhaber null ist oder der angegebene dispo negativ bzw. NaN ist
     */
    public Girokonto(Kunde inhaber, long nummer, double dispo, int aktienStueckzahl) {
        super(inhaber, nummer, aktienStueckzahl);
        if (dispo < 0 || Double.isNaN(dispo) || Double.isInfinite(dispo))
            throw new IllegalArgumentException("Der Dispo ist nicht gültig!");
        this.dispo = dispo;
    }


    @Override
    public boolean ueberweisungAbsenden(double betrag,
                                        String empfaenger, long nachKontonr,
                                        long nachBlz, String verwendungszweck)
            throws GesperrtException {
        if (this.isGesperrt())
            throw new GesperrtException(this.getKontonummer());
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag) || empfaenger == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        if (getKontostand() - betrag >= -dispo) {
            setKontostand(getKontostand() - betrag);
            return true;
        } else
            return false;
    }

    @Override
    public void ueberweisungEmpfangen(double betrag, String vonName, long vonKontonr, long vonBlz, String verwendungszweck) {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag) || vonName == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        setKontostand(getKontostand() + betrag);
    }

    @Override
    protected boolean validateBetrag(double betrag) {
        return getKontostand() - betrag >= -dispo;
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
    public void waehrungswechsel(Waehrung neu) {
        double kontoInEuro = getAktuelleWaehrung().waehrungInEuroUmrechnen(getKontostand());
        double dispoInEuro = getAktuelleWaehrung().waehrungInEuroUmrechnen(getDispo());

        setKontostand(neu.euroInWaehrungUmrechnen(kontoInEuro));
        setDispo(neu.euroInWaehrungUmrechnen(dispoInEuro));

        super.waehrungswechsel(neu);
    }

    /**
     * liefert den Dispo
     *
     * @return Dispo von this
     */
    public double getDispo() {
        return dispo;
    }

    /**
     * setzt den Dispo neu
     *
     * @param dispo muss größer sein als 0
     * @throws IllegalArgumentException wenn dispo negativ bzw. NaN ist
     */
    public void setDispo(double dispo) {
        if (dispo < 0 || Double.isNaN(dispo) || Double.isInfinite(dispo))
            throw new IllegalArgumentException("Der Dispo ist nicht gültig!");
        this.dispo = dispo;
    }

    @Override
    public String toString() {
        return "-- GIROKONTO --" + System.lineSeparator() +
                super.toString()
                + "Dispo: " + this.dispo + " " + getAktuelleWaehrung() + System.lineSeparator();
    }

}
