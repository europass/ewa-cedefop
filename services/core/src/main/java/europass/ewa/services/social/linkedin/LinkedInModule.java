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
package europass.ewa.services.social.linkedin;

import java.io.IOException;
import java.io.StringReader;

import javax.inject.Named;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import europass.ewa.Utils;
import europass.ewa.services.social.Transformer;

public class LinkedInModule extends AbstractModule {

    public static final String PUBLICATIONS_MUSTACHE_TEMPLATE = "social.linkedin.publications.template";
    public static final String RECOMMENDATIONS_MUSTACHE_TEMPLATE = "social.linkedin.recommendations.template";
    public static final String PROJECTS_TEMPLATE = "social.linkedin.projects.template";
    public static final String HONORS_AWARDS_TEMPLATE = "social.linkedin.honorsawards.template";
    public static final String PATENTS_MUSTACHE_TEMPLATE = "social.linkedin.patents.template";
    public static final String COURSES_MUSTACHE_TEMPLATE = "social.linkedin.courses.template";
    public static final String VOLUNTEER_MUSTACHE_TEMPLATE = "social.linkedin.volunteer.template";
    public static final String CERTIFICATIONS_MUSTACHE_TEMPLATE = "social.linkedin.certifications.template";

    public static final String HANDLERS_SET = "social.linkedin.handlers";

    private Mustache publicationsTpl = null;
    private Mustache recommendationsTpl = null;
    private Mustache projectsTpl = null;
    private Mustache honorsAwardsTpl = null;
    private Mustache patentsTpl = null;
    private Mustache coursesTpl = null;
    private Mustache volunteerTpl = null;
    private Mustache certificationsTpl = null;

    private static final MustacheFactory mf = new DefaultMustacheFactory();

    @Override
    protected void configure() {

        // Bind the available handlers here
        Multibinder<Transformer> handlersBinder
                = Multibinder.newSetBinder(binder(), Transformer.class, Names.named(HANDLERS_SET));

        handlersBinder.addBinding().to(TaxonomyTranslatorHandler.class);
        handlersBinder.addBinding().to(AddPrefixHandler.class);
        handlersBinder.addBinding().to(CertificationsHandler.class);
        handlersBinder.addBinding().to(CoursesHandler.class);
        handlersBinder.addBinding().to(CountryCodeHandler.class);
        handlersBinder.addBinding().to(FixValueHandler.class);
        handlersBinder.addBinding().to(HonorsAwardsHandler.class);
        handlersBinder.addBinding().to(InstantMessagingHandler.class);
        handlersBinder.addBinding().to(LanguageHandler.class);
        handlersBinder.addBinding().to(PatentsHandler.class);
        handlersBinder.addBinding().to(PhotoHandler.class);
        handlersBinder.addBinding().to(PositionTypeHandler.class);
        handlersBinder.addBinding().to(ProjectsHandler.class);
        handlersBinder.addBinding().to(PublicationsHandler.class);
        handlersBinder.addBinding().to(RecommendationHandler.class);
        handlersBinder.addBinding().to(SkillHandler.class);
        handlersBinder.addBinding().to(VolunteerHandler.class);
        handlersBinder.addBinding().to(EducationalFieldHandler.class);
    }

    @Provides
    @Named(LinkedInModule.PUBLICATIONS_MUSTACHE_TEMPLATE)
    public Mustache publicationsTemplate() throws IOException {

        if (publicationsTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/publications.mustache");

            contents = contents.replaceAll("\t|\n", "");
            publicationsTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return publicationsTpl;
    }

    @Provides
    @Named(LinkedInModule.RECOMMENDATIONS_MUSTACHE_TEMPLATE)
    public Mustache recommendationsTemplate() throws IOException {

        if (recommendationsTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/recommendations.mustache");

            contents = contents.replaceAll("\t|\n", "");
            recommendationsTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return recommendationsTpl;
    }

    @Provides
    @Named(LinkedInModule.PROJECTS_TEMPLATE)
    public Mustache projectsTemplate() throws IOException {

        if (projectsTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/projects.mustache");

            contents = contents.replaceAll("\t|\n", "");
            projectsTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return projectsTpl;
    }

    @Provides
    @Named(LinkedInModule.HONORS_AWARDS_TEMPLATE)
    public Mustache honorsAwardsTemplate() throws IOException {

        if (honorsAwardsTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/honorsawards.mustache");

            contents = contents.replaceAll("\t|\n", "");
            honorsAwardsTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return honorsAwardsTpl;
    }

    @Provides
    @Named(LinkedInModule.PATENTS_MUSTACHE_TEMPLATE)
    public Mustache patentsTemplate() throws IOException {

        if (patentsTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/patents.mustache");

            contents = contents.replaceAll("\t|\n", "");
            patentsTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return patentsTpl;
    }

    @Provides
    @Named(LinkedInModule.COURSES_MUSTACHE_TEMPLATE)
    public Mustache coursesTemplate() throws IOException {

        if (coursesTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/courses.mustache");

            contents = contents.replaceAll("\t|\n", "");
            coursesTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return coursesTpl;
    }

    @Provides
    @Named(LinkedInModule.VOLUNTEER_MUSTACHE_TEMPLATE)
    public Mustache volunteerTemplate() throws IOException {

        if (volunteerTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/volunteer.mustache");

            contents = contents.replaceAll("\t|\n", "");
            volunteerTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return volunteerTpl;
    }

    @Provides
    @Named(LinkedInModule.CERTIFICATIONS_MUSTACHE_TEMPLATE)
    public Mustache certificationsTemplate() throws IOException {

        if (certificationsTpl == null) {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String contents = Utils.readResourceAsString(loader, "/mustache/certifications.mustache");

            contents = contents.replaceAll("\t|\n", "");
            certificationsTpl = mf.compile(new StringReader(contents), "EnrichedItem");
        }

        return certificationsTpl;
    }
}
