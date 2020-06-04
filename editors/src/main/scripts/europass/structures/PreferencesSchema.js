define(
        [
            'jquery',
            'europass/structures/PreferencesSchema',
            'Utils',
            'HelperUtils',
            'i18n!localization/nls/DocumentLabel',
            'i18n!localization/nls/HeadlineType'
        ],
        function ($, Self, Utils, HelperUtils, DocumentLabels, HeadlineType) {
            if (Self === undefined || Self === null) {

                var PreferencesSchema = function () {
                    this.prefs = {
                        "LearnerInfo": {
                            "type": "root",
                            "order": ["LearnerInfo.Identification",
                                "LearnerInfo.Headline",
                                "LearnerInfo.WorkExperience",
                                "LearnerInfo.Education",
                                "LearnerInfo.Skills",
                                "LearnerInfo.Achievement",
                                "LearnerInfo.ReferenceTo",
                                "LearnerInfo.CEFLanguageLevelsGrid"
                            ],
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ECL": true,
                                "ESP": true,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification": {
                            "type": "object",
                            "order": [//"LearnerInfo.Identification.ContactInfo.Address", 
                                "LearnerInfo.Identification.ContactInfo.Telephone",
                                "LearnerInfo.Identification.ContactInfo.Email",
                                "LearnerInfo.Identification.ContactInfo.Website",
                                "LearnerInfo.Identification.ContactInfo.InstantMessaging",
                                "LearnerInfo.Identification.Demographics.Birthdate",
                                "LearnerInfo.Identification.Demographics.Gender",
                                "LearnerInfo.Identification.Demographics.Nationality",
                                "LearnerInfo.Identification.Photo"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": true,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        /*"LearnerInfo.Identification.ContactInfo.Address" :{
                         "type" : "leaf",
                         "documents":{
                         "ECV" : true,
                         "ELP" : false,
                         "ECL"  : true,
                         "ESP" : false,
                         "ECV_ESP" : true
                         }
                         }, */
                        "LearnerInfo.Identification.ContactInfo.Telephone": {
                            "type": "array",
                            "order": ["LearnerInfo.Identification.ContactInfo.Telephone.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": true,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.ContactInfo.Telephone.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "Contact"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": true,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.ContactInfo.Email": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "Contact"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": true,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.ContactInfo.Website": {
                            "type": "array",
                            "order": ["LearnerInfo.Identification.ContactInfo.Website.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.ContactInfo.Website.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "Contact"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.ContactInfo.InstantMessaging": {
                            "type": "array",
                            "order": ["LearnerInfo.Identification.ContactInfo.InstantMessaging.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": true,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.ContactInfo.InstantMessaging.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "Contact"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": true,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.Demographics.Gender": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.Demographics.Birthdate": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.Demographics.Nationality": {
                            "type": "array",
                            "order": ["LearnerInfo.Identification.ContactInfo.Nationality.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.ContactInfo.Nationality.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "Label"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Identification.Photo": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Headline": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findHeadlineTitle"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience": {
                            "type": "array",
                            "order": ["LearnerInfo.WorkExperience.array[0]"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0]": {
                            "type": "object",
                            "order": ["LearnerInfo.WorkExperience.array[0].Period",
                                "LearnerInfo.WorkExperience.array[0].Position",
                                "LearnerInfo.WorkExperience.array[0].Activities",
                                "LearnerInfo.WorkExperience.array[0].Employer",
                                "LearnerInfo.WorkExperience.array[0].ReferenceTo"
                            ],
                            "contentLabel": {
                                "property": "Position.Label"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.WorkExperience"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0].Period": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0].Position": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0].Activities": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0].Employer": {
                            "type": "object",
                            "order": [//"LearnerInfo.WorkExperience.array[0].Employer.ContactInfo.Address",
                                "LearnerInfo.WorkExperience.array[0].Employer.ContactInfo.Website",
                                "LearnerInfo.WorkExperience.array[0].Employer.Sector"
                            ],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        /*"LearnerInfo.WorkExperience.array[0].Employer.ContactInfo.Address":{
                         "type" : "leaf",
                         "documents":{
                         "ECV" : true,
                         "ELP" : false,
                         "ECL"  : false,
                         "ESP" : false,
                         "ECV_ESP" : true
                         }
                         },*/
                        "LearnerInfo.WorkExperience.array[0].Employer.ContactInfo.Website": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0].Employer.Sector": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0].ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.WorkExperience.array[0].ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.WorkExperience.array[0].ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Education": {
                            "type": "array",
                            "order": ["LearnerInfo.Education.array[0]"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0]": {
                            "type": "object",
                            "order": ["LearnerInfo.Education.array[0].Period",
                                "LearnerInfo.Education.array[0].Title",
                                "LearnerInfo.Education.array[0].Activities",
                                "LearnerInfo.Education.array[0].Organisation",
                                "LearnerInfo.Education.array[0].Level",
                                "LearnerInfo.Education.array[0].Field",
                                "LearnerInfo.Education.array[0].ReferenceTo"
                            ],
                            "contentLabel": {
                                "property": "Title"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Education"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].Period": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].Title": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].Activities": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].Organisation": {
                            "type": "object",
                            "order": [//"LearnerInfo.Education.array[0].Organisation.ContactInfo.Address",
                                "LearnerInfo.Education.array[0].Organisation.ContactInfo.Website"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        /*"LearnerInfo.Education.array[0].Organisation.ContactInfo.Address":{
                         "type" : "leaf",
                         "documents":{
                         "ECV" : true,
                         "ELP" : false,
                         "ECL"  : false,
                         "ESP" : false,
                         "ECV_ESP" : true
                         }
                         },*/
                        "LearnerInfo.Education.array[0].Organisation.ContactInfo.Website": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].Level": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].Field": {
                            "type": "leaf",
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Education.array[0].ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Education.array[0].ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills": {
                            "type": "object",
                            "order": ["LearnerInfo.Skills.Linguistic.MotherTongue",
                                "LearnerInfo.Skills.Linguistic.ForeignLanguage",
                                "LearnerInfo.Skills.Communication",
                                "LearnerInfo.Skills.Organisational",
                                "LearnerInfo.Skills.JobRelated",
                                "LearnerInfo.Skills.Computer",
                                "LearnerInfo.Skills.Other",
                                "LearnerInfo.Skills.Driving"
                            ],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.Linguistic.MotherTongue": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Linguistic.MotherTongue.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.MotherTongue.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "Description.Label"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.Linguistic.ForeignLanguage": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0]": {
                            "type": "object",
                            "order": [
                                "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Certificate",
                                "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Experience",
                                "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].ReferenceTo"
                            ],
                            "contentLabel": {
                                "property": "Description.Label"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ESP": false,
                                "ECL": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].ProficiencyLevel": {

                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "sectionKey": {
                                "_function": "findParentSection"
                            },
                            "documents": {
                                "ECV": false,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": false
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Certificate": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Certificate.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Certificate.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "Title"
                            },
                            "sectionKey": {
                                "_function": "findParentSection"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Experience": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Experience.array[0]"],
                            "documents": {
                                "ECV": false,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": false
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].Experience.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "displayPeriod"
                            },
                            "sectionKey": {
                                "_function": "findParentSection"
                            },
                            "documents": {
                                "ECV": false,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": false
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Linguistic.ForeignLanguage.array[0].ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ESP": false,
                                "ECL": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.Communication": {
                            "type": "object",
                            "order": ["LearnerInfo.Skills.Communication.ReferenceTo"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Communication.ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Communication.ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Communication.ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.Organisational": {
                            "type": "object",
                            "order": ["LearnerInfo.Skills.Organisational.ReferenceTo"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Organisational.ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Organisational.ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Organisational.ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.JobRelated": {
                            "type": "object",
                            "order": ["LearnerInfo.Skills.JobRelated.ReferenceTo"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.JobRelated.ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.JobRelated.ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.JobRelated.ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ESP": false,
                                "ECL": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.Computer": {
                            "type": "object",
                            "order": ["LearnerInfo.Skills.Computer.ReferenceTo"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Computer.ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Computer.ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Computer.ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ECL": false,
                                "ELP": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.Driving": {
                            "type": "object",
                            "order": ["LearnerInfo.Skills.Driving.ReferenceTo"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Driving.ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Driving.ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Driving.ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Skills.Other": {
                            "type": "object",
                            "order": ["LearnerInfo.Skills.Other.ReferenceTo"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Skills"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Other.ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Skills.Other.ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Skills.Other.ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Achievement": {
                            "type": "array",
                            "order": ["LearnerInfo.Achievement.array[0]"],
                            "contentLabel": {
                                "_function": "findSectionTitle"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.Achievement.array[0]": {
                            "type": "object",
                            "order": ["LearnerInfo.Achievement.array[0].ReferenceTo"],
                            "contentLabel": {
                                "property": "Title.Label"
                            },
                            "sectionKey": {
                                "property": "LearnerInfo.Achievement"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Achievement.array[0].ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.Achievement.array[0].ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.Achievement.array[0].ReferenceTo.array[0]": {
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.ReferenceTo": {
                            "type": "array",
                            "order": ["LearnerInfo.ReferenceTo.array[0]"],
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": true
                            }
                        },
                        "LearnerInfo.ReferenceTo.array[0]": {
                            "esp": true,
                            "type": "leaf",
                            "contentLabel": {
                                "_function": "findAttachment"
                            },
                            "documents": {
                                "ECV": true,
                                "ELP": false,
                                "ECL": false,
                                "ESP": true,
                                "ECV_ESP": true
                            }
                        },

                        "LearnerInfo.CEFLanguageLevelsGrid": {
                            "type": "leaf",
                            "contentLabel": {
                                "property": "LearnerInfo.CEFLanguageLevelsGrid"
                            },
                            "documents": {
                                "ECV": false,
                                "ELP": true,
                                "ECL": false,
                                "ESP": false,
                                "ECV_ESP": false
                            }
                        }
                    };
                };
                /**
                 * Change the order when switching the sections 
                 */
                PreferencesSchema.prototype.switchSectionOrder = function (section) {
                    var schema = this.resolve(section);
                    var order = (schema !== null) ? schema.order : null;
                    if (order !== undefined && order !== null) {
                        var sections = order.slice(2, 4);//get work and education
                        sections.reverse();
                        order.splice(2, 2, sections[0], sections[1]);
                    }
                };
                /**
                 * Return the value for the current live Content according to the contentLabel defined in the PreferencesSchema
                 * 
                 * @param key the key of the current live content 
                 * @param liveContent, the json of the current live model
                 * @param currentLiveContent, the json of a section of the current live model
                 */
                PreferencesSchema.prototype.getContentForKey = function (key, liveContent, currentLiveContent) {
                    var value = null;

                    key = Utils.removeSkillsPassportPrefix(key);

                    var contentKey = key; //so that the prefix is removed...
                    if (this.isIndexedTxt(key)) {
                        key = this.toArrayTxt(key);

                        key = this.toZeroIndexTxt(key);
                    }
                    var schema = this.resolve(key);
                    if (schema !== null) {
                        var contentLabel = schema.contentLabel;

                        if (contentLabel !== undefined && contentLabel !== null && currentLiveContent !== undefined) {
                            value = this.getContentLabel(contentLabel, liveContent, currentLiveContent, contentKey);
                        } else {
                            value = Utils.objAttr(liveContent, contentKey);
                        }
                    }
                    return value;
                };
                /**
                 * Return the value for the related section key according to the sectionKey defined in the PreferencesSchema
                 * 
                 * @param key the key of the current live content 
                 * @param liveContent, the json of the current live model
                 * @param currentLiveContent, the json of a section of the current live model
                 */
                PreferencesSchema.prototype.getSectionForKey = function (key, liveContent, currentLiveContent) {
                    var value = null;

                    key = Utils.removeSkillsPassportPrefix(key);

                    var contentKey = key; //so that the prefix is removed...
                    if (this.isIndexedTxt(key)) {
                        key = this.toArrayTxt(key);

                        key = this.toZeroIndexTxt(key);
                    }
                    var schema = this.resolve(key);
                    if (schema !== null) {
                        var sectionKey = schema.sectionKey;

                        var sectionKeyDefined = (sectionKey !== undefined && sectionKey !== null);
                        if (sectionKeyDefined && (currentLiveContent !== undefined || liveContent !== undefined)) {
                            value = this.getSectionKey(sectionKey, liveContent, currentLiveContent, contentKey);
                        } else {
                            value = null;
                        }
                    }
                    return value;
                };
                /**
                 * Return the value for the current live Content according to the contentLabel defined in the PreferencesSchema
                 * 
                 * @param the schema.contentLabel information
                 * @param liveContent, the json of the current live model
                 * @param currentLiveContent, the json of a section of the current live model
                 * @param key the key of the current live content  ( might be undefined or null )
                 */
                PreferencesSchema.prototype.getContentLabel = function (contentLabel, liveContent, currentLiveContent, liveKey, prefKey) {
                    var value = null;

                    var fromProperty = contentLabel.property;
                    var fromFunction = contentLabel._function;

                    if (fromProperty !== undefined && fromProperty !== null && fromProperty !== "") {
                        //get the value according to the property from to the current live content
                        value = ($.isPlainObject(currentLiveContent)
                                ? Utils.objAttr(currentLiveContent, fromProperty)
                                : liveContent);

                    } else if (fromFunction !== undefined && fromFunction !== null && fromFunction !== "") {
                        //get the value by executing the function
                        var _f = this[ fromFunction ];
                        if ($.isFunction(_f)) {
                            value = _f.apply(this, [liveContent, currentLiveContent, liveKey, prefKey]);
                        }
                    }
                    return value;
                };
                /**
                 * Return the section key for the current live Content according to the sectionKey defined in the PreferencesSchema
                 * 
                 * @param the schema.sectionKey information
                 * @param liveContent, the json of the current live model
                 * @param currentLiveContent, the json of a section of the current live model
                 * @param key the key of the current live content  ( might be undefined or null )
                 */
                PreferencesSchema.prototype.getSectionKey = function (sectionKey, liveContent, currentLiveContent, key) {
                    var value = null;

                    var fromProperty = sectionKey.property;
                    var fromFunction = sectionKey._function;

                    if (fromProperty !== undefined && fromProperty !== null && fromProperty !== "") {
                        //get the value according to the property from to the current live content
                        value = fromProperty;
                    } else if (fromFunction !== undefined && fromFunction !== null && fromFunction !== "") {
                        //get the value by executing the function
                        var _f = this[ fromFunction ];
                        if ($.isFunction(_f)) {
                            value = _f.apply(this, [liveContent, currentLiveContent, key]);
                        }
                    }
                    return value;
                };

                PreferencesSchema.prototype.getSectionLabel = function (subSectionKey, liveContent, currentLiveContent, key, noSubsection) {
                    //e.g. E.g. Electrician in Education and Training
                    var value = null;

                    subSectionKey = Utils.removeSkillsPassportPrefix(subSectionKey);

                    var onlySection = noSubsection === true;

                    //1. find the title of the subsection based on the contentLabel property
                    var subSectionTitle = onlySection ? false : this.getContentForKey(subSectionKey, liveContent, currentLiveContent);

                    //2. find the related section key based on the sectionKey property
                    var relatedSectionKey = this.getSectionForKey(subSectionKey, liveContent, currentLiveContent);

                    //3. find the title of the section based on the contentLabel property 
                    var sectionTitle = relatedSectionKey === null ? false : this.getContentForKey(relatedSectionKey, liveContent, Utils.objAttr(liveContent, relatedSectionKey));

                    var htmlSubsection = (onlySection ? "" :
                            "<span class=\"linked-subsection\">" +
                            (subSectionTitle === undefined || subSectionTitle === null || subSectionTitle === false ? "..." : subSectionTitle) +
                            "</span>");
                    var htmlSection = (sectionTitle === undefined || sectionTitle === null || sectionTitle === false)
                            ? ""
                            : "<span class=\"linked-section\">" +
                            sectionTitle +
                            "</span>";
                    value = htmlSubsection + htmlSection;

                    return value;
                };
                /**
                 * Display the Period of an Experience formatted according to the period dates formats
                 * 
                 * @param liveContent
                 * @param currentLiveContent
                 * @param key
                 */
                PreferencesSchema.prototype.displayPeriod = function (liveContent, currentLiveContent, liveKey, prefKey) {
                    var value = DocumentLabels[ Utils.removeIndexTxt(liveKey) ];
                    var prefDocument = prefKey.substring(0, prefKey.indexOf("."));
                    if (Utils.isEmptyObject(currentLiveContent) !== true) {
                        var period = currentLiveContent.Period;
                        if (Utils.isEmptyObject(period) !== true) {
                            var periodPref = liveContent.preferences.get(prefKey + ".Period");
                            var format = (periodPref === null) ? null : periodPref.format;
                            value = HelperUtils.formatPeriod(period, format, false, prefDocument);
                        }
                    }
                    return value;
                };
                PreferencesSchema.prototype.findParentSection = function (liveContent, currentLiveContent, key) {
                    return key.substring(0, key.lastIndexOf("."));
                };
                /**
                 * Find the text to display instead of Headline
                 */
                PreferencesSchema.prototype.findHeadlineTitle = function (liveContent, currentLiveContent, key) {
                    var value = null;
                    var type = "job_applied_for";

                    if (currentLiveContent !== undefined && currentLiveContent !== null
                            && currentLiveContent.Type !== undefined && currentLiveContent.Type !== null
                            && currentLiveContent.Type.Code !== undefined && currentLiveContent.Type.Code !== null) {
                        type = currentLiveContent.Type.Code;
                        value = HeadlineType[ type ];
                    }
                    return value;
                };

                /**
                 * Find the title of a section
                 */
                PreferencesSchema.prototype.findSectionTitle = function (liveContent, currentLiveContent, key) {
                    var value = null;
                    if (key !== undefined && key !== null && key !== "") {
                        var labelKey = Utils.removeIndexTxt(key);
                        value = DocumentLabels[ labelKey ];
                    }
                    return value;
                };
                /**
                 * Returns the Attachment Name according to the idref of a given ReferenceTo object.
                 * 
                 * @param liveContent, the current live model
                 * @param currentLiveContent, the json of a section of the current live model,
                 *        in this case it is a ReferenceTo json structure 
                 * @param key the key of the current live content  ( might be undefined or null )
                 */
                PreferencesSchema.prototype.findAttachment = function (liveContent, currentLiveContent, key) {
                    //Get the idref of the current liveContent
                    var idref = ($.isPlainObject(currentLiveContent)
                            ? currentLiveContent[ "idref" ] : null);
                    if (idref !== undefined && idref !== null) {
                        var matchedAtt = liveContent === undefined || liveContent === null ? null : liveContent.documentation().attachmentById(idref);
                        var att = matchedAtt === null ? false : matchedAtt.attachment;
                        var name = (att !== false && $.isPlainObject(att)) ? Utils.cropText(att.Description, 18, 8) : null;
                        var mime = (att !== false && $.isPlainObject(att)) ? att.MimeType : null;

                        var img = (mime === 'image/png' || mime === 'image/jpg' || mime === 'image/jpeg') ? 'thumb image' : 'thumb pdf';
                        return '<div class="' + img + ' name">' + name + '</div>';
                    }
                };
                PreferencesSchema.prototype.resolve = function (jsonPath, x) {
                    var pref = this.prefs[ jsonPath ];
                    if (pref === undefined)
                        return null;
                    return pref;
                };
                PreferencesSchema.prototype.getType = function (jsonPath, x) {
                    var pref = this.prefs[ jsonPath ];
                    if (pref === undefined)
                        return null;
                    return pref.type;
                };
                PreferencesSchema.prototype.getOrder = function (jsonPath, x) {
                    var pref = this.prefs[ jsonPath ];
                    if (pref === undefined)
                        return null;
                    return pref.order;
                };

                PreferencesSchema.prototype.isIndexedTxt = function (jsonPath) {
                    return (jsonPath.match(/\[\d+\]/g) !== null);
                };

                PreferencesSchema.prototype.containsArrayTxt = function (jsonPath) {
                    return (jsonPath.match(/.array\[0\]/g) !== null);
                };

                PreferencesSchema.prototype.isArrayTxt = function (jsonPath) {
                    return (jsonPath.match(/.array\[0\]$/g) !== null);
                };

                PreferencesSchema.prototype.toArrayTxt = function (jsonPath) {
                    return jsonPath.replace(/\[/g, ".array[");
                };

                PreferencesSchema.prototype.toIndexTxt = function (jsonPath) {
                    return jsonPath.replace(/\.array\[/g, "[");
                };

                PreferencesSchema.prototype.toZeroIndexTxt = function (jsonPath) {
                    return jsonPath.replace(/\[\d+\]/g, "[0]");
                };

                PreferencesSchema.prototype.getIndexed = function (jsonPath, index) {
                    return jsonPath + "[" + index + "]";
                };

                PreferencesSchema.prototype.parse = function (
                        callback,
                        endItemCallback,
                        recursionStartCallback,
                        recursionEndCallback,
                        scope,
                        model,
                        docType,
                        prefs,
                        defaultPrefs,
                        key,
                        liveKey,
                        level) {

                    if (key === undefined || key === null) {
                        key = "LearnerInfo";
                    }
                    if (liveKey === undefined || liveKey === null) {
                        liveKey = "LearnerInfo";
                    }
                    if (level === undefined || level === null) {
                        level = 0;
                    }

                    var schema = this.resolve(key);

                    if (schema === null) {
                        return false;
                    }

                    var liveData = model.get(Utils.addSkillsPassportPrefix(liveKey));

                    var prefKey = Utils.addPrefix(docType, Utils.toArrayTxt(liveKey));

                    var livePref = prefs.get(prefKey);

                    var defaultPref = defaultPrefs.get(Utils.addPrefix(docType, this.toZeroIndexTxt(prefKey)));

//				console.log("==PARSE==" 
//				+			"==Key          : " + key +"\n"
//				+			"==LiveKey      : " + liveKey +"\n"
//				+			"==livePref     : " + JSON.stringify(livePref) +"\n"
//				+			"==defaultPref  : " + JSON.stringify(defaultPref) +"\n"
//				+			"==liveData     : " + JSON.stringify(liveData) +"\n"
//				+			"==Schema       : " + JSON.stringify(schema) +"\n"
//				+			"==Level        : " + JSON.stringify(level) +"\n");

                    //handle current
                    if ($.isFunction(callback)) {
                        callback.apply((scope || this), [model, liveKey, prefKey, livePref, defaultPref, liveData, schema, level]);
                    }
                    var that = this;
                    //proceed with recursion
                    switch (schema.type) {
                        case "root":
                        { /*handle as object*/
                        }
                        case "object":
                        {
                            //console.log("--OBJECT--");
                            if ($.isFunction(recursionStartCallback)) {
                                recursionStartCallback.apply((scope || this), [liveKey, prefKey, liveData, level]);
                            }
                            $(schema.order).each(function (i, nextKey) {
                                var nextLiveKey = nextKey;
                                /*
                                 * Example here:
                                 * "LearnerInfo.WorkExperience.array[0]":{
                                 *		"type" : "object",
                                 *		"order": [ "LearnerInfo.WorkExperience.array[0].Period" ]
                                 *	}
                                 * we need the nextLiveKey to be LearnerInfo.WorkExperience[4].Period,
                                 * given that the liveKey is LearnerInfo.WorkExperience[4]
                                 * 
                                 * key = "LearnerInfo.WorkExperience.array[0]"
                                 * nextKey = "LearnerInfo.WorkExperience.array[0].Period"
                                 * suffix = ".Period"
                                 * nextLiveKey = liveKey + suffix
                                 */
                                if (that.containsArrayTxt(key)) {
                                    var start = nextKey.indexOf(key) + key.length;
                                    var suffix = nextKey.substr(start);
                                    nextLiveKey = liveKey + suffix;
                                }
                                that.parse(callback, endItemCallback, recursionStartCallback, recursionEndCallback, scope, model, docType, prefs, defaultPrefs, nextKey, nextLiveKey, (level + 1));
                            });

                            if ($.isFunction(recursionEndCallback)) {
                                recursionEndCallback.apply((scope || this), [liveKey, prefKey, liveData, level]);
                            }
                            break;
                        }
                        case "array" :
                        {
                            //console.log("--ARRAY--");
                            //go to live and get the actual array
                            var liveDataL = liveData === null ? 0 : liveData.length;

                            if (liveDataL > 0) {
                                //The content has indexed values
                                //lets see if the preferences of each index are ok
                                /*
                                 * Example here:
                                 *  "LearnerInfo.WorkExperience.array[0].ReferenceTo":{
                                 *		"type" : "array",
                                 *		"order" : ["LearnerInfo.WorkExperience.array[0].ReferenceTo.array[0]"]
                                 *	}
                                 * key = "LearnerInfo.WorkExperience.array[0].ReferenceTo"
                                 * 
                                 * liveKey = "LearnerInfo.WorkExperience[3].ReferenceTo"
                                 * 
                                 * liveItemsL = at least 4
                                 * 
                                 * The corresponding value at defaultItemsModel is { "name" : "LearnerInfo.WorkExperience[0].ReferenceTo", "show" : false },
                                 * 
                                 */
                                if ($.isFunction(recursionStartCallback)) {
                                    recursionStartCallback.apply((scope || this), [liveKey, prefKey, liveData, level]);
                                }
                                for (var i = 0; i < liveDataL; i++) {

                                    var nextLiveKey = this.getIndexed(liveKey, i);

                                    $(schema.order).each(function (i, nextKey) {
                                        that.parse(callback, endItemCallback, recursionStartCallback, recursionEndCallback, scope, model, docType, prefs, defaultPrefs, nextKey, nextLiveKey, (level + 1));
                                    });
                                }
                                if ($.isFunction(recursionEndCallback)) {
                                    recursionEndCallback.apply((scope || this), [liveKey, prefKey, liveData, level]);
                                }
                            }
                            break;
                        }
                    }
                    //handle current
                    if ($.isFunction(endItemCallback)) {
                        endItemCallback.apply((scope || this), [liveKey, prefKey, liveData, level]);
                    }
                };

                var Self = new PreferencesSchema();
            }
            return Self;
        }
);
