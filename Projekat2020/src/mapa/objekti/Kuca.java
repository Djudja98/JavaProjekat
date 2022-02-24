package mapa.objekti;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import narod.Stanovnik;

public class Kuca extends NepokretniObjekat implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final int COLOR_INT = (int)0xFF19FF;
	
	private int trenutniBrojUkucana;
	private ArrayList<Stanovnik> listaUkucana = new ArrayList<>();
	
	public Kuca(int x, int y) { // x i y slucajno generisani u simulaciji
		super(x , y);
		color = new Color(COLOR_INT);
		trenutniBrojUkucana = 0;
	}
	
	public ArrayList<Stanovnik> getListaUkucana(){
		return listaUkucana;
	}
	
	public int getTrenutniBrojUkucana() {
		return trenutniBrojUkucana;
	}
	
	public void dodajUkucana(Stanovnik s) {
		listaUkucana.add(s);
		trenutniBrojUkucana++;
		s.setKuca(this);
		s.setLokacijaX(this.pozicijaX);
		s.setLokacijaY(this.pozicijaY);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		if(((Kuca)obj).id != id){
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "KUCA";
	}

}
