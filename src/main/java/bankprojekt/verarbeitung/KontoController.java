package bankprojekt.verarbeitung;

import bankprojekt.oberflaeche.KontoOberflaeche;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KontoController extends Application {
    private Stage primaryStage;
    private Konto model;
    private KontoOberflaeche kontoUI;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        model = new Girokonto(Kunde.MUSTERMANN,512411251L,1000,0);
        kontoUI = new KontoOberflaeche(model,this);
        Scene scene = new Scene(kontoUI, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    public void sperrenAendern(boolean selected) {
        if(selected) model.sperren();
        if(!selected) model.entsperren();
    }

    public void einzahlen(TextField betragText) {
        double betrag  = Double.parseDouble(betragText.getText());
        try {
            model.einzahlen(betrag);
        } catch (IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Illegal Value");
            alert.setContentText("Cannot deposit an Illegal Value");
            alert.showAndWait();
        }
    }

    public void abheben(TextField betragText) {
        double betrag = Double.parseDouble(betragText.getText());
        try {
            boolean successfulWithdrawal = model.abheben(betrag);
            if (!successfulWithdrawal) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Withdrawal Error");
                alert.setContentText("The withdrawal amount exceeds the dispo limit.");
                alert.showAndWait();
            }
        } catch (GesperrtException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Account is locked");
            alert.setContentText("Cannot withdraw money from a locked account");
            alert.showAndWait();
        }
    }
}
