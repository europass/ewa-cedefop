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

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class CopyOfMiscTest {


	@Test
	public void asxeto() throws IOException, URISyntaxException{
		final File file = new File(  currentThread().getContextClassLoader().getResource("json/uploaded.json").toURI());
		
		String uploadResponse = FileUtils.readFileToString(file);
		
		String uploadedStr = "\"Uploaded\" : {";

		int idx1 = uploadResponse.indexOf(uploadedStr);

		int start = idx1 + uploadedStr.length();

		String feedbackStr = "\"Feedback\" : [";

		int idx2 = uploadResponse.indexOf(feedbackStr);

		int pos = idx2-1;
		char c = uploadResponse.charAt( pos );
		while ( c != ',' ){
		  pos = pos-1;
		  c = uploadResponse.charAt( pos );
		}

		int end = pos-1;

		String json = "{" + uploadResponse.substring( start, end ) + "}}";
		
		assertThat(json.indexOf("{\r\n    \"SkillsPassport")==0, CoreMatchers.is(true));
	}
}
