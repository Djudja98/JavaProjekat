package simulacija;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import mapa.objekti.Ambulanta;
import mapa.objekti.AmbulantaVozilo;
import narod.Stanovnik;

public class Nadzor implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Stack<Alarm> alarmiStack = new Stack<Alarm>();
	private List<AmbulantaVozilo> ambulantaVozila = new ArrayList<>();
	public static Lock nadzorLock = new ReentrantLock();
	
	public Nadzor() {	
	}	
	
	public Nadzor(int brojAmbulantnihVozila) {
		for(int i=0; i<brojAmbulantnihVozila; i++) {
			ambulantaVozila.add(new AmbulantaVozilo());
		}
	}
	
	public List<AmbulantaVozilo> getlistaVozila(){
		return ambulantaVozila;
	}
	
	public boolean dodajAlarm(Alarm alarm) {
		nadzorLock.lock();
		try {
			if(alarmiStack.search(alarm) == -1) {
				alarmiStack.push(alarm);
				return true;
			}
		}finally {
			nadzorLock.unlock();
		}
		return false;
	}
	
	public Alarm getAlarm() {
		if(alarmiStack.isEmpty()) {
			return null;
		}
		return alarmiStack.pop();
	}
	
	public void vratiUkucaneKuci(Alarm alarm) {
		for(int i = 0; i < Simulacija.listaKuca.size(); i++) {
			if(Simulacija.listaKuca.get(i).getID() == alarm.getKucniID()) {
				for(Stanovnik s : Simulacija.listaKuca.get(i).getListaUkucana()) {
					if(! s.zarazen) {
						s.potencijalnoZarazen = true;
						System.out.println(s + " se vraca kuci");
					}
				}
			}
		}
	}
	
	public static Ambulanta slobodnaAmbulanta() {
		Ambulanta slobodnAmbulanta = null;
		Ambulanta.ambulanteLock.lock();
		try {
			for(int i = 0; i < Simulacija.listaAmbulante.size(); i++) {
				if(Simulacija.listaAmbulante.get(i).naruciBolesnika()) {
					slobodnAmbulanta = Simulacija.listaAmbulante.get(i);
					break;
				}
			}
		}finally {
			Ambulanta.ambulanteLock.unlock();
		}
		return slobodnAmbulanta;
	}
	
	public void posaljiAmbulantnaVozila() {
		if(alarmiStack.isEmpty()) {
			System.out.println("nema zarazenih");
		}
		while(!alarmiStack.isEmpty()) {
			AmbulantaVozilo aVozilo = null;
			for (AmbulantaVozilo vozilo : ambulantaVozila) {
				if(vozilo.jeSlobodno) {
					vozilo.jeSlobodno=false;
					Ambulanta ambulanta = slobodnaAmbulanta();
					if(ambulanta == null) {
						vozilo.jeSlobodno = true;
						System.out.println("Nema dostupnih ambulanti");
						return;
					} else {
						aVozilo = vozilo;
						Alarm alarm = alarmiStack.pop();
						vratiUkucaneKuci(alarm);
						aVozilo.setAlarm(alarm);
						aVozilo.setAmbulanta(ambulanta);
						aVozilo.setLokacijaX(ambulanta.getPozicijaX());
						aVozilo.setLokacijaY(ambulanta.getPozicijaY());
						break;
					}
				}
			}
			if(aVozilo != null) {
				Thread t1=new Thread(aVozilo);
				t1.start();
			} else {
				System.out.println("Nema vise dostupnih ambulantnih vozila.");
				return;
			}
		}
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		for(AmbulantaVozilo av: ambulantaVozila) {
			av.prekidRada = true;
		}
	}
	
	private void readObject(ObjectInputStream ois) throws Exception{
		ois.defaultReadObject();
		for(AmbulantaVozilo av: ambulantaVozila) {
			av.prekidRada = false;
			new Thread(av).start();
		}
	}

}
