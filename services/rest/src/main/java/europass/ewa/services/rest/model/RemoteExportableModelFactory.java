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
package europass.ewa.services.rest.model;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.exception.JsonToPojoException;
import europass.ewa.model.conversion.exception.PojoToXmlException;
import europass.ewa.model.conversion.exception.XmlToPojoException;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.model.ExportableModelWrapper;
import europass.ewa.services.enums.XmlVersion;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.InputUndefinedException;
import europass.ewa.statistics.DocumentGenerator;

@Singleton
public class RemoteExportableModelFactory implements ExportableModelFactory<SkillsPassport> {

    @Inject
    public RemoteExportableModelFactory() {
    }

    @Override
    public ExportableModel getInstance(SkillsPassport source, ConversionFileType fileType, DocumentGenerator generator) {

        if (source != null && source instanceof SkillsPassport) {
            source.eliminateOccupationCodes();
        }

        ExportableModelWrapper wrapper = new ExportableModelWrapper();

        configureWrapper(source, generator, wrapper);

        wrapper.setModel(source, fileType);

        return wrapper;
    }

    @Override
    public ExportableModel getInstance(SkillsPassport source, EuropassDocumentType document, ConversionFileType fileType, DocumentGenerator generator) {

        source.eliminateOccupationCodes();

        ExportableModelWrapper wrapper = new ExportableModelWrapper();

        configureWrapper(source, generator, wrapper);

        wrapper.setModel(source, document, fileType);

        return wrapper;

    }

    private void configureWrapper(SkillsPassport source, DocumentGenerator generator, ExportableModelWrapper wrapper) {

        if (source == null) {
            throw ApiException.addInfo(new InputUndefinedException(),
                    new ExtraLogInfo().add(wrapper.getExtraLogInfo()).add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()));
        }

        try {
            //Service that requested the generation e.g. EWA Editor, REST service
            wrapper.setGenerator(generator);

            source.updateDocumentGenerator(generator.getDescription());
            source.updateDocumentXSDVersion(XmlVersion.LATEST.getCode());

        } catch (final PojoToXmlException | XmlToPojoException | JsonToPojoException e) {
            throw ApiException.addInfo(new ApiException(e, e.getCode(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(wrapper.getExtraLogInfo()).add(LogFields.MODULE, ServerModules.SERVICES_REST.getModule()));
        }
    }
}
