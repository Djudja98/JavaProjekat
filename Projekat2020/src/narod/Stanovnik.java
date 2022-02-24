package narod;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import mapa.objekti.Ambulanta;
import mapa.objekti.AmbulantaVozilo;
import mapa.objekti.KontrolniPunkt;
import mapa.objekti.Kuca;
import mapa.objekti.PokretniObjekat;
import mapa.objekti.Tacka;
import newgui.MainGUI;

import static narod.StanovnikUtil.*;

import static simulacija.Simulacija.mapa;

public class Stanovnik extends PokretniObjekat implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private static final int COLOR_INT = (int)0x6AFF4D;
	
	public boolean zarazen;
	public boolean potencijalnoZarazen;
	public boolean krajKretanja;
	private static int brojKreiranih;
	protected int id;
	protected String ime;
	protected String prezime;
	protected int godine;
	protected Pol pol;
	protected int kucniID;
	protected Temperatura temperatura;
	protected Kuca kuca;
	
	public Stanovnik() { //UZIMACE X I Y OD KUCE NA POCETKU
		super();
		this.ime = StanovnikUtil.generateString();
		this.prezime = StanovnikUtil.generateString();
		pol = StanovnikUtil.generatePol();
		this.id = ++brojKreiranih;
		temperatura = new Temperatura();
		color = new Color(COLOR_INT);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setKuca(Kuca k) {
		this.kuca = k;
	}
	
	public Kuca getKuca() {
		return kuca;
	}
	
	public int getID() {
		return id;
	}
	
	public Temperatura getTemperatura() {
		return temperatura;
	}
	
	public Pol getPol() {
		return pol;
	}
	
	public int getGodine() {
		return godine;
	}
	
	public String getIme() {
		return ime;
	}
	
	public String getPrezime() {
		return prezime;
	}
	
	public boolean jeUKuci() {
		return (lokacijaX == kuca.getPozicijaX() && lokacijaY == kuca.getPozicijaY());
	}
	
	public void startTemperatura() { 
		temperatura.setDaemon(true);
		temperatura.start();
	}
	
	public void napraviKorak() {
		List<Tacka> potencijalneLokacije = new ArrayList<Tacka>();
			for(int x= lokacijaX-1; x<= lokacijaX+1; x++) {
				for(int y= lokacijaY-1; y<= lokacijaY+1; y++) {
					if(!(x > granicaDesno || x < granicaLijevo 
							|| y > granicaGore || y < granicaDole 
							|| (x == lokacijaX && y == lokacijaY))){
						potencijalneLokacije.add(new Tacka(x, y));
					}
				}
			}
		Collections.shuffle(potencijalneLokacije);
		try {
			mapa.lockMap();
			for(int i=0; i<potencijalneLokacije.size();i++) {
				if(pomjeraj(potencijalneLokacije.get(i).x, potencijalneLokacije.get(i).y)) {
					return;
				}
			}
		}finally {
			mapa.unlockMap();
		}
	}
	
	@Override
	public boolean pomjeraj(int novoX, int novoY) { 
		Object temp = mapa.getMapaXY(novoX, novoY);
		
		if(temp instanceof PokretniObjekat || temp instanceof Ambulanta) {
			return false;
		}
		
		if(temp instanceof KontrolniPunkt && !this.jeUKuci()) {
			if(mapa.getMapaXY(lokacijaX, lokacijaY) instanceof Stanovnik) {
				MainGUI.setTextField(lokacijaX, lokacijaY, "", MainGUI.defaultTextFieldColor);
				mapa.setMapaXY(lokacijaX, lokacijaY, null);
			}
			((KontrolniPunkt)temp).setStanovnikZaPracenje(this);
			MainGUI.ispisNaTextArea(this + ispisSmjera(novoX, novoY) +" KP");
			lokacijaX = novoX;
			lokacijaY = novoY;
			return true;
		}else if(temp instanceof Kuca) {
			if(kuca.equals(temp) && potencijalnoZarazen) { // kad se vrati kuci zavrsava kretanje
				if(mapa.getMapaXY(lokacijaX, lokacijaY) instanceof Stanovnik) {
					mapa.setMapaXY(lokacijaX, lokacijaY, null);
					MainGUI.setTextField(lokacijaX, lokacijaY, "", MainGUI.defaultTextFieldColor);
				}
				else if(mapa.getMapaXY(lokacijaX, lokacijaY) instanceof KontrolniPunkt) {
					((KontrolniPunkt)mapa.getMapaXY(lokacijaX, lokacijaY)).setStanovnikZaPracenje(null);
				}
				MainGUI.ispisNaTextArea(this + ispisSmjera(novoX, novoY) + " U KUCI"); 
				lokacijaX = novoX;
				lokacijaY = novoY;
				krajKretanja = true;
				return true;
			}
			else {
				return false; // ne mogu uci u tudju kucu
			}
		}
		else {
			if(provjeraDistanceNaPolju(novoX, novoY) || potencijalnoZarazen) {
				if(mapa.getMapaXY(lokacijaX, lokacijaY) instanceof Stanovnik) {
					mapa.setMapaXY(lokacijaX, lokacijaY, null);
					MainGUI.updateTextField(lokacijaX, lokacijaY, novoX, novoY, String.valueOf(id), color);
				}else {
					MainGUI.setTextField(novoX, novoY, String.valueOf(id), color);
				}
				mapa.setMapaXY(novoX, novoY, this);
				MainGUI.ispisNaTextArea(this + ispisSmjera(novoX, novoY));
				lokacijaX = novoX;
				lokacijaY = novoY;
				return true;
			}
		}
		return false;
	}
	
	public boolean provjeraDistanceNaPolju(int nextX , int nextY) { // ovo ispituje razmak od 2 polja da li je ispunjen
		for(int i = nextX-2; i<= nextX + 2; i++) {
			for(int j= nextY-2; j<= nextY + 2; j++) {
				if(mapa.getMapaXY(i, j) instanceof Stanovnik && !this.equals(mapa.getMapaXY(i, j))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void korakKuci() {
		if(this.jeUKuci()) {
			krajKretanja = true;
			return;
		}
		if(lokacijaX != kuca.getPozicijaX() || lokacijaY != kuca.getPozicijaY()) {
			List<Tacka> potencijalneLokacije = new ArrayList<Tacka>();
			for(int x = lokacijaX-1; x <= lokacijaX+1; x++) {
				for (int y = lokacijaY-1; y <= lokacijaY+1; y++) {
					if(provjeriTacku(x, y, kuca.getPozicijaX(), kuca.getPozicijaY())) {
						potencijalneLokacije.add(new Tacka(x, y));
					}
				}
			} // sortiram ih po udaljenosti od odredista pa prvo pokusam najmanje udaljenu
			potencijalneLokacije.sort((t1, t2) ->
				Double.compare(
						distanca(t1.x, kuca.getPozicijaX(), t1.y, kuca.getPozicijaY()), distanca(t2.x, kuca.getPozicijaX(), t2.y, kuca.getPozicijaY())));
			try {
				mapa.lockMap();
				for(int i = 0; i < potencijalneLokacije.size(); i++) {
					int tackaX = potencijalneLokacije.get(i).x;
					int tackaY = potencijalneLokacije.get(i).y;
					Object temp = mapa.getMapaXY(tackaX, tackaY);
					if(potencijalneLokacije.indexOf(new Tacka(kuca.getPozicijaX(), kuca.getPozicijaY()))!=-1){
						pomjeraj(tackaX, tackaY);
						break;
					}
					else if(provjeriObjekat(temp, tackaX, tackaY, kuca.getPozicijaX(), kuca.getPozicijaY())) {
						pomjeraj(tackaX, tackaY);
						break;
					}
				  }
				}
			finally {
				mapa.unlockMap();
			}
			
		}
	}
	
	public boolean provjeriObjekat(Object obj, int tackaX, int tackaY,int odredisteX, int odredisteY) {
		if(!((obj instanceof Kuca && (tackaX != odredisteX || tackaY != odredisteY))
				|| obj instanceof AmbulantaVozilo
				|| (obj instanceof Stanovnik))){
				return true;
			}
		return false;
	}
		
	
	
	public void run() {  
		if(!potencijalnoZarazen) {
			provjeraOpsega();
		}
		while(!krajKretanja){	
			if(zarazen) {
				synchronized (this) {
					try {
						wait(); //Ambulanta ce notify kad ozdravi
					}catch(InterruptedException e) {
						Logger.getLogger(Stanovnik.class.getName()).log(Level.SEVERE,e.toString());
					}
				}
			}
			if(!potencijalnoZarazen) {
				napraviKorak();
			}
			else {
				korakKuci();
			}
			try {
				Thread.sleep(3000 + new Random().nextInt(2000));
			} catch (InterruptedException e) {
				Logger.getLogger(Stanovnik.class.getName()).log(Level.SEVERE,e.toString());
			}
		}
		System.out.println("KRAJ " + this);
	}
	
	@Override
	public String toString() {
		return id +" "+getClass().getSimpleName()+ " " + ime + " " + prezime + " "+ "(" + lokacijaX + " ," + lokacijaY + ") ";
	}
	
	@Override
	public boolean equals(Object obj) { // poredjenje trenutno samo po ID-u jer nema potrebe za detaljnije poredjenje
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		if(id != ((Stanovnik)obj).getID()) {
			return false;
		}
		return true;
	}

	
	public enum Pol{
		MUSKI, ZENSKI;
	}
	
}
