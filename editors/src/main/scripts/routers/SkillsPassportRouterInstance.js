define(
        [
            'routers/SkillsPassportRouter',
            'models/NavigationRoutesInstance',
            'routers/SkillsPassportRouterInstance'
        ],
        function (SkillsPassportRouter, NavigationRoutesInstance, Self) {
            if (Self === undefined || Self === null) {
                var Self = new SkillsPassportRouter({
                    model: NavigationRoutesInstance
                });
            }
            return Self;
        }
);