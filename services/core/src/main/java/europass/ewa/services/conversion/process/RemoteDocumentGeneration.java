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
package europass.ewa.services.conversion.process;

import javax.inject.Inject;

import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.steps.AttachmentsVisibilityStep;
import europass.ewa.services.conversion.steps.BytePreparationStep;
import europass.ewa.services.conversion.steps.DefaultPreferencesStep;
import europass.ewa.services.conversion.steps.DocumentUpdateDateStep;
import europass.ewa.services.conversion.steps.StatisticsLoggingStep;
import europass.ewa.services.conversion.steps.TranslationStep;

public class RemoteDocumentGeneration implements DocumentGeneration {

    private final DocumentUpdateDateStep step1;

    private final TranslationStep step2;

    private final DefaultPreferencesStep step3;

    private final AttachmentsVisibilityStep step4;

    private final BytePreparationStep step5;

    private final StatisticsLoggingStep step6;

    @Inject
    public RemoteDocumentGeneration(DocumentUpdateDateStep step1, TranslationStep step2, DefaultPreferencesStep step3, AttachmentsVisibilityStep step4,
            BytePreparationStep step5, StatisticsLoggingStep step6) {

        this.step1 = step1;
        this.step2 = step2;
        this.step3 = step3;
        this.step4 = step4;
        this.step5 = step5;
        this.step6 = step6;

        scheduleSteps();
    }

    private void scheduleSteps() {
        step1.setNext(step2);
        step2.setNext(step3);
        step3.setNext(step4);
        step4.setNext(step5);
        step5.setNext(step6);
        step6.setNext(null);
    }

    /**
     * <ol>
     * <li>
     * <em>DefaultPreferencesStep - Apply default printing preferences</em>
     * <p>
     * This will complement the printing preferences found in the input model.
     * </p>
     * </li>
     * <li>
     * <em>AttachmentsVisibilityStep - Adjust the visibility of attachments</em>
     * <p>
     * This depends on the type of the document, as it might be CV+ESP, ESP only
     * or CV only and thus the attachments (if any) should be shown or hidden.
     * </p>
     * </li>
     * <li>
     * <em>BytePreparationStep - Prepare the response bytes of the Document</em>
     * <p>
     * Prepares the bytes of the final document to be sent as response.
     * </p>
     * </li>
     * <li>
     * <em>StatisticsLoggingStep - Logs statistics about the document
     * conversion</em>
     * <p>
     * The statistics need to take into consideration the service that initiated
     * the request, and thus differentiate between the editor (possible the
     * email delivery channel), the rest services (or the soap services)
     * </p>
     * </li>
     * <li>
     * <em></em>
     * <p>
     * </p></li>
     * </ol>
     */
    @Override
    public void process(ExportableModel model) {
        step1.doStep(model);
    }

}
