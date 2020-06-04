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

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import europass.ewa.Utils;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.social.Mapping;
import europass.ewa.model.social.MappingListRoot;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.social.linkedin.api.LinkedInProfileFull;

public class MappingParser<O extends Object> {

    private static final Logger LOG = LoggerFactory.getLogger(MappingParser.class);

    private final MappingListRoot mapping;

    private final Set<Transformer> handlers;

    private O other;

    private SkillsPassport esp;

    public MappingParser(MappingListRoot mapping, Set<Transformer> handlers) {
        this.mapping = mapping;
        this.handlers = handlers;
    }

    public SkillsPassport parse(O other, Locale locale, String cookieId) throws ParseException {

        List<Mapping> mappings = this.mapping.getMappingList();

        this.other = other;

        this.esp = new SkillsPassport();
        esp.setLocale(locale);

        if (mappings == null) {
            return esp;
        }

        if (mappings.size() == 0) {
            return esp;
        }

        for (Mapping nested : mappings) {
            nested.setLocale(locale);
            recursiveParse(other, null, esp, null, nested, cookieId);
        }
        return esp;
    }

    /**
     * Utility used recursively
     *
     * @param other
     * @param europass
     * @param mapping
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    private boolean recursiveParse(Object other, Token prevOther, Object europass, Token prevEuropass, Mapping mapping, String cookieId) throws ParseException {

        if (mapping == null) {
            return false;
        }

        Locale locale = mapping.getLocale();

        MappingInfo info = consume(other, prevOther, europass, prevEuropass, mapping, cookieId);

        boolean mappingNonEmpty = info.hasContext();

        List<Mapping> nestedMappings = mapping.getMappingList();

        if (info.hasContext()
                && nestedMappings != null
                && nestedMappings.size() > 0) {

            Token fromToken = info.getFrom();
            Token toToken = info.getTo();

            switch (info.getMethod()) {
                case LIST: {
                    List<Object> listTo = (List<Object>) toToken.getObj();
                    List<Object> listFrom = (List<Object>) fromToken.getObj();
                    int listFromSize = listFrom.size();

                    for (int i = 0; i < listFromSize; i++) {
                        Object fromObj = listFrom.get(i);

                        boolean nestedFollowed = false;
                        for (int j = 0; j < nestedMappings.size(); j++) {
                            LOG.info("LIST: j: " + j + "\n nestedFollowed: " + nestedFollowed);
                            Mapping nested = nestedMappings.get(j);

                            nested.setLocale(locale);

                            //isFirst if the new item has still not be added in the list.
                            boolean isFirst = (nestedFollowed == false);

                            Object toObj = isFirst ? toToken.getObj() : listTo.get(listTo.size() - 1);
                            Token toPrevToken = isFirst ? toToken : null;

                            boolean followed = this.recursiveParse(fromObj, null, toObj, toPrevToken, nested, cookieId);
                            LOG.info("followed: " + followed);
                            if (followed) {
                                nestedFollowed = true;
                            }
                        }
                    }
                    break;
                }
                case LIST_ITEM: {
                    List<Object> listTo = (List<Object>) toToken.getObj();

                    boolean nestedFollowed = false;
                    for (int j = 0; j < nestedMappings.size(); j++) {

                        LOG.info("LIST_ITEM: j: " + j + "\n nestedFollowed: " + nestedFollowed);

                        Mapping nested = nestedMappings.get(j);

                        nested.setLocale(locale);

                        boolean isFirst = (nestedFollowed == false);
                        Object toObj = isFirst ? toToken.getObj() : listTo.get(listTo.size() - 1);
                        Token toPrevToken = isFirst ? toToken : null;

                        boolean followed = this.recursiveParse(fromToken.getObj(), fromToken, toObj, toPrevToken, nested, cookieId);
                        LOG.info("followed: " + followed);
                        if (followed) {
                            nestedFollowed = true;
                        }
                    }

                    break;
                }
                default: {
                    for (Mapping nested : mapping.getMappingList()) {
                        LOG.info("DEFAULT");

                        nested.setLocale(locale);

                        this.recursiveParse(fromToken.getObj(), fromToken, toToken.getObj(), toToken, nested, cookieId);
                    }
                }
            }

        }

        return mappingNonEmpty;
    }

    /**
     * Since the get returns an Object and not a String or a Primitive Type, the
     * result need not be a set, rather an instantiation of an object, according
     * to the To
     *
     * @param mapping
     * @return
     * @throws ParseException
     */
    private MappingInfo consume(Object fromContext, Token prevFromToken, Object toContext, Token prevToToken, Mapping mapping, String cookieId) throws ParseException {
        boolean isExtraData = false;

        if (mapping.getFrom() != null && mapping.getFrom().equals("/extraData") && other instanceof LinkedInProfileFull) {
            isExtraData = true;
        }

        MappingInfo info = new MappingInfo(mapping);
        info.setMethod(MethodType.NOTHING);

        if (mapping == null) {
            info.setMethod(MethodType.SKIP);
            return info;
        }

        // Read from Path
        Token fromToken = find(this.other, fromContext, mapping.getFrom(), prevFromToken);
        Object from = fromToken.getObj();
        if (from == null) {
            info.setMethod(MethodType.SKIP);
            return info;
        }
        boolean getValueFrom = Utils.isLeafType(fromToken.getObjClazz());
        boolean fromIsList = fromToken.isParameterized();

        Token toToken = find(this.esp, toContext, mapping.getTo(), prevToToken);
        boolean setValueTo = Utils.isLeafType(toToken.getObjClazz());
        boolean toIsList = toToken.isParameterized();

        LOG.info("From: " + fromToken.getType() + "\nTo: " + toToken.getType());
        // Handler
        String handlerName = mapping.getThrough();
        String[] params = mapping.getParamsAsArray();

        // pass cookie id in params
        if (isExtraData && handlerName.equals("PhotoHandler")) {
            List<String> paramsList = new ArrayList(Arrays.asList(params));
            paramsList.add(cookieId);
            params = new String[paramsList.size()];

            params = paramsList.toArray(params);
        }

        // --- CASES ---
        // A. There exists a Handler
        // HANDLE
        boolean hasHandler = !Strings.isNullOrEmpty(handlerName);
        if (hasHandler) {
            LOG.debug("There is a handler: " + handlerName);
            info.setMethod(MethodType.HANDLE);
            toToken.handleObj(from, this.findHandler(handlerName), params);
            if (fromIsList) {
                //stop recursion
                info.setMethod(MethodType.STOP);
            }
        }
        // B. From and To : Simple String or primitives Without a Handle
        // SET
        if (getValueFrom && setValueTo && !hasHandler) {
            info.setMethod(MethodType.SET);
            //Case Integer: 0 is considered to be null
            if (Integer.class.equals(from.getClass()) && ((Integer) from).intValue() == 0) {
                from = null;
            }
            toToken.setObjValue(from);
        }
        // C. From and To : List of
        if (fromIsList && toIsList) {
            //Loop From items and for each from create a To item.
            info.setMethod(MethodType.LIST);
        }
        // D. From is Leaf / To is List
        if (getValueFrom && toIsList) {
            info.setMethod(MethodType.LIST_ITEM);
        }

        //Update context
        info.setFrom(fromToken);
        info.setTo(toToken);

        return info;

    }

    /**
     * Will try to find a Handler instance from the given set of Handlers, by
     * testing whether the class name ends with the given name.
     *
     * @param name
     * @return
     */
    private Transformer findHandler(String name) {
        for (Transformer handler : this.handlers) {
            if (handler.getClass().getName().endsWith(name)) {
                return handler;
            }
        }
        throw new HandlerNotFoundException("Unable to load a Handler implementation by the name '" + name + "'.");
    }

    /**
     * ** STATICS ***
     */
    /**
     *
     * <ul>
     * <li><strong>Case "": </strong><em>Returns current object</em></li>
     * <li><strong>Case "/": </strong><em>Returns root object</em></li>
     * <li><strong>Case ".": </strong><em>Returns current object</em></li>
     * <li><strong>Case "/alpha": </strong>
     * <em>Returns alpha object from root</em></li>
     * <li><strong>Case "./alpha": </strong>
     * <em>Returns alpha object from current</em></li>
     * <li><strong>Case "/alpha/beta": </strong>
     * <em>Returns beta object from alpha under root</em></li>
     * <li><strong>Case "./alpha/beta": </strong>
     * <em>Returns beta object from alpha under current</em></li>
     * </ul>
     *
     * @param root
     * @param current
     * @param path
     * @return
     * @throws ParseException
     */
    private static Token find(Object root, Object current, String path, Token prevToken) throws ParseException {

        PathTokenizer tokenizer = PathTokenizer.compile(path);

        Token token = tokenizer.resolve(root, current, prevToken);

        return token;

    }

    static enum MethodType {
        NOTHING, SET, HANDLE, SKIP, LIST, LIST_ITEM, STOP;
    }

    static class MappingInfo {

        Mapping mapping;

        MethodType method;

        Token from;

        Token to;

        public MappingInfo(Mapping mapping) {
            this.mapping = mapping;
        }

        public boolean hasContext() {
            return from != null && to != null;
        }

        public MethodType getMethod() {
            return method;
        }

        public void setMethod(MethodType method) {
            this.method = method;
        }

        public Token getFrom() {
            return from;
        }

        public void setFrom(Token from) {
            this.from = from;
        }

        public Token getTo() {
            return to;
        }

        public void setTo(Token to) {
            this.to = to;
        }

    }

}
