USE [ewa_prod];

CREATE view [dbo].[ewa_stats_entry_to_europass_stat_entry]
as
select 
	[id]
	,[age]
	,case [docLanguage]
		when 'bg' then 'bg_BG'
		when 'cs' then 'cs_CZ' 
		when 'da' then 'da_DK' 
		when 'de' then 'de_DE' 
		when 'et' then 'et_EE' 
		when 'el' then 'el_GR' 
		when 'es' then 'es_ES' 
		when 'hr' then 'hr_HR' 
		when 'is' then 'is_IS' 
		when 'it' then 'it_IT' 
		when 'lv' then 'lv_LV' 
		when 'lt' then 'lt_LT' 
		when 'hu' then 'hu_HU' 
		when 'mt' then 'mt_MT' 
		when 'nl' then 'nl_NL' 
		when 'no' then 'nb_NO'
		when 'nb' then 'nb_NO'
		when 'pl' then 'pl_PL' 
		when 'pt' then 'pt_PT' 
		when 'ro' then 'ro_RO' 
		when 'sk' then 'sk_SK' 
		when 'sl' then 'sl_SI' 
		when 'sr-cyr' then 'sr_RS' 
		when 'sr-lat' then 'sr_RS'
		when 'fi' then 'fi_FI' 
		when 'sv' then 'sv_SE' 
		when 'tr' then 'tr_TR' 
		when 'en' then 'en_GB' 
		when 'fr' then 'fr_FR' 
		when 'mk' then 'mk_MK'
		else NULL
	end as [docLanguage]
	,[educationYears]
	,[gender]
	,[postalAddressCountry]
	,[workExperienceYears]
	,case [documentType]
		when 'ECV' then 'CV'
		when 'ECV_ESP' then 'ECV_ESP'
		when 'ESP' then 'ESP'
		when 'ELP' then 'LP'
		when 'ECL' then 'CL'
		else NULL
	end as [documentType]
	, [documentType] as [original_doc_type]
	,[fileFormat]
	,[creationDate]
	,[generatedBy]
	,[related_entry_id]
	,[emailHashCode]
	,[exportTo]	
from ewa_prod.dbo.ewa_stats_entry;


CREATE view [dbo].[ewa_stats_FandM_language_to_europass_stat_language]
as
select 
	id
	,SML.statsEntry_id	as [statsEntry_id]
	,case SML.language		
		-- REGULAR REFINEMENTS 
		when 'bg' then 56 
		when 'cs' then 83 
		when 'da' then  85 
		when 'nl' then  96 
		when 'en' then  103 
		when 'et' then  106 
		when 'fi' then  114 
		when 'fr' then  116 
		when 'de' then  128 
		when 'ga' then  132 
		when 'el' then  142 
		when 'hu' then  160 
		when 'is' then  164 
		when 'it' then  175 
		when 'lv' then  218 
		when 'lt' then  222 
		when 'mk' then  233
		when 'mt' then  252 
		when 'no' then  279 
		when 'pl' then  306 
		when 'pt' then  308 
		when 'ro' then 317 
		when 'hr' then  331 
		when 'sk' then  338 
		when 'sl' then  339 
		when 'es' then  353 
		when 'sv' then  363 
		when 'tr' then  391 
	
		-- FREETEXT REFINEMENTS : EN=>103
		when 'Ingles' then 103
		when 'English' then 103
		when 'ingles' then 103
		when 'Engleza' then 103
		when 'inglese' then 103
		when 'Inglés' then 103
		when 'Inglês' then 103
		when 'Inglese' then 103
		when 'Αγγλικά' then 103
		when 'INGLESE' then 103
		when 'engleza' then 103
		when 'Anglais' then 103
		when 'INGLES' then 103
		when 'english' then 103
		when 'Englisch' then 103
		when 'ENGLEZA' then 103
		when 'ΑΓΓΛΙΚΑ' then 103
		-- FREETEXT REFINEMENTS : PT=>308
		when 'Português' then 308
		when 'Portuguesa' then 308
		when 'Portugues' then 308
		when 'Portuguese' then 308
		when 'Portugais' then 308
		when 'Portugês' then 308
		when 'Portugal' then 308
		when 'Língua Portuguesa' then 308
		when 'portuguesa' then 308
		when 'PORTUGUES' then 308
		when 'Portugués' then 308
		-- FREETEXT REFINEMENTS : IT=>175
		when 'Italiano' then 175
		when 'Italian' then 175
		when 'Italiana' then 175
		when 'italiano' then 175
		when 'italiana' then 175
		-- FREETEXT REFINEMENTS : RO=>317
		when 'Romana' then 317
		when 'romana' then 317
		when 'ROMANA' then 317
		when 'Română' then 317
		when 'Romanian' then 317
		when 'Romeno' then 317
		-- FREETEXT REFINEMENTS : ES=>353
		when 'Spanish' then 353
		when 'Español' then 353
		when 'Castellano' then 353
		when 'Spagnolo' then 353
		when 'español / castellano' then 353
		when 'Spanish / Castilian' then 353
		when 'español' then 353
		when 'Español / Castellano' then 353
		when 'Spanisch' then 353
		when 'ESPAÑOL' then 353
		when 'Español / castellano' then 353
		when 'castellano' then 353
		when 'spanish' then 353
		when 'Spaans' then 353
		when 'španščina' then 353
		when 'Spaniolă' then 353
		-- FREETEXT REFINEMENTS : EL=>142
		when 'Ελληνικά' then 142
		when 'Greek' then 142
		when 'Ελληνική' then 142
		when 'ΕΛΛΗΝΙΚΑ' then 142
		when 'Eλληνικά' then 142
		-- FREETEXT REFINEMENTS : FR=>116
		when 'Français' then 116
		when 'français' then 116
		when 'French' then 116
		when 'francese' then 116
		when 'Francês' then 116
		when 'Franceza' then 116
		when 'Frances' then 116
		when 'Francés' then 116
		when 'Français' then 116
		when 'frances' then 116
		when 'Françês' then 116
		when 'Francese' then 116
		when 'FRANCESE' then 116
		when 'franceza' then 116
		when 'français' then 116
		-- FREETEXT REFINEMENTS : FR=>128
		when 'Deutsch' then 128
		when 'German' then 128
		when 'aleman' then 128
		when 'Alemán' then 128
		when 'tedesco' then 128
		when 'Germana' then 128
		-- FREETEXT REFINEMENTS : HR=>331
		when 'Hrvatski' then 331
		when 'hrvatski' then 331
		when 'Croatian' then 331
		-- FREETEXT REFINEMENTS : TR=>391
		when 'Turkish' then 391
		-- FREETEXT REFINEMENTS : FI=>114
		when 'Suomi' then 114
		when 'Suomi' then 114
		when 'Finnish' then 114
		-- FREETEXT REFINEMENTS : BG=>56
		when 'Bulgarian' then 56
		when 'Български' then 56
		-- FREETEXT REFINEMENTS : SL=>339
		when 'Slovenščina' then 339
		when 'slovenščina' then 339
		-- FREETEXT REFINEMENTS : PL=>306
		when 'Polski' then 306
		-- FREETEXT REFINEMENTS : SK=>338
		when 'Slovenský' then 338
		-- FREETEXT REFINEMENTS : TA=>366
		when 'Tamil' then 366
		-- FREETEXT REFINEMENTS : AM=>15
		when 'Amharic' then 15
		-- FREETEXT REFINEMENTS : CA=>61
		when 'Catalan' then 61
		when 'catalán' then 61
		when 'Catalán' then 61
		when 'catalan' then 61
		when 'Català' then 61
		when 'Valenciano' then 61
		-- FREETEXT REFINEMENTS : TE=>368
		when 'Telugu' then 368
		-- FREETEXT REFINEMENTS : TH=>374
		when 'Thai' then 374
		-- FREETEXT REFINEMENTS : KN=>186
		when 'Kannada' then 186
		-- FREETEXT REFINEMENTS : ML=>239
		when 'Malayalam' then 239
		-- FREETEXT REFINEMENTS : PA=>298
		when 'Punjabi' then 298
		-- FREETEXT REFINEMENTS : HU=>160
		when 'magyar' then 160
		when 'Hungarian' then 160
		when 'Magyar' then 160
		-- FREETEXT REFINEMENTS : KK=>190
		when 'Kazakh' then 190
		-- FREETEXT REFINEMENTS : UK=>398
		when 'Ukrainian' then 398
		-- FREETEXT REFINEMENTS : TL=>373
		when 'Tagalog' then 373
		-- FREETEXT REFINEMENTS : TG=>372
		when 'Tajik' then 372
		-- FREETEXT REFINEMENTS : GU=>145
		when 'Gujarati' then 145
		-- FREETEXT REFINEMENTS : YO=>418
		when 'Yoruba' then 418
		-- FREETEXT REFINEMENTS : MR=>242
		when 'Marathi' then 242
		-- FREETEXT REFINEMENTS : CS=>83
		when 'Čeština' then 83
		-- FREETEXT REFINEMENTS : MT=>252
		when 'Maltese' then 252
		-- FREETEXT REFINEMENTS : RU=>320
		when 'Russian' then 320
		-- FREETEXT REFINEMENTS : EU=>38
		when 'Euskera' then 38
	end as [language]
	,SML.language as [language_label]
	,'m'				as [language_type]
	,NULL				as [listening_level]
	,NULL				as [reading_level]
	,NULL				as [spoken_interaction_level]
	,NULL				as [spoken_production_level]
	,NULL				as [writing_level]
from ewa_prod.dbo.stats_mother_language SML
UNION ALL
select 
	id
	,SFL.statsEntry_id					as [statsEntry_id]
	,case SFL.languageType					
		-- REGULAR REFINEMENTS 
		when 'bg' then 56 
		when 'cs' then 83 
		when 'da' then  85 
		when 'nl' then  96 
		when 'en' then  103 
		when 'et' then  106 
		when 'fi' then  114 
		when 'fr' then  116 
		when 'de' then  128 
		when 'ga' then  132 
		when 'el' then  142 
		when 'hu' then  160 
		when 'is' then  164 
		when 'it' then  175 
		when 'lv' then  218 
		when 'lt' then  222 
		when 'mk' then  233
		when 'mt' then  252 
		when 'no' then  279 
		when 'pl' then  306 
		when 'pt' then  308 
		when 'ro' then 317 
		when 'hr' then  331 
		when 'sk' then  338 
		when 'sl' then  339 
		when 'es' then  353 
		when 'sv' then  363 
		when 'tr' then  391 
	
		-- FREETEXT REFINEMENTS : EN=>103
		when 'Ingles' then 103
		when 'English' then 103
		when 'ingles' then 103
		when 'Engleza' then 103
		when 'inglese' then 103
		when 'Inglés' then 103
		when 'Inglês' then 103
		when 'Inglese' then 103
		when 'Αγγλικά' then 103
		when 'INGLESE' then 103
		when 'engleza' then 103
		when 'Anglais' then 103
		when 'INGLES' then 103
		when 'english' then 103
		when 'Englisch' then 103
		when 'ENGLEZA' then 103
		when 'ΑΓΓΛΙΚΑ' then 103
		-- FREETEXT REFINEMENTS : PT=>308
		when 'Português' then 308
		when 'Portuguesa' then 308
		when 'Portugues' then 308
		when 'Portuguese' then 308
		when 'Portugais' then 308
		when 'Portugês' then 308
		when 'Portugal' then 308
		when 'Língua Portuguesa' then 308
		when 'portuguesa' then 308
		when 'PORTUGUES' then 308
		when 'Portugués' then 308
		-- FREETEXT REFINEMENTS : IT=>175
		when 'Italiano' then 175
		when 'Italian' then 175
		when 'Italiana' then 175
		when 'italiano' then 175
		when 'italiana' then 175
		-- FREETEXT REFINEMENTS : RO=>317
		when 'Romana' then 317
		when 'romana' then 317
		when 'ROMANA' then 317
		when 'Română' then 317
		when 'Romanian' then 317
		when 'Romeno' then 317
		-- FREETEXT REFINEMENTS : ES=>353
		when 'Spanish' then 353
		when 'Español' then 353
		when 'Castellano' then 353
		when 'Spagnolo' then 353
		when 'español / castellano' then 353
		when 'Spanish / Castilian' then 353
		when 'español' then 353
		when 'Español / Castellano' then 353
		when 'Spanisch' then 353
		when 'ESPAÑOL' then 353
		when 'Español / castellano' then 353
		when 'castellano' then 353
		when 'spanish' then 353
		when 'Spaans' then 353
		when 'španščina' then 353
		when 'Spaniolă' then 353
		-- FREETEXT REFINEMENTS : EL=>142
		when 'Ελληνικά' then 142
		when 'Greek' then 142
		when 'Ελληνική' then 142
		when 'ΕΛΛΗΝΙΚΑ' then 142
		when 'Eλληνικά' then 142
		-- FREETEXT REFINEMENTS : FR=>116
		when 'Français' then 116
		when 'français' then 116
		when 'French' then 116
		when 'francese' then 116
		when 'Francês' then 116
		when 'Franceza' then 116
		when 'Frances' then 116
		when 'Francés' then 116
		when 'Français' then 116
		when 'frances' then 116
		when 'Françês' then 116
		when 'Francese' then 116
		when 'FRANCESE' then 116
		when 'franceza' then 116
		when 'français' then 116
		-- FREETEXT REFINEMENTS : FR=>128
		when 'Deutsch' then 128
		when 'German' then 128
		when 'aleman' then 128
		when 'Alemán' then 128
		when 'tedesco' then 128
		when 'Germana' then 128
		-- FREETEXT REFINEMENTS : HR=>331
		when 'Hrvatski' then 331
		when 'hrvatski' then 331
		when 'Croatian' then 331
		-- FREETEXT REFINEMENTS : TR=>391
		when 'Turkish' then 391
		-- FREETEXT REFINEMENTS : FI=>114
		when 'Suomi' then 114
		when 'Suomi' then 114
		when 'Finnish' then 114
		-- FREETEXT REFINEMENTS : BG=>56
		when 'Bulgarian' then 56
		when 'Български' then 56
		-- FREETEXT REFINEMENTS : SL=>339
		when 'Slovenščina' then 339
		when 'slovenščina' then 339
		-- FREETEXT REFINEMENTS : PL=>306
		when 'Polski' then 306
		-- FREETEXT REFINEMENTS : SK=>338
		when 'Slovenský' then 338
		-- FREETEXT REFINEMENTS : TA=>366
		when 'Tamil' then 366
		-- FREETEXT REFINEMENTS : AM=>15
		when 'Amharic' then 15
		-- FREETEXT REFINEMENTS : CA=>61
		when 'Catalan' then 61
		when 'catalán' then 61
		when 'Catalán' then 61
		when 'catalan' then 61
		when 'Català' then 61
		when 'Valenciano' then 61
		-- FREETEXT REFINEMENTS : TE=>368
		when 'Telugu' then 368
		-- FREETEXT REFINEMENTS : TH=>374
		when 'Thai' then 374
		-- FREETEXT REFINEMENTS : KN=>186
		when 'Kannada' then 186
		-- FREETEXT REFINEMENTS : ML=>239
		when 'Malayalam' then 239
		-- FREETEXT REFINEMENTS : PA=>298
		when 'Punjabi' then 298
		-- FREETEXT REFINEMENTS : HU=>160
		when 'magyar' then 160
		when 'Hungarian' then 160
		when 'Magyar' then 160
		-- FREETEXT REFINEMENTS : KK=>190
		when 'Kazakh' then 190
		-- FREETEXT REFINEMENTS : UK=>398
		when 'Ukrainian' then 398
		-- FREETEXT REFINEMENTS : TL=>373
		when 'Tagalog' then 373
		-- FREETEXT REFINEMENTS : TG=>372
		when 'Tajik' then 372
		-- FREETEXT REFINEMENTS : GU=>145
		when 'Gujarati' then 145
		-- FREETEXT REFINEMENTS : YO=>418
		when 'Yoruba' then 418
		-- FREETEXT REFINEMENTS : MR=>242
		when 'Marathi' then 242
		-- FREETEXT REFINEMENTS : CS=>83
		when 'Čeština' then 83
		-- FREETEXT REFINEMENTS : MT=>252
		when 'Maltese' then 252
		-- FREETEXT REFINEMENTS : RU=>320
		when 'Russian' then 320
		-- FREETEXT REFINEMENTS : EU=>38
		when 'Euskera' then 38
	end as [language]
	,SFL.languageType as [language_label] 
	,'f'								as [language_type]
	,lower([listeningLevel])			as [listening_level]
	,lower([readingLevel])				as [reading_level]
	,lower([spokenInteractionLevel])	as [spoken_interaction_level]
	,lower([spokenInteractionLevel])	as [spoken_production_level]
	,lower([writingLevel])				as [writing_level]
from ewa_prod.dbo.stats_foreign_languages SFL;


create view [dbo].[ewa_stats_nationality_to_europass_stat_nationality]
as
select	
	[id]
	,[statsEntry_id]
	,[nationality]
from ewa_prod.dbo.stats_nationality;


CREATE VIEW [dbo].[Statistics_export]
AS
SELECT     dbo.ewa_stats_entry.age, dbo.ewa_stats_entry.docLanguage, dbo.ewa_stats_entry.educationYears, dbo.ewa_stats_entry.gender, 
                      dbo.ewa_stats_entry.postalAddressCountry, dbo.ewa_stats_entry.workExperienceYears, dbo.ewa_stats_entry.documentType, 
                      dbo.ewa_stats_entry.fileFormat, dbo.ewa_stats_entry.creationDate, dbo.ewa_stats_entry.generatedBy, dbo.ewa_stats_entry.is_new, 
                      dbo.ewa_stats_entry.date_of_birth, dbo.ewa_stats_entry.headline_type, dbo.ewa_stats_entry.headline_description, 
                      dbo.ewa_stats_entry.skills_driving, dbo.stats_achievement.category, dbo.stats_details.telephone_types, dbo.stats_details.im_types, 
                      dbo.stats_details.type_of_files, dbo.stats_details.cumulative_size, dbo.stats_details.number_of_files, dbo.stats_education.period_from, 
                      dbo.stats_education.period_to, dbo.stats_education.duration, dbo.stats_education.qualification, dbo.stats_education.organisation_country, 
                      dbo.stats_education.qualification_level, dbo.stats_education.educational_field, dbo.stats_linguistic_certificate.title, 
                      dbo.stats_linguistic_certificate.issue_date, dbo.stats_linguistic_certificate.cefr_level, dbo.stats_foreign_languages.languageType, 
                      dbo.stats_foreign_languages.listeningLevel, dbo.stats_foreign_languages.readingLevel, dbo.stats_foreign_languages.spokenInteractionLevel, 
                      dbo.stats_foreign_languages.spokenProductionLevel, dbo.stats_foreign_languages.writingLevel, dbo.stats_mother_language.language, 
                      dbo.stats_nationality.nationality, dbo.stats_work_experience.period_from AS EXPR15, dbo.stats_work_experience.period_to AS EXPR16, 
                      dbo.stats_work_experience.duration AS EXPR17, dbo.stats_work_experience.position, dbo.stats_work_experience.employer_country, 
                      dbo.stats_work_experience.employer_sector
FROM         dbo.ewa_stats_entry INNER JOIN
                      dbo.stats_achievement ON dbo.ewa_stats_entry.id = dbo.stats_achievement.statsEntry_id INNER JOIN
                      dbo.stats_details ON dbo.ewa_stats_entry.id = dbo.stats_details.statsEntry_id INNER JOIN
                      dbo.stats_education ON dbo.ewa_stats_entry.id = dbo.stats_education.statsEntry_id INNER JOIN
                      dbo.stats_foreign_languages ON dbo.ewa_stats_entry.id = dbo.stats_foreign_languages.statsEntry_id INNER JOIN
                      dbo.stats_linguistic_certificate ON dbo.stats_foreign_languages.id = dbo.stats_linguistic_certificate.stats_language_id INNER JOIN
                      dbo.stats_mother_language ON dbo.ewa_stats_entry.id = dbo.stats_mother_language.statsEntry_id INNER JOIN
                      dbo.stats_nationality ON dbo.ewa_stats_entry.id = dbo.stats_nationality.statsEntry_id INNER JOIN
                      dbo.stats_work_experience ON dbo.ewa_stats_entry.id = dbo.stats_work_experience.statsEntry_id
WHERE     (YEAR(dbo.ewa_stats_entry.creationDate) = 2013) AND (MONTH(dbo.ewa_stats_entry.creationDate) = 11) AND (DAY(dbo.ewa_stats_entry.creationDate) 
                      = 25);
