package formatierung;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Eine Klasse die Zahlen und Daten formatiert und die in einer Datei schreibt.
 */
public class ZahlenFormatter {
    /**
     * Main method to write numbers, date and time objects in a defined format to a file.
     * @param args not used
     */
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Geben Sie eine ganze Zahl ein:");
            int input = Integer.parseInt(reader.readLine());
            System.out.println("Geben Sie eine Zahl mit Nachkommaanteil ein:");
            double inputDouble = Double.parseDouble(reader.readLine());

            File file = new File("formatiert.txt");
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            //1 ohne Formatierung
            printWriter.println(input);

            //2 Zahl mit 12 stellen (falls zu kurz, vorne mit Nullen auffüllen, falls lang ist ok)
            printWriter.printf("%012d%n", input);

            //3 (+/- Zeichen und Tausendertrennzeichen)
            printWriter.printf("%+,d%n", input);

            //4 in Hex
            printWriter.printf("%X%n", input);

            //5 ohne Formatierung
            printWriter.println(inputDouble);

            //6 (+/- Zeichen und 5 Nachkommastellen)
            printWriter.printf("%+,.5f%n", inputDouble);

            //7 wissenschaftlicher Darstellung
            printWriter.printf("%E%n", inputDouble);

            //8 2 Nachkommastellen USA norm
            Locale us = Locale.US;
            printWriter.printf(us, "%.2f%n", inputDouble);

            //9 Aktuelles Datum
            LocalDate heute = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM uuuu (EE)", Locale.GERMANY);
            printWriter.println(heute.format(dateTimeFormatter));

            //10 aktuelles Datum aber italienisch
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/uuuu EEEE", Locale.ITALIAN);
            printWriter.println(heute.format(dateTimeFormatter));

            //11 aktuelle Uhrzeit aber englisch
            LocalTime now = LocalTime.now();
            dateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            printWriter.println(now.format(dateTimeFormatter));


            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
