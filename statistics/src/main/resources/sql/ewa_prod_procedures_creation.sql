CREATE procedure [dbo].[extract_languages]
as
begin
	declare @datespec datetime, @datespecprev datetime
	select @datespec = dateadd(dd,0,convert(varchar(10),getdate(),121))
	select @datespecprev = dateadd(month, -1, @datespec)

	select 
	FML.[id]
	,FML.[statsEntry_id]
	,FML.[language]
	,cast(FML.[language_type] as varchar(50)) as [language_type]
	,cast(FML.[listening_level] as varchar(50)) as [listening_level]
	,cast(FML.[reading_level] as varchar(50)) as [reading_level]
	,cast(FML.[spoken_interaction_level] as varchar(50)) as  [spoken_interaction_level]
	,cast(FML.[spoken_production_level] as varchar(50)) as [spoken_production_level]
	,cast(FML.[writing_level] as varchar(50)) as [writing_level]
	from ewa_prod.dbo.ewa_stats_FandM_language_to_europass_stat_language FML
	join ewa_prod.dbo.ewa_stats_entry SE on FML.statsEntry_id = SE.id
	where SE.creationDate >= @datespecprev and SE.creationDate < @datespec
end 


CREATE procedure [dbo].[extract_languages_monthbased]
as
begin
	declare @datespec datetime, @datespecnext datetime ; 
	select @datespec = datespec from specificDateTime  ;
	select @datespecnext = dateadd(month, 1, @datespec) ;

	select 
	FML.[id]
	,FML.[statsEntry_id]
	,FML.[language]
	,FML.[language_label]
	,cast(FML.[language_type] as varchar(50)) as [language_type]
	,cast(FML.[listening_level] as varchar(50)) as [listening_level]
	,cast(FML.[reading_level] as varchar(50)) as [reading_level]
	,cast(FML.[spoken_interaction_level] as varchar(50)) as  [spoken_interaction_level]
	,cast(FML.[spoken_production_level] as varchar(50)) as [spoken_production_level]
	,cast(FML.[writing_level] as varchar(50)) as [writing_level]
	from ewa_prod.dbo.ewa_stats_FandM_language_to_europass_stat_language FML
	join ewa_prod.dbo.ewa_stats_entry SE on FML.statsEntry_id = SE.id
	where SE.creationDate>= @datespec and SE.creationDate < @datespecnext ;
end


CREATE procedure [dbo].[extract_nationality]
as
begin
	declare @datespec datetime, @datespecprev datetime
	select @datespec = dateadd(dd,0,convert(varchar(10),getdate(),121))
	select @datespecprev = dateadd(month, -1, @datespec)

	select 
	SN.[id]
	,SN.[statsEntry_id]
	,case cast(SN.[nationality] as nvarchar(75)) 
	when 'UK' then 'GB' when 'uk' then 'GB' when 'EL' then 'GR' when 'el' then 'GR' else cast(SN.[nationality] as nvarchar(75)) 
	end as [nationality]
	from ewa_prod.dbo.ewa_stats_nationality_to_europass_stat_nationality SN
	join ewa_prod.dbo.ewa_stats_entry SE on SN.statsEntry_id = SE.id
	where [creationDate] < @datespec and [creationDate] >= @datespecprev ;
end