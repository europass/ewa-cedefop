define({
	   "root" : {
	      "CoverLetter" : {
	         "@type" : "PrintingPreference",
	         "show" : true,
	         "name" : "CoverLetter",
	         "order" : "Addressee Letter.Localisation Letter.SubjectLine Letter.OpeningSalutation Letter.Body Letter.ClosingSalutation"
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
	         "order" : "FirstName Surname"
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
	      "CoverLetter.Letter.Localisation.Date": {
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
	         "order" : "Title FirstName Surname"
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
	   }
	   ,"bg" : false,"es" : false,"cs" : false,"da" : false,"de" : false,"et" : false,"el" : false,"fr" : false,"hr" : false,"is" : false,"it" : false,"lv" : false,"lt" : false,"hu" : false,"mk" : false,"mt" : false,"nl" : false,"nb" : false,"pl" : false,"pt" : false,"ro" : false,"sk" : false,"sl" : false,"fi" : false,"sv" : false,"tr" : false,"sr-cyr" : false,"sr-lat" : false
});
