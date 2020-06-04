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
package europass.ewa.services.editor.modules;

import javax.inject.Named;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;

import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.mail.MailSender;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.annotation.EWAEditorEmail;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DefaultDocumentGeneration;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.conversion.process.EmailDocumentGeneration;
import europass.ewa.services.editor.exception.ApiExceptionMapper;
import europass.ewa.services.editor.exception.GenericExceptionMapper;
import europass.ewa.services.editor.ftp.UploadJSONZipFiles;
import europass.ewa.services.editor.messages.HtmlWrapper;
import europass.ewa.services.editor.messages.SessionAwareWithBodyHtmlWrapper;
import europass.ewa.services.editor.model.EditorExportableModelFactory;
import europass.ewa.services.editor.resources.*;
import europass.ewa.services.filters.RefererFilter;
import europass.ewa.services.mail.ServicesMailSenderImpl;

public class EditorServicesModule extends ServletModule {

    public static final String USER_AGENT_CACHE = "europass-userAgent-cache";

    public static final String USER_REQUEST_LOCALE = "europass-user-session-locale";

    public static final String USER_COOKIE_ID = "europass-editors-user";

    public static final String USER_COOKIE_PATTERN = "38400000-8cf0-11bd-b23e-10b96e4ef00d";

    public static final String CVN_TO_EPAS_URL = "europass-ewa-services.cvn.to.epas.url";

    private CachedUserAgentStringParser userAgentCache = null;

    @Override
    protected void configureServlets() {

        //Html Feedback
        binder().requestStaticInjection(HtmlResponseReporting.class);

        //--- EXCEPTION AND FEEDBACK
        //----- Html Wrapper
        bind(HtmlWrapper.class).annotatedWith(EWAEditor.class).to(SessionAwareWithBodyHtmlWrapper.class);

        //----- Exception Mapper	
        bind(ApiExceptionMapper.class);
        bind(GenericExceptionMapper.class);

        //Exportable Model Factory
        bind(new TypeLiteral<ExportableModelFactory<String>>() {
        }).annotatedWith(EWAEditor.class).to(EditorExportableModelFactory.class);

        //Orchestration of steps
        bind(DocumentGeneration.class).annotatedWith(EWAEditor.class).to(DefaultDocumentGeneration.class);
        bind(DocumentGeneration.class).annotatedWith(EWAEditorEmail.class).to(EmailDocumentGeneration.class);

        bind(JSONExportResource.class);
        bind(UploadJSONZipFiles.class);

        //Jersey Resources
        //--- Logging from Editors
        bind(RemoteLoggingResource.class);
        //--- Download Document
        bind(DownloadDocumentResource.class);
        //--- Email Document
        bind(EmailDocumentResource.class);
        //--- Upload Document
        bind(LoadResource.class);
        //--- Feedback Email
        bind(SendFeedbackResource.class);
        bind(ShareDocumentForReviewResource.class);
        bind(ShareDocumentPostbackResource.class);
        binder().requestStaticInjection(SendFeedbackResource.class);

        // --- E-Mail Manipulation ---
        bind(MailSender.class).to(ServicesMailSenderImpl.class).asEagerSingleton();

        //The session id is not needed to be present as a Header: filter("/*").through(ServicesSessionFilter.class);
        filter("/*").through(RefererFilter.class);
    }

    @Provides
    @Singleton
    @Named(USER_AGENT_CACHE)
    public CachedUserAgentStringParser userAgentParser() {
        if (userAgentCache == null) {
            userAgentCache = new CachedUserAgentStringParser();
        }
        return userAgentCache;
    }
}
