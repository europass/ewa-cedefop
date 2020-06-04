<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    version="1.0"
    xmlns="http://europass.cedefop.europa.eu/Europass"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:europass="http://europass.cedefop.europa.eu/Europass"
    xmlns:europass2="http://europass.cedefop.europa.eu/Europass/V3.3">
    
    <xsl:output method="xml" encoding="utf-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    
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
    
    <xsl:template match="@xsi:schemaLocation">  
        <xsl:attribute name="xsi:schemaLocation">http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/xml/v3.3.0/EuropassSchema.xsd</xsl:attribute>
    </xsl:template>
    
    <xsl:template match="europass:XSDVersion">  
        <xsl:element name="XSDVersion">V3.3</xsl:element>
    </xsl:template>

    <xsl:template match="europass:Field">
        <xsl:choose>
            <!-- xsl:when test="boolean(@format)=false and boolean(@position)=false and boolean(@order)=false and @name!='LearnerInfo.CEFLanguageLevelsGrid'"></xsl:when-->
            <xsl:when test="substring(@name, string-length(@name)-18)='ContactInfo.Address' "></xsl:when>
            <!-- xsl:when test="boolean(substring(@name, string-length(@name)-18)='ContactInfo.Address')=false"></xsl:when-->

            <!-- remove any preference that does not have attributes format, position, order, justify (new), 
                 this is already done in europass-cv-v3.1-to-v3.2.xsl, no need to repeat actually, but I do it for safety and insecureness -->
            <!-- remove any preference that ends with ContactInfo.Address like :
            LearnerInfo.Identification.ContactInfo.Address
            LearnerInfo.WorkExperience[0].Employer.ContactInfo.Address
            LearnerInfo.Education[0].Organisation.ContactInfo.Address
            CoverLetter.Addressee.Organisation.ContactInfo.Address -->
            <!-- leave fieldnames starting with CoverLetter -->

            <xsl:otherwise>
                
                    <xsl:element name="Field">
                        <xsl:apply-templates select="@*|node()"/>
                        <!--<xsl:attribute name="name">step1.addressInfo</xsl:attribute>-->
                    </xsl:element>
                
            </xsl:otherwise>
        </xsl:choose>
        
        
    </xsl:template>  
    
</xsl:stylesheet>