package observerMockTests;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Waehrung;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class KontoObserverTests {
    PropertyChangeListener mockListener;
    Konto konto;

    @BeforeEach
    public void setup() {
        konto = new Konto() {
            @Override
            protected boolean validateBetrag(double betrag) {
                return true;
            }
        };
        konto.anmelden(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                switch (event.getPropertyName()) {
                    case "Kontostand":
                        System.out.println("Account balance changed: " + event.getNewValue());
                        break;
                    case "AktienAnzahl":
                        System.out.println("Number of stocks changed: " + event.getNewValue());
                        break;
                    case "waehrung":
                        System.out.println("The Currency has been changed " + event.getNewValue());
                        break;
                    case "gesperrt":
                        System.out.println("The account has been suspended ");
                        break;
                    case "entsperren":
                        System.out.println("The account has been unsuspended ");
                        break;
                    default:
                        System.out.println("An unknown property was changed.");
                        break;
                }
            }
        });
        this.mockListener = mock(PropertyChangeListener.class);
        konto.anmelden(this.mockListener);
    }

    @Test
    void testEinzahlen() {
        konto.einzahlen(100);
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    void testAbheben() throws GesperrtException {
        konto.abheben(100);
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    void waehrungWechsel() {
        konto.waehrungswechsel(Waehrung.DKK);
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    void kontoSperren() {
        konto.sperren();
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    void kontoEntsperren() {
        konto.entsperren();
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

}
