package mapa.objekti;

import java.io.Serializable;

public class Tacka implements Serializable{

	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	
	public Tacka(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final Tacka other = (Tacka)obj;
		if(this.x != other.x || this.y != other.y) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "( "+ x + ", "+ y + " )";
	}

}
