define(
        [
            'models/NavigationRoutes',
            'models/NavigationRoutesInstance'
        ],
        function (NavigationRoutes, Instance) {
            if (Instance === undefined || Instance === null) {
                Instance = new NavigationRoutes();
            }
            return Instance;
        }
);