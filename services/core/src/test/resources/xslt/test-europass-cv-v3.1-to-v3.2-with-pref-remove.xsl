<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    version="1.0" 
    xmlns="http://europass.cedefop.europa.eu/Europass" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    
    <xsl:output method="xml" encoding="utf-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="node()|@*">
        <xsl:choose>
            <xsl:when test="current()[name()='XSDVersion']">
                <xsl:copy>
                    <xsl:text>V3.2</xsl:text>
                </xsl:copy>
            </xsl:when>
            
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Identification']"></xsl:when>
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Identification.ContactInfo']"></xsl:when><!-- this print preff should never be found in the first place -->
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Identification.ContactInfo.Email']"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Identification.ContactInfo.Telephone')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Identification.ContactInfo.Website')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Identification.ContactInfo.InstantMessaging')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Identification.Demographics.Gender']"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Identification.Demographics.Nationality')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Identification.Photo']"></xsl:when>
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.ProfileSummary']"></xsl:when>
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Headline']"></xsl:when>
            
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.WorkExperience']"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.WorkExperience')][contains(@name, 'Position')]  "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.WorkExperience')][contains(@name, 'Activities')]  "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.WorkExperience')][contains(@name, 'Employer.Sector')]  "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.WorkExperience')][contains(@name, 'ReferenceTo')]  "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.WorkExperience')][substring(@name, string-length(@name))=']'] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.WorkExperience')][substring(@name, string-length(@name)-9)='].Employer'] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.WorkExperience')][contains(@name, 'Employer.ContactInfo.Website')]  "></xsl:when>
            
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Education']"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][substring(@name, string-length(@name))=']'] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][contains(@name, 'Title')] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][contains(@name, 'Activities')] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][substring(@name, string-length(@name)-13)='].Organisation'] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][contains(@name, 'Level')] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][contains(@name, 'Field')] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][contains(@name, 'ReferenceTo')] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Education')][contains(@name, 'Organisation.ContactInfo.Website')]  "></xsl:when>
            
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Skills']"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Linguistic.MotherTongue')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][@name = 'LearnerInfo.Skills.Linguistic.ForeignLanguage']"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Linguistic.ForeignLanguage')][substring(@name, string-length(@name)-12)='].Certificate'] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Linguistic.ForeignLanguage')][substring(@name, string-length(@name))=']'] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Linguistic.ForeignLanguage')][contains(@name, '.ReferenceTo')] "></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Linguistic.ForeignLanguage')][substring(@name, string-length(@name)-11)='].Experience'] "></xsl:when>
            
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Communication')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Organisational')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.JobRelated')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Computer')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Driving')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Skills.Other')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.Achievement')]"></xsl:when>
            <xsl:when test="current()[name()='Field'][starts-with(@name, 'LearnerInfo.ReferenceTo')]"></xsl:when>
            
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node()|@*"/>
                </xsl:copy>        
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="@xsi:schemaLocation">  
        <xsl:attribute name="xsi:schemaLocation">http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/xml/v3.2.0/EuropassSchema.xsd</xsl:attribute>
    </xsl:template>
    
    <!-- printing preferences attribute show, revert to always true -->
    <xsl:template match="@show">
        <xsl:attribute name="show">true</xsl:attribute>
    </xsl:template>
    
</xsl:stylesheet>