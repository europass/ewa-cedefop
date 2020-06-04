/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package europass.ewa.services.social.linkedIn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import europass.ewa.model.FileData;
import europass.ewa.services.social.ExtraDataMockObjects;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.MockLinkedInProfile;
import europass.ewa.services.social.linkedin.PhotoHandler;

public class PhotoHandlerTest {

	private static FileData photoData;
	private static PhotoHandler handler;
	
	@BeforeClass
	public static void setup(){
		handler = new PhotoHandler();
	}
	
	@Test
	public void test() throws InstanceClassMismatchException{
		
		photoData = (FileData) handler.transform(ExtraDataMockObjects.pictureHashMapObject(), new FileData(), MockLinkedInProfile.COOKIE_ID);

		assertEquals("image/jpeg",photoData.getMimeType());
	}
	
	@SuppressWarnings("unused")
	private void outputBytesToConsole(byte[] bytes){
		int i = 0;
    	for( byte b : bytes ) {
    		
	    	System.out.printf("0x%x\t", b);
	    	i++;

	    	if(i%12 == 0)
    			System.out.println("");
    	}
	}
}
