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
package europass.ewa.services.social;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.Utils;
import europass.ewa.model.reflection.ReflectionUtils;
import europass.ewa.model.reflection.ReflectiveLoadingException;

public class Token {

    private static final Logger LOG = LoggerFactory.getLogger(Token.class);

    private String type;

    private boolean isParameterized;

    private ObjInfo objInfo;

    private SetInfo setInfo;

    public final static char HERE = '.';

    public final static char ROOT = '/';

    public final static char DASH = ROOT;

    public static final String TYPE_ROOT = "root";

    public static final String TYPE_CURRENT = "currentNode";

    public static final String SOURCE_FROM = "from";

    public static final String SOURCE_TO = "to";

    public Token(String type) {
        this.type = type;
    }

    public boolean canSet() {
        return this.setInfo == null ? false : this.setInfo.canSet;
    }

    public Object getObj() {
        return objInfo == null ? null : objInfo.getObj();
    }

    public Class<?> getObjClazz() {
        return objInfo == null ? null : objInfo.getObjClazz();
    }

    public String getType() {
        return type;
    }

    public boolean isParameterized() {
        return isParameterized;
    }

    public void setParameterized(boolean isParameterized) {
        this.isParameterized = isParameterized;
    }

    public SetInfo getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(SetInfo setInfo) {
        this.setInfo = setInfo;
    }

    public ObjInfo getObjInfo() {
        return objInfo;
    }

    public void setObjInfo(ObjInfo objInfo) {
        this.objInfo = objInfo;
    }

    /**
     * Main method to resolve objects
     *
     * @param root
     * @param current
     * @param prevToken
     * @return
     */
    public Token resolve(Object root, Object current, Token prevToken) {
        switch (type) {
            case TYPE_ROOT: {
                if (root == null) {
                    LOG.info("Root is null, while resolving Path Token to Object.");
                    break;
                }
                this.setObjInfo(prevToken == null ? new ObjInfo(root) : prevToken.getObjInfo());
                break;
            }
            case TYPE_CURRENT: {
                if (current == null) {
                    LOG.info("Current is null, while resolving Path Token to Object.");
                    break;
                }
                this.setObjInfo(prevToken == null ? new ObjInfo(current) : prevToken.getObjInfo());
                break;
            }
            default: {
                if (current == null) {
                    LOG.info("Current is null, cannot proceed to getters, while resolving Path Token to Object.");
                    break;
                }

                Object obj = null;

                //0. Case where previous item is a list... 
                ObjInfo previousInfo = prevToken == null ? null : prevToken.getObjInfo();

                if (!canGetObject(current, type) && previousInfo != null && previousInfo.isMember()) {

                    try {
                        Class<?> memberClazz = previousInfo.getMemberClazz();

                        Object item = memberClazz.newInstance();

                        Method add = List.class.getDeclaredMethod("add", Object.class);

                        add.invoke(previousInfo.getObj(), item);

                        LOG.info("Add to list of " + memberClazz.toString());
                        //Current was the array, we need to switch to the item
                        current = item;

                    } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e) {
                        throw new ReflectiveLoadingException("Cannot instantiate item for '" + type + "', while resolving Path Token to Object.", e);
                    } catch (final IllegalArgumentException | InvocationTargetException e) {
                        throw new ReflectiveLoadingException("Cannot add previously instantiated item for '" + type + "', while resolving Path Token to Object.", e);
                    }
                }

                //1. Get object
                Class<?> currentClazz = current.getClass();
                //true stands for excluding getters that return Objects
                Method getter = ReflectionUtils.getGetter(currentClazz, type, true);
                if (getter == null) {
                    throw new ReflectiveLoadingException("Cannot find a suitable getter for '" + type + "', while resolving Path Token to Object.");
                }
                getter.setAccessible(true);
                try {
                    obj = getter.invoke(current, new Object[0]);
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new ReflectiveLoadingException("Cannot invoke method for '" + type + "', while resolving Path Token to Object.", e);
                }

                // Get further info for this obj
                Class<?> objClazz = getter.getReturnType();
                Type genericType = getter.getGenericReturnType();
                boolean isParameterized = (genericType instanceof ParameterizedType);

                //2. If the obj is a String or any other Primitive Type, we offer the ability to set it.
                boolean isLeaf = Utils.isLeafType(objClazz);
                this.setInfo = new SetInfo(isLeaf, ReflectionUtils.getSetter(currentClazz, objClazz, type));

                Class<?> instantiationClazz = isParameterized ? ArrayList.class : objClazz;

                this.objInfo = new ObjInfo();
                this.objInfo.setObjClazz(instantiationClazz);
                this.objInfo.setParentObj(current);
                if (isParameterized) {
                    ParameterizedType pType = (ParameterizedType) genericType;
                    Class<?> memberClazz = (Class<?>) pType.getActualTypeArguments()[0];
                    this.objInfo.setMemberClazz(memberClazz);
                }
                this.setParameterized(isParameterized);

                //3. The obj is null, we need to instantiate it and set it to its previous object...
                if (obj == null && !Utils.isWrapperType(instantiationClazz)) {
                    try {
                        obj = instantiationClazz.newInstance();

                        //And set obj to previous object!!!
                        Method setter = ReflectionUtils.getSetter(currentClazz, instantiationClazz, type);

                        //We must not set to the input model. E.g. LinkedIn does not have setters...
                        if (setter == null) {
                            break;
                        }
                        setter.invoke(current, obj);

//					} catch ( final InstantiationException | IllegalAccessException e ) {
//						throw new ReflectiveLoadingException("Cannot instantiate object for '"+type+"', while resolving Path Token to Object.", e);
//					} catch ( final IllegalArgumentException | InvocationTargetException e ) {
//						throw new ReflectiveLoadingException("Cannot set previously instantiated object for '"+type+"', while resolving Path Token to Object.", e);
//					}
                    } catch (final Exception e) {
                        break;
                    }
                }

                //4. Set this Token's Object Info
                this.objInfo.setObj(obj);

                break;
            }
        }
        return this;
    }

    /**
     * External utility used to employ a handler when needing to handle a
     * specific object
     *
     * @param handler
     * @param params
     * @return
     */
    public Token handleObj(Object from, Transformer handler, Object[] params) {
        try {
            Object newObj = handler.transform(from, this.objInfo.getObj(), params);
            this.setInfo.canSet = true;
            this.setObjValue(newObj);
        } catch (InstanceClassMismatchException e) {
            throw new ReflectiveLoadingException("Failed to user handler '" + handler.getClass().getName() + "' to handle value and set value for item for '" + type + "', for the specific Token.", e);
        }

        return this;
    }

    /**
     * External utility to be called when we need to set the current object to a
     * specific object
     *
     * @param to
     * @return
     */
    public Token setObjValue(Object to) {
        try {
            if (this.setInfo.canSet) {
                this.setInfo.setter.invoke(this.objInfo.getParentObj(), to);

                this.objInfo.setObj(to);
            }
        } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectiveLoadingException("Failed to set value for item for '" + type + "', for the specific Token.", e);
        }

        return this;
    }

    private boolean canGetObject(Object from, String type) {
        try {
            return (this.getObject(from, type)) != null;
        } catch (final ReflectiveLoadingException e) {
            return false;
        }
    }

    private Object getObject(Object from, String type) {
        Class<?> currentClazz = from.getClass();
        Method getter = ReflectionUtils.getGetter(currentClazz, type, false);
        if (getter == null) {
            throw new ReflectiveLoadingException("Cannot find a suitable getter for '" + type + "', while resolving Path Token to Object.");
        }
        getter.setAccessible(true);
        try {
            return getter.invoke(from, new Object[0]);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveLoadingException("Cannot invoke method for '" + type + "', while resolving Path Token to Object.", e);
        }
    }

    private class ObjInfo {

        Object obj;

        Class<?> objClazz;

        Class<?> memberClazz;

        Object parentObj;

        public ObjInfo() {
        }

        public ObjInfo(Object obj) {
            this.obj = obj;
        }

        public boolean isMember() {
            return this.memberClazz != null;
        }

        public Object getObj() {
            return obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }

        public Class<?> getObjClazz() {
            return obj == null ? objClazz : obj.getClass();
        }

        public void setObjClazz(Class<?> objClazz) {
            this.objClazz = objClazz;
        }

        public Class<?> getMemberClazz() {
            return memberClazz;
        }

        public void setMemberClazz(Class<?> memberClazz) {
            this.memberClazz = memberClazz;
        }

        public Object getParentObj() {
            return parentObj;
        }

        public void setParentObj(Object parentObj) {
            this.parentObj = parentObj;
        }

        // ----
        @Override
        public String toString() {
            return this.obj == null ? "" : this.obj.toString() + ":" + this.memberClazz == null ? "" : this.memberClazz.toString() + ":" + this.parentObj == null ? "" : this.parentObj.toString();
        }
    }

    /**
     * Simple class holding information about using a set Method
     *
     * @author ekar
     *
     */
    private class SetInfo {

        boolean canSet;

        Method setter;

        public SetInfo(boolean canSet, Method setter) {
            this.canSet = canSet;
            this.setter = setter;
        }

        // ----
        @Override
        public String toString() {
            return this.canSet + ":" + this.setter == null ? "" : this.setter.toString();
        }
    }

    static class TokenFactory {

        public static Token getRoot() {
            return new Token(TYPE_ROOT);
        }

        public static Token getCurrent() {
            return new Token(TYPE_CURRENT);
        }
    }

    // ----
    @Override
    public String toString() {
        return this.type + ":" + this.objInfo == null ? "" : this.objInfo.toString() + ":" + this.setInfo == null ? "" : this.setInfo.toString();
    }

}
