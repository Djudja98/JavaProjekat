package mapa.objekti;

import narod.Stanovnik;
import narod.StanovnikUtil;
import newgui.MainGUI;
import simulacija.Alarm;
import static simulacija.Simulacija.mapa;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmbulantaVozilo extends PokretniObjekat implements Runnable{

	private static final long serialVersionUID = 1L;
	private static final int COLOR_INT = (int)0xC9FFE5;
	private static final int SLEEP_TIME = 1000;
	
	public boolean jeSlobodno = true;
	public boolean pokupioBolesnika;
	public boolean prekidRada;
	private Ambulanta ambulanta;
	private Alarm alarm;
	private Stanovnik bolesnik;
	
	public AmbulantaVozilo() {
		color = new Color(COLOR_INT);
	}
	
	public void setAmbulanta(Ambulanta a) {
		ambulanta = a;
	}
	
	public Ambulanta getAmbulanta() {
		return ambulanta;
	}
	
	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}
	
	public Alarm getAlarm() {
		return alarm;
	}
	
	public void prevozDo(int odredisteX, int odredisteY) throws InterruptedException { 
		while(lokacijaX != odredisteX || lokacijaY != odredisteY) { 
			List<Tacka> potencijalneLokacije = new ArrayList<Tacka>();
			for(int x = lokacijaX-1; x <= lokacijaX+1; x++) {
				for (int y = lokacijaY-1; y <= lokacijaY+1; y++) {
					if(provjeriTacku(x, y, odredisteX, odredisteY)) {
						potencijalneLokacije.add(new Tacka(x, y));
					}
				}
			} // sortiram ih po udaljenosti od odredista pa prvo pokusam najmanje udaljenu
			potencijalneLokacije.sort((t1, t2) ->
				Double.compare(StanovnikUtil.distanca(t1.x, odredisteX, t1.y, odredisteY), StanovnikUtil.distanca(t2.x, odredisteX, t2.y, odredisteY)));
			try {
				mapa.lockMap();
				if(prekidRada) {
					return;
				}
				for(int i = 0; i < potencijalneLokacije.size(); i++) {
					int tackaX = potencijalneLokacije.get(i).x;
					int tackaY = potencijalneLokacije.get(i).y;
					Object temp = mapa.getMapaXY(tackaX, tackaY);
					if(potencijalneLokacije.indexOf(new Tacka(odredisteX, odredisteY))!=-1){ // ako je odrediste odmah njega bez dodatne provjere
						pomjeraj(tackaX, tackaY);
						return;
					}
					else if(provjeriObjekat(temp, tackaX, tackaY, odredisteX, odredisteY)) {
						pomjeraj(tackaX, tackaY);
						break;
					}
				}
			}finally {
				mapa.unlockMap();
				Thread.sleep(SLEEP_TIME);
			}
		}
	}
	
	public boolean provjeriObjekat(Object obj, int tackaX, int tackaY,int odredisteX, int odredisteY) {
		if(!((obj instanceof Kuca)
			|| obj instanceof AmbulantaVozilo
			|| (obj instanceof Stanovnik &&(tackaX != odredisteX || tackaY!= odredisteY))
			|| (obj instanceof KontrolniPunkt &&(tackaX != odredisteX || tackaY!= odredisteY) ))){
			return true;
		}
		return false;
	}

	@Override
	public boolean pomjeraj(int novoX, int novoY) {
		Object temp = mapa.getMapaXY(novoX, novoY);
		if(mapa.getMapaXY(lokacijaX, lokacijaY) instanceof AmbulantaVozilo) {
			mapa.setMapaXY(lokacijaX, lokacijaY, null);
			MainGUI.setTextField(lokacijaX, lokacijaY, "" , MainGUI.defaultTextFieldColor);
		}
		
		if(temp instanceof Ambulanta) {
			MainGUI.ispisNaTextArea(this+ "(" + novoX + " ," + novoY + ") " + ispisSmjera(novoX, novoY) +" u ambulanti");
			lokacijaX = novoX;
			lokacijaY = novoY;
		}
		else if(alarm.getLokacijaX() == novoX && alarm.getLokacijaY() == novoY) {
			if(temp instanceof Stanovnik) {
				mapa.setMapaXY(novoX, novoY, this);
				MainGUI.setTextField(novoX, novoY, "AV", color);
			}
			else if(temp instanceof KontrolniPunkt) {
				((KontrolniPunkt)temp).setStanovnikZaPracenje(null);
			}
			MainGUI.ispisNaTextArea(this+ "(" + novoX + " ," +novoY + ") " + ispisSmjera(novoX, novoY));
			lokacijaX = novoX;
			lokacijaY = novoY;
		}
		else { 
			if(!(temp instanceof Ambulanta)
					|| temp instanceof KontrolniPunkt) {
				mapa.setMapaXY(novoX, novoY, this);
				MainGUI.setTextField(novoX, novoY, "AV", color);
				MainGUI.ispisNaTextArea(this+ "(" + novoX + " ," + novoY + ") " + ispisSmjera(novoX, novoY));
				this.lokacijaX = novoX;
				this.lokacijaY = novoY;
			}
		}
		return true;
	}
	
	private void prevozDoAmbulante(Ambulanta dostupnaAmbulanta){
		try {
			if(prekidRada) { // moze a i ne mora, ovo samo da ne vrsi nepotrebno racunanje
				return;
			}
			prevozDo(dostupnaAmbulanta.pozicijaX, dostupnaAmbulanta.pozicijaY);
			if(!prekidRada) {
				Ambulanta.ambulanteLock.lock();
				try {
					ambulanta.dodajBolesnika(bolesnik); // ovo poveca broj bolesnika kad se serijalizuje
				}finally {
					Ambulanta.ambulanteLock.unlock();
				}
			}
		} catch (InterruptedException e) {
			Logger.getLogger(AmbulantaVozilo.class.getName()).log(Level.SEVERE,e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		provjeraOpsega();
		if(alarm != null) {
			int alarmX = alarm.getLokacijaX();
			int alarmY = alarm.getLokacijaY();
			if(bolesnik == null) {
				if(mapa.getMapaXY(alarmX, alarmY) instanceof Stanovnik) {
					bolesnik = (Stanovnik)mapa.getMapaXY(alarmX, alarmY);
				}
				else if(mapa.getMapaXY(alarmX, alarmY) instanceof KontrolniPunkt
							&& ((KontrolniPunkt)mapa.getMapaXY(alarmX, alarmY)).getStanovnik() != null) {
					bolesnik = ((KontrolniPunkt)mapa.getMapaXY(alarmX, alarmY)).getStanovnik();
				}else {
					return;
				}
			}
			if(!pokupioBolesnika) {
				try {
					prevozDo(alarmX, alarmY);
					pokupioBolesnika = true;
				} catch (InterruptedException e) {
					Logger.getLogger(AmbulantaVozilo.class.getName()).log(Level.SEVERE,e.toString());
				}
			}
			prevozDoAmbulante(ambulanta);
			if(prekidRada) {
				prekidRada = false;
				return;
			}
			ambulanta = null;
			alarm = null;
			bolesnik = null;
			pokupioBolesnika = false;
			jeSlobodno = true;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+ " ";
	}

}
