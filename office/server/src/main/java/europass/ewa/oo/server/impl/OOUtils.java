package europass.ewa.oo.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OOUtils  {
	
	private OOUtils(){}
  
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(OOUtils.class);
  
  public static  String ooFilePath(String absolutePath) {
    
    absolutePath = absolutePath.replace('\\','/');
    
    if ( !absolutePath.startsWith("/") )
      absolutePath = "/" + absolutePath;
      
    return "file://" + absolutePath;    
  }


  public static String contentType(String extension) {
    
    if ( "odt".equals(extension) ) {
      return "application/swriter";
    } 
    
    if ( "doc".equals(extension) ) {
      return "application/msword";
    } 
    
    if ( "rtf".equals(extension) ) {
      return "text/rtf";
    } 
    
    if ( "pdf".equals(extension) ) {
      return "application/pdf";
    }
    
    return null;
  }
  
  public static String sanitize(String str) {
    return str.replaceAll("\\r\\n", "\n");    
  }
  
  public static String getFilterName(String extension) {
    if ( "doc".equalsIgnoreCase(extension) ) {
      return "MS Word 97";
    } else if ( "rtf".equalsIgnoreCase(extension) ) {
      return "Rich Text Format";
    } else if ( "pdf".equalsIgnoreCase(extension) ) {
      return "writer_pdf_Export";
    } 
    return null;
  }

  
}