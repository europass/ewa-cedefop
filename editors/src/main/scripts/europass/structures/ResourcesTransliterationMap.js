define(
        ['underscore', 'jquery', 'latinise', 'europass/structures/KeyValueMap'],
        function (_, $, latinise, KeyValueMap) {

            var locale = window.sessionLocale;

            var localizedSubstitutions = new Array();

            function ResourcesTransliterationMap() {

                localizedSubstitutions["el"] =
                        [
                            {"pattern": "Ά", "wildchar": "Α"},
                            {"pattern": "Έ", "wildchar": "Ε"},
                            {"pattern": "Ή", "wildchar": "Η"},
                            {"pattern": "Ί|Ϊ", "wildchar": "Ι"},
                            {"pattern": "Ό", "wildchar": "Ο"},
                            {"pattern": "Ύ|Ϋ", "wildchar": "Υ"},
                            {"pattern": "Ώ", "wildchar": "Ω"},
                            {"pattern": "ά", "wildchar": "α"},
                            {"pattern": "έ", "wildchar": "ε"},
                            {"pattern": "ή", "wildchar": "η"},
                            {"pattern": "ί|ϊ|ΐ", "wildchar": "ι"},
                            {"pattern": "ό", "wildchar": "ο"},
                            {"pattern": "ύ|ϋ|ΰ", "wildchar": "υ"},
                            {"pattern": "ώ", "wildchar": "ω"},
                            {"pattern": "A", "wildchar": "Ωσ"},
                            {"pattern": "B", "wildchar": "Ωτ"},
                            {"pattern": "C", "wildchar": "Ωυ"},
                            {"pattern": "D", "wildchar": "Ωφ"},
                            {"pattern": "F", "wildchar": "Ωχ"},
                            {"pattern": "M", "wildchar": "Ωψ"},
                            {"pattern": "P", "wildchar": "Ωω"},
                            {"pattern": "S", "wildchar": "Ωωω"},
                            {"pattern": "V", "wildchar": "Ωωωω"}
                        ];
            }
            ;

            ResourcesTransliterationMap.prototype.sortMap = function (keyMap, order) {

                var arrayObjects = keyMap.arrayObj;

                var arrayObjectsReplaced = [];
                $.each(arrayObjects, function () {

                    var currentValue = this.value;

                    if (locale === 'el') {
                        var substitutionsMap = localizedSubstitutions['el'];
//				if(substitutionsMap !== undefined &&  substitutionsMap.length > 0 ){

                        for (var j = 0; j < substitutionsMap.length; j++) {
                            var elem = substitutionsMap[j];
                            currentValue = currentValue.replace(new RegExp(elem.pattern, 'g'), elem.wildchar);
                        }
                    } else {
                        if (locale === 'cs')
                            currentValue = currentValue.replace(new RegExp("Ch|CH|ch", 'g'), "Hzzzzz");
                        else if (locale === 'sr-cyr' || locale === 'mk') {
                            currentValue = currentValue.replace(new RegExp("Ј", 'g'), "Ишшшшшшшш");
                            currentValue = currentValue.replace(new RegExp("Џ", 'g'), "Чшшшшшшшш");
                        }

                        currentValue = currentValue.latinise();
                    }
                    arrayObjectsReplaced.push({"key": this.key, "value": currentValue});
                });
                arrayObjects = arrayObjectsReplaced;

                // Sort the array objects copy
                var arrayObjectsSorted = _.sortBy(arrayObjects, function (obj) {
                    return obj.value;
                });

                if (!_.isUndefined(order) && !_.isNull(order) && order === "desc")
                    arrayObjectsSorted.reverse();

                // Construct the keys sequence according to the actual ordering and store the sorted values
                var keysArraySorted = [];
                var valuesArraySorted = [];
                var finalArrayObjects = [];
                $.each(arrayObjectsSorted, function () {

                    keysArraySorted.push(this.key);

                    // We need the orginal map value to store in the sorted values array
                    var originalValue = keyMap.get(this.key);
                    valuesArraySorted.push(originalValue);

                    finalArrayObjects.push({"key": this.key, "value": originalValue});
                });

                //Reflect the changes in the original map
                keyMap.keys = keysArraySorted;
                keyMap.values = valuesArraySorted;
                keyMap.arrayObj = finalArrayObjects;

            };
            return ResourcesTransliterationMap;
        });