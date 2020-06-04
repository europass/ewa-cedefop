package europass.ewa.oo.server.exception;

public class IllegalODTTemplateException extends RuntimeException {

	private static final long serialVersionUID = 6568175921611158608L;

	public IllegalODTTemplateException( String message ){
		super( message );
	}
	
	public IllegalODTTemplateException( Throwable cause ){
		super( cause );
	}
	
	public IllegalODTTemplateException( String message, Throwable cause ){
		super( message, cause );
	}
}
