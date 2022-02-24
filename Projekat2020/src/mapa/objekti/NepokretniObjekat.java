package mapa.objekti;

import java.awt.Color;
import java.io.Serializable;

public class NepokretniObjekat implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static int count;
	protected int id;
	protected Color color;
	protected int pozicijaX;
	protected int pozicijaY;
	
	public NepokretniObjekat(int x,int y) {
		id = ++count;
		this.pozicijaX = x;
		this.pozicijaY = y;
	}
	
	public int getID() {
		return id;
	}
	
	public int getPozicijaX() {
		return pozicijaX;
	}
	
	public int getPozicijaY() {
		return pozicijaY;
	}
	
	public Color getColor() {
		return color;
	}

}
