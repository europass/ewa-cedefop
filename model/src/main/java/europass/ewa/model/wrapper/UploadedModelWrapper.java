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
package europass.ewa.model.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import europass.ewa.model.SkillsPassport;

@JsonRootName("Uploaded")
public class UploadedModelWrapper implements ModelContainer {

    private SkillsPassport esp;

    private NegativeFeedbackList info;

    public UploadedModelWrapper(SkillsPassport esp) {
        this.setEsp(esp);
    }

    @JsonProperty("SkillsPassport")
    public SkillsPassport getEsp() {
        if (esp == null) {
            esp = new SkillsPassport();
        }
        return esp;
    }

    public void setEsp(SkillsPassport esp) {
        this.esp = esp;
    }

    @JsonProperty("Feedback")
    public NegativeFeedbackList getInfo() {
        if (info == null) {
            info = new NegativeFeedbackList(new ArrayList<Feedback>());
        }
        return info;
    }

    public void setInfo(List<Feedback> furtherInfo) {
        getInfo().addAll(furtherInfo);
    }

    public boolean add(Feedback feedback) {
        return getInfo().add(feedback);
    }

    @JsonIgnore
    @Override
    public SkillsPassport getModel() {
        return this.getEsp();
    }
}
