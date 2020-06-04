define([ 'jquery',
         'jasmine',
         'model/europass/SkillsPassport'], 

function( $, jasmine, SkillsPassport ) {
	
	describe("SubModel :: Set/Get Tests ", function(){
		
		var esp = new SkillsPassport();
		
		describe("-- Setting PersonName results in -- ", function(){
			
			var firstNameKey = "LearnerInfo.Identification.PersonName.FirstName";
			var firstNameValue = "Babis";
			
			esp.set( firstNameKey, firstNameValue );
			
			it("the FirstName to match the expected value '"+firstNameValue+"'", function(){
				var v = esp.get(firstNameKey);
				expect( v ).toBe( firstNameValue );
			});
		});
	
	
		describe("-- Set List of Nationality --", function(){
		
			var nationalityKey = "LearnerInfo.Identification.Demographics.Nationality";
			
			
			describe("-- Set Nationality details at index 0 --", function(){
				var natCode = "el";
				var natLabel = "Greek";
				
				var codeKey = nationalityKey+"[0].Code";
				esp.set( codeKey, natCode );
				
				it("Code matches the expected value '"+natCode+"'", function(){
					var v = esp.get(codeKey);
					expect( v ).toBe( natCode );
				});
				
				var labelKey = nationalityKey+"[0].Label";
				esp.set( labelKey, natLabel );
				
				it("Label matches the expected value '"+natLabel+"'", function(){
					var v = esp.get(labelKey);
					expect( v ).toBe( natLabel );
				});
			});
			
			describe("-- Set Nationality details at index 1 --", function(){
				var natCode = "it";
				var natLabel = "Italian";
				
				var codeKey = nationalityKey+"[1].Code";
				esp.set( codeKey, natCode );
				
				it("Code matches the expected value '"+natCode+"'", function(){
					var v = esp.get(codeKey);
					expect( v ).toBe( natCode );
				});
				
				var labelKey = nationalityKey+"[1].Label";
				esp.set( labelKey, natLabel );
				
				it("Label matches the expected value '"+natLabel+"'", function(){
					var v = esp.get(labelKey);
					expect( v ).toBe( natLabel );
				});
			});
			
			describe("-- After setting two items, the Nationality Collection --", function(){
				
				it ("Length is two (2)", function(){
					var c = esp.get( nationalityKey );
					
					expect( ( c instanceof Backbone.Collection ) ).toBe( true );
					
					expect( c.length ).toBe( 2 );
				});
			});
			
		});
		
		describe("-- Foreign Language (nested Models and Collections) -- ", function(){
			
			var foreignKey = "LearnerInfo.Skills.Linguistic.ForeignLanguage";

			describe("-- Set ForeignLanguage details at index 0 --", function(){
				var code = "en";
				var label = "English";
				
				var codeKey = foreignKey+"[0].Description.Code";
				esp.set( codeKey, code );
				
				it("Code matches the expected value '"+code+"'", function(){
					var v = esp.get(codeKey);
					expect( v ).toBe( code );
				});
				
				var labelKey = foreignKey+"[0].Description.Label";
				esp.set( labelKey, label );
				
				it("Label matches the expected value '"+label+"'", function(){
					var v = esp.get(labelKey);
					expect( v ).toBe( label );
				});
			});
			
			describe("-- Set ForeignLanguage details at index 1 --", function(){
				var code = "es";
				var label = "Spanish";
				
				var codeKey = foreignKey+"[1].Description.Code";
				esp.set( codeKey, code );
				
				it("Code matches the expected value '"+code+"'", function(){
					var v = esp.get(codeKey);
					expect( v ).toBe( code );
				});
				
				var labelKey = foreignKey+"[1].Description.Label";
				esp.set( labelKey, label );
				
				it("Label matches the expected value '"+label+"'", function(){
					var v = esp.get(labelKey);
					expect( v ).toBe( label );
				});
				
				var certificateKey = foreignKey+"[1].Certificate";
				
				describe("-- Certificate details for Language at index 1 --", function(){
					describe("-- Set Certificate details at index 0 --", function(){
						var title = "Inicial";
						var titleKey = certificateKey+"[0].Title";
						
						esp.set( titleKey, title );
						
						it("Title @ '"+titleKey+"' matches the expected value '"+title+"'", function(){
							var v = esp.get(titleKey);
							expect( v ).toBe( title );
						});
					});
					
					describe("-- Set Certificate details at index 1 --", function(){
						var title = "Intermedio";
						var titleKey = certificateKey+"[1].Title";
						
						esp.set( titleKey, title );
						
						it("Title @ '"+titleKey+"' matches the expected value '"+title+"'", function(){
							var v = esp.get(titleKey);
							expect( v ).toBe( title );
						});
					});
					
					describe("-- Certificate Collection --", function(){
						
						it ("Contains two Certificate items", function(){
							var c = esp.get( certificateKey );
							
							expect( ( c instanceof Backbone.Collection ) ).toBe( true );
							
							expect( c.length ).toBe( 2 );
						});
						
					});
				});
				
			});
			
			describe("-- ForeignLanguage Collection --", function(){
				
				it ("Contains two Foreign Language items", function(){
					var c = esp.get( foreignKey );
					
					expect( ( c instanceof Backbone.Collection ) ).toBe( true );
					
					expect( c.length ).toBe( 2 );
				});
			});
		});
		
	});
	
});