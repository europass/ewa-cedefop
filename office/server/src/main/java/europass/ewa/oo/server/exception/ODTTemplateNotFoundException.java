package europass.ewa.oo.server.exception;

public class ODTTemplateNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6568175921611158608L;

	public ODTTemplateNotFoundException( String message ){
		super( message );
	}
	
	public ODTTemplateNotFoundException( Throwable cause ){
		super( cause );
	}
	
	public ODTTemplateNotFoundException( String message, Throwable cause ){
		super( message, cause );
	}
}
