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
package europass.ewa.conversion.odt;

import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.Transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.conversion.exception.ODTAssemblyException;
import europass.ewa.conversion.exception.ODTTemplateNotFoundException;
import europass.ewa.conversion.modules.ConversionModule;
import europass.ewa.model.CoverLetter;
import europass.ewa.model.SkillsPassport;

public class ECLODTGenerator extends ODTGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ECLODTGenerator.class);

    @Inject
    public ECLODTGenerator(@Named(ConversionModule.ODT_BASE_PATH_ECL) String basePath,
            ODTMustacheFactory factory,
            @Named(ConversionModule.HTML_TO_ODT_XSLT) Transformer htmlTransformer) {
        super(factory, htmlTransformer);
        this.setBasePath(basePath);
    }

    @Override
    public void generate(OutputStream out, SkillsPassport document) throws ODTTemplateNotFoundException, ODTAssemblyException {
        //Instantiate only the Cover Letter, so that we will be able to fetch the
        //CoverLetter-specific LearnerInfo.Identification.ContactInfo
        CoverLetter cover = document.getCoverLetter();
        if (cover == null) {
            try {
                cover = CoverLetter.class.newInstance();
            } catch (Exception e) {
                LOG.error("prepareModel - unable to instantiate object from class '" + CoverLetter.class.getName() + "'", e);
            }
        }
        if (cover != null) {
            document.setCoverLetter(cover);
        }

        super.generate(out, document);
    }

}
