
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import automat.Fach;
import automat.FachImplementierung;
import automat.KasseImplementierung;
import automat.Kassenmodul;
import automat.Suesskramautomat;
import automat.VerklemmtException;

import org.junit.jupiter.api.DisplayName;
 
public class SuesskramautomatTest{
 
  @Test
  @DisplayName("Test von waehlen(0) bei genug Geld, Fach voll")
  public void erstesVollesFachMitAusreichendGeldWaehlenTest() throws Exception {
 	//SetUp:
	  Kassenmodul kasse = Mockito.mock(Kassenmodul.class);
	  Fach fach = Mockito.mock(Fach.class);
	  Mockito.when(fach.isVoll()).thenReturn(true);
	  Mockito.when(fach.getPreis()).thenReturn(17);
	  Mockito.when(kasse.getKontostand()).thenReturn(42);
	  Fach[] faecher = {fach};
	  Suesskramautomat automat = new Suesskramautomat(kasse, faecher);
	//Exercise:
	  boolean hatGeklappt = automat.waehlen(0);
	  
	//Verify:
	  assertTrue(hatGeklappt);
	  ArgumentCaptor<Integer> cap = ArgumentCaptor.forClass(Integer.class);
	  Mockito.verify(kasse).abziehen(cap.capture());
	  assertEquals(17, cap.getValue());
	  Mockito.verify(fach).auswerfen();
 
  }
  
  @Test
  @DisplayName("Test von waehlen(0) bei genug Geld, Fach leer")
  public void erstesLeeresFachMitAusreichendGeldWaehlenTest() throws Exception {
 	//SetUp:
	  Kassenmodul kasse = Mockito.mock(Kassenmodul.class);
	  Fach fach = Mockito.mock(Fach.class);
	  Mockito.when(fach.isVoll()).thenReturn(false);
	  Mockito.when(fach.getPreis()).thenReturn(17);
	  Mockito.when(kasse.getKontostand()).thenReturn(42);
	  Fach[] faecher = {fach};
	  Suesskramautomat automat = new Suesskramautomat(kasse, faecher);
	//Exercise:
	  boolean hatGeklappt = automat.waehlen(0);
	  
	//Verify:
	  assertFalse(hatGeklappt);
	  Mockito.verify(kasse, Mockito.times(0))
	  				.abziehen(ArgumentMatchers.anyInt());
	  Mockito.verify(fach, Mockito.times(0)).auswerfen();
 
  }
  
  @Test
  @DisplayName("Test von waehlen(0) bei genug Geld, Fach voll")
  public void erstesVollesVerklemmtesFachMitAusreichendGeldWaehlenTest() throws Exception {
 	//SetUp:
	  Kassenmodul kasse = Mockito.mock(Kassenmodul.class);
	  Fach fach = Mockito.mock(Fach.class);
	  Mockito.when(fach.isVoll()).thenReturn(true);
	  Mockito.when(fach.getPreis()).thenReturn(17);
	  //Mockito.when(fach.auswerfen()).thenThrow(new VerklemmtException());
	  Mockito.doThrow(VerklemmtException.class)
	  						.when(fach).auswerfen();
	  Mockito.when(kasse.getKontostand()).thenReturn(42);
	  Fach[] faecher = {fach};
	  Suesskramautomat automat = new Suesskramautomat(kasse, faecher);
	//Exercise:
	  try {
		  boolean hatGeklappt = automat.waehlen(0);
		  fail("Exception ist nicht aufgetreten!");
	  } catch (VerklemmtException e)
	  {}
	//Verify:
	  Mockito.verify(kasse, Mockito.times(0)).abziehen(17);
	  //Mockito.verify(fach, Mockito.times(0)).auswerfen();
 
  }
 
}