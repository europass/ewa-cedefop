define(
        [
            'jquery',
            'xdate',
            'models/SkillsPassport',
            'models/SkillsPassportInstance',
            'europass/http/SessionManagerInstance',
            'europass/http/WindowConfigInstance'
        ],
        function ($, XDate, SkillsPassport, Instance, SessionManager, Config) {

            if (Instance === undefined || Instance === null) {
                Instance = new SkillsPassport();

                var prevModel = null;

                try {
                    prevModel = window.localStorage.getItem("europass.ewa.skillspassport.v3");
                    //console.log("get from local storage after manual reload" +  JSON.stringify(prevModel) );
                } catch (err) {
                }

                if (prevModel !== undefined && prevModel !== null) {
//				console.log("Populate from prev model and consider UI vs Model locale...");
                    Instance.populateModel(prevModel, true);
                }
            }
            return Instance;
        }
);