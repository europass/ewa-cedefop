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

import europass.ewa.model.reflection.ReflectiveLoadingException;

/**
 * CLASS NOT USED
 *
 * This class is developed as an idea for re-factoring the Social to Europass
 * translation according to the "Chain of Responsibility Pattern". The main
 * reason for this refactoring would be to differentiate the process of
 * reflectively reading the Social Java Model, from the process of reflectively
 * loading the Europass Java Model.
 *
 * @author ekar
 *
 */
public class Experimental_Token {

    private TokenInfo info;

    public final static char HERE = '.';

    public final static char ROOT = '/';

    public final static char DASH = ROOT;

    public Experimental_Token(String type) {
        this.getInfo().setType(type);
    }

    public boolean canSet() {
        return this.getInfo().getSetInfo().canSet;
    }

    public Object getObj() {
        return this.getInfo().getObjInfo().getObj();
    }

    public Class<?> getObjClazz() {
        return this.getInfo().getObjInfo().getObjClazz();
    }

    public String getType() {
        return this.getInfo().getType();
    }

    public String getSource() {
        return this.getInfo().getSource();
    }

    public boolean isParameterized() {
        return this.getInfo().getObjInfo().isParameterized();
    }

    public TokenInfo getInfo() {
        if (info == null) {
            info = new TokenInfo();
        }
        return info;
    }

    public void setInfo(TokenInfo info) {
        this.info = info;
    }

    /**
     * Main method to resolve objects
     *
     * @param root
     * @param current
     * @param prevToken
     * @return
     */
    public Experimental_Token resolve(Object root, Object current, Experimental_Token prevToken) {
        switch (getType()) {
            case TokenInfo.TYPE_ROOT: {
                if (root == null) {
                    throw new ReflectiveLoadingException("Root is null, while resolving Path Token to Object.");
                }
                getInfo().setObjInfo(prevToken == null ? new ObjInfo(root) : prevToken.getInfo().getObjInfo());
                break;
            }
            case TokenInfo.TYPE_CURRENT: {
                if (current == null) {
                    throw new ReflectiveLoadingException("Current is null, while resolving Path Token to Object.");
                }
                getInfo().setObjInfo(prevToken == null ? new ObjInfo(current) : prevToken.getInfo().getObjInfo());
                break;
            }
            default: {
                switch (getSource()) {
                    case TokenInfo.SOURCE_FROM: {
                        FromTokenConsumeProcess process = new FromTokenConsumeProcess();
                        process.process(getInfo());
                        break;
                    }
                    default: {
                        ToTokenConsumeProcess process = new ToTokenConsumeProcess();
                        process.process(getInfo());
                        break;
                    }
                }
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
    public Experimental_Token handleObj(Object from, Transformer handler, Object[] params) {
        try {
            ObjInfo objInfo = getInfo().getObjInfo();
            Object newObj = handler.transform(from, objInfo.getObj(), params);
            SetInfo setInfo = getInfo().getSetInfo();
            setInfo.canSet = true;
            this.setObjValue(newObj);
        } catch (InstanceClassMismatchException e) {
            throw new ReflectiveLoadingException("Failed to handle value and set it for item for '" + getType()
                    + "', for the specific Token.", e);
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
    public Experimental_Token setObjValue(Object to) {
        try {
            ObjInfo objInfo = getInfo().getObjInfo();
            SetInfo setInfo = getInfo().getSetInfo();
            if (setInfo.canSet) {
                setInfo.setter.invoke(objInfo.getParentObj(), to);

                objInfo.setObj(to);
            }
        } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectiveLoadingException("Failed to set value for item for '" + getType() + "', for the specific Token.", e);
        }

        return this;
    }

    static class TokenFactory {

        public static Experimental_Token getRoot() {
            return new Experimental_Token(TokenInfo.TYPE_ROOT);
        }

        public static Experimental_Token getCurrent() {
            return new Experimental_Token(TokenInfo.TYPE_CURRENT);
        }
    }

}
