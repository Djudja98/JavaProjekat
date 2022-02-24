package mapa.objekti;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import narod.Stanovnik;
import newgui.MainGUI;
import simulacija.Alarm;
import simulacija.Simulacija;

import static simulacija.Simulacija.mapa;

public class KontrolniPunkt extends NepokretniObjekat implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private static final int SLEEP_TIME = 2000;
	private static final int COLOR_INT = (int)0xFF4D4D;
	private boolean running;
	
	private Stanovnik stanovnikZaPracenje;

	public KontrolniPunkt(int x, int y) { // ovo u simulaciji ide random i uvijek ce biti udaljeni 2 polja
		super(x, y);
		color = new Color(COLOR_INT);
		running = true;
	}
	
	public void setStanovnikZaPracenje(Stanovnik s) {
		this.stanovnikZaPracenje = s;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public Stanovnik getStanovnik() {
		return stanovnikZaPracenje;
	}
	
	public void prekidIzvrsavanjaPunkt() {
		running = false;
	}
	
	@Override
	public void run() { 
		int posmatranjeX[] = {pozicijaX-1, pozicijaX, pozicijaX+1 }; // sigurno je bez uslova jer po projektu KP nikad nije u prvoj/zadnjoj koloni/redu
		int posmatranjeY[] = {pozicijaY-1, pozicijaY, pozicijaY+1 };
		while(running) {
			try {
				mapa.lockMap();
				for(int x: posmatranjeX) {
					for(int y: posmatranjeY) { // stanovnik ce sam setovati zaPracenje ako stane na KP 
						if(mapa.getMapaXY(x, y) instanceof Stanovnik) {
							Stanovnik temp = (Stanovnik)mapa.getMapaXY(x, y);
							if(!temp.potencijalnoZarazen &&  !temp.zarazen) {
								this.stanovnikZaPracenje = (Stanovnik)mapa.getMapaXY(x, y);
							}
						}
					}
				}
				if(stanovnikZaPracenje != null && stanovnikZaPracenje.getTemperatura().getIzmjerenaTemperatura() >=37) {
					if(Simulacija.sistemZaNadzor.dodajAlarm(new Alarm(stanovnikZaPracenje.getKuca().getID(), stanovnikZaPracenje.getLokacijaX(), stanovnikZaPracenje.getLokacijaY()))){
						stanovnikZaPracenje.zarazen = true;
						System.out.println("STANOVNIK " + stanovnikZaPracenje.getID() + "POVISENO "+ stanovnikZaPracenje.getTemperatura().getIzmjerenaTemperatura());
						MainGUI.ispisNaTextArea("STANOVNIK " + stanovnikZaPracenje.getID() + "POVISENO "+ stanovnikZaPracenje.getTemperatura().getIzmjerenaTemperatura());
					}
				}
			}finally {
				stanovnikZaPracenje = null;
				mapa.unlockMap();
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				Logger.getLogger(KontrolniPunkt.class.getName()).log(Level.SEVERE,e.toString());
			}
		}
	}
	

}
