package narod;

import static simulacija.Simulacija.mapa;

import java.util.Random;

public class Dijete extends Stanovnik{

	private static final long serialVersionUID = 1L;

	public Dijete() {
		super();
		godine = new Random().nextInt(18);
	}

	@Override
	public boolean provjeraDistanceNaPolju(int x , int y) {
		for(int i = x-2; i<= x+2; i++) {
			for(int j= y-2; j<= y+2; j++) {
				if(mapa.getMapaXY(x, y) instanceof Stari) {
					return false;
				}
			}
		}
		return true;
	}
	
}
