define(
        [
            'models/SkillsPassportInstance',
            'views/attachment/AttachmentManagerView',
            'views/attachment/AttachmentManagerInstance'
        ],
        function (SkillsPassportInstance, AttachmentManagerView, Instance) {

            if (Instance === undefined || Instance === null) {
                Instance = new AttachmentManagerView({
                    el: "body",
                    model: SkillsPassportInstance
                });
            }
            return Instance;
        }
);