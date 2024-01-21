package bankprojekt.oberflaeche;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.KontoController;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Eine Oberfläche für ein einzelnes Konto. Man kann einzahlen
 * und abheben und sperren und die Adresse des Kontoinhabers
 * ändern
 *
 * @author Doro
 */
public class KontoOberflaeche extends BorderPane {
    private Text ueberschrift;
    private GridPane anzeige;
    private Text txtNummer;
    /**
     * Anzeige der Kontonummer
     */
    private Text nummer;
    private Text txtStand;
    /**
     * Anzeige des Kontostandes
     */
    private Text stand;
    private Text txtGesperrt;
    /**
     * Anzeige und Änderung des Gesperrt-Zustandes
     */
    private CheckBox gesperrt;
    private Text txtAdresse;
    /**
     * Anzeige und Änderung der Adresse des Kontoinhabers
     */
    private TextArea adresse;
    /**
     * Anzeige von Meldungen über Kontoaktionen
     */
    private Text meldung;
    private HBox aktionen;
    /**
     * Auswahl des Betrags für eine Kontoaktion
     */
    private TextField betrag;
    /**
     * löst eine Einzahlung aus
     */
    private Button einzahlen;
    /**
     * löst eine Abhebung aus
     */
    private Button abheben;

    /**
     * Creates a new instance of KontoOberflaeche with the given Konto model and KontoController controller.
     *
     * @param model      The Konto object to display and modify.
     * @param controller The controller object responsible for handling user actions.
     */
    public KontoOberflaeche(Konto model, KontoController controller) {
        ueberschrift = new Text("Ein Konto verändern");
        ueberschrift.setFont(new Font("Sans Serif", 25));
        BorderPane.setAlignment(ueberschrift, Pos.CENTER);
        this.setTop(ueberschrift);

        anzeige = new GridPane();
        anzeige.setPadding(new Insets(20));
        anzeige.setVgap(10);
        anzeige.setAlignment(Pos.CENTER);

        txtNummer = new Text("Kontonummer:");
        txtNummer.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtNummer, 0, 0);
        nummer = new Text();
        nummer.setFont(new Font("Sans Serif", 15));
        GridPane.setHalignment(nummer, HPos.RIGHT);
        anzeige.add(nummer, 1, 0);
        nummer.textProperty().set(model.getKontonummerFormatiert());

        txtStand = new Text("Kontostand:");
        txtStand.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtStand, 0, 1);
        stand = new Text();
        stand.setFont(new Font("Sans Serif", 15));
        GridPane.setHalignment(stand, HPos.RIGHT);
        anzeige.add(stand, 1, 1);
        stand.textProperty().bind(model.kontostandProperty().asString().concat(" " + model.getAktuelleWaehrung()));
        stand.fillProperty().bind(Bindings.when(model.kontostandImPlusProperty()).then(Color.GREEN).otherwise(Color.RED));

        if (model.isGesperrt()) {
            txtGesperrt = new Text("Gesperrt");
            txtGesperrt.setFill(Color.RED);
        } else {
            txtGesperrt = new Text("Entsperrt");
            txtGesperrt.setFill(Color.GREEN);
        }
        txtGesperrt.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtGesperrt, 0, 2);
        gesperrt = new CheckBox();
        GridPane.setHalignment(gesperrt, HPos.RIGHT);
        anzeige.add(gesperrt, 1, 2);
        gesperrt.selectedProperty().addListener(e -> controller.sperrenAendern(gesperrt.isSelected()));
        txtGesperrt.textProperty().bind(Bindings.when(model.gesperrtProperty()).then("Gesperrt")
                .otherwise("Entsperrt"));
        txtGesperrt.fillProperty().bind(Bindings.when(model.gesperrtProperty()).then(Color.RED)
                .otherwise(Color.GREEN));

        txtAdresse = new Text("Adresse: ");
        txtAdresse.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtAdresse, 0, 3);
        adresse = new TextArea();
        adresse.setPrefColumnCount(25);
        adresse.setPrefRowCount(2);
        GridPane.setHalignment(adresse, HPos.RIGHT);
        anzeige.add(adresse, 1, 3);
        adresse.textProperty().bindBidirectional(model.getInhaber().adresseProperty());

        meldung = new Text("Willkommen lieber Benutzer");
        meldung.setFont(new Font("Sans Serif", 15));
        meldung.setFill(Color.RED);
        anzeige.add(meldung, 0, 4, 2, 1);

        this.setCenter(anzeige);

        aktionen = new HBox();
        aktionen.setSpacing(10);
        aktionen.setAlignment(Pos.CENTER);

        betrag = new TextField("100.00");
        aktionen.getChildren().add(betrag);

        einzahlen = new Button("Einzahlen");
        aktionen.getChildren().add(einzahlen);
        einzahlen.setOnAction(e -> controller.einzahlen(betrag));

        abheben = new Button("Abheben");
        aktionen.getChildren().add(abheben);
        abheben.setOnAction(e -> controller.abheben(betrag));

        this.setBottom(aktionen);

    }
}
