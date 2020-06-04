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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import europass.ewa.Utils;

public class UtilsTest {

	
	@Test
	public void permissions(){
	
		Map<Integer,Boolean> expected = new HashMap<>();
		expected.put( Integer.valueOf( 0 ), Boolean.FALSE );
		expected.put( Integer.valueOf( -1852 ), Boolean.FALSE );
		expected.put( Integer.valueOf( -1292 ), Boolean.FALSE );
		expected.put( Integer.valueOf( -1084 ), Boolean.FALSE );
		expected.put( Integer.valueOf( 1852 ), Boolean.TRUE );
		expected.put( Integer.valueOf( 1292 ), Boolean.TRUE );
		expected.put( Integer.valueOf( 1084 ), Boolean.TRUE );
		
		for ( Entry<Integer, Boolean> entry : expected.entrySet() ){
			int v = entry.getKey().intValue();
			
			boolean isOn = Utils.isBitOn( v, 10 );
			
			Assert.assertThat("Bit 11 in Value "+ v, isOn, CoreMatchers.is( entry.getValue().booleanValue() ));
		}
	}
}
