package narod;

import java.io.Serializable;

import narod.Stanovnik.Pol;

public class CovjekStatistika implements Serializable{

	private static final long serialVersionUID = 1L;
	public String ime;
	public String prezime;
	public Pol pol;
	public int godine;
	
	public CovjekStatistika(Pol pol, int godine,String ime,String prezime) {
		this.pol = pol;
		this.godine = godine;
		this.ime = ime;
		this.prezime = prezime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		if(pol != ((CovjekStatistika)obj).pol || godine != ((CovjekStatistika)obj).godine){
			return false;
		}
		return true;
	}
	
	public String toCSV() {
		return ime + "," + prezime + "," + pol + "," + godine;
	}

}
