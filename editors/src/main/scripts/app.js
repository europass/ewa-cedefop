define(
        [
            'domReady',
            'fastclick',
            'europass/europass'
        ],
        function (domReady, fastclick, Europass) {
            var init = function () {
                domReady(function () {
                    fastclick.attach(document.body);
                    Europass.initialize();
                });
            };
            return {
                init: init
            };
        }
);