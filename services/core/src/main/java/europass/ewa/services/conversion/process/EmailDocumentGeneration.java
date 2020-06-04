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
import europass.ewa.services.conversion.steps.BinaryDataPopulationStep;
import europass.ewa.services.conversion.steps.BytePreparationStep;
import europass.ewa.services.conversion.steps.DefaultPreferencesStep;
import europass.ewa.services.conversion.steps.EmailDocumentStep;
import europass.ewa.services.conversion.steps.StatisticsLoggingStep;

public class EmailDocumentGeneration implements DocumentGeneration {

    private final DefaultPreferencesStep step1;

    private final BinaryDataPopulationStep step2;

    private final AttachmentsVisibilityStep step3;

    private final BytePreparationStep step4;

    private final EmailDocumentStep step5;

    private final StatisticsLoggingStep step6;

    @Inject
    public EmailDocumentGeneration(DefaultPreferencesStep step1, BinaryDataPopulationStep step2, AttachmentsVisibilityStep step3,
            BytePreparationStep step4, EmailDocumentStep step5, StatisticsLoggingStep step6) {
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
     * <em>BinaryDataPopulationStep - Add binary data of photo and attachment
     * (if missing) and remove the temp uri (if present)</em>
     * <p>
     * This is most valid for the input model submitted by the EWA Editor, as it
     * is expected that remote application that need to convert to Europass
     * document would already supply the binary data inline with the model to be
     * sent.
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
     * <em>EmailDocumentStep - Send the document by email</em>
     * <p>
     * Uses an email client to send an email
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
     * </ol>
     */
    @Override
    public void process(ExportableModel model) {
        step1.doStep(model);
    }

}
