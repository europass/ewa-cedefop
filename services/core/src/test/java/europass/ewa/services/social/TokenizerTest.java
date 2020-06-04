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
package europass.ewa.services.social;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import europass.ewa.services.social.PathTokenizer;
import europass.ewa.services.social.Token;

public class TokenizerTest {

	private static final String A = "alpha";
	
	private static final String B = "beta";
	
	@Test
	public void current() throws ParseException{
		String p = "";
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 1 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_CURRENT );
	}
	
	@Test
	public void root() throws ParseException{
		String p = "/";
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 1 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_ROOT );
	}
	
	@Test
	public void alpha() throws ParseException{
		
		PathTokenizer tokenizer = PathTokenizer.compile( A );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 2 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_CURRENT );
		
		assertEquals( tokens.get( 1 ).getType(), A );
	}
	
	@Test
	public void alphaBelowRoot() throws ParseException{
		
		String p = "/"+A;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 2 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_ROOT );
		
		assertEquals( tokens.get( 1 ).getType(), A );
	}
	
	@Test
	public void alphaBelowCurrent() throws ParseException{
		String p = "./"+A;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 2 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_CURRENT );
		
		assertEquals( tokens.get( 1 ).getType(), A );
	}
	
	@Test
	public void betaBelowAlphaAndRoot() throws ParseException{
		String p = "/"+A+"/"+B;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 3 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_ROOT );
		
		assertEquals( tokens.get( 1 ).getType(), A );
		
		assertEquals( tokens.get( 2 ).getType(), B );
	}
	
	@Test
	public void betaBelowAlphaAndCurrent() throws ParseException{
		String p = "./"+A+"/"+B;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 3 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_CURRENT );
		
		assertEquals( tokens.get( 1 ).getType(), A );
		
		assertEquals( tokens.get( 2 ).getType(), B );
	}
	
	@Test
	public void betaBelowAlpha() throws ParseException{
		String p = A+"/"+B;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		List<Token> tokens = tokenizer.tokens();
		
		assertThat( tokens.size(), CoreMatchers.is( 3 ) );
		
		assertEquals( tokens.get( 0 ).getType(), Token.TYPE_CURRENT );
		
		assertEquals( tokens.get( 1 ).getType(), A );
		
		assertEquals( tokens.get( 2 ).getType(), B );
	}
}
