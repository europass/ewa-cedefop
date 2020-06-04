/*
 * JobRelatedException.java
 *
 * Created on May 31, 2016
 *            www.eworx.gr
 */

package europass.ewa.oo.server.exception;

/**
 *
 * @author JK
 */
public class JobRelatedException extends RuntimeException{
	
	
	private static final long serialVersionUID = 7548155921411858408L;	
	
	public JobRelatedException( String message ){
		super( message );
	}
	
	public JobRelatedException( Throwable cause ){
		super( cause );
	}
	
	public JobRelatedException( String message, Throwable cause ){
		super( message, cause );
	}
}