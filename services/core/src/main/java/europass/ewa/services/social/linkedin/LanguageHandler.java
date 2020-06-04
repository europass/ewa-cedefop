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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import europass.ewa.model.CEFRLevel;
import europass.ewa.model.CodeLabel;
import europass.ewa.model.LinguisticSkill;
import europass.ewa.model.LinguisticSkills;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;
//import org.springframework.social.linkedin.api.Language;

public class LanguageHandler implements Transformer {

    private static final String EXTRA_DATA_LANGUAGES_KEY = "languages";

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(from instanceof HashMap)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof LinguisticSkills)) {
            throw new InstanceClassMismatchException();
        }
        try {

            ArrayList<LinkedHashMap<String, ?>> languagesValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_DATA_LANGUAGES_KEY);

            LinguisticSkills skills = (LinguisticSkills) to;

            for (LinkedHashMap<String, ?> valuesMap : languagesValues) {

                String language = (String) ((LinkedHashMap<String, String>) valuesMap.get("language")).get("name");

                LinkedHashMap<String, String> proficencyMap = (LinkedHashMap<String, String>) valuesMap.get("proficiency");
                String proficiencyLevel = proficencyMap != null ? proficencyMap.get("level") : null;

                LinguisticSkill skill = new LinguisticSkill(new CodeLabel(null, language));

                boolean isMother = (proficiencyLevel == null ? false : proficiencyLevel.equals("native_or_bilingual"));
                List<LinguisticSkill> list = isMother ? skills.getMotherTongue() : skills.getForeignLanguage();

                if (list == null) {
                    list = new ArrayList<LinguisticSkill>();
                }

                if (isMother) {
                    list.add(skill);
                    skills.setMotherTongue(list);
                } else {

                    if (proficiencyLevel != null) {
                        CEFRLevel level = null;
                        switch (proficiencyLevel) {

                            case "elementary":
                                level = new CEFRLevel("A1", "A1", "A1", "A1", "A1");
                                break;
                            case "limited_working":
                                level = new CEFRLevel("B1", "B1", "B1", "B1", "B1");
                                break;
                            case "professional_working":
                                level = new CEFRLevel("C1", "C1", "C1", "C1", "C1");
                                break;
                            case "full_professional":
                                level = new CEFRLevel("C2", "C2", "C2", "C2", "C2");
                                break;
                            default:
                                level = new CEFRLevel();
                                break;
                        }

                        skill.setProficiencyLevel(level);
                    }

                    list.add(skill);
                    skills.setForeignLanguage(list);
                }

            }

            return skills;

        } catch (final Exception e) {
            return to;
        }
    }
}
