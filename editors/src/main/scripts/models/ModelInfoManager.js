define(
        ['jquery', 'underscore', 'Utils']
        , function ($, _, Utils) {
            var ModelInfoManager = function (model) {
                this.model = model;
            };

            /**
             * Check if the model has photo
             * 
             * @return boolean
             */
            ModelInfoManager.prototype.hasPhoto = function () {
                var photo = this.model.get(this.model.PHOTO);
                return (photo !== undefined && photo !== null
                        && photo !== "" && $.isPlainObject(photo));
            };

            /**
             * Check if the model has signature
             * 
             * @return boolean
             */
            ModelInfoManager.prototype.hasSignature = function () {
                var signature = this.model.get(this.model.SIGNATURE);
                return (signature !== undefined && signature !== null
                        && signature !== "" && $.isPlainObject(signature));
            };

            /**
             * Check if Model has Attachments
             * 
             * @return boolean
             */
            ModelInfoManager.prototype.hasAttachments = function () {
                var attachments = this.model.get(this.model.ATTACHMENT_SECTION);

                return (attachments !== undefined
                        && attachments !== null
                        && $.isArray(attachments) && attachments.length > 0);
            };
            /**
             * Check if Model has Content and/or Attachments
             * 
             * @return String
             */
            ModelInfoManager.prototype.checkDocumentContent = function () {

                var espInfoOnly = this.hasEspInfoOnly();
                var hasAttachments = this.hasAttachments();

                // If it has attachments and the CV is not empty
                // (except for PersonName and ReferenceTo)
                // then it is ESP
                if (hasAttachments && espInfoOnly) {
                    return "ESP";
                } else if (hasAttachments && !espInfoOnly) {
                    return "ECV_ESP";
                }
                return "ECV";
            };
            /**
             * Read the document type from the Model
             * @returns
             */
            ModelInfoManager.prototype.readDocumentType = function () {
                var type = this.model.get(this.model.DOCUMENT_TYPE);
                if (type === undefined || type === null || type === "") {
                    return "ECV";
                }
                return type;
            };
            /**
             * Decides if the LearnerInfo section identified by the sectionKey is empty after having removed the Printing Preference related information.
             */
            ModelInfoManager.prototype.isSectionEmpty = function (sectionKey) {
                var section = $.extend({}, this.model.get("SkillsPassport.LearnerInfo." + sectionKey));
                delete section.show;
                delete section.order;
                delete section.format;
                delete section.current_pref_item;
                var isEmptySection = Utils.isEmptyObject(section) ? true :
                        (Object.keys(section).length === 1 && ("ReferenceTo" in section) && Utils.isEmptyObject(section["ReferenceTo"]) ? true
                                : false);
                return isEmptySection;
            };

            /**
             * Decides if the LearnerInfo section identified by the sectionKey is empty after having removed the Printing Preference related information.
             */
            ModelInfoManager.prototype.isCLSectionEmpty = function (sectionPath) {
                var section = $.extend({}, this.model.get(sectionPath));
                delete section.show;
                delete section.order;
                delete section.format;
                delete section.current_pref_item;
                var isEmptySection = Utils.isEmptyObject(section) ? true :
                        (Object.keys(section).length === 1 && ("ReferenceTo" in section) && Utils.isEmptyObject(section["ReferenceTo"]) ? true
                                : false);
                return isEmptySection;
            };
            /**
             * Checks whether identification is empty
             */
            ModelInfoManager.prototype.isIdentificationEmpty = function () {
                var res =
                        this.isSectionEmpty("Identification.PersonName")
                        && this.isSectionEmpty("Identification.ContactInfo")
                        && this.isSectionEmpty("Identification.Demographics");
                return res;
            };
            /**
             * Checks whether identification does not have
             * ContactInfo and Demographics
             */
            ModelInfoManager.prototype.espIdentification = function () {
                var res =
                        this.isSectionEmpty("Identification.ContactInfo")
                        && this.isSectionEmpty("Identification.Demographics");
                return res;
            };
            /**
             * Checks whether ESP-specific info is included
             * (PersonName, ReferenceTo)
             */
            ModelInfoManager.prototype.hasEspInfoOnly = function () {
                var res =
                        this.espIdentification()
                        && !this.isSectionEmpty("ReferenceTo")
                        && this.isSectionEmpty("Headline")
                        && this.isSectionEmpty("WorkExperience")
                        && this.isSectionEmpty("Education")
                        && this.isSectionEmpty("Skills")
                        && this.isSectionEmpty("Achievement");
                return res;
            };
            /**
             * Checks whether photo is empty
             */
            ModelInfoManager.prototype.isPhotoEmpty = function () {
                var res =
                        this.isSectionEmpty("Identification.Photo");
                return res;
            };

            ModelInfoManager.prototype.isSignatureEmpty = function () {
                var res =
                        this.isSectionEmpty("Identification.Signature");
                return res;
            };
            /**
             * Functions used for checking if the Europass Documents are empty
             * (Usage example in EnclosedFormView)
             */

            // Checks if CV is Empty
            ModelInfoManager.prototype.isCVEmpty = function () {
                var emptySkills = this.isSectionSkillsEmpty();

                var Identification = !_.isUndefined(this.model.attributes) ?
                        !_.isUndefined(this.model.attributes.SkillsPassport) ?
                        !_.isUndefined(this.model.attributes.SkillsPassport.LearnerInfo) ?
                        this.model.attributes.SkillsPassport.LearnerInfo.Identification : undefined
                        : undefined
                        : undefined;

                var photo = Identification !== undefined ? Identification.Photo : undefined;

                var isIdentificationEmpty = this.isIdentificationEmpty() && Utils.isEmptyObject(photo !== undefined ? photo : null) || Identification === undefined;

                var isCVOnlyEmpty = emptySkills
                        && this.isSectionEmpty("Headline")
                        && this.isSectionEmpty("WorkExperience")
                        && this.isSectionEmpty("Education")
                        && this.isSectionEmpty("Achievement")
                        && isIdentificationEmpty
                        && !this.hasPhoto();

                var isCVwithESPEmpty = this.hasEspInfoOnly() && isIdentificationEmpty;

                return isCVwithESPEmpty || isCVOnlyEmpty;
            };

            ModelInfoManager.prototype.isSectionSkillsEmpty = function () {
                return		this.isSectionEmpty("Skills.Linguistic.MotherTongue")
                        && this.isSectionEmpty("Skills.Linguistic.ForeignLanguage")
                        && this.isSectionEmpty("Skills.Communication")
                        && this.isSectionEmpty("Skills.Organisational")
                        && this.isSectionEmpty("Skills.JobRelated")
                        && this.isSectionEmpty("Skills.Computer")
                        && this.isSectionEmpty("Skills.Driving")
                        && this.isSectionEmpty("Skills.Other");
            };

            // Checks if LP is Empty
            ModelInfoManager.prototype.isLPEmpty = function () {

                var foreignLanguages = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage");

                var emptyCertificate = true;

                var emptyExperience = true;

                if (foreignLanguages !== undefined && $.isArray(foreignLanguages)) {
                    for (var i = 0; i < foreignLanguages.length; i++) {

                        if (_.isUndefined(foreignLanguages[i]) || foreignLanguages[i] === null)
                            break;

                        for (var c in foreignLanguages[i].Certificate) {

                            // pgia: fix for EWA-1365 (side effects EWA-1375, EWA-1381)
                            var certificate = foreignLanguages[i].Certificate[c];
                            if (_.isUndefined(certificate) || certificate === null)
                                continue;

                            if (!Utils.isEmptyObject(certificate.AwardingBody) || !Utils.isEmptyObject(certificate.Date) || !Utils.isEmptyObject(certificate.Level)) {
                                emptyCertificate = false;
                                break;
                            }
                        }
                        for (var e in foreignLanguages[i].Experience) {
                            // pgia: fix for EWA-1365 (side effects EWA-1375, EWA-1381)
                            var experience = foreignLanguages[i].Experience[e];
                            if (_.isUndefined(experience) || experience === null)
                                continue;

                            if (!Utils.isEmptyObject(experience)) {
                                emptyExperience = false;
                                break;
                            }
                        }
                    }
                }

                return emptyCertificate && emptyExperience;
            };

            // Checks if CL is Empty
            ModelInfoManager.prototype.isCLEmpty = function () {

                var section = $.extend({}, this.model.get("SkillsPassport.CoverLetter.Letter"));

                //Properly delete null values in Cover letter section by calling removeEmptyKeysInObject
                var whitelist = ["Localisation", "SubjectLine", "OpeningSalutation", "Body", "ClosingSalutation"];
                if (!_.isUndefined(section) && !_.isNull(section)) {
                    section = Utils.removeEmptyKeysInObject(section, whitelist);
                }

                var addressee = $.extend({}, this.model.get("SkillsPassport.CoverLetter.Addressee"));
                var documentation = $.extend({}, this.model.get("SkillsPassport.CoverLetter.Documentation"));
                delete section.show;
                delete section.order;
                delete section.format;
                delete section.current_pref_item;
                return Utils.isEmptyObject(section) && Utils.isEmptyObject(addressee) && Utils.isEmptyObject(documentation) && this.isSignatureEmpty();
            };

            // Checks if ESP is Empty
            ModelInfoManager.prototype.isESPEmpty = function () {
                var section = $.extend({}, this.model.get("SkillsPassport.Attachment"));
                return Utils.isEmptyObject(section);
            };

            ModelInfoManager.prototype.isEmpty = function (doc) {
                if (_.isEmpty(doc))
                    return true;

                switch (doc) {
                    case "ESP":
                    {
                        return this.isESPEmpty();
                    }
                    case "ECL":
                    {
                        return this.isCLEmpty();
                    }
                    case "ELP":
                    {
                        return this.isLPEmpty();
                    }
                    default:
                    {
                        return this.isCVEmpty();
                    }
                }
            };

            ModelInfoManager.prototype.isCompletelyEmpty = function (doc) {
                return this.isCVEmpty() && this.isLPEmpty() && this.isCLEmpty() && this.isESPEmpty();
            };

            return ModelInfoManager;
        }
);