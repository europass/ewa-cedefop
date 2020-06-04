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
package europass.ewa;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import europass.ewa.enums.UserAgent;
import europass.ewa.module.EditorsModule;
import europass.ewa.modules.SupportedLocaleModule;
import europass.ewa.page.EWAPageKeyFormat;
import europass.ewa.page.PageKey;

public class PathPatternTest {

    static Injector injector = null;

    static EWAPageKeyFormat pageKeyFormat = null;

    static Pattern urlRootPattern;

    static Pattern urlAnyPattern;

    @BeforeClass
    public static void prepare() {
        injector = Guice.createInjector(
                new AbstractModule() {

            @Override
            protected void configure() {
                bindConstant()
                        .annotatedWith(Names.named(EditorsModule.EWA_DEFAULT_CONTEXT))
                        .to("editors");

                bindConstant()
                        .annotatedWith(Names.named(EditorsModule.EWA_PATH_TEMPLATE))
                        .to("/{locale}[/{path}][.{channel}]");

                bindConstant()
                        .annotatedWith(Names.named(SupportedLocaleModule.EWA_DEFAULT_LANGUAGE_KEY))
                        .to("en");

                bindConstant()
                        .annotatedWith(Names.named(EditorsModule.EWA_DEFAULT_PATH))
                        .to("/cv-esp/compose/cv");

                bindConstant()
                        .annotatedWith(Names.named(EditorsModule.EWA_UNSUPPORTED_UAS_KEY))
                        .to("MSIE6 MSIE7");

                bindConstant()
                        .annotatedWith(Names.named(EditorsModule.EWA_NON_HTML5_UAS_KEY))
                        .to("MSIE8 MSIE9");

                bindConstant()
                        .annotatedWith(Names.named(SupportedLocaleModule.EWA_SUPPORTED_LANGUAGES_KEY))
                        .to("en fr el");

                bindConstant()
                        .annotatedWith(Names.named(EditorsModule.REDIRECT_URLS_MISSING_PATH_LOCALE))
                        .to("/editors/cv-esp/upload,/editors/esp/compose");

                bindConstant()
                        .annotatedWith(Names.named(EditorsModule.REDIRECT_URLS_CUSTOM_PATH))
                        .to("/cv-esp/upload /cv/upload,/cv-esp/download /cv/download");

            }
        },
                new SupportedLocaleModule(),
                new EditorsModule(null)
        );
        pageKeyFormat = injector.getInstance(EWAPageKeyFormat.class);

        urlRootPattern = Pattern.compile(EditorsModule.EWA_URL_ROOT_REGEXP);

        urlAnyPattern = Pattern.compile(EditorsModule.EWA_URL_ANY_PATH_REGEXP);

    }
    // ------------------------ ROOT PATH MATCH ------------------------

    @Test
    public void matchRoot() {
        Matcher matcher = urlRootPattern.matcher("/");
        assertThat("Root: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchRootScript() {
        Matcher matcher = urlRootPattern.matcher("/scripts");
        assertThat("Script: ", matcher.matches(), CoreMatchers.is(false));
    }

    @Test
    public void matchRootEditorsPath() {
        Matcher matcher = urlRootPattern.matcher("/en/cv-esp/download");
        assertThat("Editos path: ", matcher.matches(), CoreMatchers.is(false));
    }
    // ------------------------ ANY PATH MATCH ------------------------

    @Test
    public void matchAnyRoot() {
        Matcher matcher = urlAnyPattern.matcher("/");
        assertThat("Root: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchLocale() {
        Matcher matcher = urlAnyPattern.matcher("/en");
        assertThat("Locale: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchPath() {
        Matcher matcher = urlAnyPattern.matcher("/cv-esp/download");
        assertThat("Path: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchFullPath() {
        Matcher matcher = urlAnyPattern.matcher("/en/cv-esp/download");
        assertThat("Full path: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchScripts() {
        Matcher matcher = urlAnyPattern.matcher("/scripts");
        assertThat("matchScripts: ", matcher.matches(), CoreMatchers.is(false));
    }

    @Test
    public void matchCss() {
        Matcher matcher = urlAnyPattern.matcher("/css");
        assertThat("css: ", matcher.matches(), CoreMatchers.is(false));
    }

    @Test
    public void matchStaticStyles() {
        Matcher matcher = urlAnyPattern.matcher("/editors/static/ewa/styles/main-styles.css");
        assertThat("styles: ", matcher.matches(), CoreMatchers.is(false));
    }

    @Test
    public void matchStaticScripts() {
        Matcher matcher = urlAnyPattern.matcher("/editors/static/ewa/scripts/main.js");
        assertThat("scripts: ", matcher.matches(), CoreMatchers.is(false));
    }

    @Test
    public void matchStaticImages() {
        Matcher matcher = urlAnyPattern.matcher("/editors/static/ewa/images/log.png");
        assertThat("images: ", matcher.matches(), CoreMatchers.is(false));
    }

    @Test
    public void matchImages() {
        Matcher matcher = urlAnyPattern.matcher("/images");
        assertThat("Images: ", matcher.matches(), CoreMatchers.is(false));
    }

    //---
    @Test
    public void matchSocialCallback() {
        Matcher matcher = urlAnyPattern.matcher("/en/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE");
        assertThat("Editors path: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchSocialCallbackWithJVM() {
        Matcher matcher = urlAnyPattern.matcher("/en/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE.myjvm");
        assertThat("Editors path: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchSocialCallbackWithJVMAndHash() {
        Matcher matcher = urlAnyPattern.matcher("/en/#/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE.myjvm");
        assertThat("Editors path: ", matcher.matches(), CoreMatchers.is(true));
    }

    @Test
    public void matchSocialErrorCallback() {
        Matcher matcher = urlAnyPattern.matcher("/en/social/linkedin/code/400/error/social-data-retrieval-error/trace/ErrCode:gtKce95F");
        assertThat("Editors path: ", matcher.matches(), CoreMatchers.is(true));
    }

    // ------------------------ PATTERN PARSE ------------------------
    @Test
    public void socialCallbackPathSuccess() {
        String path = "/en/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE"));
    }

    @Test
    public void socialCallbackPathSuccessWithJVM() {
        String path = "/en/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE.myjvm";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE"));

        assertThat("Channel: ", key.getChannel(), CoreMatchers.is("myjvm"));
    }

    @Test
    public void socialCallbackPathSuccessWithJVMAndHash() {
        String path = "/en/#/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE.myjvm";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/#/social/linkedin/ok/0B94C8CCF504C9ACAA883B09A56FFEEE"));

        assertThat("Channel: ", key.getChannel(), CoreMatchers.is("myjvm"));
    }

    @Test
    public void socialCallbackPathFail() {
        String path = "/en/social/linkedin/error/file-forbidden";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/social/linkedin/error/file-forbidden"));
    }

    @Test
    public void socialCallbackPathFailComplete() {
        String path = "/en/social/linkedin/code/400/error/social-data-retrieval-error/trace/ErrCode:gtKce95F";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/social/linkedin/code/400/error/social-data-retrieval-error/trace/ErrCode:gtKce95F"));
    }

    @Test
    public void composePath() {
        String path = "/fr/cv-esp/compose/cv";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.FRENCH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/compose/cv"));
    }

    @Test
    public void downloadPath() {
        String path = "/el/cv-esp/download";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(new Locale("el")));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/download"));
    }

    @Test
    public void lpComposePath() {
        String path = "/el/elp/compose";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(new Locale("el")));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/elp/compose"));
    }

    @Test
    public void anyPath() {
        String path = "/el/";

        //This is not a matchable path
        PageKey key = pageKeyFormat.parse(path);

        assertNotSame("Locale: ", key.getLocale(), CoreMatchers.is(new Locale("el")));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/compose/cv"));
    }

    @Test
    public void localePath() {
        String path = "/el";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(new Locale("el")));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/compose/cv"));
    }

    @Test
    @Ignore
    public void noLocalePath() {
        String path = "/cv-esp/download";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/compose/cv"));

    }

    @Test
    public void noLocaleLikeCVPath() {
        String path = "/cv/compose";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        //We messed up the path, but what to do...
        assertThat("Path: ", key.getPath(), CoreMatchers.is("/compose"));

    }

    @Test
    public void unsupportedLocalePath() {
        String path = "/xx/cv-esp/download";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/download"));

    }

    @Test
    public void invalidLocalePath() {
        String path = "/xxxxxx/cv-esp/download";

        PageKey key = pageKeyFormat.parse(path);

        //Returns the default page Key!
        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/compose/cv"));

    }

    @Test
    public void invalidPath() {
        String path = "/xxxxxx/yyyy888/eee/ddesad";

        PageKey key = pageKeyFormat.parse(path);

        //Returns the default page Key!
        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/compose/cv"));

    }

    @Test
    public void emptyPath() {
        String path = "";

        PageKey key = pageKeyFormat.parse(path);

        assertThat("Locale: ", key.getLocale(), CoreMatchers.is(Locale.ENGLISH));

        assertThat("Path: ", key.getPath(), CoreMatchers.is("/cv-esp/compose/cv"));

    }

    // ------------------------ ANY PATH FORMAT ------------------------
    @Test
    public void formatFull() {
        PageKey key = new PageKey(Locale.FRENCH, "/cv-esp/download");

        String path = pageKeyFormat.format(key);

        assertThat("URL: ", path, CoreMatchers.is("/editors/fr/cv-esp/download"));
    }

    @Test
    public void formatLP() {
        PageKey key = new PageKey(Locale.FRENCH, "/elp/download");

        String path = pageKeyFormat.format(key);

        assertThat("URL: ", path, CoreMatchers.is("/editors/fr/elp/download"));
    }

    @Test
    public void formatEmpty() {
        PageKey key = new PageKey();

        String path = pageKeyFormat.format(key);

        assertThat("URL: ", path, CoreMatchers.is("/editors/en/cv-esp/compose/cv"));
    }

    @Test
    public void formatLocale() {
        PageKey key = new PageKey();
        key.setLocale(Locale.FRENCH);

        String path = pageKeyFormat.format(key);

        assertThat("URL: ", path, CoreMatchers.is("/editors/fr/cv-esp/compose/cv"));
    }

    @Test
    public void formatUnsupportedLocale() {
        PageKey key = new PageKey();
        key.setLocale(new Locale("xx"));

        String path = pageKeyFormat.format(key);

        assertThat("URL: ", path, CoreMatchers.is("/editors/en/cv-esp/compose/cv"));
    }

    @Test
    public void formatPath() {
        PageKey key = new PageKey();
        key.setPath("/cv-esp/download");

        String path = pageKeyFormat.format(key);

        assertThat("URL: ", path, CoreMatchers.is("/editors/en/cv-esp/download"));
    }

    @Test
    public void formatIEPath() {
        PageKey key = new PageKey();
        key.setPath("/cv-esp/download");
        key.setUserAgent(UserAgent.MSIE8);

        String path = pageKeyFormat.format(key);

        assertThat("URL: ", path, CoreMatchers.is("/editors/en/#/cv-esp/download"));
    }

}
