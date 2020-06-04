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
package europass.ewa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import europass.ewa.collections.Predicates;

public class CleanupUtils {

    private CleanupUtils() {
    }

    /**
     * Will modify the given esp
     *
     * @param esp
     */
    public static void unresolvedAttachmentRefs(SkillsPassport esp) {
        Map<String, List<ReferenceTo>> sectionsWithRef = esp.getSectionOfReferenceTo();

        List<Attachment> attachments = esp.getAttachmentList();

        if (attachments == null) {
            return;
        }

        List<PrintingPreference> prefsToBeDeleted = new ArrayList<PrintingPreference>();
        //for each section check for unresolved references to attachments and 
        //if exists remove from the relevant section 
        for (Entry<String, List<ReferenceTo>> entry : sectionsWithRef.entrySet()) {
            String prefKey = entry.getKey();
            List<ReferenceTo> refSection = entry.getValue();

            if (prefKey == null || refSection == null || (refSection != null && refSection.size() == 0)) {
                continue;
            }

            List<Integer> indexes = new ArrayList<Integer>();
            for (ReferenceTo ref : refSection) {
                if (!isIDRefValid(attachments, ref.getIdref())) {
                    int idx = refSection.indexOf(ref);
                    indexes.add(idx);
                    prefsToBeDeleted.addAll(CleanupUtils.getRemovablePreferenceOfReference(esp, idx, prefKey));
                }
            }

            int removableNo = indexes.size();
            if (removableNo == 0) {
                continue;
            }

            for (int i = removableNo - 1; i >= 0; i--) {
                int idx = indexes.get(i);
                refSection.remove(idx);
            }

            CleanupUtils.removeReferenceOfPreference(esp, prefsToBeDeleted, prefKey);
            prefsToBeDeleted.clear();
        }

    }

    /**
     * Disables attachments from Annexes
     *
     * @param att
     */
    public static void disableAttachment(SkillsPassport esp, Attachment att) {
        if (Strings.isNullOrEmpty(att.getId())) {
            return;
        }

        if (esp.getLearnerInfo() == null) {
            return;
        }

        if (esp.getSectionOfReferenceTo() == null) {
            esp.sectionOfReferenceTo();
        }

        for (Entry< String, List<ReferenceTo>> entry : esp.getSectionOfReferenceTo().entrySet()) {
            String prefKey = entry.getKey();
            List<ReferenceTo> refSection = entry.getValue();

            if (prefKey == null || refSection == null || (refSection != null && refSection.size() == 0)) {
                continue;
            }
            CleanupUtils.removeReferenceToAttachment(esp, refSection, prefKey, att.getId());
        }
    }

    private static final Pattern REFERENCE_TO_PATTERN = Pattern.compile("^(.*\\.ReferenceTo\\[)(\\d+)(\\])$");

    /**
     *
     * @param refs list of referenceTo (annex, work experience, etc)
     * @param prefKey, the prefix (LearnerInfo, LearnerInfo.WorkExperience
     * @param attachmentId
     */
    public static void removeReferenceToAttachment(SkillsPassport esp, List<ReferenceTo> refs, String prefKey, String attachmentId) {
        List<PrintingPreference> prefsToBeDeleted = new ArrayList<PrintingPreference>();

        List<Integer> indexes = new ArrayList<Integer>();

        for (ReferenceTo ref : refs) {
            String idref = ref.getIdref();
            if (idref == null | "".equals(idref)) {
                continue;
            }
            if (attachmentId.compareTo(idref) == 0) {
                int idx = refs.indexOf(ref);
                indexes.add(idx);
                prefsToBeDeleted.addAll(CleanupUtils.getRemovablePreferenceOfReference(esp, idx, prefKey));
            }
        }
        int removableNo = indexes.size();
        if (removableNo == 0) {
            return;
        }

        for (int i = removableNo - 1; i >= 0; i--) {
            int idx = indexes.get(i);
            refs.remove(idx);
        }

        CleanupUtils.removeReferenceOfPreference(esp, prefsToBeDeleted, prefKey);
    }

    private static void removeReferenceOfPreference(SkillsPassport esp, List<PrintingPreference> prefsToBeDeleted, String sectionPrefKey) {
        List<PrintingPreference> prefs = esp.getDocumentPrintingPrefs().get(esp.getPrefDocumentName());

        List<PrintingPreference> matchingPrefs = Lists.newArrayList(getPreferencesByRefSection(esp, sectionPrefKey));
        int matchingPrefSize = matchingPrefs.size();

        if (matchingPrefSize == 0) {
            return;
        }

        for (PrintingPreference deleteThis : prefsToBeDeleted) {
            if (deleteThis == null) {
                continue;
            }
            //Do not only remove, but also re-index the rest
            int startIndex = matchingPrefs.indexOf(deleteThis);
            int delIndex = prefs.indexOf(deleteThis);
            matchingPrefs.remove(startIndex);
            prefs.remove(delIndex);

            //we must re-calculate size after removing from the list
            matchingPrefSize = matchingPrefs.size();
            //Re-index the rest references
            for (int i = startIndex; i < matchingPrefSize; i++) {
                PrintingPreference finalPref = matchingPrefs.get(i);
                String prevName = finalPref.getName().toString();

                Matcher matcher = REFERENCE_TO_PATTERN.matcher(prevName);
                //Only one match
                String frontPart = "", endPart = "";
                int prevIndex = 0;
                while (matcher.find()) {
                    frontPart = matcher.group(1);

                    String prevIndexStr = matcher.group(2);
                    prevIndex = Integer.parseInt(prevIndexStr);

                    endPart = matcher.group(3);
                }
                int newIndex = prevIndex - 1;
                if (newIndex >= 0) {
                    //e.g. LearnerInfo.ReferenceTo[4] --> LearnerInfo.ReferenceTo[3]
                    String newName = frontPart + (prevIndex - 1) + endPart;
                    finalPref.setName(newName);
                }
            }
        }

    }

    /**
     * Returns a list of printing preferences of ReferenceTo at index, that
     * start with the given pattern
     *
     * @param index
     * @param patternStart
     * @return
     */
    private static Collection<PrintingPreference> getRemovablePreferenceOfReference(SkillsPassport esp, int index, String patternStart) {
        List<PrintingPreference> prefs = esp.getDocumentPrintingPrefs().get(esp.getPrefDocumentName());

        String regex = patternStart.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") + Predicates.REFERENCETO_PREFS;
        regex = regex.replace("XXX", String.valueOf(index));

        return Collections2.filter(prefs, Predicates.containsPattern(regex));

    }

    private static Collection<PrintingPreference> getPreferencesByRefSection(SkillsPassport esp, String patternStart) {
        List<PrintingPreference> prefs = esp.getDocumentPrintingPrefs().get(esp.getPrefDocumentName());

        String regex = patternStart.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") + Predicates.REFERENCETO_PREFS_ANY_DIGIT;

        return Collections2.filter(prefs, Predicates.containsPattern(regex));

    }

    /**
     * Updates the Printing Preferences of those preferences whose name matches
     * the regular expression by setting the show attribute to the show
     * parameter.
     *
     * @param regex , the regular expression
     * @param show , the show value to set on the preference item
     */
    public static void updatePreferenceVisibility(SkillsPassport esp, String regex, boolean show) {
        if (esp.getDocumentPrintingPrefs() == null) {
            return;
        }
        // 1. Get the List of PrintingPreferences for the current Document
        List<PrintingPreference> prefs = esp.getDocumentPrintingPrefs().get(esp.getPrefDocumentName());

        Collection<PrintingPreference> matchedPrefs = Collections2.filter(prefs, Predicates.containsPattern(regex));

        for (PrintingPreference pref : matchedPrefs) {
            pref.setShow(show);
        }
    }

    //**** Disable Photo and Attachments ****/
    public static boolean disablePhoto(SkillsPassport esp) {
        FileData photo = esp.personalPhoto();
        if (photo != null) {
            //if we are here, learnerinfo and identification are not null
            //set photo
            esp.getLearnerInfo().getIdentification().setPhoto(null);
        }
        //hide photo
        CleanupUtils.updatePreferenceVisibility(esp, Predicates.ESP_PHOTO_PREF, false);
        return true;
    }

    //**** Disable signature ****/
    public static boolean disableSignature(SkillsPassport esp) {
        FileData signature = esp.personalSignature();
        if (signature != null) {
            //if we are here, learnerinfo and identification are not null
            //set photo
            esp.getLearnerInfo().getIdentification().setSignature(null);
        }

        return true;
    }

    /**
     *
     * @param attachments
     * @param idRef
     * @return true if the reference is to an existing attachment, false
     * otherwise
     */
    private static boolean isIDRefValid(List<Attachment> attachments, String idRef) {
        for (Attachment a : attachments) {
            if (a == null) {
                continue;
            }
            String id = a.getId();
            if (Strings.isNullOrEmpty(id)) {
                continue;
            }
            if (id.equals(idRef)) {
                return true;
            }
        }
        return false;
    }

}
