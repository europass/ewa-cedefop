USE [europass_statistics]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		eworx
-- Create date: 09/11/2017
-- Description:	update tables that are used for 
-- monthly statistics reporting (cube_*)
-- with last month's data
-- =============================================
CREATE PROCEDURE [dbo].[update_cubes_with_last_months_data]
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

insert into cube_entry select * from v_cube_entry_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_nat select * from v_cube_entry_nat_ewx
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_country_vs_nationality select * from v_cube_entry_country_vs_nationality_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_mlang select * from v_cube_entry_mlang_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_flang select * from v_cube_entry_flang_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_flang_counter select * from v_cube_entry_flang_counter_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_generations select * from v_cube_entry_generations
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_generations_country select * from v_cube_entry_generations_country 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_generations_nat select * from v_cube_entry_generations_nat 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_gender select * from v_cube_entry_gender_ewx
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_langs select * from v_cube_entry_langs_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_nat_langs select * from v_cube_entry_nat_langs_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_nat_mlang select * from v_cube_entry_nat_mlang_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_nat_flang select * from v_cube_entry_nat_flang_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_age select * from v_cube_entry_age_ewx
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_workexp select * from v_cube_entry_workexp_ewx
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_docs select * from v_cube_entry_docs_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_docs_langs select * from v_cube_entry_docs_langs_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_flang_pivot select * from v_cube_entry_flang_pivot_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

	
insert into cube_entry_short select * from v_cube_entry_short_ewx 
	where year_no = YEAR(DATEADD(month, -1, GETDATE())) and month_no = MONTH(DATEADD(month, -1, GETDATE()));

END
GO
