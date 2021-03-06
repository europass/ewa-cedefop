USE ewa_prod;
GO

CREATE TABLE dbo.ewa_stats_entry(
	id numeric(19,0) IDENTITY NOT NULL,
	age numeric(19,0),
	creationDate datetime,
	date_of_birth datetime,
	documentType varchar(255),
	educationYears numeric(19,0),
	exportTo nvarchar(255),
	fileFormat varchar(255),
	gender char(1),
	generatedBy varchar(255),
	headline_description nvarchar(255),
	headline_type nvarchar(255),
	is_new tinyint,
	docLanguage varchar(255),
	postalAddressCountry nvarchar(255),
	related_entry_id numeric(19,0),
	skills_driving nvarchar(255),
	workExperienceYears numeric(19,0),
	emailHashCode numeric(19,0),
        primary key (id)
);

CREATE TABLE dbo.stats_achievement(
	id numeric(19,0) IDENTITY NOT NULL,
	statsEntry_id numeric(19,0) NOT NULL,
	category nvarchar(255) NULL,
        primary key (id)
);

CREATE TABLE dbo.stats_details(
	id numeric(19,0) IDENTITY NOT NULL,
	cumulative_size decimal(19, 0) NULL,
	im_types nvarchar(255) NULL,
	number_of_files numeric(19,0) NULL,
	telephone_types nvarchar(255) NULL,
	type_of_files nvarchar(255) NULL,
	statsEntry_id numeric(19,0) NULL, 
        primary key (id)
);

CREATE TABLE dbo.stats_education(
	id numeric(19,0) IDENTITY NOT NULL,
	statsEntry_id numeric(19,0) NOT NULL,
	educational_field nvarchar(255) NULL,
	organisation_country nvarchar(255) NULL,
	qualification nvarchar(255) NULL,
	qualification_level nvarchar(255) NULL,
	duration numeric(19,0) NULL,
	period_from datetime NULL,
	period_to datetime NULL, 
        primary key (id)
);

CREATE TABLE dbo.stats_foreign_languages(
	id numeric(19,0) IDENTITY NOT NULL,
	languageType nvarchar(255) NULL,
	listeningLevel nchar(10) NULL,
	readingLevel nchar(10) NULL,
	spokenInteractionLevel nchar(10) NULL,
	spokenProductionLevel nchar(10) NULL,
	writingLevel nchar(10) NULL,
	statsEntry_id numeric(19,0) NULL, 
        primary key (id)
);

CREATE TABLE dbo.stats_linguistic_certificate(
	id numeric(19,0) IDENTITY NOT NULL,
	stats_language_id numeric(19,0) NOT NULL,
	cefr_level nvarchar(255) NULL,
	issue_date datetime NULL,
	title nvarchar(255) NULL, 
        primary key(id)
);

CREATE TABLE dbo.stats_mother_language(
	id numeric(19,0) IDENTITY NOT NULL,
	statsEntry_id numeric(19,0) NOT NULL,
	language nvarchar(255) NULL,
        primary key (id)
);

CREATE TABLE dbo.stats_nationality(
	id numeric(19,0) IDENTITY NOT NULL,
	statsEntry_id numeric(19,0) NOT NULL,
	nationality nvarchar(255) NULL,
        primary key (id)
);

CREATE TABLE dbo.stats_work_experience(
	id numeric(19,0) IDENTITY NOT NULL,
	statsEntry_id numeric(19,0) NOT NULL,
	employer_country nvarchar(255) NULL,
	employer_sector nvarchar(255) NULL,
	position nvarchar(255) NULL,
	duration numeric(19,0) NULL,
	period_from datetime NULL,
	period_to datetime NULL, 
        primary key (id)
);
GO

ALTER TABLE dbo.stats_achievement ADD CONSTRAINT [FK3EFCBB8F205889A2] 
FOREIGN KEY(statsEntry_id) REFERENCES dbo.ewa_stats_entry (id);
GO

ALTER TABLE dbo.stats_details ADD CONSTRAINT [FK41704EA2205889A2] 
FOREIGN KEY(statsEntry_id) REFERENCES dbo.ewa_stats_entry (id);

ALTER TABLE dbo.stats_education ADD CONSTRAINT [FKADD2C08205889A2] 
FOREIGN KEY(statsEntry_id) REFERENCES dbo.ewa_stats_entry (id);

ALTER TABLE dbo.stats_foreign_languages ADD CONSTRAINT [FK5437BED0205889A2] 
FOREIGN KEY(statsEntry_id) REFERENCES dbo.ewa_stats_entry (id);

ALTER TABLE dbo.stats_linguistic_certificate ADD CONSTRAINT [FKD771433DF131C399] 
FOREIGN KEY([stats_language_id]) REFERENCES dbo.stats_foreign_languages (id);

ALTER TABLE dbo.stats_mother_language ADD CONSTRAINT [FK206C71B4205889A2] 
FOREIGN KEY(statsEntry_id) REFERENCES dbo.ewa_stats_entry (id);

ALTER TABLE dbo.stats_nationality ADD CONSTRAINT [FKDC5AEA1C205889A2] 
FOREIGN KEY(statsEntry_id) REFERENCES dbo.ewa_stats_entry (id);

ALTER TABLE dbo.stats_work_experience ADD CONSTRAINT [FKECAB7FB8205889A2] 
FOREIGN KEY(statsEntry_id) REFERENCES dbo.ewa_stats_entry (id);
GO

