package narod;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Temperatura extends Thread implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final int SLEEP_TIME = 30000;
	private final int BROJ_MJERENJA = 3;
	private double izmjerenaTemperatura;
	private double[] zadnjaTriMjerenja = new double[BROJ_MJERENJA];
	
	public Temperatura() {
		super();
		for(int i=0;i<BROJ_MJERENJA;i++) {
			zadnjaTriMjerenja[i] = 36.5;
		}
	}
	
	public double getIzmjerenaTemperatura() {
		return izmjerenaTemperatura;
	}
	
	public double prosjekTemperature() {   
		double suma = 0.0;
		for(double x : zadnjaTriMjerenja) {
			suma += x;
		}
		return suma / BROJ_MJERENJA;
	}
	
	public void run() {
		int i = 0;
		while(true) {
			try {
				Thread.sleep(SLEEP_TIME);
			}catch(InterruptedException e) { 
				Logger.getLogger(Temperatura.class.getName()).log(Level.SEVERE,e.toString());
			}
			izmjerenaTemperatura = 35.0 + Math.random() * 4;
			zadnjaTriMjerenja[i++ % BROJ_MJERENJA] =izmjerenaTemperatura;
		}
	}

}
