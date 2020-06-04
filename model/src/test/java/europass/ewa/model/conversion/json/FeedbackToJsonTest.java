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
package europass.ewa.model.conversion.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.Feedback.Code;
import europass.ewa.model.wrapper.Feedback.Level;
import europass.ewa.model.wrapper.UploadedModelWrapper;

public class FeedbackToJsonTest extends JsonMapperTest {

    @Test
    public void asJson() throws JsonProcessingException {

        List<Feedback> f = new ArrayList<Feedback>();
        f.add(new Feedback(Level.WARN, Code.UPLOAD_PHOTO));
        f.add(new Feedback(Level.WARN, Code.UPLOAD_ATTACHMENT, "MyDiploma.pdf"));

        ObjectMapper m = this.getMapper();
        m.configure(SerializationFeature.INDENT_OUTPUT, false);
        m.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        String s = m.writeValueAsString(f);

        assertNotNull(s);

        String expected = "["
                + "{\"level\":\"WARN\","
                + "\"code\":\"esp.upload.no.photo\"},"
                + "{\"level\":\"WARN\","
                + "\"code\":\"esp.upload.no.attachment\","
                + "\"section\":{\"key\":\"[[section]]\",\"value\":\"MyDiploma.pdf\"}}]";

        assertThat(s, CoreMatchers.is(expected));
    }

    @Test
    public void asJsonr() throws JsonProcessingException {

        List<Feedback> f = new ArrayList<Feedback>();
        f.add(new Feedback(Level.WARN, Code.UPLOAD_PHOTO));
        f.add(new Feedback(Level.WARN, Code.UPLOAD_ATTACHMENT, "MyDiploma.pdf"));

        UploadedModelWrapper w = new UploadedModelWrapper(null);
        w.setInfo(f);

        ObjectMapper m = this.getMapper();
        m.configure(SerializationFeature.INDENT_OUTPUT, false);
        m.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        String s = m.writeValueAsString(w);

        assertNotNull(s);

        assertThat(s.startsWith("{\"SkillsPassport\":"), CoreMatchers.is(true));
    }
}
