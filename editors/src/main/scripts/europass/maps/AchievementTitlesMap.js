define(
        [
            'jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/AchievementType',
            'europass/maps/AchievementTitlesMap'
        ],
        function ($, KeyValueMap, AchievementTitles, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(AchievementTitles, function (i) {
                    Self.put(AchievementTitles[i], i);
                });

                Self.sortByOrder('asc');
            }
            return Self;
        }
);