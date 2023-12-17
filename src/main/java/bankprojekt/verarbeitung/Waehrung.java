package bankprojekt.verarbeitung;

import org.decimal4j.util.DoubleRounder;

import java.io.Serializable;
import java.math.RoundingMode;

/**
 * The Waehrung class represents different currencies with their corresponding exchange rates to Euro.
 * It provides methods to convert amounts between Euro and a specified currency.
 */
public enum Waehrung implements Serializable {
    /**
     * Euro
     */
    EUR(1),
    /**
     * Bulgarische Lew
     */
    BGN(1.9558),
    /**
     * Dänische Krone
     */
    DKK(7.4604),
    /**
     * Mazedonischer Denar
     */
    MKD(61.62);

    /**
     * The umrechnungskurs variable represents the conversion rate used for currency conversion.
     * This variable is a private final double, which means it cannot be modified once it is assigned a value.
     * The umrechnungskurs value should be a decimal number representing the conversion rate between two currencies.
     */
    private final double umrechnungskurs;

    /**
     * Retrieves the conversion rate.
     *
     * @return The conversion rate as a double value.
     */
    public double getUmrechnungskurs() {
        return this.umrechnungskurs;
    }

    /**
     * Creates a new currency object with the specified exchange rate in Euros.
     *
     * @param exchangeRate the exchange rate in Euros
     */
    Waehrung(double exchangeRate) {
        this.umrechnungskurs = exchangeRate;
    }

    /**
     * Converts the given amount from Euro to a specified currency using the conversion rate.
     *
     * @param betrag the amount in Euro to be converted
     * @return the converted amount in the specified currency
     */
    public double euroInWaehrungUmrechnen(double betrag) throws IllegalArgumentException {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }
        return DoubleRounder.round(betrag * this.umrechnungskurs, 2, RoundingMode.DOWN);
    }

    /**
     * Converts the given amount from a specified currency to Euro using the conversion rate.
     *
     * @param betrag the amount in the specified currency to be converted
     * @return the converted amount in Euro
     */
    public double waehrungInEuroUmrechnen(double betrag) throws IllegalArgumentException {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }
        return DoubleRounder.round(betrag / this.umrechnungskurs, 2, RoundingMode.DOWN);
    }
}
