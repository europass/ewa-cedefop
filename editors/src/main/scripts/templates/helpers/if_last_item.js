/**
 *  If last item in the list
 *  context: is the length of the list
 *  options.hash.compare: is the index of the item, this will be increased by one, because this would be an array index (starting from 0) 
 *  example {{#if_eq ../../SkillsPassport.LearnerInfo.WorkExperience.length compare=index}}
 *  used in the workexperiencelist.hbs, to show the move-to-top sorting button
 */
define(['handlebars'], function (Handlebars) {
    function if_last_item(context, options) {
        var c = "";
        var compare = null;

        if (context !== undefined)
            c = context.toString();

        if (context !== undefined)
            c = context.toString();

        if (options !== undefined && options.hash !== undefined && $.isPlainObject(options.hash)) {
            if (options.hash.compare !== undefined && options.hash.compare !== null && options.hash.compare !== "") {
                compare = parseInt(options.hash.compare) + 1;
            } else {
                return options.inverse(this);
            }
        } else {
            return options.inverse(this);
        }


        if (c == compare) {
            return options.fn(this);
        }

        return options.inverse(this);
    }
    Handlebars.registerHelper('if_last_item', if_last_item);
    return if_last_item;
});