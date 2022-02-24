package simulacija;

import java.io.Serializable;

public class Alarm implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int kucniID;
	private int lokacijaX;
	private int lokacijaY;
	
	public Alarm(int kucaid, int x, int y) {
		kucniID = kucaid;
		lokacijaX = x;
		lokacijaY = y;
	}

	public int getKucniID() {
		return kucniID;
	}

	public int getLokacijaX() {
		return lokacijaX;
	}

	public int getLokacijaY() {
		return lokacijaY;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		if(kucniID != ((Alarm)obj).getKucniID() || lokacijaX !=((Alarm)obj).getLokacijaX() || lokacijaY != ((Alarm)obj).getLokacijaY()){
			return false;
		}
		return true;
	}
	
}
