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

import java.util.List;

import javax.inject.Inject;

import europass.ewa.model.SkillsPassport;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.files.ModelFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryDataPopulationStep extends AbstractDocumentGenerationStep {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryDataPopulationStep.class);

    private final ModelFileManager fileManager;

    @Inject
    public BinaryDataPopulationStep(@EWAEditor ModelFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void doStep(ExportableModel model) {
        final long time = System.currentTimeMillis();

        SkillsPassport esp = model.getModel();

        List<Feedback> feedback = fileManager.augmentWithData(esp);

        model.addFeedback(feedback);
        LOG.debug("finished step " + this + " after " + (System.currentTimeMillis() - time) + "ms");
        super.doStep(model);
    }

}
