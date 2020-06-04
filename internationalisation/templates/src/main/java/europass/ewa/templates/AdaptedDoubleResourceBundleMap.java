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
package europass.ewa.templates;

import java.util.ResourceBundle;

import javax.xml.transform.Transformer;

public class AdaptedDoubleResourceBundleMap extends DoubleResourceBundleMap<Object> implements TemplateContext {

    private final Transformer htmlTransformer;

    public AdaptedDoubleResourceBundleMap(ResourceBundle bundle1,
            ResourceBundle bundle2, Transformer htmlTransformer) {
        super(bundle1, bundle2);
        this.htmlTransformer = htmlTransformer;
    }

    @Override
    public AdaptedValue get(Object key) {
        Object obj = super.get(key);
        return new AdaptedValue(obj, this.htmlTransformer);
    }

    @Override
    protected String adaptKey(Object key) {
        return BundleValue.adaptKey(key);
    }

}
