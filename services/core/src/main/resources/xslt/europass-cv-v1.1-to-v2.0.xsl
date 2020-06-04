<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
							  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
							  xmlns:europass="http://europass.cedefop.europa.eu/Europass/V2.0" 
							  xmlns:europass1="http://europass.cedefop.europa.eu/Europass/V1.1">
	<xsl:output method="xml" encoding="utf-8" indent="yes"/>
	<xsl:template match="/learnerinfo">
		<xsl:element name="europass:learnerinfo">
		
			<xsl:attribute name="xsi:schemaLocation">http://europass.cedefop.europa.eu/Europass/V2.0 http://europass.cedefop.europa.eu/xml/EuropassSchema_V2.0.xsd</xsl:attribute>
			<xsl:attribute name="locale"><xsl:value-of select="@locale"/></xsl:attribute>
			
			<xsl:element name="docinfo">
				<xsl:for-each select="docinfo">
					<xsl:call-template name="docinfo"></xsl:call-template>
				</xsl:for-each>
			</xsl:element>
			
			<xsl:element name="prefs">
				<xsl:for-each select="prefs/field">
					<xsl:call-template name="field"></xsl:call-template>
				</xsl:for-each>
				<xsl:element name="field">
					<xsl:attribute name="name">step1.telephone</xsl:attribute>
					<xsl:attribute name="keep">false</xsl:attribute>
				</xsl:element>
			</xsl:element>
			
			<xsl:element name="identification">
				<xsl:for-each select="identification">
					<xsl:call-template name="identification"></xsl:call-template>
				</xsl:for-each>
			</xsl:element>
			
			<xsl:element name="application">
				<xsl:call-template name="application"></xsl:call-template>
			</xsl:element>
			
			<xsl:element name="workexperiencelist">
				<xsl:for-each select="workexperiencelist/workexperience">
					<xsl:element name="workexperience">
						<xsl:call-template name="workexperience"></xsl:call-template>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
			
			<xsl:element name="educationlist">
				<xsl:for-each select="educationlist/education">
					<xsl:element name="education">
						<xsl:call-template name="education"></xsl:call-template>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
			
			<xsl:element name="languagelist">
				<xsl:for-each select="languagelist/language">
					<xsl:call-template name="language"></xsl:call-template>
				</xsl:for-each>
			</xsl:element>
			
			<xsl:element name="skilllist">
				<xsl:for-each select="skilllist/skill">
					<xsl:call-template name="skill"></xsl:call-template>
				</xsl:for-each>
			</xsl:element>
			
			<xsl:element name="misclist">
				<xsl:for-each select="misclist">
					<xsl:for-each select="misc">
						<xsl:call-template name="misc"></xsl:call-template>
					</xsl:for-each>
				</xsl:for-each>
			</xsl:element>
			
		</xsl:element>
	</xsl:template>
	
	<!-- docinfo -->
	<xsl:template name="docinfo">
		<xsl:element name="issuedate">
			<xsl:value-of select="issuedate"/>
		</xsl:element>
		<xsl:element name="xsdversion">V2.0</xsl:element>
		<xsl:element name="comment">Automatically generated Europass CV  converted from 1.1 to 2.0</xsl:element>
	</xsl:template>
	
	<!-- prefs/field -->
  <xsl:template name="field">
    <xsl:choose>
      <xsl:when test="@name='step1.application'">
        <xsl:element name="field">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:attribute name="name">step1.application.label</xsl:attribute>
        </xsl:element>
      </xsl:when>
	  <xsl:when test="@name='step1.address'">
        <xsl:element name="field">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:attribute name="name">step1.addressInfo</xsl:attribute>
        </xsl:element>
      </xsl:when>
      <xsl:when test="substring-before(@name,'position')!=''">
        <xsl:element name="field">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:attribute name="name">      <xsl:value-of select="substring-before(@name,'position')"/>position.label</xsl:attribute>
        </xsl:element>
      </xsl:when>
      <xsl:when test="substring-before(@name,'employer')!=''">
        <xsl:element name="field">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:attribute name="name">      <xsl:value-of select="substring-before(@name,'employer')"/>company.name</xsl:attribute>
        </xsl:element>
      </xsl:when>
      <xsl:when test="substring-before(@name,'sector')!=''">
        <xsl:element name="field">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:attribute name="name">      <xsl:value-of select="substring-before(@name,'sector')"/>company.sector.label</xsl:attribute>
        </xsl:element>
      </xsl:when>
      <xsl:when test="substring-before(@name,'organisational')!=''">
          <xsl:element name="field">
            <xsl:apply-templates select="@*|node()"/>
          </xsl:element>
      </xsl:when>
      <xsl:when test="substring-before(@name,'organisation')!=''">
        <xsl:if test="substring-after(@name,'organisation')=''">
          <xsl:element name="field">
	          <xsl:apply-templates select="@*|node()"/>
	          <xsl:attribute name="name">      <xsl:value-of select="substring-before(@name,'organisation')"/>educationalOrg.name</xsl:attribute>
	      </xsl:element>
        </xsl:if>
      </xsl:when>
      <xsl:when test="substring-before(@name,'level')!=''">
        <xsl:element name="field">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:attribute name="name">      <xsl:value-of select="substring-before(@name,'level')"/>level.label</xsl:attribute>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="field">
          <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
	
	<!-- Identification -->
	<xsl:template name="identification">
		<xsl:element name="firstname"><xsl:value-of select="firstname" /></xsl:element>
		<xsl:element name="lastname"><xsl:value-of select="lastname" /></xsl:element>
		<xsl:element name="contactinfo">
			<xsl:element name="address">
				<xsl:element name="addressLine"><xsl:value-of select="contactinfo/address" /></xsl:element>
				<xsl:element name="municipality"><xsl:value-of select="contactinfo/address" /></xsl:element>
				<xsl:element name="postalCode"><xsl:value-of select="contactinfo/address" /></xsl:element>
				<xsl:element name="country">
					<!--xsl:element name="code"><xsl:value-of select="contactinfo/address/country/code" /></xsl:element-->
					<xsl:element name="label"><xsl:value-of select="contactinfo/address" /></xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:element name="telephone"><xsl:value-of select="contactinfo/telephone" /></xsl:element>
			<xsl:element name="fax"><xsl:value-of select="contactinfo/fax" /></xsl:element>
			<xsl:element name="mobile"><xsl:value-of select="contactinfo/mobile" /></xsl:element>
			<xsl:element name="email"><xsl:value-of select="contactinfo/email" /></xsl:element>
		</xsl:element>
		<xsl:element name="demographics">
			<xsl:element name="birthdate"><xsl:value-of select="demographics/birthdate" /></xsl:element>
			<xsl:element name="gender"><xsl:value-of select="demographics/gender" /></xsl:element>
			<xsl:element name="nationality">
				<!--xsl:element name="code"><xsl:value-of select="demographics/nationality" /></xsl:element-->
				<xsl:element name="label"><xsl:value-of select="demographics/nationality" /></xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="photo">
			<xsl:attribute name="type">JPEG</xsl:attribute>
			<xsl:value-of select="photo" />
		</xsl:element>
	</xsl:template>
	
	<!-- application -->
	<xsl:template name="application">
		<xsl:element name="label"><xsl:value-of select="application" /></xsl:element>
	</xsl:template>
	
	<!-- workexperiencelist/workexperience  -->
  <xsl:template name="workexperience">
    <xsl:element name="period">
		<xsl:element name="from">
			<xsl:if test="period/from/year != 'null'" >
				<xsl:element name="year"><xsl:value-of select="period/from/year" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/from/month != 'null'" >
				<xsl:element name="month"><xsl:value-of select="period/from/month" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/from/day != 'null'" >
				<xsl:element name="day"><xsl:value-of select="period/from/day" /></xsl:element>
			</xsl:if>
		</xsl:element>
		<xsl:element name="to">
			<xsl:if test="period/to/year != 'null'" >
				<xsl:element name="year"><xsl:value-of select="period/to/year" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/to/month != 'null'" >
				<xsl:element name="month"><xsl:value-of select="period/to/month" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/to/day != 'null'" >
				<xsl:element name="day"><xsl:value-of select="period/to/day" /></xsl:element>
			</xsl:if>
		</xsl:element>
    </xsl:element>
    <xsl:element name="position">
      <xsl:element name="label">
        <xsl:value-of select="position"/>
      </xsl:element>
    </xsl:element>
    <xsl:element name="activities">
      <xsl:for-each select="activities">
        <xsl:apply-templates />
      </xsl:for-each>
    </xsl:element>
    <xsl:element name="employer">
      <xsl:element name="name">
        <xsl:value-of select="employer"/>
      </xsl:element>
      <xsl:element name="address">
        <xsl:element name="addressLine"><xsl:value-of select="employer" /></xsl:element>
        <xsl:element name="municipality"><xsl:value-of select="employer" /></xsl:element>
        <xsl:element name="postalCode"><xsl:value-of select="employer" /></xsl:element>
        <xsl:element name="country">
			<xsl:element name="label"><xsl:value-of select="employer" /></xsl:element>
        </xsl:element>
      </xsl:element>
      <xsl:element name="sector">
        <xsl:element name="label">
          <xsl:value-of select="sector"/>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:template>
  
  <!-- educationlist/education -->
  <xsl:template name="education">
    <xsl:element name="period">
		<xsl:element name="from">
			<xsl:if test="period/from/year != 'null'" >
				<xsl:element name="year"><xsl:value-of select="period/from/year" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/from/month != 'null'" >
				<xsl:element name="month"><xsl:value-of select="period/from/month" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/from/day != 'null'" >
				<xsl:element name="day"><xsl:value-of select="period/from/day" /></xsl:element>
			</xsl:if>
		</xsl:element>
		<xsl:element name="to">
			<xsl:if test="period/to/year != 'null'" >
				<xsl:element name="year"><xsl:value-of select="period/to/year" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/to/month != 'null'" >
				<xsl:element name="month"><xsl:value-of select="period/to/month" /></xsl:element>
			</xsl:if>
			<xsl:if test="period/to/day != 'null'" >
				<xsl:element name="day"><xsl:value-of select="period/to/day" /></xsl:element>
			</xsl:if>
		</xsl:element>
    </xsl:element>
    <xsl:element name="title">
        <xsl:value-of select="title"/>
    </xsl:element>
    <xsl:element name="skills">
		<xsl:value-of select="skills"/>
    </xsl:element>
    <xsl:element name="organisation">
		<xsl:element name="name"><xsl:value-of select="organisation"/></xsl:element>
		<xsl:element name="address">
			<xsl:element name="addressLine"><xsl:value-of select="organisation"/></xsl:element>
			<xsl:element name="municipality"><xsl:value-of select="organisation"/></xsl:element>
			<xsl:element name="postalCode"><xsl:value-of select="organisation"/></xsl:element>
			<xsl:element name="country">
				<xsl:element name="label"><xsl:value-of select="organisation"/></xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="type"><xsl:value-of select="organisation" /></xsl:element>
    </xsl:element>
    <xsl:element name="level">
		<xsl:element name="label">
			<xsl:value-of select="level"/>
		</xsl:element>
    </xsl:element>
    <xsl:element name="educationalfield">
		<xsl:element name="label"><xsl:value-of select="organisation" /></xsl:element>
    </xsl:element>
  </xsl:template>
  
	<!-- languagelist/language -->
	<xsl:template name="language">
		<xsl:choose>
			<xsl:when test="@type='mother'">
				<xsl:element name="language">
					<xsl:attribute name="xsi:type">europass:mother</xsl:attribute>
					<!--xsl:element name="code"><xsl:value-of select="code" /></xsl:element-->
					<xsl:element name="label"><xsl:value-of select="name" /></xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="language">
					<xsl:attribute name="xsi:type">europass:<xsl:value-of select="@type"/></xsl:attribute>
					<!--xsl:element name="code"><xsl:value-of select="code" /></xsl:element-->
					<xsl:element name="label"><xsl:value-of select="name" /></xsl:element>
					<xsl:element name="level">
						<xsl:element name="listening"><xsl:value-of select="level/listening" /></xsl:element>
						<xsl:element name="reading"><xsl:value-of select="level/reading" /></xsl:element>
						<xsl:element name="spokeninteraction"><xsl:value-of select="level/spokeninteraction" /></xsl:element>
						<xsl:element name="spokenproduction"><xsl:value-of select="level/spokenproduction" /></xsl:element>
						<xsl:element name="writing"><xsl:value-of select="level/writing" /></xsl:element>
					</xsl:element>
					<xsl:element name="diplomalist">
						<xsl:for-each select="diplomalist/diploma">
							<xsl:call-template name="diploma"></xsl:call-template>
						</xsl:for-each>
					</xsl:element>
					<xsl:element name="experiencelist">
						<xsl:for-each select="experiencelist/experience">
							<xsl:call-template name="experience"></xsl:call-template>
						</xsl:for-each>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- foreign language diplomas template -->
	<xsl:template name="diploma">
		<xsl:element name="diploma">
			<xsl:element name="title"><xsl:value-of select="title"/></xsl:element>
			<xsl:element name="awardingBody"><xsl:value-of select="awardingBody"/></xsl:element>
			<xsl:element name="date">
				<xsl:if test="year != 'null'" >
					<xsl:element name="year"><xsl:value-of select="year"/></xsl:element>
				</xsl:if>
				<xsl:if test="month != 'null'" >
					<xsl:element name="month"><xsl:value-of select="month"/></xsl:element>
				</xsl:if>
				<xsl:if test="day != 'null'" >
					<xsl:element name="day"><xsl:value-of select="day"/></xsl:element>
				</xsl:if>
			</xsl:element>
			<xsl:element name="level"><xsl:value-of select="level"/></xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- foreign language experience template -->
	<xsl:template name="experience">
		<xsl:element name="experience">
			<xsl:element name="period">
				<xsl:element name="from">
					<xsl:if test="period/from/year != 'null'" >
						<xsl:element name="year"><xsl:value-of select="period/from/year" /></xsl:element>
					</xsl:if>
					<xsl:if test="period/from/month != 'null'" >
						<xsl:element name="month"><xsl:value-of select="period/from/month" /></xsl:element>
					</xsl:if>
					<xsl:if test="period/from/day != 'null'" >
						<xsl:element name="day"><xsl:value-of select="period/from/day" /></xsl:element>
					</xsl:if>
				</xsl:element>
				<xsl:element name="to">
					<xsl:if test="period/to/year != 'null'" >
						<xsl:element name="year"><xsl:value-of select="period/to/year" /></xsl:element>
					</xsl:if>
					<xsl:if test="period/to/month != 'null'" >
						<xsl:element name="month"><xsl:value-of select="period/to/month" /></xsl:element>
					</xsl:if>
					<xsl:if test="period/to/day != 'null'" >
						<xsl:element name="day"><xsl:value-of select="period/to/day" /></xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:element>
			<xsl:element name="description"><xsl:value-of select="description"/></xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- skilllist/skill -->
	<xsl:template name="skill">
		<xsl:choose>
			<xsl:when test="@type='driving'">
				<xsl:element name="structured-skill">
					<xsl:attribute name="xsi:type">europass:driving</xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="skill">
					<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- misclist -->
	<xsl:template name="misc">
		<xsl:element name="misc">
			<xsl:attribute name="type"><xsl:value-of select="@type" /></xsl:attribute>
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>
	
	<!-- templates used for the copy of elements and attributes without including previous europass release namespace reference  -->
	<xsl:template match="/" >
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="@*">
		<xsl:attribute name="{local-name()}">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>
  
</xsl:stylesheet>