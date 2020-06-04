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
import europass.ewa.templates.HbsTemplateTranslator;

public class HbsTemplateModule extends AbstractModule {

    public static final String HBS_TEMPLATES_DIRS = "hbs.templates.dirs";

    @Override
    protected void configure() {
        bind(MustacheFactory.class).to(DefaultMustacheFactory.class);

        Multibinder<String> templateBinder
                = Multibinder.newSetBinder(binder(), String.class, Names.named(HBS_TEMPLATES_DIRS));
        templateBinder.addBinding().toInstance("hbs/compose");
        templateBinder.addBinding().toInstance("hbs/forms");

        bind(HbsTemplateTranslator.class);
    }
}
