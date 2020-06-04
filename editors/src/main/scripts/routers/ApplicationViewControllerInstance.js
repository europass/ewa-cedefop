define(
        [
            'routers/ApplicationViewController',
            'routers/ApplicationViewControllerInstance'
        ],
        function (ApplicationViewController, Self) {
            if (Self === undefined || Self === null) {
                var Self = new ApplicationViewController();
            }
            return Self;
        }
);