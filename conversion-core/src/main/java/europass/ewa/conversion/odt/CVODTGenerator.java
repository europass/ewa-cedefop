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

import europass.ewa.conversion.exception.ODTAssemblyException;
import europass.ewa.conversion.exception.ODTTemplateNotFoundException;
import europass.ewa.conversion.modules.ConversionModule;
import europass.ewa.model.SkillsPassport;

public class CVODTGenerator extends ODTGenerator {

    @Inject
    public CVODTGenerator(@Named(ConversionModule.ODT_BASE_PATH_CV) String basePath,
            ODTMustacheFactory factory,
            @Named(ConversionModule.HTML_TO_ODT_XSLT) Transformer htmlTransformer) {
        super(factory, htmlTransformer);
        this.setBasePath(basePath);
    }

    @Override
    public void generate(OutputStream out, SkillsPassport document) throws ODTTemplateNotFoundException, ODTAssemblyException {

        /*
		 * Before calling the odt generation we need to modify the
		 * SkillsPassport in order to complete the model with non-null
		 * objects. This is necessary in order to allow the production of an
		 * ODT with empty sections when there is no information, but the
		 * corresponding printing preference dictates that still the section
		 * needs to be printed.
		 * 
		 * So for example, if one has chosen to display the section of
		 * Telephones, but has not added any telephone, then the use of the
		 * prepareModel method allows the creation of a list of at least one
		 * Telephone. Then the odt generator will print an empty section
		 * there.
         */
//		document.prepareModel();
        super.generate(out, document);
    }

}
