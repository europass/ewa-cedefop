define(
        ['jquery'
                    , 'i18n!localization/nls/OpeningSalutation'
                    , 'europass/resources/FilteredOpeningSalutation'],
        function ($, SalutationLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = {};
                $.each(SalutationLabels, function (i) {
                    if (SalutationLabels[i] !== "")
                        Self [i] = SalutationLabels[i];
                });
            }
            return Self;
        }
);