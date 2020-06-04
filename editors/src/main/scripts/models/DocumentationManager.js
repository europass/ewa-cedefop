define(
        [
            'jquery',
            'underscore',
            'jsonpath',
            'europass/http/SessionManagerInstance',
            'europass/http/Resource'
//	  ,'i18n!localization/nls/Notification'
        ]
        , function ($, _, _JSONPATH, SessionManager, Resource/*, Notification*/) {
            var DocumentationManager = function (model) {
                this.model = model;
            };
            /**
             * Returns the Attachment object and its index based on
             * the ReferenceTo path, or null if: - there are no
             * attachments - or there is no ReferenceTo with the
             * specific path - or there is no idref - or there is no
             * attachment with id that corresponds to the idref of
             * the referenceTo.
             */
            DocumentationManager.prototype.findAttachment = function (referenceToPath) {
                // SkillsPassport.Attachment
                var attachments = this.model.get(this.model.ATTACHMENT_SECTION);
                if (attachments === undefined || attachments === null) {
                    return null;
                }
                // SkillsPassport.LearnerInfo.ReferenceTo[x]
                var referenceTo = this.model.get(referenceToPath);
                if (referenceTo === undefined || referenceTo === null) {
                    return null;
                }
                // SkillsPassport.LearnerInfo.ReferenceTo[x].idref
                var idref = referenceTo.idref;
                if (idref === undefined || idref === null || idref === "") {
                    return null;
                }
                var attachment = null;
                var index = null;
                for (var idx = 0; idx < attachments.length; idx++) {
                    var att = attachments[idx];
                    if (att.Id === idref) {
                        attachment = att;
                        index = idx;
                        break;
                    }
                }
                if (attachment === null || index === null) {
                    return null;
                }
                return {
                    "attachment": attachment,
                    "index": index
                };
            };
            /**
             * Find the reference to the given attachment for the
             * given section
             * 
             * @param the section in which to search
             * @param the attachment id to find
             * 
             * @return and object comprised of the index of the
             *         found or not element and the length of the
             *         array
             * 
             * the index of the Reference inside the ReferenceTo
             * array or -1 if not found or -2 if the section does
             * not exist or -3 if the sections exists, but is not an
             * array
             */
            DocumentationManager.prototype.findReferenceTo = function (id, section) {
                var result = {
                    index: -1,
                    length: -1
                };
                var noSection = {
                    index: -2,
                    length: -1
                };
                var notArray = {
                    index: -3,
                    length: -1
                };

                if (id === undefined || id === null || id === "") {
                    return null;
                }

                if (section === undefined || section === null || section === "") {
                    section = this.model.ANNEXES_SECTION;
                }

                section = this.referenceToPath(section);

                var refs = this.model.get(section);

                // There is no such path in the model
                if (refs === undefined || refs === null) {
                    return noSection;
                }
                // This section exists but it is not an array
                if (refs !== undefined && refs !== null && !$.isArray(refs)) {
                    return notArray;
                }

                var refsLength = refs.length;
                for (var idx = 0; idx < refsLength; idx++) {
                    var ref = refs[idx];
                    if (ref.idref === id) {
                        result.index = idx;
                        break;
                    }
                }
                result.length = refsLength;
                return result;
            };
            /**
             * Find Reference To based on a Given Attachment ID
             * 
             * @return the complete path to the specific reference
             */
            DocumentationManager.prototype.findReferencesTo = function (id, excludeAnnexes) {
                var results = [];
                var jsonPath = "$..ReferenceTo[?(@.idref=='" + id + "')]";

                var model = {};
                $.extend(true, model, this.model.attributes);

                if (excludeAnnexes === true) {
                    if (model.SkillsPassport.LearnerInfo !== undefined && model.SkillsPassport.LearnerInfo.ReferenceTo !== undefined)
                        delete model.SkillsPassport.LearnerInfo.ReferenceTo;
                }

                var sectionPaths = _JSONPATH(model, jsonPath, {
                    resultType: "PATH"
                });

                if (sectionPaths === false) {
                    return [];
                }
                for (var idx = 0; idx < sectionPaths.length; idx++) {
                    var sectionPath = sectionPaths[ idx ];
                    var s = sectionPath.substr(1);
                    s = s.replace(/(\[\d+\])/g, "$1.");
                    s = s.replace(/\['/g, "");
                    s = s.replace(/\']/g, ".");
                    s = s.replace(/\.(\[\d+\])/g, "$1");
                    s = (s.length > 0) ? s.substr(0, s.length - 1) : s;
                    results.push(s);
                }

                return results;
            };
            /**
             * Find Reference To sections
             * 
             * @return the complete path to the specific reference
             */
            DocumentationManager.prototype.findReferencesToSections = function (excludeAnnexes) {
                var results = [];

                var jsonPath = "$..ReferenceTo";

                var model = {};
                $.extend(true, model, this.model.attributes);

                if (excludeAnnexes === true) {
                    if (model.SkillsPassport.LearnerInfo !== undefined && model.SkillsPassport.LearnerInfo.ReferenceTo !== undefined)
                        delete model.SkillsPassport.LearnerInfo.ReferenceTo;
                }

                var sectionPaths = _JSONPATH(model, jsonPath, {
                    resultType: "PATH"
                });

                if (sectionPaths === false) {
                    return [];
                }
                for (var idx = 0; idx < sectionPaths.length; idx++) {
                    var sectionPath = sectionPaths[ idx ];
                    var s = sectionPath.substr(1);
                    s = s.replace(/(\[\d+\])/g, "$1.");
                    s = s.replace(/\['/g, "");
                    s = s.replace(/\']/g, ".");
                    s = s.replace(/\.(\[\d+\])/g, "$1");
                    s = (s.length > 0) ? s.substr(0, s.length - 1) : s;
                    results.push(s);
                }
                return results;
            };
            /**
             * If the section does not end with "ReferenceTo",
             * append it
             */
            DocumentationManager.prototype.referenceToPath = function (path) {
                var suffix = this.model.REFERENCETO;
                if (path.indexOf(suffix,
                        (path.length - suffix.length)) !== -1) {
                    return path;
                }
                return path + suffix;
            };
            DocumentationManager.prototype.attachmentById = function (attachId) {
                // SkillsPassport.Attachment
                var attachments = this.model.get(this.model.ATTACHMENT_SECTION);
                if (attachments === undefined || attachments === null) {
                    return null;
                }

                var attachment = null;
                var index = null;
                for (var idx = 0; idx < attachments.length; idx++) {
                    var att = attachments[ idx ];
                    if (att === null) {
                        continue;
                    }
                    if (att.Id === attachId) {
                        attachment = att;
                        index = idx;
                        break;
                    }
                }
                if (attachment === null || index === null) {
                    return null;
                }
                return {
                    "attachment": attachment,
                    "index": index
                };
            };
            DocumentationManager.prototype.getAttachmentURI = function (referenceToPath) {
                var matchedAttachment = this.findAttachment(referenceToPath);
                if (matchedAttachment === null) {
                    return false;
                }

                // Delete service
                if (matchedAttachment.attachment === null) {
                    return false;
                }
                var uri = matchedAttachment.attachment.TempURI;
                if (uri === undefined || uri === null || uri === "") {
                    return null;
                }
                return uri;
            };
            /**
             * HTTP Delete for all attachments
             */
            DocumentationManager.prototype.restRemoveAllAttachments = function () {
                var attachments = this.model.get(this.model.ATTACHMENT_SECTION);
                if (attachments !== undefined && $.isArray(attachments)) {
                    for (var i = 0; i < attachments.length; i++) {
                        var attachment = attachments[i];
                        if (attachment === undefined || attachment === null) {
                            continue;
                        }
                        this.restDeleteAttachment(attachment.TempURI, true);
                    }
                }
            };
            /**
             * HTTP Delete for one Attachment by reference path
             * 
             * @param referenceToPath
             * @param silently
             * @returns {Boolean}
             */
            DocumentationManager.prototype.restRemoveAttachment = function (referenceToPath, silently) {

                var uri = this.getAttachmentURI(referenceToPath);
                if (uri === null) {
                    return false;
                }
                this.restDeleteAttachment(uri, silently);
            };
            /**
             * Actual HTTP Delete
             * 
             * @param uri
             * @param silently
             */
            DocumentationManager.prototype.restDeleteAttachment = function (uri, silently) {
                var resource = new Resource(uri);
                resource._delete({
                    success: {
                        scope: this,
                        callback: function () {

                        }
                    },
                    error: {
                        scope: this,
                        callback: function (statusObj) {
                            require(
                                    ['i18n!localization/nls/Notification'],
                                    function (Notification) {
                                        var errorMsg = Notification["error.code.file.delete"];
                                        $("body > section.notifications").trigger("europass:message:show", ["error", errorMsg]);
                                    }
                            );
                        }
                    }
                });
            };
            /**
             * Update the model by removing all attachments
             */
            DocumentationManager.prototype.updateAllRemovedAttachments = function () {

                var attachments = this.model.get(this.model.ATTACHMENT_SECTION);

                if (attachments !== undefined && $.isArray(attachments)) {
                    for (var i = 0; i < attachments.length; i++) {
                        var attachment = attachments[i];
                        if (attachment === undefined || attachment === null) {
                            continue;
                        }
                        // remove the links to this attachment from other sections
                        var refs = this.findReferencesTo(attachment.Id);
                        for (var idx = 0; idx < refs.length; idx++) {
                            this.model.reset(refs[ idx ], true);
                        }
                    }
                    var empty = {};
                    empty[ this.model.ATTACHMENT_SECTION ] = null;
                    this.model.set(empty);
                }
            };
            /**
             * Update the model accordingly to provide for the removed attachment
             */
            DocumentationManager.prototype.updateRemovedAttachment = function (matchedAttachment, silently) {

                var attachId = matchedAttachment.attachment.Id;
                var sectionIdx = this.model.ATTACHMENT_SECTION + "[" + matchedAttachment.index + "]";

//			console.log( "Delete Attachment at (" + sectionIdx +") named ("+matchedAttachment.attachment.Name+")"  );
                // First, delete Attcachment
                this.model.remove(sectionIdx, {silently: true});

                // Also remove the links to this attachment from other sections
                var refs = this.findReferencesTo(attachId);

                for (var i = 0; i < refs.length; i++) {
                    // Clear printing preferences is handled here..
//				console.log("Clear Reference To: " + refs[i] );
                    this.model.reset(refs[i], true);
                }

                if (silently !== true) {
                    // finally call an event which will trigger the re-render of the entire compose
                    this.model.trigger("linked:attachment:changed");
                }
                // Stop the wait indicator
                $("body").trigger("europass:waiting:indicator:hide");
            };
            /**
             * Delete an attachment from the model based on the
             * ReferenceToPath
             * 
             */
            DocumentationManager.prototype.removeAttachment = function (referenceToPath, silently) {
                var matchedAttachment = this.findAttachment(referenceToPath);
                if (matchedAttachment === null) {
                    return false;
                }

                // Delete service
                var uri = matchedAttachment.attachment.TempURI;
                if (uri === undefined || uri === null || uri === "") {
                    return false;
                }

                var resource = new Resource(uri);
                resource._delete({
                    success: {
                        scope: this,
                        callback: function () {
                            this.updateRemovedAttachment(matchedAttachment);
                            return true;
                        }
                    },
                    error: {
                        scope: this,
                        callback: function (statusObj) {
                            this.updateRemovedAttachment(matchedAttachment);
                            return false;
                        }
                    }
                });
            };
            return DocumentationManager;
        }
);