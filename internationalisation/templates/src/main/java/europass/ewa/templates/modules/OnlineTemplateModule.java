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
package europass.ewa.templates.modules;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.templates.DocumentInfo;
import europass.ewa.templates.OnlineTemplateTranslator;

/**
 *
 * @author at
 */
public class OnlineTemplateModule extends AbstractModule {

    public static final String ONLINE_TEMPLATES_DIRS = "online.templates.dirs";

    @Override
    protected void configure() {
        bind(MustacheFactory.class).to(DefaultMustacheFactory.class);

        DocumentInfo cvInfo = new DocumentInfo(EuropassDocumentType.ECV, "odt/cv");
        DocumentInfo espInfo = new DocumentInfo(EuropassDocumentType.ESP, "odt/esp");
        DocumentInfo lpInfo = new DocumentInfo(EuropassDocumentType.ELP, "odt/elp");
        DocumentInfo clInfo = new DocumentInfo(EuropassDocumentType.ECL, "odt/ecl");
        DocumentInfo ictInfo = new DocumentInfo(EuropassDocumentType.ECV, "odt/ict");

        Multibinder<DocumentInfo> templateBinder
                = Multibinder.newSetBinder(binder(), DocumentInfo.class, Names.named(ONLINE_TEMPLATES_DIRS));
        templateBinder.addBinding().toInstance(cvInfo);
        templateBinder.addBinding().toInstance(espInfo);
        templateBinder.addBinding().toInstance(lpInfo);
        templateBinder.addBinding().toInstance(clInfo);
        templateBinder.addBinding().toInstance(ictInfo);

        bind(OnlineTemplateTranslator.class);
    }
}
