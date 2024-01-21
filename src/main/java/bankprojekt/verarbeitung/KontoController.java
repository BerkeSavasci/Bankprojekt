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
    private Alert alert = new Alert(Alert.AlertType.ERROR);

    /**
     * Starts the application by creating a stage, setting up the necessary components, and displaying the stage.
     *
     * @param stage the primary stage of the application
     * @throws Exception if an error occurs during the execution of the method
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        model = new Girokonto(Kunde.MUSTERMANN, 512411251L, 1000, 0);
        kontoUI = new KontoOberflaeche(model, this);
        Scene scene = new Scene(kontoUI, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes the lock status of the account.
     *
     * @param selected true to lock the account, false to unlock the account
     */
    public void sperrenAendern(boolean selected) {
        if (selected) model.sperren();
        if (!selected) model.entsperren();
    }

    /**
     * Determines whether the account is locked or not.
     *
     * @return true if the account is locked
     */
    public boolean isGesperrt() {
        return model.isGesperrt();
    }

    /**
     * Deposits the specified amount into the account.
     *
     * @param betragText the text field containing the deposit amount
     */
    public void einzahlen(TextField betragText) {
        if (model.isGesperrt()) {
            alert.setTitle("Error");
            alert.setHeaderText("Account is locked");
            alert.setContentText("Cannot withdraw money from a locked account");
            alert.showAndWait();
            return;
        }
        double betrag = Double.parseDouble(betragText.getText());
        try {
            model.einzahlen(betrag);
        } catch (IllegalArgumentException e) {
            alert.setTitle("Error");
            alert.setHeaderText("Illegal Value");
            alert.setContentText("Cannot deposit a negative or zero value");
            alert.showAndWait();
        }
    }

    /**
     * Tries to withdraw the specified amount from the account.
     *
     * @param betragText the text field containing the withdrawal amount
     * */
    public void abheben(TextField betragText) {
        double betrag = Double.parseDouble(betragText.getText());
        try {
            boolean successfulWithdrawal = model.abheben(betrag);
            if (!successfulWithdrawal) {
                alert.setTitle("Error");
                alert.setHeaderText("Withdrawal Error");
                alert.setContentText("The withdrawal amount exceeds the dispo limit.");
                alert.showAndWait();
            }
        } catch (GesperrtException e) {
            alert.setTitle("Error");
            alert.setHeaderText("Account is locked");
            alert.setContentText("Cannot withdraw money from a locked account");
            alert.showAndWait();
        } catch (IllegalArgumentException e) {
            alert.setTitle("Error");
            alert.setHeaderText("Illegal Value");
            alert.setContentText("Cannot withdraw a negative or zero value");
            alert.showAndWait();
        }
    }
}
