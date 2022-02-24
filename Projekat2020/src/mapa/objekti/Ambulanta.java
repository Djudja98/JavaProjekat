package mapa.objekti;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import narod.CovjekStatistika;
import narod.Stanovnik;
import simulacija.Simulacija;

public class Ambulanta extends NepokretniObjekat implements Runnable{

	private static final long serialVersionUID = 1L; 
	public static final String BOLNICA_STATISTIKA ="bolnicaStatistika.txt";
	private static final int COLOR_INT = (int)0xC9FFE5;
	private static final int SLEEP_TIME = 5000;
	
	private static int brojAmbulanti;
	private int kapacitet;	// kapacitet setovati kad krene simulacija na 10-15%
	public boolean prekidRada;
	private int naruceno;
	ArrayList<Stanovnik> listaBolesnika = new ArrayList<>();
	private static ReadWriteLock statistikaFajlLock = new ReentrantReadWriteLock();
	public static Lock ambulanteLock = new ReentrantLock();
	
	static {
		statistikaFajlLock.writeLock().lock();
		try(PrintWriter pw = new PrintWriter(new File(BOLNICA_STATISTIKA))){
			pw.print("zarazeni#0#oporavljeni#0");
		} catch (FileNotFoundException e) {
			Logger.getLogger(Ambulanta.class.getName()).log(Level.SEVERE,e.toString());
		}
		finally {
			statistikaFajlLock.writeLock().unlock();
		}
	}

	public Ambulanta(int x, int y) {
		super(x, y);
		brojAmbulanti++;
		color = new Color(COLOR_INT);
	}
	
	public int getBrojAmbulanti() {
		return brojAmbulanti;
	}
	
	public void setKapacitet(int kapacitet) {
		this.kapacitet = kapacitet;
	}
	
	public int getKapacitet() {
		return kapacitet;
	}
	
	public int getBrojSlobodnihMjesta() {
		return kapacitet - naruceno - listaBolesnika.size();
	}
	
	public boolean isFull() {
		return getBrojSlobodnihMjesta() == 0; 
	}
	
	@Override
	public String toString() {
		return "Ambul";
	
	}
	
	public boolean naruciBolesnika() {
		if(listaBolesnika.size() + naruceno < kapacitet) {
			naruceno++;
			return true;
		}
		return false;
	}
	
	public static int getBrojBolesnika() {
		int brojBolesnika = 0;
		statistikaFajlLock.readLock().lock();
		try(BufferedReader br = new BufferedReader(new FileReader(new File(BOLNICA_STATISTIKA)))){
			String tekst = br.readLine();
			brojBolesnika = Integer.parseInt(tekst.split("#")[1]);
		} catch (Exception e) {
			Logger.getLogger(Ambulanta.class.getName()).log(Level.SEVERE,e.toString());
		}finally {
			statistikaFajlLock.readLock().unlock();
		}
		return brojBolesnika;
	}
	
	public static int getBrojOporavljenih() {
		int brojOporavljenih = 0;
		statistikaFajlLock.readLock().lock();
		try(BufferedReader br = new BufferedReader(new FileReader(new File(BOLNICA_STATISTIKA)))){
			String tekst = br.readLine();
			brojOporavljenih = Integer.parseInt(tekst.split("#")[3]);
		} catch (Exception e) {
			Logger.getLogger(Ambulanta.class.getName()).log(Level.SEVERE,e.toString());
		}finally {
			statistikaFajlLock.readLock().unlock();
		}
		return brojOporavljenih;
	}

	public boolean dodajBolesnika(Stanovnik bolesnik) {
		if(naruceno + listaBolesnika.size() <= kapacitet) {
			int brojZarazenih = getBrojBolesnika();
			int brojOporavljenih = getBrojOporavljenih();
			brojZarazenih++;
			statistikaFajlLock.writeLock().lock();
			try(PrintWriter pw = new PrintWriter(new File(BOLNICA_STATISTIKA))){
				pw.print("zarazeni#"+ brojZarazenih +"#oporavljeni#" + brojOporavljenih);
			}catch(FileNotFoundException e) {
				Logger.getLogger(Ambulanta.class.getName()).log(Level.SEVERE,e.toString());
			}finally {
				Simulacija.listaZarazenih.add(new CovjekStatistika(bolesnik.getPol(),
												bolesnik.getGodine(),bolesnik.getIme(),bolesnik.getPrezime()));
				statistikaFajlLock.writeLock().unlock();
			}
			if(!listaBolesnika.contains(bolesnik)) {
				listaBolesnika.add(bolesnik);
			}
			naruceno--;
			return true;
		}
		else {
			return false;
		}
	}
	
	public void otpustiBolesnika(Stanovnik bolesnik) {
		listaBolesnika.remove(bolesnik);
		int brojZarazenih = getBrojBolesnika();
		int brojOporavljenih = getBrojOporavljenih();
		brojZarazenih--;
		brojOporavljenih++;
		statistikaFajlLock.writeLock().lock();
		try(PrintWriter pw = new PrintWriter(new File(BOLNICA_STATISTIKA))){
			pw.print("zarazeni#"+ brojZarazenih +"#oporavljeni#" + brojOporavljenih);
		}catch(FileNotFoundException e) {
			Logger.getLogger(Ambulanta.class.getName()).log(Level.SEVERE,e.toString());
		}finally {
			Simulacija.listaOporavljenih.add(new CovjekStatistika(bolesnik.getPol(),
					bolesnik.getGodine(),bolesnik.getIme(),bolesnik.getPrezime()));
			Simulacija.listaZarazenih.remove(new CovjekStatistika(bolesnik.getPol(),
					bolesnik.getGodine(),bolesnik.getIme(),bolesnik.getPrezime()));
			statistikaFajlLock.writeLock().unlock();
		}
		bolesnik.zarazen = false;
		bolesnik.setDefaultGranice();
		bolesnik.potencijalnoZarazen = true;
		bolesnik.setLokacijaX(pozicijaX);
		bolesnik.setLokacijaY(pozicijaY);
		synchronized (bolesnik) {
			bolesnik.notify();
		}
	}
	
	@Override
	public void run() {
		while(!prekidRada) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (Exception e) {
				Logger.getLogger(Ambulanta.class.getName()).log(Level.SEVERE,e.toString());
			}
			for(int i=0; i < listaBolesnika.size(); i++) {
				if(listaBolesnika.get(i).getTemperatura().prosjekTemperature() < 37) {
					System.out.println("Stanovnik "+ listaBolesnika.get(i) + " ozdravio");
					otpustiBolesnika(listaBolesnika.get(i));
				}
			}
		}
	}

}
