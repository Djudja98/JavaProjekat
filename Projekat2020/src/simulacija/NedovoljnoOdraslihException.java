package simulacija;

public class NedovoljnoOdraslihException extends Exception{
	private static final long serialVersionUID = 1L;

	public NedovoljnoOdraslihException() {
		super("Nema dovoljno odraslih i starih da se djeca rasporede");
	}
	
	public NedovoljnoOdraslihException(String msg) {
		super(msg);
	}

}
