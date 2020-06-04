define(
        [
            'jquery',
            'underscore',
            'views/forms/FormView',
            'hbs!templates/forms/elp/elpCertificate',
            'europass/structures/PreferencesSchema'//'Utils',
        ],
        function ($, _, FormView, HtmlTemplate, PreferencesSchema) {

            var ElpLanguageCertificateFormView = FormView.extend({

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "change.fs": "onChange"
                }, FormView.prototype.events)

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    //ENABLE THE AUTOCOMPLETE AND MULTIFIELD FUNCTIONALITIES
                    /**
                     * Return the title of this section
                     */
                    var header = this.$el.find("header > legend > .subsection-title");
                    var title = PreferencesSchema.getSectionLabel(this.section, this.model.attributes.SkillsPassport, this.model.get(this.section), null, true);
                    header.html(title);

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this, []);
                }
                , onChange: function (event) {
                    this.formatSelected($(event.target).siblings(".trigger"));
                }
            });

            return ElpLanguageCertificateFormView;
        }
);