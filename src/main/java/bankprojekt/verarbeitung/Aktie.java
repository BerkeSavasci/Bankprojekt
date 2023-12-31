package bankprojekt.verarbeitung;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A class to generate Stocks
 */
public class Aktie {
    /**
     * Represents the name of an Aktienobject.
     */
    private String name;
    /**
     * The wertpapierkennummer represents the unique identifier for a security.
     */
    private int wertpapierkennummer;
    /**
     * This variable represents the exchange rate of a currency.
     */
    private double kurs;
    private final int UPPER_BOUND = 3;
    private final int LOWER_BOUND = -3;
    private static ScheduledExecutorService executorService;

    /**
     * Creates a new instance of Aktie with the specified name, wertpapierkennummer, and kurs.
     * The Aktie object represents a stock with the given attributes.
     *
     * @param name                 the name of the stock
     * @param wertpapierkennummer  the unique identifier for the stock
     * @param kurs                 the initial value of the stock
     */
    public Aktie(String name, int wertpapierkennummer, double kurs) {
        this.name = name;
        this.wertpapierkennummer = wertpapierkennummer;
        this.kurs = kurs;

        executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(this::randKursGenerator, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Generates a random price for the Aktie object.
     * The generated price is in percentage, which is added to the current price.
     * The generated price is a random value between the lower bound and upper boound.
     *
     */
    private void randKursGenerator() {
        Random r = new Random();
        double zufallProzent = LOWER_BOUND + (UPPER_BOUND - LOWER_BOUND) * r.nextDouble();

        this.kurs += this.kurs * zufallProzent / 100;
    }

    /**
     * Returns the current price of the Aktie object.
     *
     * @return The current price of the Aktie object.
     */
    public double getKurs() {
        return kurs;
    }

    /**
     * Returns the name of the Aktie object.
     *
     * @return The name of the Aktie object.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unique identifier for this Aktie object.
     *
     * @return the wertpapierkennummer of the Aktie object
     */
    public int getWertpapierkennummer() {
        return wertpapierkennummer;
    }

    /**
     * Sets the name of the Aktie object.
     *
     * @param name The new name for the Aktie object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the Wertpapierkennummer of the object.
     *
     * @param wertpapierkennummer The new Wertpapierkennummer for the object.
     */
    public void setWertpapierkennummer(int wertpapierkennummer) {
        this.wertpapierkennummer = wertpapierkennummer;
    }

    /**
     * Sets the kurs of the object.
     *
     * @param kurs The new kurs for the object.
     */
    public void setKurs(double kurs) {
        this.kurs = kurs;
    }

}
