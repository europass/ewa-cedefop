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
import europass.ewa.services.conversion.steps.DefaultPreferencesStep;
import europass.ewa.services.conversion.steps.StatisticsLoggingStep;

public class SimpleRemoteDocumentGeneration implements DocumentGeneration {

    private final DefaultPreferencesStep step1;

    private final AttachmentsVisibilityStep step2;

    private final StatisticsLoggingStep step3;

    @Inject
    public SimpleRemoteDocumentGeneration(DefaultPreferencesStep step1, AttachmentsVisibilityStep step2,
            StatisticsLoggingStep step3) {
        this.step1 = step1;
        this.step2 = step2;
        this.step3 = step3;

        scheduleSteps();
    }

    private void scheduleSteps() {
        step1.setNext(step2);
        step2.setNext(step3);
        step3.setNext(null);
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
