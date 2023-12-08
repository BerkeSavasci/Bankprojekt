package bankprojekt.verarbeitung;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author berkesavasci
 * 08.12.23
 * project: Uebung_3_new
 */
public class Aktie {
    private String name;
    private int wertpapierkennummer;
    private double kurs;
    private final int UPPER_BOUND = 3;
    private final int LOWER_BOUND = -3;
    private static ScheduledExecutorService executorService;

    public Aktie(String name, int wertpapierkennummer, double kurs) {
        this.name = name;
        this.wertpapierkennummer = wertpapierkennummer;
        this.kurs = kurs;

        executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(this::randKursGenerator, 0, 1, TimeUnit.SECONDS);
    }

    public int getWertpapierkennummer() {
        return wertpapierkennummer;
    }

    private void randKursGenerator() {
        Random r = new Random();
        double zufallProzent = LOWER_BOUND + (UPPER_BOUND - LOWER_BOUND) * r.nextDouble();

        this.kurs += this.kurs * zufallProzent / 100;
    }

    public double getKurs() {
        return kurs;
    }
}
