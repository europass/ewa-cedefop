define([ 'jquery',
         'underscore',
         'backbone', 
         'jasmine-html',
         'xdate', 
         'views/validation/ValidationFormView',
         'europass/structures/ValidationError'],

function($, _,
		Backbone, 
		jasmine,
		XDate,
		ValidationFormView,
		ValidationError) {
			
	$("body").attr("id","test");
	
	describe("ValidationFormView.dateValidate() :: ", function() {

		describe("returns error codes...", function() {
			
			it("0: When Date is valid", function() {
	
				var validation = new ValidationError(undefined,0,"validation.error.ok");
	
				var view = new ValidationFormView();
				view.initialize();
				view.el = "body";
	
				var result = view.dateValidate(1,1,2000);
				view.remove();
				
				expect(result).toEqual(validation);
			});
			
			it("0: When Date is invalid and is compared to currentDate", function() {
				
				var validation = new ValidationError(undefined,0,"validation.error.ok");
	
				var view = new ValidationFormView();
				view.initialize();
				view.el = "body";
	
				var result = view.currentDateValidate(1,"",2013);
				view.remove();
				
				expect(result).toEqual(validation);
			});
	
			it("1: When Date is empty", function() {
	
				var validation = new ValidationError(undefined,1,"validation.error.noInput");
	
				var view = new ValidationFormView();
				view.initialize();
				view.el = "body";
	
				var result = view.dateValidate('','','');
				view.remove();
				
				expect(result).toEqual(validation);
			});
			
			it("1: When Date has only year", function() {
				
				var validation = new ValidationError(undefined,0,"validation.error.ok");
	
				var view = new ValidationFormView();
				view.initialize();
				view.el = "body";
	
				var result = view.dateValidate('','',2013);
				view.remove();
				
				expect(result).toEqual(validation);
			});
			
			it("100: When Date is invalid (ex: 29/02/2013)", function() {
				
				var validation = new ValidationError(undefined,100,"validation.error.invalidDate");
	
				var view = new ValidationFormView();
				view.initialize();
				view.el = "body";
	
				var result = view.dateValidate(29,2,2013);
				view.remove();
				
				expect(result).toEqual(validation);
			});

			xit("101: When Date is later than today (ex: 31/12/2100)", function() {
				
				var validation = new ValidationError(undefined,101,"validation.error.dateAfterToday");
	
				var view = new ValidationFormView();
				view.initialize();
				view.el = "body";
	
				var result = view.dateValidate(31,12,2100);
				view.remove();
				
				expect(result).toEqual(validation);
			});
			
		});	

		describe("validates the following dates as...", function() {
		
			function testInvalidDate(dateStr,validateResult){
				
				var message = "Date "+dateStr+" is valid";
				var validation = new ValidationError(undefined,0,"validation.error.ok");
	
				if(validateResult.errorCode == 100){
					validation = new ValidationError(undefined,100,"validation.error.invalidDate");
					message = "Date "+dateStr+" is invalid";
				}
				
				it(message, function() {
		
					expect(validateResult).toEqual(validation);
				});
				
			}
	
			for(var y = 2011; y<=2012; y++){
				for(var m = 1; m<=12; m++){
					
					var startDay = 31; 
					if(m == 2)
						startDay = 29;
					
					for(var d = startDay; d<=31; d++){
					
						var view = new ValidationFormView();
						view.initialize();
						view.el = "body";
						var result = view.dateValidate(d,m,y);
						
						testInvalidDate(d+'/'+m+'/'+y,result);
						view.remove();
					}
				}
			}
		});	
	});
	
	describe("ValidationFormView.periodValidate() :: returns error codes...", function() {

		var fromDate = new Array(1,1,2012);
		var toDate = new Array(1,1,2013);
		
		// No From Date
		
		it("0: When 'From' AND 'To' Date are valid", function() {

			var view = new ValidationFormView();
			view.initialize();
			view.el = "body";
			
			var validFromTo = view.periodValidate(new Array(20,02,2013),new Array("","",2014),false,"test");
			expect(validFromTo[0]).toEqual(new ValidationError("test.From",0,"validation.error.ok"));
			expect(validFromTo[1]).toEqual(new ValidationError("test.To",0,"validation.error.ok"));
			
			view.remove();
		});

		it("1: When 'From' AND 'To' Date are empty and 'Current' is unchecked", function() {

			var view = new ValidationFormView();
			view.initialize();
			view.el = "body";
			
			var emptyArray = new Array();
			emptyArray[0] = '';
			emptyArray[1] = '';
			emptyArray[2] = '';
			
			var valid = view.periodValidate(emptyArray,emptyArray,false,"test");
			expect(valid[0]).toEqual(new ValidationError("test",1,"validation.error.noInput"));
			view.remove();
		});

		it("100: When 'From' AND/OR 'To' Date is invalid", function() {

			var view = new ValidationFormView();
			view.initialize();
			view.el = "body";
			
			var invalidFromTo = view.periodValidate(new Array(29,02,2013),new Array(31,04,2013),false,"test");
			expect(invalidFromTo[0]).toEqual(new ValidationError("test.From",100,"validation.error.invalidDate"));
//			expect(invalidFromTo[1]).toEqual(new ValidationError("test.To",100,"validation.error.invalidDate"));
			
			view.remove();
		});
		
		xit("101: When 'From' AND/OR 'To' Date is a later date than Today", function() {

			var view = new ValidationFormView();
			view.initialize();
			
			var invalidFromTo = view.periodValidate(new Array(27,02,2114),new Array(29,05,2114),false,"test");
			expect(invalidFromTo[0]).toEqual(new ValidationError("test.From",101,"validation.error.dateAfterToday"));
			expect(invalidFromTo[1]).toEqual(new ValidationError("test.To",101,"validation.error.dateAfterToday"));
			
//			var invalidTo = view.periodValidate(fromDate,new Array(27,02,2014),false,"test");
//			expect(invalidTo[0]).toEqual(new ValidationError("test.From",0,"validation.error.ok"));
//			expect(invalidTo[1]).toEqual(new ValidationError("test.To",101,"validation.error.dateAfterToday"));
			
			view.remove();
		});

		it("201: When 'From' Date cannot be a later date than 'To' OR 'Current' Date", function() {
			
			var view = new ValidationFormView();
			view.initialize();
			
			var fromBeforeTo = view.periodValidate(toDate,fromDate,false,"test");
			expect(fromBeforeTo[0]).toEqual(new ValidationError("test.From",0,"validation.error.ok"));
			expect(fromBeforeTo[1]).toEqual(new ValidationError("test.From",201,"validation.error.negativePeriod"));

//			var fromBeforeCurrent = view.periodValidate(new Array(1,1,2150),undefined,true,"test");
//			expect(fromBeforeCurrent[1]).toEqual(new ValidationError("test.From",201,"validation.error.negativePeriod"));
			
			view.remove();
		});

		it("202: When 'From' Date does not exist while 'To' OR 'Current' Date exists", function() {
			
			var view = new ValidationFormView();
			view.initialize();

			var nonExistFrom = view.periodValidate(undefined,toDate,false,"test");
			expect(nonExistFrom[0]).toEqual(new ValidationError("test.To",0,"validation.error.ok"));
			expect(nonExistFrom[1]).toEqual(new ValidationError("test.From",202,"validation.error.nonexistFrom"));
			
			var nonExistFromTo = view.periodValidate(undefined,undefined,true,"test");
			expect(nonExistFromTo[0]).toEqual(new ValidationError("test.From",202,"validation.error.nonexistFrom"));
			
			view.remove();
		});

		it("203: When 'To' Date does not exist while Current is not checked", function() {
			
			var view = new ValidationFormView();
			view.initialize();
			
			var toExistsWhileCurrent = view.periodValidate(fromDate,undefined,false,"test");
			
			expect(toExistsWhileCurrent[0]).toEqual(new ValidationError("test.From",0,"validation.error.ok"));
			view.remove();
		});
	});
});