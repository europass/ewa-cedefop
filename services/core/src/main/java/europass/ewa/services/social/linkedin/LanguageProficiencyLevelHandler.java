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
package europass.ewa.services.social.linkedin;

import java.util.List;

import europass.ewa.model.CEFRLevel;
import europass.ewa.model.CodeLabel;
import europass.ewa.model.LinguisticSkill;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;

public class LanguageProficiencyLevelHandler implements Transformer {

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(from instanceof List<?>)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof List<?>)) {
            throw new InstanceClassMismatchException();
        }
        if (!(params[0] instanceof List<?>)) {
            throw new InstanceClassMismatchException();
        }

        if (((List<?>) from).size() != ((List<?>) params[0]).size()) {
            return to;
        }

        try {
            List<String> languagesList = (List<String>) from;
            List<String> languagesLevelList = (List<String>) params[0];
            List<LinguisticSkill> foreign = (List<LinguisticSkill>) to;

            CEFRLevel level = null;
            LinguisticSkill skill = null;

            for (String lang : languagesList) {

                CodeLabel cLabel = new CodeLabel();
                cLabel.setLabel(lang);

                int index = languagesList.indexOf(lang);

                switch (languagesLevelList.get(index)) {

                    case "elementary":
                        level = new CEFRLevel("A1", "A1", "A1", "A1", "A1");
                        break;
                    case "limited-working":
                        level = new CEFRLevel("B1", "B1", "B1", "B1", "B1");
                        break;
                    case "professional-working":
                        level = new CEFRLevel("C1", "C1", "C1", "C1", "C1");
                        break;
                    case "full-professional":
                        level = new CEFRLevel("C2", "C2", "C2", "C2", "C2");
                        break;
                    default:
                        level = new CEFRLevel();
                        break;
                }

                skill = new LinguisticSkill(cLabel);
                skill.setProficiencyLevel(level);
                foreign.add(skill);

            }
            return foreign;

        } catch (final Exception e) {
            return to;
        }
    }
}
