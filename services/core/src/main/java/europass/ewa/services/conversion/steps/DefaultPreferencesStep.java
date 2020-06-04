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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.LogFields;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.services.conversion.model.ExportableModel;

public class DefaultPreferencesStep extends AbstractDocumentGenerationStep {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPreferencesStep.class);

    @Inject
    @Named(ModelModule.DEFAULT_CV_PREFS)
    private static Map<String, PrintingPreference> cvDefaultPrintingPreferences;

    @Inject
    @Named(ModelModule.DEFAULT_LP_PREFS)
    private static Map<String, PrintingPreference> elpDefaultPrintingPreferences;

    @Inject
    @Named(ModelModule.DEFAULT_CL_PREFS)
    private static Map<String, PrintingPreference> eclDefaultPrintingPreferences;

    @Override
    public void doStep(ExportableModel model) {
        final long time = System.currentTimeMillis();
        try {
            SkillsPassport esp = model.getModel();

            EuropassDocumentType document = model.getDocumentType();

            Map<String, PrintingPreference> defaults = getDefaults(document);
            esp.activatePreferences(esp.getPrefDocumentName(), defaults);
            esp.applyDefaultPreferences();
            // Default prefs applied...

            //Check Bundled Documents
            DocumentInfo docInfo = esp.getDocumentInfo();
            if (docInfo != null) {
                List<EuropassDocumentType> bundleDocuments = docInfo.getBundleDocuments();
                for (EuropassDocumentType bundledDoc : bundleDocuments) {
                    String prefDocName = bundledDoc.getPreferencesAcronym();
                    esp.activatePreferences(prefDocName, getDefaults(bundledDoc));
                    esp.applyDefaultPreferences(prefDocName);
                    // EWA 1549 add each bundled Doc to the extra log list
                    model.augmentLogInfo(LogFields.DOCTYPE, bundledDoc.getDesription());
                }
            }

        } catch (IllegalStateException e) {
            LOG.error("Apply Default PrintingPreferences on model...", e);
        }
        LOG.debug("finished step " + this + " after " + (System.currentTimeMillis() - time) + "ms");
        super.doStep(model);
    }

    private Map<String, PrintingPreference> getDefaults(EuropassDocumentType document) {
        switch (document) {
            case ECV:
                return cvDefaultPrintingPreferences;
            case ELP:
                return elpDefaultPrintingPreferences;
            case ECL:
                return eclDefaultPrintingPreferences;
            default:
                //ESP case here too
                return cvDefaultPrintingPreferences;
        }
    }

}
