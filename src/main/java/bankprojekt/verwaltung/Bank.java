package bankprojekt.verwaltung;


import bankprojekt.verarbeitung.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a bank and manages its accounts.
 */
public class Bank implements Cloneable, Serializable {

    /**
     * The default value for the dispo variable.
     */
    private static final int DEFAULT_DISPO = 500;
    /**
     * die Bankleitzahl von der Bank
     */
    private final long bankleitzahl;
    private static long currKontoNum = 10000000L;
    private static final long MAX_KONTO_NUM = 99999999L;
    /**
     * Ein Hashmap um die Konten zu speichern.
     * Kontonummer als Key, Konto als Value
     */
    private final HashMap<Long, Konto> bankKonten = new HashMap<>();

    @Override
    public Object clone() {
        byte[] arr;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(bos)) {

            os.writeObject(this);
            os.flush();
            arr = bos.toByteArray();

            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ObjectInputStream is = new ObjectInputStream(bis);

            Object copy = is.readObject();
            is.close();
            return copy;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Die Methode `pleitegeierSperren` sperrt alle Konten, deren Kontostand im Minus ist.
     */
    public void pleitegeierSperren() {
        bankKonten.values().stream().filter(konto -> konto.getKontostand() < 0).forEach(Konto::sperren);
    }

    /**
     * Die Methode liefert eine Liste aller Kunden, die auf einem Konto einen Kontostand haben, der mindestens minimum beträgt.
     *
     * @param minimum Der minimale Kontostand, den ein Konto haben muss, damit der Kunde in der Liste erscheint.
     * @return Eine Liste von Kunden, deren Kontostand mindestens minimum beträgt.
     */
    public List<Kunde> getKundenMitVollemKonto(double minimum) {
        return bankKonten.values().stream().filter(konto -> konto.getKontostand() >= minimum).map(Konto::getInhaber).toList();
    }

    /**
     * liefert die Namen und Adressen aller Kunden der Bank.
     * Doppelte Kunden werden dabei aussortiert.
     * Die Liste wird nach dem Vornamen sortiert.
     *
     * @return Eine String-Repräsentation der Namen und Adressen aller Kunden.
     */
    public String getKundenadressen() {
        return bankKonten.values().stream().map(Konto::getInhaber).distinct()
                .map(kunde -> kunde.getName() + ", " + kunde.getAdresse()).collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Liefert eine Liste aller freien Kontonummern, die im von Ihnen vergebenen Bereich liegen.
     * Es werden die Kontonummern zurückgegeben, die zwischen der Untergrenze und der Obergrenze
     * liegen, jedoch derzeit kein Konto für diese Nummern existiert, z.B. weil es gelöscht wurde.
     *
     * @return Eine Liste der freien Kontonummern.
     */
    public List<Long> getKontonummernLuecken() {
        List<Long> availableKontoNums = new ArrayList<>();

        for (long i = currKontoNum; i < MAX_KONTO_NUM; i++) {
            if (!containsKey(i))
                availableKontoNums.add(i);
        }

        return availableKontoNums;
    }

    /**
     * Erstellt eine Bank mit der angegebenen Bankleitzahl
     *
     * @param bankleitzahl Bankleitzahl
     * @throws IllegalArgumentException falls die Bankleitzahl kleiner oder gleich 0 ist
     */
    public Bank(long bankleitzahl) throws IllegalArgumentException {
        if (bankleitzahl <= 0)
            throw new IllegalArgumentException("Bankleitzahl muss größer als 0 sein");
        this.bankleitzahl = bankleitzahl;
    }

    /**
     * Gibt die Bankleitzahl der Bank zurück.
     *
     * @return die Bankleitzahl der Bank
     */
    public long getBankleitzahl() {
        return this.bankleitzahl;
    }

    private boolean containsKey(long key) {
        return bankKonten.containsKey(key);
    }

    /**
     * Erstellt eine neue, noch nie vorgegebene Kontonummer
     *
     * @return die neue Kontonummer
     */
    private long erstellKontonummer() {
        if (currKontoNum < MAX_KONTO_NUM)
            return currKontoNum++;
        else {
            throw new RuntimeException("Max Limit reached for konto Numbers");
        }
    }

    /**
     * Fügt das gegebene Mock-Konto in die Kontenliste der Bank ein und gibt die dabei von der Bank
     * vergebene Kontonummer zurück.
     *
     * @param k Das Mock-Konto, das eingefügt werden soll
     * @return Die von der Bank vergebene Kontonummer für das eingefügte Konto
     * @throws NullPointerException Wenn das Konto-Objekt null ist
     */
    public long mockEinfuegen(Konto k) {
        long kontoNr = erstellKontonummer();
        bankKonten.put(kontoNr, k);

        return kontoNr;
    }

    /**
     * Erstellt ein neues Girokonto für einen Kunden und weist ihm eine eindeutige Kontonummer zu.
     *
     * @param inhaber der Kontoinhaber
     * @return die neue Kontonummer
     * @throws NullPointerException wenn der Kontoinhaber null ist
     */
    public long girokontoErstellen(Kunde inhaber) throws NullPointerException {
        if (inhaber == null)
            throw new NullPointerException("Inhaber darf nicht null gesetzt werden");
        long kontoNr = erstellKontonummer();

        Girokonto newGiro = new Girokonto(inhaber, kontoNr, DEFAULT_DISPO, 0);
        bankKonten.put(kontoNr, newGiro);

        return kontoNr;
    }

    /**
     * Erstellt ein neues Sparbuch für einen Kunden und weist ihm eine eindeutige Kontonummer zu.
     *
     * @param inhaber der Kontoinhaber
     * @return die neue Kontonummer
     * @throws NullPointerException wenn der Kontoinhaber null ist
     */
    public long sparbuchErstellen(Kunde inhaber) throws NullPointerException {
        if (inhaber == null)
            throw new NullPointerException("Inhaber darf nicht null gesetzt werden");
        long kontoNr = erstellKontonummer();

        Sparbuch newSparbuch = new Sparbuch(inhaber, kontoNr, 0);
        bankKonten.put(kontoNr, newSparbuch);

        return kontoNr;
    }

    /**
     * liefert eine Auflistung von Kontonummer und Kontostand zu jedem Konto zurück
     *
     * @return die Kontonummer und deren Kontostand
     */
    public String getAlleKonten() {
        StringBuilder str = new StringBuilder();
        for (Long key : bankKonten.keySet()) {
            Konto k = bankKonten.get(key);
            str.append(key);
            str.append(": ");
            str.append(k.getKontostand()).append(" ").append(k.getAktuelleWaehrung());
            str.append(System.lineSeparator());
        }
        return str.toString();
    }

    /**
     * liefert eine Liste aller gültigen Kontonummern in der Bank
     *
     * @return eine Liste von Kontonummern
     */
    public List<Long> getAlleKontonummern() {
        return new LinkedList<>(bankKonten.keySet());
    }

    /**
     * hebt den Betrag vom Konto mit der angegebenen Kontonummer ab und gibt zurück, ob die Abhebung erfolgreich war.
     *
     * @param von    die Kontonummer, von der der Betrag abgehoben werden soll
     * @param betrag der abzuhebende Betrag
     * @return true, wenn die Abhebung erfolgreich war, sonst false
     * @throws GesperrtException                  wenn das Konto gesperrt ist und keine Abhebungen erlaubt sind
     * @throws IllegalArgumentException           wenn Betrag kleiner-gleich 0 ist
     * @throws KontonummerNichtVorhandenException wenn die angegebene Kontonummer nicht vorhanden ist
     */
    public boolean geldAbheben(long von, double betrag) throws
            GesperrtException, IllegalArgumentException, KontonummerNichtVorhandenException {
        if (containsKey(von)) {
            if (betrag <= 0)
                throw new IllegalArgumentException("Betrag muss größer als 0 sein");
            Konto k = bankKonten.get(von);
            return k.abheben(betrag);
        } else
            throw new KontonummerNichtVorhandenException(von);
    }


    /**
     * zahlt den angegebenen Betrag auf das Konto mit der angegebenen Kontonummer ein.
     *
     * @param auf    die Kontonummer, auf das der Betrag eingezahlt werden soll
     * @param betrag der einzuzahlende Betrag
     * @throws IllegalArgumentException           wenn Betrag kleiner-gleich 0 ist
     * @throws KontonummerNichtVorhandenException wenn die angegebene Kontonummer nicht vorhanden ist
     */
    public void geldEinzahlen(long auf, double betrag) throws
            IllegalArgumentException, KontonummerNichtVorhandenException {
        if (containsKey(auf)) {
            if (betrag <= 0)
                throw new IllegalArgumentException("Betrag muss größer als 0 sein");
            Konto k = bankKonten.get(auf);
            k.einzahlen(betrag);
        } else
            throw new KontonummerNichtVorhandenException(auf);
    }

    /**
     * Löscht das Konto mit der angegebenen Kontonummer.
     *
     * @param nummer die Kontonummer des zu löschenden Kontos
     * @return true, wenn das Konto erfolgreich gelöscht wurde, false, wenn die Kontonummer nicht existiert
     */
    public boolean kontoLoeschen(long nummer) {
        if (containsKey(nummer)) {
            bankKonten.remove(nummer);
            return true;
        } else
            return false;
    }

    /**
     * Liefert den Kontostand des Kontos mit der angegebenen Kontonummer zurück.
     *
     * @param nummer die Kontonummer des Kontos
     * @return den Kontostand des Kontos mit der angegebenen Kontonummer
     * @throws KontonummerNichtVorhandenException wenn die angegebene Kontonummer nicht existiert
     */
    public double getKontostand(long nummer) throws KontonummerNichtVorhandenException {
        if (containsKey(nummer)) {
            return bankKonten.get(nummer).getKontostand();
        } else
            throw new KontonummerNichtVorhandenException(nummer);
    }

    /**
     * Überweist den angegebenen Betrag vom überweisungsfähigen Konto mit der Nummer vonKontoNr zum überweisungsfähigen
     * Konto mit der Nummer nachKontoNr und gibt zurück, ob die Überweisung erfolgreich war.
     *
     * @param vonKontoNr       die Kontonummer des überweisungsfähigen Kontos, von dem der Betrag abgebucht wird
     * @param nachKontoNr      die Kontonummer des überweisungsfähigen Kontos, auf das der Betrag überwiesen wird
     * @param betrag           der zu überweisende Betrag
     * @param verwendungszweck optionaler Verwendungszweck für die Überweisung
     * @return true, wenn die Überweisung erfolgreich war, false sonst
     * @throws IllegalArgumentException wenn einer der Parameter fehlerhaft ist
     */
    public boolean geldUeberweisen(long vonKontoNr, long nachKontoNr, double betrag, String verwendungszweck)
            throws IllegalArgumentException {

        if (!containsKey(nachKontoNr) || !containsKey(vonKontoNr)) {
            return false;
        }

        Konto vonKonto = bankKonten.get(vonKontoNr);
        Konto nachKonto = bankKonten.get(nachKontoNr);

        if (vonKonto instanceof Ueberweisungsfaehig && nachKonto instanceof Ueberweisungsfaehig) {
            boolean ueberweisungGeklappt = sendeUeberweisung((Ueberweisungsfaehig) vonKonto, betrag, nachKonto.getInhaber().getName(), nachKonto.getKontonummer(),
                    getBankleitzahl(), verwendungszweck);

            if (ueberweisungGeklappt) {
                empfangeUeberweisung((Ueberweisungsfaehig) nachKonto, betrag, vonKonto.getInhaber().getName(), vonKonto.getKontonummer(),
                        bankleitzahl, verwendungszweck);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Sends a money transfer from the sender's account to the recipient's account and returns
     * whether the transfer was successful.
     *
     * @param sender          the sender's account
     * @param betrag          the amount to be transferred
     * @param empfaenger      the name of the recipient
     * @param nachKontonr     the account number of the recipient's account
     * @param nachBlz         the bank code of the recipient's bank
     * @param verwendugszweck optional purpose for the transfer
     * @return true if the transfer was successful, false otherwise
     * @throws IllegalArgumentException if any of the parameters are invalid
     */
    boolean sendeUeberweisung(Ueberweisungsfaehig sender, double betrag, String empfaenger, long nachKontonr,
                              long nachBlz, String verwendugszweck) {
        try {
            return sender.ueberweisungAbsenden(betrag, empfaenger, nachKontonr, nachBlz, verwendugszweck);
        } catch (GesperrtException e) {
            return false;
        }
    }

    /**
     * Receives a money transfer from the sender's account and updates the recipient's account balance.
     *
     * @param empfaenger       the recipient's account
     * @param betrag           the amount received
     * @param vorname          the firstname of the recipient
     * @param vonKontonr       the account number from which the transfer was made
     * @param vonBlz           the bank code of the sender's bank
     * @param verwendungszweck optional purpose for the transfer
     * @throws IllegalArgumentException if any of the parameters are invalid
     */
    void empfangeUeberweisung(Ueberweisungsfaehig empfaenger, double betrag, String vorname, long vonKontonr,
                              long vonBlz, String verwendungszweck) {
        empfaenger.ueberweisungEmpfangen(betrag, vorname, vonKontonr, vonBlz, verwendungszweck);
    }
}
