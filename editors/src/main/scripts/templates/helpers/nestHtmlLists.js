define(
    [
        'jquery',
        'handlebars',
        'domPurify'
    ], 
    function ($, Handlebars, DOMPurify) {
        function nestHtmlLists (html, options) {
            if (html === undefined || html === null) {
                return html;
            }
            try {
                var sanitizedHtml = DOMPurify.sanitize(html);
                var el = $("<div>" + sanitizedHtml + "</div>");

                el.find("li > ul, li > ol").each( function( i, ls ){
                    var li = $(ls).closest("li");
                    li.addClass("nesting");
                });
                return new Handlebars.SafeString( el.html() );
            } catch( err) {
                return html;
            }
        }
        Handlebars.registerHelper( 'nestHtmlLists', nestHtmlLists );
        return nestHtmlLists;
    }
);