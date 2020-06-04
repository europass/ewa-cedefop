define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/IctProblemSolvingLevel',
            'europass/maps/IctProblemSolvingLevelMap'],
        function ($, KeyValueMap, IctProblemSolvingLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(IctProblemSolvingLevel, function (i) {
                    Self.put(IctProblemSolvingLevel[i], i);
                });
            }
            return Self;
        }
);