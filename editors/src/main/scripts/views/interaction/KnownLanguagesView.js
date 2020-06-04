define([
    'jquery',
//	'underscore',
    'backbone',
    'i18n!localization/nls/MotherLanguage',
    'i18n!localization/nls/Language'
],
        function ($, Backbone, MotherLanguage, ForeignLanguage) {

            var KnownLanguagesView = Backbone.View.extend({

                events: {
                    "europass:restore:taxonomy ": "restoreTaxonomy",
                    "click :button[name=\"compound_multifield_btn_remove\"]": "addToTaxonomy"
                }

                , MOTHER_TONGUE: "MotherTongue"
                , FOREIGN_LANGUAGE: "ForeignLanguage"

                , onClose: function () {
                    //perform any clean-up here
                }
                , initialize: function (options) {
                    this.motherlanguage = MotherLanguage;
                    this.foreignlanguage = ForeignLanguage;
                }

                , restoreTaxonomy: function (event, removedObj) {
                    var langToUpdate = this.langViewToUpdate(event);
                    var excludeLang = this.getExcludedLang(event);
                    var that = this;
                    $(this.el).find("div.composite.select2autocomplete[name\*=\"" + "." + langToUpdate + "\"]").each(function (idx, el) {
                        $(el).find(":input:not(button)[name$=\"Label\"]").each(function (idx, el) {
                            $(el).trigger("europass:remove:from:taxonomy", [excludeLang]);
                            if (that.allowAddInTaxonomy(langToUpdate, removedObj)) {
                                $(el).trigger("europass:add:to:taxonomy", removedObj);
                            }
                        });
                    });
                }

                , addToTaxonomy: function (event) {
                    var langToUpdate = this.langViewToUpdate(event);
                    var removedObj = this.getRemovedLang(event);
                    var that = this;
                    $(this.el).find("div.composite.select2autocomplete[name\*=\"" + "." + langToUpdate + "\"]").each(function (idx, el) {
                        $(el).find(":input:not(button)[name$=\"Label\"]").each(function (idx, el) {
                            if (that.allowAddInTaxonomy(langToUpdate, removedObj)) {
                                $(el).trigger("europass:add:to:taxonomy", removedObj);
                            }
                        });
                    });
                }
                , langViewToUpdate: function (event) {
                    var langFieldName = $(event.target).parents(".composite[data-index]").find(":input:not(:button)[name$=\"Label\"]").attr("name");
                    var langField = this.MOTHER_TONGUE;
                    if (langFieldName.indexOf(this.MOTHER_TONGUE) > -1) {
                        langField = this.FOREIGN_LANGUAGE;
                    }

                    return langField;
                }
                , getExcludedLang: function (event) {
                    var newLang = $(event.target).val();
                    var excludeLang = [];
                    excludeLang.push(newLang);
                    return excludeLang;
                }
                , getRemovedLang: function (event) {
                    var removedLangCode = $(event.target).parents(".composite[data-index]").find(":input:not(:button)[name$=\"Code\"]").attr("data-prev-code");
                    var removedLangLabel = $(event.target).parents(".composite[data-index]").find(":input:not(:button)[name$=\"Label\"]").attr("data-prev-label");
                    var removedLang = {id: removedLangCode, text: removedLangLabel};
                    return removedLang;
                }

                /**
                 * check if code of the removed language exists in original map
                 */
                , allowAddInTaxonomy: function (langToUpdate, removedLang) {
                    if ((langToUpdate === this.MOTHER_TONGUE && MotherLanguage[removedLang.id] !== undefined) || (langToUpdate === this.FOREIGN_LANGUAGE && ForeignLanguage[removedLang.id] !== undefined)) {
                        return true;
                    }
                    return false;
                }
            });

            return KnownLanguagesView;
        }
);