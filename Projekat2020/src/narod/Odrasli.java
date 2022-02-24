package narod;

import static simulacija.Simulacija.mapa;

import java.util.Random;

public class Odrasli extends Stanovnik{

	private static final long serialVersionUID = 1L;

	public Odrasli() {
		super();
		godine = 18 + new Random().nextInt(47);
	}
	
	@Override
	public void provjeraOpsega() {// sva polja( >= Lijevo , <= Desno , >= Dole , <= Gore)
		
		int duzinaMape = mapa.getMapSize();
		int radijusKretanja = (int)Math.round(duzinaMape/4.0);
		granicaDesno = kuca.getPozicijaX() + radijusKretanja;
		granicaLijevo = kuca.getPozicijaX() - radijusKretanja;
		granicaDole = kuca.getPozicijaY() - radijusKretanja;
		granicaGore = kuca.getPozicijaY() + radijusKretanja;
		
		if(kuca.getPozicijaX() + radijusKretanja >= duzinaMape) {
			int razlika = duzinaMape - kuca.getPozicijaX()- 1;
			granicaDesno = duzinaMape-1;
			granicaLijevo += radijusKretanja - razlika;
		}
		if(kuca.getPozicijaX() - radijusKretanja < 0) {
			int razlika = radijusKretanja - kuca.getPozicijaX();
			granicaDesno += razlika;
			granicaLijevo = 0;
		}
		if(kuca.getPozicijaY() + radijusKretanja >= duzinaMape) {
			int razlika = duzinaMape - kuca.getPozicijaY() -1;
			granicaGore = duzinaMape -1;
			granicaDole += radijusKretanja - razlika;
		}
		if(kuca.getPozicijaY() - radijusKretanja < 0) {
			int razlika = radijusKretanja - kuca.getPozicijaY();
			granicaGore += razlika;
			granicaDole = 0;
		}
	}

}
