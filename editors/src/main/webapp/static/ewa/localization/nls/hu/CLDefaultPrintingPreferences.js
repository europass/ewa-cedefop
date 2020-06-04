define({
	"CoverLetter" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "CoverLetter",
		"order" : "Addressee Letter.SubjectLine Letter.OpeningSalutation Letter.Body Letter.Localisation Letter.ClosingSalutation"
	},
    "CoverLetter.Justification" : {
      	 "@type" : "PrintingPreference",
      	 "justify" : false,
   	     "name" : "CoverLetter.Justification"
	},
    "CoverLetter.SignatureName" : {
        "@type" : "PrintingPreference",
        "enableName" : true,
        "name" : "CoverLetter.SignatureName"
    },
	"LearnerInfo.Identification.PersonName" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "LearnerInfo.Identification.PersonName",
		"order" : "Surname FirstName"
	},
	"LearnerInfo.Identification.ContactInfo.Address" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "LearnerInfo.Identification.ContactInfo.Address",
		"format" : "s, m, z c"
	},
	"CoverLetter.Letter.Localisation" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "CoverLetter.Letter.Localisation",
		"position" : "left-align",
		"order" : "Place Date"
	},
	"CoverLetter.Letter.Localisation.Date" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "CoverLetter.Letter.Localisation.Date",
		"format" : "text/short"
	},
	"CoverLetter.Addressee" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "CoverLetter.Addressee",
		"position" : "left-align"
	},
	"CoverLetter.Addressee.PersonName" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "CoverLetter.Addressee.PersonName",
		"order" : "Title Surname FirstName"
	},
	"CoverLetter.Letter.ClosingSalutation" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "CoverLetter.Letter.ClosingSalutation",
		"position" : "left-align"
	},
	"CoverLetter.Documentation" : {
		"@type" : "PrintingPreference",
		"show" : true,
		"name" : "CoverLetter.Documentation",
		"format" : "text"
	}
});