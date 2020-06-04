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
package europass.ewa.model.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;

import europass.ewa.model.CodeLabel;
import europass.ewa.model.PrintableObject;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.translation.TranslatableImpl.TaxonomyItemInfo;

public class ReflectionUtils {

    private ReflectionUtils() {
        throw new AssertionError();
    }

    public static Function<TaxonomyItemInfo, String> ADJUST_OCCUPATION_LABEL = new Function<TaxonomyItemInfo, String>() {

        @SuppressWarnings("unchecked")
        @Override
        @Nullable
        public String apply(@Nullable TaxonomyItemInfo info) {
            String gender = "M";

            Object code = info.getItem();
            SkillsPassport esp = info.getDocument();

            if (esp != null) {
                CodeLabel pGender = esp.personGender();
                gender = (pGender != null && !Strings.isNullOrEmpty(pGender.getCode())) ? pGender.getCode() : gender;
            }
            return ((Map<String, String>) code).get(gender);
        }

    };

    public static Function<String, String> ADJUST_GENDER_CODE = new Function<String, String>() {
        @Override
        @Nullable
        public String apply(@Nullable String code) {
            return "LearnerInfo.Identification.Demographics.Gender." + code;
        }
    };

    private static boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get[A-Z].*")
                    && !method.getReturnType().equals(void.class)) {
                return true;
            }
            if (method.getName().matches("^is[A-Z].*")
                    && method.getReturnType().equals(boolean.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param method
     * @param field, needs to start with an UpperCase letter
     * @param boolean to exclude getters that return Object.class or not
     * @return
     */
    private static boolean isGetter(Method method, String field, boolean excludeObjectReturns) {
//		if (Modifier.isPublic(method.getModifiers()) &&
//	      method.getParameterTypes().length == 0) {
//	         if (method.getName().matches("^get"+field) &&
//	            !method.getReturnType().equals(void.class))
//	               return true;
//	         if (method.getName().matches("^is"+field) &&
//	            method.getReturnType().equals(boolean.class))
//	               return true;
//	   }
//	   return false;
        if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0) {

            if (method.getName().matches("^is" + field) && method.getReturnType().equals(boolean.class)) {
                return true;
            }

            if (method.getName().matches("^get" + field)) {
                Class<?> returnClazz = method.getReturnType();

                if (returnClazz.equals(void.class)) {
                    return false;
                }

                //check return class
                if (excludeObjectReturns && returnClazz.equals(Object.class)) {
                    return false;
                }

                return true;
            }
        }
        return false;
    }

    private static boolean isSetter(Method method, Class<?> setClazz, String field) {
        if (Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 1) {
            if (method.getName().matches("^set" + field)
                    && !method.getReturnType().equals(setClazz)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasInterface(Class<?> clazz, Class<?> interfaze) {
        for (Class<?> c : clazz.getInterfaces()) {
            if (c.equals(interfaze)) {
                return true;
            }
        }
        return false;
    }

    public static Method[] getGetters(Class<?> objClazz) {
        Method[] ms = objClazz.getMethods();
        List<Method> mL = new ArrayList<Method>();
        for (Method m : ms) {
            if (isGetter(m)) {
                mL.add(m);
            }
        }
        return mL.toArray(new Method[0]);
    }

    /**
     * Find a specific getter that matches the given name
     *
     * @param objClazz
     * @param name
     * @param boolean to exclude getters that return Object.class or not
     * @return
     */
    public static Method getGetter(Class<?> objClazz, String name, boolean excludeObjectReturns) {

        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        String field = name.substring(0, 1).toUpperCase() + name.substring(1);

        for (Method m : objClazz.getMethods()) {
            if (isGetter(m, field, excludeObjectReturns)) {
                return m;
            }
        }
        return null;
    }

    public static Method getSetter(Class<?> objClazz, Class<?> setClazz, String name) {

        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        String field = name.substring(0, 1).toUpperCase() + name.substring(1);

        for (Method m : objClazz.getMethods()) {
            if (isSetter(m, setClazz, field)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Checks whether a given object is a list
     *
     * @param obj
     * @return
     */
    public static boolean isList(Object obj) {
        Class<?> clazz = obj.getClass();

        return (clazz.getDeclaringClass().equals(List.class));
    }

    /**
     * Iterates recursively an object and invokes the "translateTo" method on
     * instances of PrintableObject.class Note that when a
     * List<? extends PrintableObject> is met, then each item is handled
     * recursively
     *
     * @param SkillsPassport esp
     * @param obj The object to iterate
     * @param translationLocale The locale of translation
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static void deepTranslateTo(SkillsPassport esp, Object obj, Locale translationLocale) throws IllegalArgumentException, IllegalAccessException {
        if (obj == null) {
            return;
        }
        Class<?> objClazz = obj.getClass();

        //Get all getters and the getters of the super class if other than PrintableObject
        Method[] getters = getGetters(objClazz);
        Class<?> objSuperClazz = objClazz.getSuperclass();

        if (!objSuperClazz.equals(PrintableObject.class)) {
            Method[] superGetters = getGetters(objSuperClazz);

            getters = ObjectArrays.concat(getters, superGetters, Method.class);
        }

        for (Method getter : getters) {

            getter.setAccessible(true);

            Object childObj = null;
            try {
                childObj = getter.invoke(obj, new Object[0]);
            } catch (final InvocationTargetException e) {
                continue;
            }

            if (childObj == null) {
                continue;
            }

            Class<?> clazz = getter.getReturnType();

            boolean isObject = PrintableObject.class.isAssignableFrom(clazz);

            //Check if Parameterized Type such as Collection<T>
            boolean isListOfObject = false;
            Type genericType = getter.getGenericReturnType();
            boolean parameterizedType = (genericType instanceof ParameterizedType);
            try {
                if (parameterizedType) {
                    ParameterizedType pType = (ParameterizedType) genericType;
                    Class<?> memberClazz = (Class<?>) pType.getActualTypeArguments()[0];
                    isListOfObject = PrintableObject.class.isAssignableFrom(memberClazz);
                }
            } catch (Exception e) {
                continue;
            }
            //If not a PrintableObject OR not a list of PrintableObject, although parameterized type
            if (!isObject && !isListOfObject) {
                continue;
            }

            if (isListOfObject) {
                //The object is a List of Printable Object
                @SuppressWarnings("unchecked")
                List<? extends PrintableObject> childAsList = (List<? extends PrintableObject>) childObj;
                //recursive for each
                for (PrintableObject memberObj : childAsList) {
                    try {
                        //Translate this member
                        Method translateTo = memberObj.getClass().getMethod("translateTo", SkillsPassport.class, Locale.class);
                        translateTo.invoke(memberObj, esp, translationLocale);

                        //Translate the member children too
                        deepTranslateTo(esp, memberObj, translationLocale);

                    } catch (final Exception e) {
                        deepTranslateTo(esp, memberObj, translationLocale);
                    }
                }
            } else {
                //The object is a Printable Object
                try {
                    Method translateTo = clazz.getMethod("translateTo", SkillsPassport.class, Locale.class);
                    translateTo.invoke(childObj, esp, translationLocale);

                    //Translate its children too
                    deepTranslateTo(esp, childObj, translationLocale);
                } catch (final Exception e) {
                    deepTranslateTo(esp, childObj, translationLocale);
                }
            }
        }
    }

}
