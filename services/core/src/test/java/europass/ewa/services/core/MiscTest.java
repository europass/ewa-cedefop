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
package europass.ewa.services.core;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.junit.Test;

import europass.ewa.Utils;
import europass.ewa.model.Identification;
import europass.ewa.model.format.NumericUtils;

public class MiscTest {

	@Test
	public void stringToTitleCase(){
		String str = "van de    Broek";
		
		String title = Utils.removePunctuation(str);
		
		assertThat( title, is("VanDeBroek") );
	}
	
	@Test
	public void greekStringToTitleCase(){
		String str = "Αντωνίου παπαδοπούλου";
		
		String title = Utils.removePunctuation(str);
		
		assertThat( title, is("ΑντωνίουΠαπαδοπούλου") );
	}
	
	@Test
	public void greekStringToUpperCase(){
		String str = "Κώστας";
		
		String title = capitalize(str);
		
		assertThat( title, is("ΚΩΣΤΑΣ") );
	}
	
	public String capitalize( String str ){
		str = Normalizer.normalize(str, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return str.toUpperCase();
	}
	
	@Test
	public void intWithPrecisionThree(){
		double n = (double) 1030 / 1200;
		
		int res = NumericUtils.asAugmentedInt( n , 3 );
		
		assertThat( res, is( 858 ) );
	}
	
	@Test
	public void intWithPrecisionTwo(){
		double n = (double) 1030 / 1200;
		
		int res = NumericUtils.asAugmentedInt( n , 2 );
		
		assertThat( res, is( 86 ) );
	}
	
	@Test
	public void intWithPrecisionOne(){
		double n = (double) 1030 / 1200;
		
		int res = NumericUtils.asAugmentedInt( n , 1 );
		
		assertThat( res, is( 9 ) );
	}
	
	@Test
	public void bigIntWithPrecisionOne(){
		double n = (double) 2830 / 1200;
		
		int res = NumericUtils.asAugmentedInt( n , 1 );
		
		assertThat( res, is( 2 ) );
	}
	
	@Test
	public void intWithZeroDecimalPrecision(){
		double n = 1000.68888;
		
		int res = NumericUtils.asInt( n );
		
		assertThat( res, is( 1001 ) );
	}
	
	@Test
	public void intToInt(){
		double n = 1002;
		
		int res = NumericUtils.asInt( n );
		
		assertThat( res, is( 1002 ) );
	}
	
	@Test
	public void acceptedRatio(){
		assertThat( Identification.isPhotoRatioCompatible( 1035, 1200) , is(true));
	}
	@Test
	public void acceptedRatioClose(){
		assertThat( Identification.isPhotoRatioCompatible( 1030, 1200), is(true));
	}
	@Test
	public void notAcceptedRatioPortrait(){
		int w = 1000;
		int h = 1200;
		
		assertThat( Identification.isPhotoRatioCompatible( w, h ), is(false));
		int[] in = { w, h };
		int[] d = Identification.asCompatiblePhoto( in );
		assertThat("new w ", d[0], is(1000) );
		assertThat("new h", d[1], is(1158) );
	}
	@Test
	public void notAcceptedRatioLandscape(){
		int w = 1200;
		int h = 1000;
		
		assertThat( Identification.isPhotoRatioCompatible( w, h ), is(false));
		int[] in = { w, h };
		int[] d = Identification.asCompatiblePhoto( in );
		assertThat("new w ", d[0], is(864) );
		assertThat("new h", d[1], is(1000) );
	}
}
