package bankprojekt.verarbeitung;

import com.google.common.primitives.Doubles;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * stellt ein allgemeines Bank-Konto dar
 */
public abstract class Konto implements Comparable<Konto>, Serializable {
    /**
     * der Kontoinhaber
     */
    private Kunde inhaber;

    /**
     * die Kontonummer
     */
    private final long nummer;

    /**
     * der aktuelle Kontostand
     */
    private final transient ReadOnlyDoubleWrapper kontostand = new ReadOnlyDoubleWrapper();

    /**
     * der aktuelle gesperrt-Zustand
     */
    private final transient ReadOnlyBooleanWrapper gesperrt = new ReadOnlyBooleanWrapper();

    /**
     * Zustand der Bilanz.
     * True, wenn der Kontostand im Plus ist, false, wenn er im Minus ist.
     */
    private final transient ReadOnlyBooleanWrapper kontoImPlus = new ReadOnlyBooleanWrapper();

    /**
     * in welcher Währung das Konto geführt wird
     */
    private Waehrung w = Waehrung.EUR;

    /**
     * The variable {@code aktienStückzahl} represents the number of shares.
     */
    private int aktienStueckzahl;

    /**
     * Represents a lock that can be used for synchronizing access to a specific section of code.
     */
    private final transient Lock lock = new ReentrantLock();


    /**
     * This variable represents a map that stores the stock portfolio of a user.
     */
    private Map<Integer, AbstractMap.SimpleEntry<Aktie, Integer>> depotMap;
    /**
     * This variable represents a private static ScheduledExecutorService object that is used to schedule and execute tasks
     * periodically or at a specific time in the future.
     */
    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    /**
     * A private final PropertyChangeSupport variable that is used to support property change events in this class.
     */
    private final PropertyChangeSupport prop = new PropertyChangeSupport(this);

    /**
     * setzt alle Eigenschaften des Kontos auf Standardwerte
     */
    protected Konto() {
        this(Kunde.MUSTERMANN, 1234567, 0);
    }

    /**
     * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
     * der anfängliche Kontostand wird auf 0 gesetzt.
     *
     * @param inhaber          der Inhaber
     * @param kontonummer      die gewünschte Kontonummer
     * @param aktienStueckzahl Anzahl der Aktien
     * @throws IllegalArgumentException wenn der inhaber null ist
     */
    protected Konto(Kunde inhaber, long kontonummer, int aktienStueckzahl) {
        this.aktienStueckzahl = aktienStueckzahl;
        if (inhaber == null)
            throw new IllegalArgumentException("Inhaber darf nicht null sein!");
        this.inhaber = inhaber;
        this.nummer = kontonummer;
        this.kontostand.set(0.0);
        this.gesperrt.set(false);
        depotMap = new HashMap<>();
    }

    /**
     * Adds a property change listener to the account.
     *
     * @param propertyChangeListener the listener to be added
     */
    public void anmelden(PropertyChangeListener propertyChangeListener) {
        prop.addPropertyChangeListener(propertyChangeListener);
    }

    /**
     * Removes the property change listener from the account.
     *
     * @param propertyChangeListener the listener to be removed
     */
    public void abmelden(PropertyChangeListener propertyChangeListener) {
        prop.removePropertyChangeListener(propertyChangeListener);
    }

    /**
     * Führt einen Kaufauftrag für eine bestimmte Aktie aus, indem der Kontostand des Kontos überprüft wird
     * und entsprechend Aktien gekauft werden. Gibt den Gesamtpreis der gekauften Aktien zurück.
     *
     * @param a            die Aktie, die gekauft werden soll
     * @param anzahl       die Anzahl der zu kaufenden Aktien
     * @param hoechstpreis der Höchstpreis pro Aktie, zu dem der Kauf erfolgt
     * @return Gesamtpreis der gekauften Aktien
     */
    public Future<Double> kaufauftrag(Aktie a, int anzahl, double hoechstpreis) {
        return executorService.submit(() -> {
            while (true) {
                double kurs;
                lock.lock();
                try {
                    kurs = a.getKurs();
                } finally {
                    lock.unlock();
                }

                if (kurs <= hoechstpreis) {
                    double kontostandTemp;

                    lock.lock();
                    try {
                        kontostandTemp = getKontostand();
                    } finally {
                        lock.unlock();
                    }

                    if (kontostandTemp >= kurs * anzahl) {
                        lock.lock();
                        try {
                            this.setKontostand(kontostandTemp - anzahl * kurs);
                            setAktienStueckzahl(getAktienStueckzahl() + anzahl);
                            depotMap.put(a.getWertpapierkennummer(), new AbstractMap.SimpleEntry<>(a, anzahl));
                        } finally {
                            lock.unlock();
                        }
                        return anzahl * kurs;
                    } else {
                        return 0.0;
                    }
                }
            }
        });
    }

    /**
     * Führt einen Verkaufsauftrag für eine bestimmte Aktie aus, indem der Kontostand des Kontos überprüft wird
     * und entsprechend Aktien verkauft werden. Gibt den Gesamtpreis der verkauften Aktien zurück.
     *
     * @param wkn          die Wertpapierkennnummer der Aktie, die verkauft werden soll
     * @param minimalpreis der Minimalpreis pro Aktie, zu dem der Verkauf erfolgt
     * @return Gesamtpreis der verkauften Aktien
     */
    public Future<Double> verkaufauftrag(String wkn, double minimalpreis) {
        return executorService.submit(() -> {
            Aktie a;
            AbstractMap.SimpleEntry<Aktie, Integer> aktieIntegerSimpleEntry;
            lock.lock();
            try {
                a = getAktieMitWkn(wkn);
                if (a == null) {
                    executorService.shutdownNow();
                    return 0.0;
                }
                aktieIntegerSimpleEntry = depotMap.get(Integer.parseInt(wkn));

            } finally {
                lock.unlock();
            }

            while (true) {
                double kurs;
                lock.lock();
                try {
                    kurs = a.getKurs();
                } finally {
                    lock.unlock();
                }
                if (kurs >= minimalpreis) {
                    int aktienStueckzahlTemp;
                    lock.lock();
                    try {
                        aktienStueckzahlTemp = getNumberOfAktien(wkn);
                        this.setKontostand(this.getKontostand() + kurs * aktienStueckzahlTemp);
                        setAktienStueckzahl(getAktienStueckzahl() - aktieIntegerSimpleEntry.getValue());
                        depotMap.remove(Integer.parseInt(wkn));
                    } finally {
                        lock.unlock();
                    }
                    return kurs * aktienStueckzahlTemp;
                }
            }
        });
    }

    /**
     * Gibt eine Aktie mit der angegebenen Wertpapierkennnummer zurück.
     *
     * @param wkn die Wertpapierkennnummer der gesuchten Aktie
     * @return die Aktie mit der angegebenen Wertpapierkennnummer oder null, wenn keine Aktie gefunden wurde
     */
    private Aktie getAktieMitWkn(String wkn) {
        AbstractMap.SimpleEntry<Aktie, Integer> aktieIntegerSimpleEntry = depotMap.get(Integer.parseInt(wkn));
        if (aktieIntegerSimpleEntry == null)
            return null;

        return aktieIntegerSimpleEntry.getKey();
    }

    /**
     * Gibt die Anzahl von Aktien mit der angegebenen Wertpapierkennnummer zurück.
     *
     * @param wkn die Wertpapierkennnummer der gesuchten Aktie
     * @return die Anzahl von Aktien mit der angegebenen Wertpapierkennnummer oder -1, wenn keine Aktie gefunden wurde
     */
    private int getNumberOfAktien(String wkn) {
        AbstractMap.SimpleEntry<Aktie, Integer> aktieIntegerSimpleEntry = depotMap.get(Integer.parseInt(wkn));
        if (aktieIntegerSimpleEntry == null)
            return -1;

        return aktieIntegerSimpleEntry.getValue();
    }


    /**
     * Sie zahlt den in der Währung w angegebenen Betrag ein.
     *
     * @param betrag Geld einzuzahlen
     * @param w      Währung des Geldes
     */
    public void einzahlen(double betrag, Waehrung w) {
        if (betrag < 0 || !Doubles.isFinite(betrag)) {
            throw new IllegalArgumentException("Falscher Betrag");
        }
        double betragInEuro = w.waehrungInEuroUmrechnen(betrag);
        double betragInKontoWaehrung = getAktuelleWaehrung().euroInWaehrungUmrechnen(betragInEuro);

        einzahlen(betragInKontoWaehrung);
    }

    /**
     * Erhöht den Kontostand um den eingezahlten Betrag.
     *
     * @param betrag double
     * @throws IllegalArgumentException wenn der betrag negativ ist
     */
    public void einzahlen(double betrag) {
        if (betrag < 0 || !Doubles.isFinite(betrag)) {
            throw new IllegalArgumentException("Falscher Betrag");
        }
        setKontostand(getKontostand() + betrag);
    }

    /**
     * Versucht den angegebenen Betrag in der angegebenen Währung vom Konto abzuheben.
     *
     * @param betrag der abzuhebende Betrag
     * @param w      die Währung des Betrags
     * @return true, wenn die Abhebung erfolgreich war, sonst false
     * @throws GesperrtException wenn das Konto gesperrt ist
     */
    public boolean abheben(double betrag, Waehrung w) throws GesperrtException {
        return false;
    }

    /**
     * Versucht den angegebenen Betrag vom Konto abzuheben.
     *
     * @param betrag der abzuhebende Betrag
     * @return true, wenn die Abhebung erfolgreich war, sonst false
     * @throws GesperrtException        wenn das Konto gesperrt ist
     * @throws IllegalArgumentException wenn der Betrag ungültig ist
     */
    public final boolean abheben(double betrag) throws GesperrtException {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }
        if (this.isGesperrt())
            throw new GesperrtException(this.getKontonummer());
        if (validateBetrag(betrag)) {
            return executeAbheben(betrag);
        } else
            return false;
    }

    /**
     * Method to validate a given betrag.
     *
     * @param betrag The betrag to be validated.
     * @return Returns true if the betrag is valid, false otherwise.
     */
    protected abstract boolean validateBetrag(double betrag);

    /**
     * Führt einen Abhebevorgang mit dem angegebenen Betrag durch, in Abhängigkeit vom aktuellen Kontostand.
     *
     * @param betrag der abzuhebende Betrag
     * @return true, wenn die Abhebung erfolgreich war, sonst false
     */
    protected boolean executeAbheben(double betrag) {
        setKontostand(getKontostand() - betrag);
        return true;
    }

    /**
     * Gibt die Anzahl der Aktien zurück.
     *
     * @return die Anzahl der Aktien
     */
    public int getAktienStueckzahl() {
        return aktienStueckzahl;
    }

    /**
     * Sets the number of shares for the object, fires a property change.
     *
     * @param aktienStueckzahl the new number of shares
     */
    public void setAktienStueckzahl(int aktienStueckzahl) {
        int oldAktienStueckzZahl = this.aktienStueckzahl;
        this.aktienStueckzahl = aktienStueckzahl;
        prop.firePropertyChange("AktienAnzahl", oldAktienStueckzZahl, getAktienStueckzahl());
    }


    /**
     * Sie liefert die Währung zurück, in der das Konto aktuell geführt wird.
     *
     * @return Währung des Kontos
     */
    public Waehrung getAktuelleWaehrung() {
        return this.w;
    }

    /**
     * Changes the currency to a new currency, fires a property change.
     *
     * @param neu The new currency to be set.
     */
    public void waehrungswechsel(Waehrung neu) {
        Waehrung old = this.w;
        this.w = neu;
        prop.firePropertyChange("waehrung", old, getAktuelleWaehrung());
    }

    /**
     * Sets the kontostand (account balance) to the specified value, fires a property change.
     *
     * @param kontostand the new kontostand to be set
     */
    protected void setKontostand(double kontostand) {
        double oldKontoStand = this.kontostand.get();
        this.kontostand.set(kontostand);
        kontoImPlus.set(this.kontostand.get() >= 0);
        prop.firePropertyChange("kontostand", oldKontoStand, getKontostand());
    }


    /**
     * liefert den Kontoinhaber zurück
     *
     * @return der Inhaber
     */
    public Kunde getInhaber() {
        return this.inhaber;
    }

    /**
     * setzt den Kontoinhaber
     *
     * @param kinh neuer Kontoinhaber
     * @throws GesperrtException        wenn das Konto gesperrt ist
     * @throws IllegalArgumentException wenn kinh null ist
     */
    public final void setInhaber(Kunde kinh) throws GesperrtException {
        if (kinh == null)
            throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
        if (this.gesperrt.get())
            throw new GesperrtException(this.nummer);
        this.inhaber = kinh;
    }

    /**
     * Retrieves the property describing the current balance.
     *
     * @return ReadOnlyDoubleProperty representing current balance.
     */
    public ReadOnlyDoubleProperty kontostandProperty() {
        return kontostand.getReadOnlyProperty();
    }

    /**
     * Retrieves the property describing whether the account is locked.
     * @return ReadOnlyBooleanProperty representing the locked status.
     */
    public ReadOnlyBooleanProperty gesperrtProperty() {
        return gesperrt.getReadOnlyProperty();
    }

    /**
     * Gibt zurück, ob der Kontostand im Plus ist.
     *
     * @return ReadOnlyBooleanProperty repräsentiert, ob der Kontostand im Plus ist.
     */
    public ReadOnlyBooleanProperty kontostandImPlusProperty() {
        return kontoImPlus.getReadOnlyProperty();
    }

    /**
     * liefert den aktuellen Kontostand
     *
     * @return Kontostand
     */
    public double getKontostand() {
        return kontostand.get();
    }

    /**
     * liefert die Kontonummer zurück
     *
     * @return Kontonummer
     */
    public long getKontonummer() {
        return nummer;
    }

    /**
     * liefert zurück, ob das Konto gesperrt ist oder nicht
     *
     * @return true, wenn das Konto gesperrt ist
     */
    public final boolean isGesperrt() {
        return gesperrt.get();
    }


    /**
     * Sets the "gesperrt" flag to true and fires a property change event.
     */
    public void sperren() {
        this.gesperrt.set(true);
        prop.firePropertyChange("gesperrt", false, true);
    }

    /**
     * Sets the "gesperrt" flag to false and fires a property change event.
     */
    public final void entsperren() {
        this.gesperrt.set(false);
        prop.firePropertyChange("entsperren", true, false);
    }

    /**
     * liefert die ordentlich formatierte Kontonummer
     *
     * @return auf 10 Stellen formatierte Kontonummer
     */
    public String getKontonummerFormatiert() {
        return String.format("%10d", this.nummer);
    }

    /**
     * liefert den ordentlich formatierten Kontostand
     *
     * @return formatierter Kontostand mit 2 Nachkommastellen und Währungssymbol
     */
    public String getKontostandFormatiert() {
        return String.format("%10.2f %s", this.getKontostand(), this.w);
    }

    /**
     * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
     *
     * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
     */
    public final String getGesperrtText() {
        if (this.gesperrt.get()) {
            return "GESPERRT";
        } else {
            return "";
        }
    }

    /**
     * Vergleich von this mit other; Zwei Konten gelten als gleich,
     * wen sie die gleiche Kontonummer haben
     *
     * @param other das Vergleichskonto
     * @return true, wenn beide Konten die gleiche Nummer haben
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        if (this.nummer == ((Konto) other).nummer)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
    }

    @Override
    public int compareTo(Konto other) {
        if (other.getKontonummer() > this.getKontonummer())
            return -1;
        if (other.getKontonummer() < this.getKontonummer())
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        String ausgabe;
        ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
                + System.lineSeparator();
        ausgabe += "Inhaber: " + this.inhaber;
        ausgabe += "Aktueller Kontostand: " + getKontostandFormatiert() + " ";
        ausgabe += this.getGesperrtText() + System.lineSeparator();
        return ausgabe;
    }
}
