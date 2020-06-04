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
package europass.ewa.services.conversion.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.model.SkillsPassport;
import europass.ewa.model.reflection.ReflectionUtils;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.TranslationInfo;

/**
 * Performs a translation of the CodeLabel.Label based on CodeLabel.Code
 *
 * @author ekar
 *
 */
public class TranslationStep extends AbstractDocumentGenerationStep {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationStep.class);

    @Override
    public void doStep(ExportableModel model) {
        final long time = System.currentTimeMillis();

        try {
            TranslationInfo info = model.getTranslationInfo();
            SkillsPassport esp = model.getModel();

            if (esp != null && info != null && info.getLocale() != null) {

                ReflectionUtils.deepTranslateTo(esp, esp, info.getLocale());

            }
        } catch (final Exception e) {
            LOG.error("Failed to translate the model to the required language", e);
        }
        LOG.debug("finished step " + this + " after " + (System.currentTimeMillis() - time) + "ms");
        super.doStep(model);
    }

}
