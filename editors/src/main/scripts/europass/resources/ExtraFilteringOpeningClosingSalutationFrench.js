define('europass/resources/ExtraFilteringOpeningClosingSalutationFrench',
        [
            'jquery',
            'europass/maps/ClosingSalutationMap',
            'europass/maps/OpeningSalutationMap'
        ],
        function ($, ClosingSalutationMap, OpeningSalutationMap) {
            var ExtraFilteringOpeningClosingSalutationFrench = {};

            // for fr locale we need to do some extra filtering on closing salutation map based on OpeningSalutation input ..
            ExtraFilteringOpeningClosingSalutationFrench.filteringClosingSalutationMap = function (model) {

                if (ewaLocale !== 'fr')
                    return ClosingSalutationMap;

                // cloning original Map.
                var filteredClosingSalutationMap = $.extend(true, {}, ClosingSalutationMap);

                // Adding all keys that need to be removed from ClosingSalutationMap..
                var mapRemoveClosingSalut = {};
                mapRemoveClosingSalut["opening-salut-5-impersonal"] =
                        ["closing-salut-1", "closing-salut-5", "closing-salut-9",
                            "closing-salut-2", "closing-salut-6", "closing-salut-10",
                            "closing-salut-3", "closing-salut-7", "closing-salut-11"];
                mapRemoveClosingSalut["opening-salut-6-impersonal"] =
                        ["closing-salut-4", "closing-salut-8", "closing-salut-12",
                            "closing-salut-2", "closing-salut-6", "closing-salut-10",
                            "closing-salut-3", "closing-salut-7", "closing-salut-11"];
                mapRemoveClosingSalut["opening-salut-7-impersonal"] =
                        ["closing-salut-4", "closing-salut-8", "closing-salut-12",
                            "closing-salut-1", "closing-salut-5", "closing-salut-9",
                            "closing-salut-3", "closing-salut-7", "closing-salut-11"];
                mapRemoveClosingSalut["opening-salut-12-impersonal"] =
                        ["closing-salut-4", "closing-salut-8", "closing-salut-12",
                            "closing-salut-1", "closing-salut-5", "closing-salut-9",
                            "closing-salut-2", "closing-salut-6", "closing-salut-10"];

                var openingSalutationCode = model.get("SkillsPassport.CoverLetter.Letter.OpeningSalutation");
                if (openingSalutationCode != null &&
                        typeof openingSalutationCode !== "undefined" &&
                        typeof openingSalutationCode.Salutation !== "undefined" &&
                        typeof openingSalutationCode.Salutation.Code !== "undefined") {

                    var arrayToRemove = mapRemoveClosingSalut[openingSalutationCode.Salutation.Code];
                    if (typeof arrayToRemove === "undefined") {
                        arrayToRemove = [];
                    }
                    var csmKeys = filteredClosingSalutationMap.keys;
                    for (var j = 0; j < arrayToRemove.length; j++) {
                        if (csmKeys.indexOf(arrayToRemove[j]) > -1) {
                            var indexRemove = csmKeys.indexOf(arrayToRemove[j]);
                            filteredClosingSalutationMap.arrayObj.splice(indexRemove, 1);
                            filteredClosingSalutationMap.keys.splice(indexRemove, 1);
                            filteredClosingSalutationMap.values.splice(indexRemove, 1);
                        }
                    }
                }

                return filteredClosingSalutationMap;
            };

            // vice-versa for fr locale we need to do some extra filtering on OpeningSalutationMap based on ClosingSalutation input ..
            ExtraFilteringOpeningClosingSalutationFrench.filteringOpeningSalutationMap = function (model) {

                if (ewaLocale !== 'fr')
                    return OpeningSalutationMap;

                // cloning original Map.
                var filteredOpeningSalutationMap = $.extend(true, {}, OpeningSalutationMap);

                // Adding all keys that need to be removed from OpeningSalutationMap..
                var mapRemoveOpeningSalut = {};
                mapRemoveOpeningSalut["closing-salut-1"] = ["opening-salut-5-impersonal", "opening-salut-7-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-2"] = ["opening-salut-5-impersonal", "opening-salut-6-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-3"] = ["opening-salut-5-impersonal", "opening-salut-6-impersonal", "opening-salut-7-impersonal"];
                mapRemoveOpeningSalut["closing-salut-4"] = ["opening-salut-6-impersonal", "opening-salut-7-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-5"] = ["opening-salut-5-impersonal", "opening-salut-7-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-6"] = ["opening-salut-5-impersonal", "opening-salut-6-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-7"] = ["opening-salut-5-impersonal", "opening-salut-6-impersonal", "opening-salut-7-impersonal"];
                mapRemoveOpeningSalut["closing-salut-8"] = ["opening-salut-6-impersonal", "opening-salut-7-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-9"] = ["opening-salut-5-impersonal", "opening-salut-7-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-10"] = ["opening-salut-5-impersonal", "opening-salut-6-impersonal", "opening-salut-12-impersonal"];
                mapRemoveOpeningSalut["closing-salut-11"] = ["opening-salut-5-impersonal", "opening-salut-6-impersonal", "opening-salut-7-impersonal"];
                mapRemoveOpeningSalut["closing-salut-12"] = ["opening-salut-6-impersonal", "opening-salut-7-impersonal", "opening-salut-12-impersonal"];

                var closingSalutationCode = model.get("SkillsPassport.CoverLetter.Letter.ClosingSalutation");
                if (closingSalutationCode != null &&
                        typeof closingSalutationCode !== "undefined" &&
                        typeof closingSalutationCode.Code !== "undefined") {

                    var arrayToRemove = mapRemoveOpeningSalut[closingSalutationCode.Code];
                    if (typeof arrayToRemove === "undefined") {
                        arrayToRemove = [];
                    }
                    var csmKeys = filteredOpeningSalutationMap.keys;
                    for (var j = 0; j < arrayToRemove.length; j++) {
                        if (csmKeys.indexOf(arrayToRemove[j]) > -1) {
                            var indexRemove = csmKeys.indexOf(arrayToRemove[j]);
                            filteredOpeningSalutationMap.arrayObj.splice(indexRemove, 1);
                            filteredOpeningSalutationMap.keys.splice(indexRemove, 1);
                            filteredOpeningSalutationMap.values.splice(indexRemove, 1);
                        }
                    }
                }

                return filteredOpeningSalutationMap;
            };

            return ExtraFilteringOpeningClosingSalutationFrench;
        }
);