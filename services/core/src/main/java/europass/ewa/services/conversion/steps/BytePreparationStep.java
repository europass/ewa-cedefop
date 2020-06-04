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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import europass.ewa.Constants;
import europass.ewa.conversion.exception.ODTAssemblyException;
import europass.ewa.conversion.exception.ODTTemplateNotFoundException;
import europass.ewa.conversion.odt.ECL;
import europass.ewa.conversion.odt.ELP;
import europass.ewa.conversion.odt.ODTGenerator;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.Attachment;
import europass.ewa.model.AttachmentsInfo;
import europass.ewa.model.CV;
import europass.ewa.model.ESP;
import europass.ewa.model.Metadata;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.Converter;
import europass.ewa.model.conversion.exception.PojoToJsonException;
import europass.ewa.model.conversion.exception.PojoToXmlException;
import europass.ewa.model.conversion.json.JSON;
import europass.ewa.model.conversion.xml.XML;
import europass.ewa.oo.client.OfficeClient;
import europass.ewa.oo.client.exception.AttachmentError;
import europass.ewa.oo.client.exception.ConversionError;
import europass.ewa.oo.client.exception.NoServerAvailable;
import europass.ewa.oo.client.exception.NoServerConfiguration;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.enums.DownloadStatus;
import europass.ewa.services.exception.ApiException;

public class BytePreparationStep extends AbstractDocumentGenerationStep {

    private static final String module = ServerModules.SERVICES_CORE.getModule();

    @Inject
    @XML
    private static Converter<SkillsPassport> xmlConverter;

    @Inject
    @JSON
    private static Converter<SkillsPassport> jsonConverter;

    @Inject
    @CV
    private static ODTGenerator cvOdtGenerator;

    @Inject
    @ESP
    private static ODTGenerator espOdtGenerator;

    @Inject
    @ELP
    private static ODTGenerator elpOdtGenerator;

    @Inject
    @ECL
    private static ODTGenerator eclOdtGenerator;

    private final OfficeClient officeClient;

    @Inject
    public BytePreparationStep(OfficeClient officeClient) {
        this.officeClient = officeClient;
    }

    @Override
    public void doStep(ExportableModel model) {

        ConversionFileType fileType = model.getFileType();

        switch (fileType) {
            case JSON: {
                model.cleanUpCropping();
                String json = prepareJson(model);
                model.setJsonRepresentation(json);
                model.setBytes(prepareJsonBytes(json));
                break;
            }
            case XML: {
                model.cleanUpCropping();
                String xml = prepareXml(model);
                model.setXmlRepresentation(xml);
                model.setBytes(prepareXmlBytes(xml));
                break;
            }
            case OPEN_DOC: {
                model.setBytes(prepareOpenDocument(model));
                break;
            }
            case WORD_DOC: {
                model.setBytes(prepareWord(model));
                break;
            }
            case PDF: {
                model.setBytes(preparePDF(model));
                break;
            }
            default: {
                break;
            }
        }

        super.doStep(model);
    }

    /**
     * Returns the attachments to be included. The AttachmentsInfo object is
     * inspected, otherwise the attachments of the model (if any, are returned)
     *
     * @param model
     * @return
     */
    private List<Attachment> getAttachments(ExportableModel model) {
        SkillsPassport esp = model.getModel();

        AttachmentsInfo info = esp.getAttachmentsInfo();

        if (info == null) {
            return esp.getAttachmentList();
        }

        return info.getVisibleAttachments();
    }

    private byte[] prepareOpenDocument(ExportableModel model) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            SkillsPassport esp = model.getModel();
            esp.setAttachmentList(getAttachments(model));

            switch (model.getDocumentType()) {
                case ELP: {
                    elpOdtGenerator.generate(outStream, esp);
                    break;
                }
                case ECL: {
                    eclOdtGenerator.generate(outStream, esp);
                    break;
                }
                case ECV_ESP: {
                    cvOdtGenerator.generate(outStream, esp);
                    break;
                }
                case ESP: {
                    espOdtGenerator.generate(outStream, esp);
                    break;
                }
                default: {
                    cvOdtGenerator.generate(outStream, esp);
                    break;
                }
            }

            safeFlush(outStream);
            byte[] bytes = outStream.toByteArray();
            safeClose(outStream);

            if (bytes == null) {
                throw ApiException.addInfo(new ApiException("CONVERT - model to null bytes", DownloadStatus.MODEL_TO_BYTES.getDescription(),
                        Status.INTERNAL_SERVER_ERROR), new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
            }
            return bytes;

        } catch (ODTTemplateNotFoundException | ODTAssemblyException e) {
            throw ApiException.addInfo(new ApiException(e, e.getCode(), Status.INTERNAL_SERVER_ERROR), new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        }
    }

    private byte[] prepareWord(ExportableModel model) {
        // Convert POJO to word by first employing the generation to ODT and
        // then the ODT converion to the desired type

        //Set attachments, if any
        SkillsPassport esp = model.getModel();
        esp.setAttachmentList(getAttachments(model));
        return this.odtConvert(model);
    }

    private byte[] preparePDF(ExportableModel model) {
        // Prepare an Attachment of the XML itself

        Metadata photoCroppingExportMeta = model.croppingExportPhotoMeta();
        Metadata signatureCroppingExportMeta = model.croppingExportSignatureMeta();

        if (photoCroppingExportMeta != null || signatureCroppingExportMeta != null) {
            model.cleanUpCropping();
        }

        Attachment xmlAttachment = new Attachment("", Constants.ECV_XML_ATTACHMENT_DESCRIPTION,
                (Constants.PDF_XML_ATTACHMENT + ConversionFileType.XML.getExtension()), ConversionFileType.XML.getMimeType(), "",
                prepareXmlBytes(model));

        if (photoCroppingExportMeta != null) {
            model.getModel().personalPhoto().getMetadata().add(photoCroppingExportMeta);
        }
        if (signatureCroppingExportMeta != null) {
            model.getModel().personalSignature().getMetadata().add(signatureCroppingExportMeta);
        }

        // Convert POJO to word by first employing the generation to ODT and
        // then the ODT conversion to the desired type
        return this.odtConvert(model, xmlAttachment, getAttachments(model));
    }

    /**
     * ****************************************************************************************
     */
    /**
     * Produces the given POJO as bytes of the given file type. Employs the ODT
     * generator to produce the odt from the POJO, which will then be fed to the
     * ODT conversion engine.
     *
     * @param model
     * @param List
     * <Attachment> a list of attachments to be handled as needed during the
     * document conversion
     * @return byte[] or null if an exception is thrown.
     */
    private byte[] odtConvert(ExportableModel model, Attachment xmlAttachment, List<Attachment> attachments) {

        ExtraLogInfo extraInfo = model.getExtraLogField(LogFields.REQ_ID);

        String requestId = extraInfo != null ? extraInfo.getLogEntry(LogFields.REQ_ID) : "";

        try {
            ODTGenerator generator;
            switch (model.getDocumentType()) {
                case ESP: {
                    generator = espOdtGenerator;
                    break;
                }
                case ELP: {
                    generator = elpOdtGenerator;
                    break;
                }
                case ECL: {
                    generator = eclOdtGenerator;
                    break;
                }
                default: {
                    generator = cvOdtGenerator;
                    break;
                }
            }

            SkillsPassport esp = model.getModel();

            OutputStream outStream = officeClient.startConvert(requestId);
            generator.generate(outStream, esp);
            safeFlush(outStream);

            InputStream inStream = officeClient.endConvert(outStream, model.getFileType(), xmlAttachment, attachments, requestId);
            safeClose(outStream);
            byte[] bytes = IOUtils.toByteArray(inStream);
            safeClose(inStream);

            if (bytes == null) {
                throw ApiException.addInfo(new ApiException("Failed to generate file from model", DownloadStatus.MODEL_TO_BYTES.getDescription(),
                        Status.INTERNAL_SERVER_ERROR), new ExtraLogInfo().add(model.getExtraLogInfo()));
            }

            //Get feedback from Office Client and set it to the model container
            model.addFeedback(officeClient.getFeedback());

            return bytes;
        } catch (final NoServerAvailable | NoServerConfiguration e) {

            String msg = "OO server error - ";
            String exMsg = e.getMessage().toLowerCase();

            // EWA 1549, display specific log message depending on OO server state
            if (e instanceof NoServerAvailable) {
                if (exMsg.contains("service unavailable")) {
                    msg += "Office binaries down";
                } else if (exMsg.contains("not found")) {
                    msg += "Office API down";
                }
            } else {
                msg += e.getMessage();
            }

            throw ApiException.addInfo(new ApiException(msg, e, DownloadStatus.OTHER.getDescription(),
                    Status.INTERNAL_SERVER_ERROR), new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        } catch (final ConversionError | AttachmentError | ODTTemplateNotFoundException | ODTAssemblyException e) {
            throw ApiException.addInfo(new ApiException("IOException while writing bytes", e, DownloadStatus.OTHER.getDescription(),
                    Status.INTERNAL_SERVER_ERROR), new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        } catch (final IOException e) {
            throw ApiException.addInfo(new ApiException("IOException while writing bytes", e, DownloadStatus.OTHER.getDescription(),
                    Status.INTERNAL_SERVER_ERROR), new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        } catch (final NullPointerException e) {
            throw ApiException.addInfo(new ApiException("NullPointerException while writing bytes", e, DownloadStatus.OTHER.getDescription(),
                    Status.INTERNAL_SERVER_ERROR), new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        } finally {
            officeClient.release(requestId);
        }
    }

    /**
     * Produces the given POJO as bytes of the given file type. Employs the ODT
     * generator to produce the odt from the POJO, which will then be fed to the
     * ODT conversion engine.
     *
     * @param model
     */
    private byte[] odtConvert(ExportableModel model) {
        return this.odtConvert(model, null, null);
    }

    /**
     * *************************************************************************************************************
     */

    private String prepareJson(ExportableModel model) {
        try {
            return jsonConverter.write(model.getModel());
        } catch (final PojoToJsonException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.MODEL_TO_JSON.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        }
    }

    private byte[] prepareJsonBytes(String json) {
        try {
            return json.getBytes(Constants.UTF8_ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.MODEL_TO_JSON.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.MODULE, module));
        }
    }

    @SuppressWarnings("unused")
    private byte[] prepareJsonBytes(ExportableModel model) {
        try {
            return prepareJson(model).getBytes(Constants.UTF8_ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.MODEL_TO_XML.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        }
    }

    private String prepareXml(ExportableModel model) {
        try {
            return xmlConverter.write(model.getModel());
        } catch (final PojoToXmlException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.MODEL_TO_XML.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        }

    }

    private byte[] prepareXmlBytes(String xml) {
        try {
            return xml.getBytes(Constants.UTF8_ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.MODEL_TO_XML.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.MODULE, module));
        }
    }

    private byte[] prepareXmlBytes(ExportableModel model) {
        try {
            return prepareXml(model).getBytes(Constants.UTF8_ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.MODEL_TO_XML.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(model.getExtraLogInfo()).add(LogFields.MODULE, module));
        }
    }

    /**
     * *************************************************************************************************************
     */
    private void safeClose(OutputStream out) {
        try {
            out.close();
        } catch (Exception e) {
        }
    }

    private void safeFlush(OutputStream out) {
        try {
            out.flush();
        } catch (Exception e) {
        }
    }

    private void safeClose(InputStream in) {
        try {
            in.close();
        } catch (Exception e) {
        }
    }

}
