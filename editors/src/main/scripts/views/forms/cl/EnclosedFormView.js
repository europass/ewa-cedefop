define(
        [
            'jquery',
            'views/forms/FormView',
            'europass/GlobalDocumentInstance',
//		 'europass/maps/EnclosedLabelMap',
//		'views/interaction/Select2AutocompleteView'
        ],
        function ($, FormView, GlobalDocument) {//EnclosedLabelMap,Select2AutocompleteView

            var EnclosedFormView = FormView.extend({
                htmlTemplate: "forms/cl/enclosed"

                , enableFunctionalities: function () {

                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    //ENABLE THE AUTOCOMPLETE AND MULTIFIELD FUNCTIONALITIES
//			var frm = this.frm;;
//			var that = this;
//			frm.find("div.composite.select2autocomplete[name\*=\".Heading\"]").each ( function( idx, el){
//				var enclosedLabel = new Select2AutocompleteView({
//					el : $(el),
//					minLength: 1,
//					topN: 10,
//					map: EnclosedLabelMap
//				});
//				that.addToViewsIndex( enclosedLabel );
//			});

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }
                /**
                 * Adjust the names of the checked Europass Documents and delegate to parent formToModel
                 * @Override
                 */
                , formToModel: function (frm) {
                    frm.find(":checkbox.europass-document:checked").each(function (idx, el) {
                        var ch = $(el);
                        var name = ch.attr("name");
                        var newName = name.replace(/^(.*InterDocument\[)(\d+)(\]\.ref)$/, "$1" + idx + "$3");
                        ch.attr("name", newName);
                    });
                    return FormView.prototype.formToModel.apply(this, [frm]);
                }

                /**
                 * Adjust the names of the checked Europass Documents and delegate to parent adjustContext
                 * @Override
                 */
                , adjustContext: function (context, index, subsection) {

                    var totalDocuments = GlobalDocument.europassDocuments().length;

                    var documentsExcluded = 1;
                    var withoutDocuments = "ECL";

                    if (this.model.info().isCVEmpty()) {
                        documentsExcluded++;
                        withoutDocuments += " ECV";
                    }
                    if (this.model.info().isLPEmpty()) {
                        documentsExcluded++;
                        withoutDocuments += " ELP";
                    }
                    if (this.model.info().isESPEmpty()) {
                        documentsExcluded++;
                        withoutDocuments += " ESP";
                    }

                    context.Extra = withoutDocuments;
                    context.excludeAll = (totalDocuments === documentsExcluded ? true : false);

                    return FormView.prototype.adjustContext.apply(this, [context, index, subsection]);
                }
            });
            return EnclosedFormView;
        });