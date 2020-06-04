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

import europass.ewa.Utils;
import europass.ewa.model.reflection.ReflectionUtils;
import europass.ewa.model.reflection.ReflectiveLoadingException;

/**
 * CLASS(ES) NOT USED
 *
 * The included classes is developed as an idea for re-factoring the Social to
 * Europass translation according to the "Chain of Responsibility Pattern". The
 * main reason for this refactoring would be to differentiate the process of
 * reflectively reading the Social Java Model, from the process of reflectively
 * loading the Europass Java Model.
 *
 * @author ekar
 *
 */
public interface TokenConsumeProcess {

    void process(TokenInfo info);

}

class FromTokenConsumeProcess implements TokenConsumeProcess {

    TokenConsumeStep step1;
    TokenConsumeStep step3;
    TokenConsumeStep step4;

    public FromTokenConsumeProcess() {
        step1 = new GetObjectStep();
        step3 = new SetterInfoStep();
        step4 = new ObjectInfoStep();

        this.scheduleSteps();
    }

    private void scheduleSteps() {
        step1.setNext(step3);
        step3.setNext(step4);
        step4.setNext(null);
    }

    @Override
    public void process(TokenInfo info) {
        step1.consume(info);
    }
}

class ToTokenConsumeProcess implements TokenConsumeProcess {

    TokenConsumeStep step0;
    TokenConsumeStep step1;
    TokenConsumeStep step2;
    TokenConsumeStep step3;
    TokenConsumeStep step4;

    public ToTokenConsumeProcess() {
        step0 = new NewListItemStep();
        step1 = new GetObjectStep();
        step2 = new InstantiateObjectStep();
        step3 = new SetterInfoStep();
        step4 = new ObjectInfoStep();

        this.scheduleSteps();
    }

    private void scheduleSteps() {
        step0.setNext(step1);
        step1.setNext(step2);
        step2.setNext(step3);
        step3.setNext(step4);
        step4.setNext(null);
    }

    @Override
    public void process(TokenInfo info) {
        step1.consume(info);
    }
}
//Other

class SimpleSetInfo extends AbstractTokenConsumeStep {

    @Override
    public void consume(TokenInfo info) {

        //Consume for root and current
        super.consume(info);
    }
}
//0

class NewListItemStep extends AbstractTokenConsumeStep {

    @Override
    public void consume(TokenInfo info) {

        Object current = null;

        String type = info.getType();

        Token prevToken = info.getPrevToken();

        ObjInfo previousInfo = prevToken == null ? null : info.getObjInfo();
        // WRONG - needs to be info.getPrevToken().getInfo().getObjInfo() ;

        if (previousInfo != null && previousInfo.isMember()) {
            try {
                Class<?> memberClazz = previousInfo.getMemberClazz();

                Object item = memberClazz.newInstance();

                Method add = List.class.getDeclaredMethod("add", Object.class);

                add.invoke(previousInfo.getObj(), item);

                //Current was the array, we need to switch to the item
                current = item;

            } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e) {
                throw new ReflectiveLoadingException("Cannot instantiate item for '" + type + "', while resolving Path Token to Object.", e);
            } catch (final IllegalArgumentException | InvocationTargetException e) {
                throw new ReflectiveLoadingException("Cannot add previously instantiated item for '" + type + "', while resolving Path Token to Object.", e);
            }
        }

        info.setCurrent(current);

        super.consume(info);
    }
}
//1

class GetObjectStep extends AbstractTokenConsumeStep {

    @Override
    public void consume(TokenInfo info) {

        Object current = info.getCurrent();
        if (current == null) {
            throw new ReflectiveLoadingException("Current is null, cannot proceed to getters, while resolving Path Token to Object.");
        }

        String type = info.getType();

        ObjInfo objInfo = info.getObjInfo();

        Object obj = null;
        Class<?> currentClazz = current.getClass();
        //true stands for excluding getters of Object
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

        objInfo.setGenericType(genericType);

        //2. The obj is null, we need to instantiate it and set it to its previous object...
        Class<?> instantiationClazz = isParameterized ? ArrayList.class : objClazz;
        objInfo.setObjClazz(instantiationClazz);

        objInfo.setObj(obj);

        super.consume(info);
    }
}
//2

class InstantiateObjectStep extends AbstractTokenConsumeStep {

    @Override
    public void consume(TokenInfo info) {
        Object current = info.getCurrent();
        if (current == null) {
            throw new ReflectiveLoadingException("Current is null, cannot proceed to getters, while resolving Path Token to Object.");
        }
        String type = info.getType();

        ObjInfo objInfo = info.getObjInfo();

        Object obj = objInfo.getObj();

        Class<?> instantiationClazz = objInfo.getObjClazz();

        if (obj == null && !Utils.isWrapperType(instantiationClazz)) {
            try {
                obj = instantiationClazz.newInstance();

                //And set obj to previous object!!!
                Method setter = ReflectionUtils.getSetter(current.getClass(), instantiationClazz, type);
                if (setter == null) {
                    throw new ReflectiveLoadingException("Cannot find a suitable setter for '" + type + "', while resolving Path Token to Object.");
                }
                setter.invoke(current, obj);

            } catch (final InstantiationException | IllegalAccessException e) {
                throw new ReflectiveLoadingException("Cannot instantiate object for '" + type + "', while resolving Path Token to Object.", e);
            } catch (final IllegalArgumentException | InvocationTargetException e) {
                throw new ReflectiveLoadingException("Cannot set previously instantiated object for '" + type + "', while resolving Path Token to Object.", e);
            }
        }
        objInfo.setObj(obj);

        super.consume(info);
    }
}
//3

class SetterInfoStep extends AbstractTokenConsumeStep {

    @Override
    public void consume(TokenInfo info) {
        Object current = info.getCurrent();
        if (current == null) {
            throw new ReflectiveLoadingException("Current is null, cannot proceed to getters, while resolving Path Token to Object.");
        }
        String type = info.getType();

        ObjInfo objInfo = info.getObjInfo();

        Class<?> objClazz = objInfo.getObjClazz();

        SetInfo setInfo = new SetInfo(Utils.isLeafType(objClazz), ReflectionUtils.getSetter(current.getClass(), objClazz, type));
        info.setSetInfo(setInfo);

        super.consume(info);
    }
}
//4

class ObjectInfoStep extends AbstractTokenConsumeStep {

    @Override
    public void consume(TokenInfo info) {
        Object current = info.getCurrent();
        if (current == null) {
            throw new ReflectiveLoadingException("Current is null, cannot proceed to getters, while resolving Path Token to Object.");
        }

        ObjInfo objInfo = info.getObjInfo();

        objInfo.setParentObj(current);
        if (objInfo.isParameterized()) {
            ParameterizedType pType = (ParameterizedType) objInfo.getGenericType();
            Class<?> memberClazz = (Class<?>) pType.getActualTypeArguments()[0];
            objInfo.setMemberClazz(memberClazz);
        }

        super.consume(info);
    }
}

class TokenInfo {

    public static final String TYPE_ROOT = "root";

    public static final String TYPE_CURRENT = "currentNode";

    public static final String SOURCE_FROM = "from";

    public static final String SOURCE_TO = "to";

    private Object current;

    private String type;

    private String source;

    private ObjInfo objInfo;

    private SetInfo setInfo;

    private Token prevToken;

    public Object getCurrent() {
        return current;
    }

    public void setCurrent(Object current) {
        this.current = current;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ObjInfo getObjInfo() {
        if (objInfo == null) {
            objInfo = new ObjInfo();
        }
        return objInfo;
    }

    public void setObjInfo(ObjInfo objInfo) {
        this.objInfo = objInfo;
    }

    public SetInfo getSetInfo() {
        if (setInfo == null) {
            setInfo = new SetInfo();
        }
        return setInfo;
    }

    public void setSetInfo(SetInfo setInfo) {
        this.setInfo = setInfo;
    }

    public Token getPrevToken() {
        return prevToken;
    }

    public void setPrevToken(Token prevToken) {
        this.prevToken = prevToken;
    }

}

class ObjInfo {

    Object obj;

    Class<?> objClazz;

    Type genericType;

    Class<?> memberClazz;

    Object parentObj;

    public ObjInfo() {
    }

    public ObjInfo(Object obj) {
        this.obj = obj;
    }

    public boolean isParameterized() {
        return genericType instanceof ParameterizedType;
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

    public Type getGenericType() {
        return genericType;
    }

    public void setGenericType(Type genericType) {
        this.genericType = genericType;
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
class SetInfo {

    boolean canSet;

    Method setter;

    public SetInfo() {
    }

    public SetInfo(boolean canSet, Method setter) {
        this.canSet = canSet;
        this.setter = setter;
    }

    @Override
    public String toString() {
        return this.canSet + ":" + this.setter == null ? "" : this.setter.toString();
    }
}

abstract class AbstractTokenConsumeStep implements TokenConsumeStep {

    private TokenConsumeStep nextStep = null;

    @Override
    public void setNext(TokenConsumeStep nextStep) {
        this.nextStep = nextStep;
    }

    @Override
    public void consume(TokenInfo info) {
        if (nextStep != null) {
            nextStep.consume(info);
        }
    }
}

interface TokenConsumeStep {

    void consume(TokenInfo info);

    void setNext(TokenConsumeStep step);
}

//switch ( source ){
//case SOURCE_FROM:{
//	//1. Get object and further info
//	//IN: current, type 
//	//OUT: obj, getter, genericType, isParameterized, instantiationClass
//	
//	//3. Set Info
//	//IN: isLeaf, setter
//	
//	//4. Set this Token's Object Info
//	//IN: obj, instantiationClazz, genericType
//	//OUT void (this.objInfo is updated)
//	
//	break;
//}
//default:{
//	//0. New List Item
//	//IN: prevToken
//	//OUT: current
//	
//	//1. Get object and further info
//
//	//2. The obj is null, we need to instantiate it and set it to its previous object...
//	//IN: objClazz, currentClazz
//	//OUT: obj
//	
//	//3. Set Info
//	
//	//4. Token Info
//}
//}
