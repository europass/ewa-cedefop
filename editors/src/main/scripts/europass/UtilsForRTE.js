define('UtilsForRTE',
        [
            'require',
            'jquery',
            'underscore',
            'jqueryui'
        ],
        function (require, $, _, jqueryui) {
            var UtilsForRTE = {};

            UtilsForRTE.XML_CHAR_MAP = {
                '>': '&gt;',
                '&': '&amp;',
                '"': '&quot;',
                "'": '&apos;',
                "%": '&#37;'
            };

            /**
             * used by TextareaView, but not actually called?
             */
            UtilsForRTE.escapeXml = function (s) {
                if (s === undefined || s === null || s === "") {
                    return s;
                }

                return s.replace(/[<>]/g, function (ch) {
                    return UtilsForRTE.XML_CHAR_MAP[ch];
                });
            };

            /**
             * used by TranslationManager, TextareaView.
             */
            UtilsForRTE.stripHtml = function (s) {
                if (s === undefined || s === null || s === "") {
                    return s;
                }

                var r = s.replace(/<\/p>|<br( )?[\/\\]?>/gi, "\n");
                return  r.replace(/<(?:.|\n)*?>/gm, '');
            };

            /**
             * used by RichTextEditorView.
             */
            UtilsForRTE.txtareaValueIsEmpty = function (value) {
                //vpol wasRegExp(/^((<br[\/\\]?>)*|(<p>(<\/p>)?))$/);
                var empty_regexp = new RegExp(/^((<br[\/\\]?>)*|(<p>(<br[\/\\]?>)?(<\/p>)?))$/);
                return (value === "" || $.trim(value) === "" || empty_regexp.test(value));
            };

            UtilsForRTE.htmlifyTextarea = function (textarea, content) {
                if (content === undefined || content === null)
                    content = $.trim(textarea.val());

                var txt = content;
                txt = txt.replace(/\t/g, "<spanclass=\"tab\"></span>");
                txt = txt.replace(/\n/g, "<br/>");
                // The character class \s does not just contain the space character but also other Unicode white space characters.
                txt = txt.replace(/\s/g, "<span class=\"space\"></span>");
                //the following line is necessary to re-instate the tab span
                txt = txt.replace(/<spanclass=(")?tab(")?><\/span>/gi, "<span class=\"tab\"><\/span>");

                //Wrap in root, to make sure that ODT will display it properly
                //If left unwrapped, orphan texts will be wrapped into <text:p>
                if (txt !== "" && $.trim(txt) !== "") {
                    txt = "<p class=\"root\">" + txt + "</p>";
                }
                textarea.val(txt);
            };

            UtilsForRTE.textifyTextarea = function (textarea, content) {
                if (content === undefined || content === null)
                    content = $.trim(textarea.val());

                var txt = content;
                //Unwrap root when showing within the textarea
                var match = content.match(/^<p class="root">(.*)<\/p>/i);
                if (match !== null && match[1] !== undefined) {
                    txt = match[1];
                }
                txt = txt.replace(/<br( )?[\/\\]?>/gi, "\n");
                txt = txt.replace(/<span class=(")?tab(")?><\/span>/gi, "\t");
                txt = txt.replace(/&nbsp;/gi, " ");
                txt = txt.replace(/<span class=(")?space(")?><\/span>/gi, " ");
                textarea.val(txt);
            };

            return UtilsForRTE;
        }
);