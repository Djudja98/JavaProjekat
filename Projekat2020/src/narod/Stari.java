package narod;

import static simulacija.Simulacija.mapa;

import java.util.Random;

public class Stari extends Stanovnik{

	private static final long serialVersionUID = 1L;

	public Stari() {
		super();
		godine = 65 + new Random().nextInt(35);
	}
	
	@Override 
	public void provjeraOpsega() {
		int duzinaMape = mapa.getMapSize(); 
		int radijus = 3;
		
		granicaDesno = kuca.getPozicijaX() + radijus;
		granicaLijevo = kuca.getPozicijaX() - radijus;
		granicaGore = kuca.getPozicijaY() + radijus;
		granicaDole = kuca.getPozicijaY() - radijus;
		
		if(kuca.getPozicijaX() + radijus >= duzinaMape) {
			granicaDesno = duzinaMape - 1;
		}
		if(kuca.getPozicijaX() - radijus < 0) {
			granicaLijevo = 0;
		}
		if(kuca.getPozicijaY() + radijus >= duzinaMape) {
			granicaGore = duzinaMape -1;
		}
		if(kuca.getPozicijaY() - radijus < 0) {
			granicaDole = 0;
		}
	}
	
}
