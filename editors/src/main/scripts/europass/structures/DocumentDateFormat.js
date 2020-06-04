/**
 * Maintains a list of preferences per document
 */
define(
        ['jquery',
            'underscore',
            'Utils',
            'europass/GlobalDocumentInstance',
            'europass/structures/ExperienceDateFormat'],
        function ($, _, Utils, GlobalDocument, ExperienceDateFormat) {

            var DocumentDateFormat = function () {
                this.init();
            };

            DocumentDateFormat.prototype.init = function () {
                this.docs = {};
                this.docs[ "ECV" ] = new ExperienceDateFormat("ECV");
                this.docs[ "ELP" ] = new ExperienceDateFormat("ELP");
                this.docs[ "ECL" ] = new ExperienceDateFormat("ECL");
            };

            DocumentDateFormat.prototype.get = function (document) {
                return this.docs[ document ];
            };

            /**
             * 
             * @param config
             * {
             * 	f    : function(),
             *  args : array 
             * }
             * @param document one of "ECV", "ELP", "CL" or "ALL" 
             * - when none specified it uses the current document (GlobalDocument.get())
             * 
             * Important Note: There are functions that return something, for which it does not make sense to use ALL;
             */
            DocumentDateFormat.prototype.applyFunction = function (config, document) {
                if (Utils.isEmptyObject(config)) {
                    return false;
                }

                var functionName = config.f;
                if (functionName === null || functionName === "") {
                    return false;
                }

                var args = config.args;
                if ($.isArray(args) === false) {
                    args = [];
                }

                var isEmpty = _.isEmpty(this.docs);
                if (isEmpty)
                    return;

                var global = GlobalDocument.get();
                var docName = _.isString(document) ? document : (!_.isEmpty(global) ? global.prefDocument : null);

                if (!_.isString(docName)) {
                    return;
                }
                var docObj = this.docs[ docName ];

                if (docObj === undefined || docObj === null)
                    return;

                var _function = docObj[ functionName ];
                if ($.isFunction(_function) === false) {
                    return;
                }

                return _function.apply(docObj, args);
            };
            return DocumentDateFormat;
        }
);