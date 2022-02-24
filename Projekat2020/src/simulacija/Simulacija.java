package simulacija;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mapa.Mapa;
import mapa.objekti.Ambulanta;
import mapa.objekti.AmbulantaVozilo;
import mapa.objekti.KontrolniPunkt;
import mapa.objekti.Kuca;
import mapa.objekti.PokretniObjekat;
import narod.CovjekStatistika;
import narod.Dijete;
import narod.Odrasli;
import narod.Stanovnik;
import narod.Stari;
import newgui.MainGUI;

public class Simulacija implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int MIN_BOLNICA_KAPACITET = 1;
	
	public long vrijemeTrajanjaSimulacije;
	public static long startTime;
	public static Mapa mapa;
	public static List<Stanovnik> listaStanovnika = new ArrayList<>();
	public static List<Kuca> listaKuca = new ArrayList<>();
	public static List<Ambulanta> listaAmbulante = new ArrayList<>();
	public static List<KontrolniPunkt> listaKP = new ArrayList<>();
	public static Nadzor sistemZaNadzor;
	private int mapSize;
	public int brojAmbulVozila;
	public int brojPunoljetnih;
	public int brojAmbulanti = 4;
	public int brojKuca;
	public int brojKontPunktova;
	public static List<CovjekStatistika> listaZarazenih = new ArrayList<>();
	public static List<CovjekStatistika> listaOporavljenih = new ArrayList<>();
	
	private static Random random = new Random();
	
	public Simulacija(int mapSize, int brojOdraslih, int brojStarih, int brojDjece, int brojKuca, int brojKP, int brojAV)
						throws NedovoljnoOdraslihException{
		brojPunoljetnih = brojOdraslih + brojStarih;
		this.mapSize = mapSize;
		this.brojKuca = brojKuca;
		this.brojKontPunktova = brojKP;
		mapa = new Mapa(mapSize);
		brojAmbulVozila = brojAV;
		sistemZaNadzor = new Nadzor(brojAV);
		for(int i=0; i<brojOdraslih;i++) {
			listaStanovnika.add(new Odrasli()); // treba start i start temperatura
		}
		for(int i=0; i<brojStarih;i++) {
			listaStanovnika.add(new Stari());
		}
		for(int i=0; i<brojDjece;i++) {
			listaStanovnika.add(new Dijete());
		}
	}
	
	public void rasporediStanovnikePoKucama() {
		for(int i=0; i< listaStanovnika.size(); i++) {
			if(!(listaStanovnika.get(i) instanceof Dijete)) {
				listaKuca.get(i % listaKuca.size()).dodajUkucana(listaStanovnika.get(i));
			}
		}

		for(int i=0, count = 0; i <listaStanovnika.size();i++) {
			if(listaStanovnika.get(i) instanceof Dijete) {
				listaKuca.get(count % brojPunoljetnih).dodajUkucana(listaStanovnika.get(i));
				count++;
			}
		}
	}
	
	public void postaviKontrolnePunktove(int brojKP) { 
		int count = 0;
		while(count < brojKP) {
			int x = 1 + random.nextInt(mapSize-2);
			int y = 1 + random.nextInt(mapSize-2);
			boolean preblizu = false;
			if(mapa.getMapaXY(x, y) == null) {
				for(int i = x-1 ; i <= x+1; i++) {
					for(int j = y-1; j<= y+1; j++) {
						if(mapa.getMapaXY(i, j) != null)
							preblizu = true;
					}
				}
				if(!preblizu) {
					KontrolniPunkt temp = new KontrolniPunkt(x, y);
					listaKP.add(temp);
					mapa.setMapaXY(x, y, temp);
					MainGUI.setTextField(x,y ,"KP", temp.getColor());
					count++;
				}
			}
		}
	}
	
	public void postaviKuce(int brojKuca) {
		int count = 0;
		while(count < brojKuca) {
			int x = 1 + random.nextInt(mapSize-2);
			int y = 1 + random.nextInt(mapSize-2);
			if(mapa.getMapaXY(x, y) == null) {
				Kuca tempKuca = new Kuca(x, y);
				listaKuca.add(tempKuca);
				mapa.setMapaXY(x, y, tempKuca);
				MainGUI.setTextField(x,y ,"K", tempKuca.getColor());
				count++;
			}
		}
	}
	
	public void postaviAmbulante() {
		dodajAmbulantu(0, 0);
		dodajAmbulantu(mapSize-1, 0);
		dodajAmbulantu(0, mapSize-1);
		dodajAmbulantu(mapSize-1, mapSize-1);
	}
	
	public static boolean dodajAmbulantu(int mapaX,int mapaY) {
		if(listaAmbulante.size() == 4 * mapa.getMapSize()) {
			System.out.println("nema mjesta za novu ambulantu");
			return false;
		}
		mapa.lockMap();
		try {
			if(mapa.getMapaXY(mapaX, mapaY) instanceof Ambulanta || mapa.getMapaXY(mapaX, mapaY) instanceof PokretniObjekat) {
				return false;
			}else {
				Ambulanta ambulanta = new Ambulanta(mapaX, mapaY);
				ambulanta.setKapacitet(izracunajKapacitetAmbulante(listaStanovnika.size()));
				listaAmbulante.add(ambulanta);
				mapa.setMapaXY(mapaX, mapaY, ambulanta);
				MainGUI.setTextField(mapaX, mapaY, "A", ambulanta.getColor());
				return true;
			}
		}finally {
			mapa.unlockMap();
		}
	}
	
	public static int izracunajKapacitetAmbulante(int brojStanovnika) {
		int rez = (int)(brojStanovnika*((10.0+Math.random()*5.0)/100.0));
		if(rez < 1) 
			return MIN_BOLNICA_KAPACITET;
		return rez;
	}

	public static void startSimulacija() {
		for(Ambulanta ambulanta : listaAmbulante) {
			Thread thread = new Thread(ambulanta);
			thread.start();
		}
		
		for(KontrolniPunkt kp : listaKP) {
			Thread thread = new Thread(kp);
			thread.start();
		}
		
		for(Stanovnik stanovnik : listaStanovnika) {
			Thread thread = new Thread(stanovnik);
			thread.start();
			stanovnik.startTemperatura();
		}
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeObject(listaStanovnika);
		oos.writeObject(listaZarazenih);
		oos.writeObject(listaOporavljenih);
		oos.writeObject(listaKuca);
		oos.writeObject(listaAmbulante);
		oos.writeObject(listaKP);
		oos.writeObject(sistemZaNadzor);
		for(Stanovnik s: listaStanovnika) {
			s.krajKretanja = true;
		}
		for(Ambulanta a: listaAmbulante) {
			a.prekidRada = true;
		}
		for(KontrolniPunkt kp: listaKP) {
			kp.prekidIzvrsavanjaPunkt();
		}
	}
	
	@SuppressWarnings("unchecked") // VRIJEME PAUZIRATI KAD SE PAUZIRA SIMULACIJA, KOD PONOVNOG POKRETANJA IMA SRANJA POGLEDATI
	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
		listaStanovnika.clear();
		listaZarazenih.clear();
		listaOporavljenih.clear();
		listaKuca.clear();
		listaAmbulante.clear();
		listaKP.clear();
		listaStanovnika = (ArrayList<Stanovnik>)ois.readObject();
		listaZarazenih = (List<CovjekStatistika>)ois.readObject();
		listaOporavljenih = (List<CovjekStatistika>)ois.readObject();
		listaKuca = (ArrayList<Kuca>)ois.readObject();
		listaAmbulante = (ArrayList<Ambulanta>)ois.readObject();
		listaKP = (ArrayList<KontrolniPunkt>)ois.readObject();
		sistemZaNadzor = (Nadzor)ois.readObject();
		postaviMapu();
		startSimulacija();
	}
	
	public void postaviMapu() {
		mapa = new Mapa(mapSize);
		for(Ambulanta a: listaAmbulante) {
			a.prekidRada = false;
			mapa.setMapaXY(a.getPozicijaX(), a.getPozicijaY(), a);
		}
		for(Stanovnik s: listaStanovnika) {
			s.krajKretanja = false;
			mapa.setMapaXY(s.getLokacijaX(), s.getLokacijaY(), s);
		}
		for(Kuca k: listaKuca) {
			mapa.setMapaXY(k.getPozicijaX(), k.getPozicijaY(), k);
		}
		for(KontrolniPunkt kp : listaKP) {
			kp.setRunning(true);
			mapa.setMapaXY(kp.getPozicijaX(), kp.getPozicijaY(), kp);
		}
		for(AmbulantaVozilo av: sistemZaNadzor.getlistaVozila()) {
			if(!av.jeSlobodno) {
				mapa.setMapaXY(av.getLokacijaX(), av.getLokacijaY(), av);
			}
		}
	}

}
