<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
	version="1.0" 
	xmlns="http://europass.cedefop.europa.eu/Europass" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:europass_prev="http://europass.cedefop.europa.eu/Europass/V2.0"
    xmlns:europass="http://europass.cedefop.europa.eu/Europass/V3.0">
    
    <xsl:output method="xml" encoding="utf-8" indent="yes"/>
    <xsl:template match="/europass_prev:learnerinfo">
        
        <xsl:element name="SkillsPassport">
            
            <xsl:attribute name="xsi:schemaLocation">http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.2.xsd</xsl:attribute>
            <xsl:attribute name="locale">
                <xsl:value-of select="substring-before(@locale, '_')"/>
            </xsl:attribute>

            <xsl:element name="DocumentInfo">
                <xsl:call-template name="DocumentInfo"/>
            </xsl:element>

            <xsl:element name="PrintingPreferences">
                <xsl:element name="Document">
                    <xsl:attribute name="type">
                        <xsl:variable name="documentType">
                            <xsl:choose>
                                <xsl:when test="languagelist//diplomalist or languagelist//experiencelist">
                                    <xsl:value-of select="'ELP'"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="'ECV'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:value-of select="$documentType"/>
                    </xsl:attribute>
                    <xsl:for-each select="prefs/field">
                        <xsl:call-template name="Field"/>
                    </xsl:for-each>
                </xsl:element>
 			</xsl:element>
 			
            <xsl:element name="LearnerInfo">
                <xsl:call-template name="Identification"/>
                
                <xsl:call-template name="Headline"/>
                
                <xsl:call-template name="WorkExperience"/>
                
                <xsl:call-template name="Education"/>
                
                <xsl:call-template name="Skills"/>
              
                <xsl:call-template name="AdditionalInfo"/>
               
            </xsl:element>

        </xsl:element>
    </xsl:template>

    <!-- Document Information-->
    <xsl:template name="DocumentInfo">
    	<xsl:element name="DocumentType">
    	    <xsl:variable name="documentType">
    	        <xsl:choose>
    	            <xsl:when test="languagelist//diplomalist or languagelist//experiencelist">
    	                <xsl:value-of select="'ELP'"/>
    	            </xsl:when>
    	            <xsl:otherwise>
    	                <xsl:value-of select="'ECV'"/>
    	            </xsl:otherwise>
    	        </xsl:choose>
    	    </xsl:variable>
    	    <xsl:value-of select="$documentType"/>
        </xsl:element>
        <xsl:element name="CreationDate">
        	<xsl:choose>
        		<xsl:when test="(docinfo/issuedate!='null' and string(docinfo/issuedate))">
        		 <xsl:value-of
                            select="substring-before(docinfo/issuedate,'-')"
                 />-<xsl:value-of
                            select="substring-before(substring-after(docinfo/issuedate,'-'),'-')"
                 />-<xsl:value-of
                            select="substring-before(substring-after(substring-after(docinfo/issuedate,'-'),'-'), 'T')"
                 />T00:00:00Z</xsl:when>
        		<xsl:otherwise>2012-12-01T00:00:00Z</xsl:otherwise>
        	</xsl:choose>
        </xsl:element>
        <xsl:element name="LastUpdateDate">
            <xsl:choose>
        		<xsl:when test="(docinfo/issuedate!='null' and string(docinfo/issuedate))">
        		 <xsl:value-of
                            select="substring-before(docinfo/issuedate,'-')"
                 />-<xsl:value-of
                            select="substring-before(substring-after(docinfo/issuedate,'-'),'-')"
                 />-<xsl:value-of
                            select="substring-before(substring-after(substring-after(docinfo/issuedate,'-'),'-'), 'T')"
                 />T00:00:00Z</xsl:when>
        		<xsl:otherwise>2012-12-01T00:00:00Z</xsl:otherwise>
        	</xsl:choose>
        </xsl:element>
        <xsl:element name="XSDVersion">
            <xsl:value-of select="'V3.0'"/>
        </xsl:element>
        <xsl:element name="Generator">
            <xsl:value-of select="'EWA'"/>
        </xsl:element>
        <xsl:element name="Comment">
            <xsl:value-of select="docinfo/comment"/>
        </xsl:element>
    </xsl:template>

    <!-- Printing Preferences Fields -->

    <xsl:template name="Field">
        
        <xsl:choose>
            
            
            <!-- Order of Sections -->
            <xsl:when test="@name='step3List' and @before='step4List' ">
                <xsl:element name="Field">
                    <xsl:attribute name="name">LearnerInfo</xsl:attribute>
                    <xsl:attribute name="order">Identification Headline WorkExperience Education Skills Achievement ReferenceTo</xsl:attribute>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@name='step4List' and @before='step3List' ">
                <xsl:element name="Field">
                    <xsl:attribute name="name">LearnerInfo</xsl:attribute>
                    <xsl:attribute name="order">Identification Headline Education WorkExperience Skills Achievement ReferenceTo</xsl:attribute>
                </xsl:element>
            </xsl:when>
            
            <xsl:when test="substring-after(@name,'step1')!=''">
                <xsl:choose>
                	<xsl:when test="@name='step1.lastName'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name">
                                <xsl:value-of
                                    select="'LearnerInfo.Identification.PersonName'"/>
                            </xsl:attribute>
                            <xsl:attribute name="show">true</xsl:attribute>
                            <xsl:attribute name="order">Surname FirstName</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    
                    <xsl:when test="@name='step1.firstName'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name">
                                <xsl:value-of
                                    select="'LearnerInfo.Identification.PersonName'"/>
                            </xsl:attribute>
                            <xsl:attribute name="show">true</xsl:attribute>
                            <xsl:attribute name="order">FirstName Surname</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    
                    <xsl:when test="@name='step1.addressInfo'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name">
                                <xsl:value-of
                                    select="'LearnerInfo.Identification.ContactInfo.Address'"/>
                            </xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                            <xsl:attribute name="format">s, z m (c)</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                   
                    <xsl:when test="@name='step1.telephone'">   
                       <xsl:element name="Field">
                            <xsl:attribute name="name">LearnerInfo.Identification.ContactInfo.Telephone</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>  
                        <xsl:element name="Field">
                            <xsl:attribute name="name">LearnerInfo.Identification.ContactInfo.Telephone[0]</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>             
                    </xsl:when>

                    <!--Hard to handle @name='step1.mobile' and @name='step1.fax'-->
                    <xsl:when test="@name='step1.email'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Identification.ContactInfo.Email</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                        
                    </xsl:when>
                    <xsl:when test="@name='step1.birthDate'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Identification.Demographics.Birthdate</xsl:attribute>
                            <xsl:attribute name="show"><xsl:value-of select="@keep"/></xsl:attribute>
                            <xsl:attribute name="format">
                                <xsl:choose>
                                    <xsl:when test="@format != '' and 
                                        ( contains(@format, 'text/long') or 
                                        contains(@format,'text/short') or 
                                        contains(@format, 'numeric/long') or 
                                        contains(@format,'numeric/medium') or 
                                        contains(@format,'numeric/short') ) ">
                                        <xsl:choose>
                                            <xsl:when test="starts-with(@format,'/')"><xsl:value-of select="substring-after(@format,'/')"/></xsl:when>
                                            <xsl:otherwise><xsl:value-of select="@format"/></xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:otherwise>text/short</xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@name='step1.gender'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Identification.Demographics.Gender</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:choose>
                                    <xsl:when test="../../identification/demographics/gender = 'NA'">false</xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="@keep"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@name='step1.nationality'">
                    	 <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Identification.Demographics.Nationality</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Identification.Demographics.Nationality[0]</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    
                    <xsl:when test="@name='step1.photo'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Identification.Photo</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    
                    <xsl:when test="@name='step1.application.label'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Headline</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    
                </xsl:choose>
            </xsl:when>
            <!--  WorkExperience -->
            <xsl:when test="starts-with(@name, 'step3List[' )">
                
                <xsl:choose>
                    <!-- Period -->
                    <xsl:when test="substring-after(@name, ']') = '.period' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.WorkExperience[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Period</xsl:attribute>
                            <xsl:attribute name="show">true</xsl:attribute>
                            <xsl:attribute name="format">
                                <xsl:choose>
                                    <xsl:when test="@format != '' and 
                                        ( contains(@format, 'text/long') or 
                                        contains(@format,'text/short') or 
                                        contains(@format, 'numeric/long') or 
                                        contains(@format,'numeric/medium') or 
                                        contains(@format,'numeric/short') ) ">
                                        <xsl:choose>
                                            <xsl:when test="starts-with(@format,'/')"><xsl:value-of select="substring-after(@format,'/')"/></xsl:when>
                                            <xsl:otherwise><xsl:value-of select="@format"/></xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:otherwise>text/short</xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Title -->
                    <xsl:when test="substring-after(@name, ']') = '.position.label' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.WorkExperience[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Position</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Skills -->
                    <xsl:when test="substring-after(@name, ']') = '.activities' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.WorkExperience[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Activities</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Organisation -->
                    <xsl:when test="substring-after(@name, ']') = '.employer' or substring-after(@name, ']') = '.company.name' or substring-after(@name, ']') = '.company.addressInfo'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.WorkExperience[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Employer</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.WorkExperience[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Employer.ContactInfo.Address</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                            <xsl:attribute name="format">s, z m (c)</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Sector -->
                    <xsl:when test="substring-after(@name, ']') = '.sector.label' or substring-after(@name, ']') = '.company.sector.label' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.WorkExperience[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Employer.Sector</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
                
            </xsl:when>
            <!--  Education -->
            <xsl:when test="starts-with(@name, 'step4List[' )">
                
                <xsl:choose>
                    <!-- Period -->
                    <xsl:when test="substring-after(@name, ']') = '.period' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Education[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Period</xsl:attribute>
                            <xsl:attribute name="show">true</xsl:attribute>
                            <xsl:attribute name="format">
                                <xsl:choose>
                                    <xsl:when test="@format != '' and 
                                        ( contains(@format, 'text/long') or 
                                        contains(@format,'text/short') or 
                                        contains(@format, 'numeric/long') or 
                                        contains(@format,'numeric/medium') or 
                                        contains(@format,'numeric/short') ) ">
                                        <xsl:choose>
                                            <xsl:when test="starts-with(@format,'/')"><xsl:value-of select="substring-after(@format,'/')"/></xsl:when>
                                            <xsl:otherwise><xsl:value-of select="@format"/></xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:otherwise>text/short</xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Title -->
                    <xsl:when test="substring-after(@name, ']') = '.title' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Education[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Title</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Skills -->
                    <xsl:when test="substring-after(@name, ']') = '.skills' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Education[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Activities</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Organisation -->
                    <xsl:when test="substring-after(@name, ']') = '.educationalOrg' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Education[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Organisation</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Education[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Organisation.ContactInfo.Address</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                            <xsl:attribute name="format">s, z m (c)</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Level -->
                    <xsl:when test="substring-after(@name, ']') = '.level.label' ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Education[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>].Level</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
                
            </xsl:when>
            
            <!-- Mother Tongues -->
            <xsl:when test="@name='step5.motherLanguages'">
                <xsl:element name="Field">
                    <xsl:attribute name="name"
                        >LearnerInfo.Skills.Linguistic.MotherTongue</xsl:attribute>
                    <xsl:attribute name="show">
                        <xsl:value-of select="@keep"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="Field">
                    <xsl:attribute name="name"
                        >LearnerInfo.Skills.Linguistic.MotherTongue[0]</xsl:attribute>
                    <xsl:attribute name="show">
                        <xsl:value-of select="@keep"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:when>
            
            <!-- Foreign Languages  -->
            <xsl:when test="@name='step5.foreignLanguageList'">
                <xsl:element name="Field">
                    <xsl:attribute name="name"
                        >LearnerInfo.Skills.Linguistic.ForeignLanguage</xsl:attribute>
                    <xsl:attribute name="show">
                        <xsl:value-of select="@keep"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:when>
            <xsl:when test="starts-with(@name, 'step5.foreignLanguageList[' )">
                <xsl:element name="Field">
                    <xsl:attribute name="name"
                        >LearnerInfo.Skills.Linguistic.ForeignLanguage[<xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>]</xsl:attribute>
                    <xsl:attribute name="show">
                        <xsl:value-of select="@keep"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:when>
            
            <!-- Rest Personal Skills -->
            <xsl:when test="substring-after(@name,'step6')!=''">
                <xsl:choose>
                    <xsl:when test="@name='step6.socialSkills'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Communication</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@name='step6.organisationalSkills'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Organisational</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@name='step6.technicalSkills'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.JobRelated</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@name='step6.computerSkills'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Computer</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@name='step6.drivingLicences'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Driving</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@name='step6.otherSkills'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Other</xsl:attribute>
                            <xsl:attribute name="show">
                                <xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            
            <!-- Additional information / Achievements -->
            <xsl:when test="@name='step7.additionalInfo'">
                <xsl:element name="Field">
                    <xsl:attribute name="name">LearnerInfo.Achievement</xsl:attribute>
                    <xsl:attribute name="show">
                        <xsl:value-of select="@keep"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="Field">
                    <xsl:attribute name="name">LearnerInfo.Achievement[0]</xsl:attribute>
                    <xsl:attribute name="show">
                        <xsl:value-of select="@keep"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:when>
        
            <!-- LP Related -->
            
            <!--  Person Name -->
            <xsl:when test="starts-with(@name, 'personal.' )">
                <xsl:choose>
                    <xsl:when test="@name='personal.lastName'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name">
                                <xsl:value-of
                                    select="'LearnerInfo.Identification.PersonName'"/>
                            </xsl:attribute>
                            <xsl:attribute name="show">true</xsl:attribute>
                            <xsl:attribute name="order">Surname FirstName</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    
                    <xsl:when test="@name='personal.firstName'">
                        <xsl:element name="Field">
                            <xsl:attribute name="name">
                                <xsl:value-of
                                    select="'LearnerInfo.Identification.PersonName'"/>
                            </xsl:attribute>
                            <xsl:attribute name="show">true</xsl:attribute>
                            <xsl:attribute name="order">FirstName Surname</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <!-- List of Foreign Languages from LP -->
            <xsl:when test="@name='foreignLanguageList'">
                <xsl:element name="Field">
                    <xsl:attribute name="name"
                        >LearnerInfo.Skills.Linguistic.ForeignLanguage</xsl:attribute>
                    <xsl:attribute name="show">
                        <xsl:value-of select="@keep"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:when>
            <xsl:when test="starts-with(@name, 'foreignLanguageList[' )">
                <xsl:variable name="foreignIdx">
                    <xsl:value-of select="substring-before( substring-after(@name,'[' ), ']' )"/>
                </xsl:variable>
                <xsl:variable name="foreignRest">
                    <xsl:value-of select="substring-after(@name,'].' )"/>
                </xsl:variable>
                <xsl:variable name="foreignRestIdx">
                    <xsl:value-of select="substring-before( substring-after($foreignRest,'[' ), ']' )"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="contains(@name, 'diplomaList') ">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Linguistic.ForeignLanguage[<xsl:value-of select="$foreignIdx"/>].Certificate[<xsl:value-of select="$foreignRestIdx"/>]
                            </xsl:attribute>
                            <xsl:attribute name="show">
                        		<xsl:value-of select="@keep"/>
                    		</xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <!-- Experience Period -->
                    <xsl:when test="(contains(@name, 'experienceList')) and (contains($foreignRest, '.period'))">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Linguistic.ForeignLanguage[<xsl:value-of select="$foreignIdx"/>].Experience[<xsl:value-of select="$foreignRestIdx"/>].Period
                            </xsl:attribute>
                            <xsl:attribute name="format">
                                <xsl:choose>
                                    <xsl:when test="@format != '' and 
                                        ( contains(@format, 'text/long') or 
                                        contains(@format,'text/short') or 
                                        contains(@format, 'numeric/long') or 
                                        contains(@format,'numeric/medium') or 
                                        contains(@format,'numeric/short') ) ">
                                        <xsl:choose>
                                            <xsl:when test="starts-with(@format,'/')"><xsl:value-of select="substring-after(@format,'/')"/></xsl:when>
                                            <xsl:otherwise><xsl:value-of select="@format"/></xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:otherwise>text/short</xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="(contains(@name, 'experienceList'))">
                        <xsl:element name="Field">
                            <xsl:attribute name="name"
                                >LearnerInfo.Skills.Linguistic.ForeignLanguage[<xsl:value-of select="$foreignIdx"/>].Experience[<xsl:value-of select="$foreignRestIdx"/>]
                            </xsl:attribute>
                            <xsl:attribute name="show">
                            	<xsl:value-of select="@keep"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
      
    </xsl:template>


    <xsl:template name="Identification">
        <xsl:element name="Identification">
            <xsl:element name="PersonName">
                <xsl:element name="FirstName">
                    <xsl:value-of select="identification/firstname"/>
                </xsl:element>
                <xsl:element name="Surname">
                    <xsl:value-of select="identification/lastname"/>
                </xsl:element>
            </xsl:element>

            <xsl:if test="identification/contactinfo !='' and identification/contactinfo/*">
                <xsl:call-template name="ContactInfo"/>
            </xsl:if>

            <xsl:if test="identification/demographics !='' and identification/demographics/*">
                <xsl:call-template name="Demographics"/>
            </xsl:if>

            <xsl:if test="identification/photo != 'null'">
                <xsl:element name="Photo">
                    <xsl:element name="MimeType">
                        <!-- JPEG is fixed value for version 2, the following if is redundant-->
                        <xsl:if test="identification/photo/@type = 'JPEG'">
                            <xsl:value-of select="'image/jpeg'"/>
                        </xsl:if>
                    </xsl:element>
                    <xsl:element name="Data">
                        <xsl:value-of select="identification/photo"/>
                    </xsl:element>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template name="ContactInfo">
        <xsl:element name="ContactInfo">
            <xsl:choose>
                <xsl:when test="identification/contactinfo/address !='' and identification/contactinfo/address/*">
                    <xsl:for-each select="identification/contactinfo/address">
                        <xsl:call-template name="Address"/>
                    </xsl:for-each>
                </xsl:when>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="identification/contactinfo/email !='' and string(identification/contactinfo/email)">
                    <xsl:element name="Email">
                        <xsl:element name="Contact">
                            <xsl:value-of select="identification/contactinfo/email"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:when>
            </xsl:choose>
            <xsl:choose>
                <xsl:when
                    test="( identification/contactinfo/telephone !='' and string(identification/contactinfo/telephone) ) or 
                          ( identification/contactinfo/fax !='' and string(identification/contactinfo/fax) ) or 
                          ( identification/contactinfo/mobile !='' and string(identification/contactinfo/mobile) )">
                    <xsl:element name="TelephoneList">
                        <xsl:choose>
                            <xsl:when test="identification/contactinfo/telephone !='' and string(identification/contactinfo/telephone)">
                                <xsl:element name="Telephone">
                                    <xsl:element name="Contact">
                                        <xsl:value-of select="identification/contactinfo/telephone"/>
                                    </xsl:element>
                                    <xsl:element name="Use">
                                        <xsl:element name="Code">home</xsl:element>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:when>
                        </xsl:choose>

                        <xsl:choose>
                            <xsl:when test="identification/contactinfo/fax !='' and string(identification/contactinfo/fax)">
                                <xsl:element name="Telephone">
                                    <xsl:element name="Contact">
                                        <xsl:value-of select="identification/contactinfo/fax"/>
                                    </xsl:element>
                                    <xsl:element name="Use">
                                        <xsl:element name="Label">fax</xsl:element>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:when>
                        </xsl:choose>

                        <xsl:choose>
                            <xsl:when test="identification/contactinfo/mobile !='' and string(identification/contactinfo/mobile)">
                                <xsl:element name="Telephone">
                                    <xsl:element name="Contact">
                                        <xsl:value-of select="identification/contactinfo/mobile"/>
                                    </xsl:element>
                                    <xsl:element name="Use">
                                        <xsl:element name="Code">mobile</xsl:element>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:when>
                        </xsl:choose>

                    </xsl:element>
                </xsl:when>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xsl:template name="Demographics">
        <xsl:element name="Demographics">
            <xsl:if test="identification/demographics/birthdate != 'null' 
            			and string(identification/demographics/birthdate)
            			and identification/demographics/birthdate != '--' 
                		and not(contains(identification/demographics/birthdate, '--')) ">
                <xsl:element name="Birthdate">
                    <xsl:attribute name="year">
                        <xsl:value-of
                            select="substring-before(identification/demographics/birthdate,'-')"/>
                    </xsl:attribute>
                    <xsl:attribute name="month">
                        <xsl:value-of select="'--'"/>
                        <xsl:value-of
                            select="substring-before(substring-after(identification/demographics/birthdate,'-'),'-')"
                        />
                    </xsl:attribute>
                    <xsl:attribute name="day">
                        <xsl:value-of select="'---'"/>
                        <xsl:value-of
                            select="substring-after(substring-after(identification/demographics/birthdate,'-'),'-')"
                        />
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
            <xsl:if test="identification/demographics/gender != 'null' and string(identification/demographics/gender) and identification/demographics/gender != 'NA'">
                <xsl:element name="Gender">
                    <xsl:element name="Code">
                        <xsl:value-of select="identification/demographics/gender"/>
                    </xsl:element>
                </xsl:element>
            </xsl:if>

            <xsl:if test="identification/demographics/nationality != 'null' and identification/demographics/nationality/*">
                <xsl:element name="NationalityList">
                    <xsl:for-each select="identification/demographics/nationality">
                        <xsl:call-template name="Nationality"/>
                    </xsl:for-each>
                </xsl:element>
            </xsl:if>
        </xsl:element>

    </xsl:template>

    <xsl:template name="Nationality">
        <xsl:if test="node() and ( string(code) or string(label) )"></xsl:if>
        <xsl:element name="Nationality">
            <xsl:if test="code != 'null' and string(code)">
                <xsl:element name="Code">
                    <xsl:value-of select="string(code)"/>
                    <xsl:choose>
                        <xsl:when test="code = 'GR'">EL</xsl:when>
                        <xsl:when test="code = 'GB'">UK</xsl:when>
                        <xsl:otherwise><xsl:value-of select="code"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
            </xsl:if>
            <xsl:element name="Label">
                <xsl:value-of select="label"/>
            </xsl:element>

        </xsl:element>
    </xsl:template>

    <xsl:template name="Headline">
        <xsl:if test="application != 'null' and application/* and ( string(application/code) or string(application/label) )">
            <xsl:element name="Headline">
                <xsl:element name="Type">
                    <xsl:element name="Code">preferred_job</xsl:element>
                    <xsl:element name="Label">Preferred Job</xsl:element>
                </xsl:element>
                <xsl:element name="Description">
                    <xsl:if test="application/code != '' and string(application/code)">
                        <xsl:element name="Code">
                            <xsl:value-of select="application/code"/>
                        </xsl:element>
                    </xsl:if>
                    <xsl:element name="Label">
                        <xsl:value-of select="application/label"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="WorkExperience">
        <xsl:if test="workexperiencelist != 'null' and workexperiencelist/*">
            <xsl:element name="WorkExperienceList">
                <xsl:for-each select="workexperiencelist/workexperience">
                    <xsl:if test="*">
                        <xsl:element name="WorkExperience">
                            <xsl:for-each select="period">
                                <xsl:call-template name="Period"/>
                            </xsl:for-each>
                            <!-- Position is mandatory -->
                            <xsl:element name="Position">
                                <xsl:if test="position/code != 'null' and string(position/code)">
                                    <xsl:element name="Code">
                                        <xsl:value-of select="position/code"/>
                                    </xsl:element>
                                </xsl:if>
                                <xsl:element name="Label">
                                    <xsl:value-of select="position/label"/>
                                </xsl:element>
                            </xsl:element>
                            
                            <xsl:if test="activities != 'null' and string(activities)">
                                <xsl:element name="Activities">
                                    <xsl:value-of select="activities"/>
                                </xsl:element>
                            </xsl:if>
                            
                            <xsl:if test="employer != 'null' and employer/*">
                                <xsl:call-template name="Employer"/>
                            </xsl:if>
                            
                        </xsl:element>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>

        </xsl:if>
    </xsl:template>

    <xsl:template name="Education">
    	<xsl:if test="educationlist != 'null' and educationlist/*">
           <xsl:element name="EducationList">
               <xsl:for-each select="educationlist/education">
                   <xsl:if test="*">
                       <xsl:element name="Education">
                           <xsl:for-each select="period">
                               <xsl:call-template name="Period"/>
                           </xsl:for-each>
                           <!-- Title is mandatory -->
                           <xsl:element name="Title">
                               <xsl:value-of select="title"/>
                           </xsl:element>
                           
                           <xsl:if test="skills != 'null' and string(skills)">
                               <xsl:element name="Activities">
                                   <xsl:value-of select="skills"/>
                               </xsl:element>
                           </xsl:if>
                           
                           <xsl:if test="organisation != 'null' and organisation/*">
                               <xsl:element name="Organisation">
                                   <!-- Name is mandatory -->
                                   <xsl:element name="Name">
                                       <xsl:value-of select="organisation/name"/>
                                   </xsl:element>
                                   <xsl:if test="organisation/address != 'null' and organisation/address/*">
                                       <xsl:element name="ContactInfo">
                                           <xsl:for-each select="organisation/address">
                                               <xsl:call-template name="Address"/>
                                           </xsl:for-each>
                                       </xsl:element>
                                   </xsl:if>
                               </xsl:element>
                               
                           </xsl:if>
                           
                           <!-- We cannot establish a correspondence between ISCED and EQF. Therefore we will only transfer the Label of the Educational Field. -->
                           <xsl:if test="level != 'null' and level/* and level/label != 'null' and string(level/label)">
                               <xsl:element name="Level">
                                   <xsl:element name="Label">
                                       <xsl:value-of select="level/label"/>
                                   </xsl:element>
                               </xsl:element>
                           </xsl:if>
                           
                           <xsl:if test="educationalfield != 'null' and educationalfield/* and (string(educationalfield/code) or string(educationalfield/label) )">
                               <xsl:element name="Field">
                                   <xsl:if test="educationalfield/code != 'null' and string(educationalfield/code)">
                                       <xsl:element name="Code">
                                           <xsl:value-of select="educationalfield/code"/>
                                       </xsl:element>
                                   </xsl:if>
                                   <xsl:if test="educationalfield/label != 'null' and string(educationalfield/label)">
                                       <xsl:element name="Label">
                                           <xsl:value-of select="educationalfield/label"/>
                                       </xsl:element>
                                   </xsl:if>
                               </xsl:element>
                           </xsl:if>
                       </xsl:element>
                   </xsl:if>
               </xsl:for-each>
           </xsl:element>
       </xsl:if>
    </xsl:template>

    <xsl:template name="NativeLinguisticSkill">
        <xsl:if test="@xsi:type = 'europass:mother' and *" xml:base="http://www.w3.org/2001/XMLSchema-instance">
            <xsl:element name="MotherTongue">
                <xsl:call-template name="Language"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <xsl:template name="ForeignLinguisticSkill">
        <xsl:if test="@xsi:type = 'europass:foreign' and *" xml:base="http://www.w3.org/2001/XMLSchema-instance">
            <xsl:element name="ForeignLanguage">
                <xsl:call-template name="Language"/>
                <xsl:call-template name="CEFRLevel"/>
                <xsl:call-template name="LinguisticCertificate"/>
                <xsl:call-template name="LinguisticExperience"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="Skills">
    	<xsl:if test="languagelist != 'null' or skilllist != 'null'">
           <xsl:element name="Skills">
               <xsl:if test="languagelist != 'null'">
                  <xsl:element name="Linguistic">
                       <!-- List of Mother Languages -->
                       <xsl:if test="count(languagelist/language[@xsi:type = 'europass:mother']) > 0" xml:base="http://www.w3.org/2001/XMLSchema-instance">
                           <xsl:element name="MotherTongueList">
                               <xsl:for-each select="languagelist/language">
                                   <xsl:call-template name="NativeLinguisticSkill"/>
                               </xsl:for-each>
                           </xsl:element>
                       </xsl:if> 
                       <!-- List of Foreign Languages -->
                      <xsl:if test="count(languagelist/language[@xsi:type = 'europass:foreign']) > 0" xml:base="http://www.w3.org/2001/XMLSchema-instance">
                           <xsl:element name="ForeignLanguageList">
                               <xsl:for-each select="languagelist/language">
                                   <xsl:call-template name="ForeignLinguisticSkill"/>
                               </xsl:for-each>
                           </xsl:element>
                       </xsl:if>
                   </xsl:element>
               </xsl:if>
               <xsl:if test="skilllist != 'null'">
                  <xsl:for-each select="skilllist/skill">

                      <xsl:if test="@type= 'social' and string(.)">
                           <xsl:element name="Communication">
                               <xsl:element name="Description">
                                   <xsl:value-of select="."/>
                               </xsl:element>
                           </xsl:element>
                      </xsl:if>
                      <xsl:if test="@type= 'organisational' and string(.)">
                           <xsl:element name="Organisational">
                               <xsl:element name="Description">
                                   <xsl:value-of select="."/>
                               </xsl:element>
                           </xsl:element>
                       </xsl:if>
                      <xsl:if test="@type= 'technical' and string(.)">
                           <xsl:element name="JobRelated">
                               <xsl:element name="Description">
                                   <xsl:value-of select="."/>
                               </xsl:element>
                           </xsl:element>
                       </xsl:if>
                      <xsl:if test="@type= 'computer' and string(.)">
                           <xsl:element name="Computer">
                               <xsl:element name="Description">
                                   <xsl:value-of select="."/>
                               </xsl:element>
                           </xsl:element>
                       </xsl:if>
                   </xsl:for-each>
                  
                  
                  <xsl:for-each select="skilllist/structured-skill">
                      <xsl:if test="@xsi:type='europass:driving' and *" xml:base="http://www.w3.org/2001/XMLSchema-instance">
                        <xsl:element name="Driving">
                           <xsl:element name="Description">
                            <xsl:for-each select="./drivinglicence">
                                <xsl:element name="Licence">
                                    <xsl:value-of select="."/>
                                </xsl:element>
                            </xsl:for-each>
                           </xsl:element>
                        </xsl:element>
                      </xsl:if>
                   </xsl:for-each>
                   
                   
                   <!-- Other should come last, otherwise the xml will not be valid-->
                   <xsl:if test="(skilllist/skill[@type='other'] and string(skilllist/skill[@type='other'])) 
                       or (skilllist/skill[@type='artistic'] and string(skilllist/skill[@type='artistic']) )" >
                       <xsl:element name="Other">
                           <xsl:element name="Description">
                               <xsl:if test="skilllist/skill[@type='other'] and string(skilllist/skill[@type='other'])" >
                                   <xsl:value-of select="skilllist/skill[@type='other']"/>
                               </xsl:if>
                               <xsl:if test="(skilllist/skill[@type='other'] and string(skilllist/skill[@type='other'])) 
                                   and (skilllist/skill[@type='artistic'] and string(skilllist/skill[@type='artistic']) )">
                                   <xsl:text> </xsl:text>
                               </xsl:if>
                               <xsl:if test="skilllist/skill[@type='artistic'] and string(skilllist/skill[@type='artistic'])" >
                                   <xsl:value-of select="skilllist/skill[@type='artistic']"/>
                               </xsl:if>
                           </xsl:element>
                       </xsl:element>
                   </xsl:if>
               </xsl:if>
           </xsl:element>
       </xsl:if>
    </xsl:template>
    
    <xsl:template name="AdditionalInfo">
    	<xsl:if test="misclist != 'null'">
            <xsl:element name="AchievementList">
            <xsl:for-each select="misclist/misc">

                <xsl:element name="Achievement">
                    <xsl:element name="Title">
                        <xsl:element name="Label">
                            <xsl:text> </xsl:text>
                        </xsl:element>
                    </xsl:element>
                    <xsl:element name="Description">
                        <xsl:value-of select="."/>
                       </xsl:element>
                   </xsl:element>
               </xsl:for-each>
           </xsl:element>
       </xsl:if>
    </xsl:template>
    
    <xsl:template name="Period">
        <xsl:element name="Period">
            <xsl:element name="From">
                <xsl:attribute name="year">
                    <xsl:value-of select="normalize-space(./from/year)"/>
                </xsl:attribute>
                <xsl:if test="./from/month != 'null' and string(./from/month)">
                    <xsl:attribute name="month">
                        <xsl:value-of select="normalize-space(./from/month)"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="./from/day != 'null' and string(./from/day)">
                    <xsl:attribute name="day">
                        <xsl:value-of select="normalize-space(./from/day)"/>
                    </xsl:attribute>
                </xsl:if>
            </xsl:element>
            
            <xsl:if test="./to != 'null' and ./to/year != 'null'">
                <xsl:element name="To">      
                    <xsl:attribute name="year">
                        <xsl:value-of select="normalize-space(./to/year)"/>
                    </xsl:attribute>
                    <xsl:if test="./to/month != 'null' and string(./to/month)">
                        <xsl:attribute name="month">
                            <xsl:value-of select="normalize-space(./to/month)"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:if test="./to/day != 'null' and string(./to/day)">
                        <xsl:attribute name="day">
                            <xsl:value-of select="normalize-space(./to/day)"/>
                        </xsl:attribute>
                    </xsl:if>
                </xsl:element>
            </xsl:if>
        </xsl:element>

    </xsl:template>

    <xsl:template name="Employer">

        <xsl:element name="Employer">
            <!-- Name is mandatory -->
            <xsl:element name="Name">
                <xsl:value-of select="employer/name"/>
            </xsl:element>

            <xsl:if test="employer/address != 'null' and employer/address/*">
                <xsl:element name="ContactInfo">
                    <xsl:for-each select="employer/address">
                        <xsl:call-template name="Address"/>
                    </xsl:for-each>
                </xsl:element>
            </xsl:if>

            <xsl:if test="employer/sector != 'null' and employer/sector/*">
                <xsl:element name="Sector">
                    <xsl:for-each select="employer/sector">
                        <xsl:call-template name="LabelType"/>
                    </xsl:for-each>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template name="Address">
        <xsl:element name="Address">
            <xsl:element name="Contact">
                <xsl:choose>
                    <xsl:when test="./addressLine !='' and string(./addressLine)">
                        <xsl:element name="AddressLine">
                            <xsl:value-of select="./addressLine"/>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="./postalCode !='' and string(./postalCode)">
                        <xsl:element name="PostalCode">
                            <xsl:value-of select="./postalCode"/>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="./municipality !='' and string(./municipality)">
                        <xsl:element name="Municipality">
                            <xsl:value-of select="./municipality"/>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="./country !='' and ./country/*">
                        <xsl:element name="Country">
                            <xsl:for-each select="./country">
                                <xsl:call-template name="CountryLabelType"/>
                            </xsl:for-each>
                        </xsl:element>

                    </xsl:when>
                </xsl:choose>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template name="LabelType">
        <xsl:if test="./code !='' and string(./code)">
            <xsl:element name="Code">
                <xsl:value-of select="./code"/>
            </xsl:element>
        </xsl:if>
        <xsl:if test="./label !='' and string(./label)">
            <xsl:element name="Label">
                <xsl:value-of select="./label"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="CountryLabelType">
        
        <xsl:if test="./code !='' and string(code) != 'NU' and string(./code)">       
            <xsl:element name="Code">
                <xsl:choose>
                    <xsl:when test="./code = 'GR'">EL</xsl:when>
                    <xsl:when test="./code = 'GB'">UK</xsl:when>
                    <xsl:otherwise><xsl:value-of select="./code"/></xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        </xsl:if>
        <xsl:if test="./label !='' and string(./label)">
            <xsl:element name="Label">
                <xsl:value-of select="./label"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="ExperiencePeriod">

        <xsl:for-each select="period">
            <xsl:call-template name="Period"/>
        </xsl:for-each>

    </xsl:template>

    <xsl:template name="Language">
        <xsl:element name="Description">
            <xsl:call-template name="LabelType"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="CEFRLevel">
        <xsl:if test="level != 'null' and level/*">
            <xsl:element name="ProficiencyLevel">
                <xsl:if test="level/listening != '' and string(level/listening)">
                    <xsl:element name="Listening">
                        <xsl:value-of select="translate(level/listening, $smallcase, $uppercase)" />
                    </xsl:element>
                </xsl:if>
                <xsl:if test="level/reading != '' and string(level/reading)">
                    <xsl:element name="Reading">
                        <xsl:value-of select="translate(level/reading, $smallcase, $uppercase)" />
                    </xsl:element>
                </xsl:if>
                <xsl:if test="level/spokeninteraction != ''">
                    <xsl:element name="SpokenInteraction">
                        <xsl:value-of select="translate(level/spokeninteraction, $smallcase, $uppercase)" />
                    </xsl:element>
                </xsl:if>
                <xsl:if test="level/spokenproduction != '' and (level/spokenproduction)">
                    <xsl:element name="SpokenProduction">
                        <xsl:value-of select="translate(level/spokenproduction, $smallcase, $uppercase)" />
                    </xsl:element>
                </xsl:if>
                <xsl:if test="level/writing != '' and string(level/writing)">
                    <xsl:element name="Writing">
                        <xsl:value-of select="translate(level/writing, $smallcase, $uppercase)" />
                    </xsl:element>
                </xsl:if>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template name="LinguisticCertificate">
        <xsl:if test="diplomalist != 'null' and diplomalist/*">
            <xsl:element name="VerifiedBy">
                <xsl:for-each select="diplomalist/diploma">
                    <xsl:if test="*">
                        <xsl:element name="Certificate">
                            <xsl:if test="title != 'null' and string(title)">
                                <xsl:element name="Title">
                                    <xsl:value-of select="title"/>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="awardingBody != 'null' and string(awardingBody)">
                                <xsl:element name="AwardingBody">
                                    <xsl:value-of select="awardingBody"/>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="date != 'null'">
                                <xsl:element name="Date">
                                    <xsl:attribute name="year">
                                        <xsl:value-of select="normalize-space(./date/year)"/>
                                    </xsl:attribute>
                                    <xsl:if test="./date/month != 'null' and string(./date/month)">
                                        <xsl:attribute name="month">
                                            <xsl:value-of select="normalize-space(./date/month)"/>
                                        </xsl:attribute>
                                    </xsl:if>
                                    <xsl:if test="./date/day != 'null' and string(./date/day)">
                                        <xsl:attribute name="day">
                                            <xsl:value-of select="normalize-space(./date/day)"/>
                                        </xsl:attribute>
                                    </xsl:if>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="level != 'null' and string(level)">
                                <xsl:element name="Level">
                                    <xsl:value-of select="translate(level, $smallcase, $uppercase)" />
                                </xsl:element>
                            </xsl:if>
                        </xsl:element>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="LinguisticExperience">
        <xsl:if test="experiencelist != 'null' and experiencelist/*">
            <xsl:element name="AcquiredDuring">
                <xsl:for-each select="experiencelist/experience">
                    <xsl:if test="*">
                        <xsl:element name="Experience">
                            <xsl:for-each select="period">
                                <xsl:call-template name="Period"/>
                            </xsl:for-each>
                            <xsl:if test="description != 'null' and string(description)">
                                <xsl:element name="Description">
                                    <xsl:value-of select="description"/>
                                </xsl:element>
                            </xsl:if>
                        </xsl:element>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <!-- Translate English chars -->
    <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
    
</xsl:stylesheet>