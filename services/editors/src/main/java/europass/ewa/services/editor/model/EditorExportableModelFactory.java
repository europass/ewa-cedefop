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
package europass.ewa.services.editor.model;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Strings;

import europass.ewa.Utils;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.Converter;
import europass.ewa.model.conversion.json.JSON;
import europass.ewa.model.conversion.xml.XML;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.model.ExportableModelWrapper;
import europass.ewa.services.enums.XmlVersion;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.InputUndefinedException;
import europass.ewa.statistics.DocumentGenerator;

public class EditorExportableModelFactory implements ExportableModelFactory<String> {

    private Converter<SkillsPassport> jsonconverter;

    private Converter<SkillsPassport> xmlconverter;

    @Inject
    public EditorExportableModelFactory(@JSON Converter<SkillsPassport> jsonconverter, @XML Converter<SkillsPassport> xmlconverter) {
        this.jsonconverter = jsonconverter;
        this.xmlconverter = xmlconverter;
    }

    @Override
    public ExportableModel getInstance(String source, ConversionFileType fileType, DocumentGenerator generator) {

        ExportableModelWrapper wrapper = new ExportableModelWrapper();

        SkillsPassport esp = configureWrapper(wrapper, source, fileType, generator);

        wrapper.setModel(esp, fileType);

        return wrapper;
    }

    @Override
    public ExportableModel getInstance(String source, EuropassDocumentType document, ConversionFileType fileType, DocumentGenerator generator) {

        ExportableModelWrapper wrapper = new ExportableModelWrapper();

        SkillsPassport esp = configureWrapper(wrapper, source, fileType, generator);

        wrapper.setModel(esp, document, fileType);

        return wrapper;

    }

    private SkillsPassport configureWrapper(ExportableModelWrapper wrapper, String source, ConversionFileType fileType, DocumentGenerator generator) {

        if (Strings.isNullOrEmpty(source)) {
            throw ApiException.addInfo(new InputUndefinedException(),
                    new ExtraLogInfo().add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()).add(wrapper.getExtraLogInfo()));
        }

        SkillsPassport esp = pojoFromJson(source, fileType);

        //Service that requested the generation e.g. EWA Editor, REST service
        wrapper.setGenerator(generator);

        esp.updateDocumentGenerator(generator.getDescription());
        esp.updateDocumentXSDVersion(XmlVersion.LATEST.getCode());

        //File name
        prepareFileNames(esp, fileType);

        return esp;
    }

    private SkillsPassport pojoFromJson(String json, ConversionFileType fileType) {

        String decodedJsonESP = Utils.urlDecode(json);

        SkillsPassport esp = null;

        // EWA-376: Attention if we have to produce ODT, WORD or PDF, we
        // need to
        // sanitize the XML from invalid characters
        // To achieve this, we do: json-string -> POJO -> xml-string -> POJO
        // during the POJO -> xml-string the WoodStox xml processor is
        // employed, which is configured (through the Jackson XmlMapper) to
        // replace invalid characters with the empty character.
        // The ideal solution would have been to make the Json ObjectMapper
        // handle and replace those characters which are considered invalid
        // by XML.
        // After a very brief search, we did not find such a solution,
        // therefore we went with the work-around below.
        // Ugly...but it works.
        if (!ConversionFileType.XML.equals(fileType)) {
            String xml = jsonconverter.convert(decodedJsonESP);
            esp = xmlconverter.load(xml);
        } else {
            esp = jsonconverter.load(decodedJsonESP);
        }

        return esp;
    }

    /**
     * ******************************************************************************************************
     */
    /**
     * ******************************************************************************************************
     */
    /**
     * Prepares a file name from the model
     *
     * @param fileType
     * @return
     */
    private void prepareFileNames(SkillsPassport esp, ConversionFileType fileType) {
        esp.setFilename(prepareFileName(esp, fileType, true));
        esp.setSimpleFilename(prepareFileName(esp, fileType, false));
    }

    /**
     * Prepares the File Name. Does NOT apply any URL Encoding.
     *
     * e.g. Europass-CV-121031-Kargioti.pdf
     *
     * @param esp
     * @param fileType
     * @return
     */
    private static String prepareFileName(SkillsPassport esp, ConversionFileType fileType, boolean includeSurname) {

        EuropassDocumentType document = esp.returnDocumentType();

        String filename = document.getDesription() + "-";

        // Include date
        filename += Utils.getCurrentDate();

        if (includeSurname) {
            String surname = esp.personSurname();
            // Include surname ...
            if (!"".equals(surname)) {
                filename += "-" + Utils.removePunctuation(surname);
            }
        }
        // Include locale
        Locale locale = esp.getLocale();
        if (locale != null) {
            filename += "-" + locale.toString().toUpperCase();
        }

        // Include extension
        filename += fileType.getExtension();

        return filename;
    }

}
