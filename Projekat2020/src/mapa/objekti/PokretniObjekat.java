package mapa.objekti;

import static simulacija.Simulacija.mapa;

import java.awt.Color;
import java.io.Serializable;

import narod.StanovnikUtil;

public abstract class PokretniObjekat implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected int lokacijaX;
	protected int lokacijaY;
	protected Color color;
	protected int granicaDesno, granicaLijevo; // opseg je dovoljno jednom na pocetku izracunati
	protected int granicaGore, granicaDole;
	
	public PokretniObjekat() { 
	}
	
	public void provjeraOpsega() {   // sva polja( => Lijevo , <= Desno , => Dole , <= Gore)
		setDefaultGranice();
	}
	
	public abstract boolean pomjeraj(int novoX, int novoY);
	
	public Color getColor() {
		return color;
	}
	
	public int getLokacijaX() {
		return lokacijaX;
	}
	
	public int getLokacijaY() {
		return lokacijaY;
	}
	
	public void setLokacijaX(int x) {
		this.lokacijaX = x;
	}
	
	public void setLokacijaY(int y) {
		this.lokacijaY = y;
	}
	
	public String ispisSmjera(int novoX, int novoY) {
		String smjer = "";
		if(novoX > lokacijaX) {
			smjer = SMJER_KRETANJA.DESNO.name();
			if(novoY < lokacijaY) {
				smjer = SMJER_KRETANJA.DOLE_DESNO.name();
			}
			if(novoY > lokacijaY) {
				smjer = SMJER_KRETANJA.GORE_DESNO.name();
			}
		}
		if(novoX < lokacijaX) {
			smjer = SMJER_KRETANJA.LIJEVO.name();
			if(novoY < lokacijaY) {
				smjer = SMJER_KRETANJA.DOLE_LIJEVO.name();
			}
			if(novoY > lokacijaY) {
				smjer = SMJER_KRETANJA.GORE_LIJEVO.name();
			}
		}
		if(novoX == lokacijaX) {
			if(novoY < lokacijaY) {
				smjer = SMJER_KRETANJA.DOLE.name();
			}
			else {
				smjer = SMJER_KRETANJA.GORE.name();
			}
		}
		return smjer;
	}
	
	public boolean provjeriTacku(int x, int y, int odredisteX, int odredisteY) {
		if(x > granicaDesno || x < granicaLijevo 
				|| y > granicaGore || y < granicaDole 
				|| (x == lokacijaX && y == lokacijaY)
				|| (StanovnikUtil.distanca(odredisteX, x, odredisteY, y) > StanovnikUtil.distanca(odredisteX, lokacijaX, odredisteY, lokacijaY))) {
			return false;
		}
		return true;
	}
	
	public void setDefaultGranice() {
		granicaDesno = mapa.getMapSize() -1;
		granicaGore = mapa.getMapSize()-1;
		granicaDole = 0;
		granicaLijevo = 0;
	}
	
	public enum SMJER_KRETANJA{
		U_LERU, 
		GORE, 
		DESNO, 
		LIJEVO, 
		DOLE,
		GORE_DESNO,
		GORE_LIJEVO,
		DOLE_DESNO,
		DOLE_LIJEVO;
	}
}
