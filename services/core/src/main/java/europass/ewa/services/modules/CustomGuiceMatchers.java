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
package europass.ewa.services.modules;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;

public final class CustomGuiceMatchers<T extends TypeLiteral<?>> extends AbstractMatcher<T> {

    private TypeLiteral<?> baseType;

    /**
     * Constructor for instances that return true for TypeLiteral<X> where X is
     * a subclass of base.
     */
    public CustomGuiceMatchers(Class<?> base) {
        baseType = TypeLiteral.get(base);
    }

    /**
     * Compare type T against the type of the class passed to the constructor.
     */
    public boolean matches(T type) {
        return typeIsSubtypeOf(type, baseType);
    }

    /**
     * Utility method to implement subtype comparisons on TypeLiteral objects;
     * unfortunately they don't provide this built-in.
     */
    private static boolean typeIsSubtypeOf(TypeLiteral<?> subtype, TypeLiteral<?> supertype) {
        if (subtype.equals(supertype)) {
            return true;
        }

        Class<?> superRawType = supertype.getRawType();
        Class<?> subRawType = subtype.getRawType();

        // Test non-generics compatibility
        if (!superRawType.isAssignableFrom(subRawType)) {
            return false;
        }

        // Now find the generic ancestor (if any) which is based on non-generic
        // type superRawType.
        if (!supertype.equals(subtype.getSupertype(superRawType))) {
            return false;
        }

        return true;
    }
}
