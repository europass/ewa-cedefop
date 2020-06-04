define(
        [
            'jquery'
                    , 'i18n!localization/nls/ClosingSalutation'
                    , 'europass/resources/FilteredClosingSalutation'],
        function ($, ClosingSalutationLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = {};
                $.each(ClosingSalutationLabels, function (i) {
                    var label = ClosingSalutationLabels[i];
                    if (label !== "")
                        Self[i] = ClosingSalutationLabels[i];
                });
            }
            return Self;
        }
);