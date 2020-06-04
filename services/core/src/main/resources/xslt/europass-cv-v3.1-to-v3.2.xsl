<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    version="1.0"
    xmlns="http://europass.cedefop.europa.eu/Europass"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:europass="http://europass.cedefop.europa.eu/Europass"
    xmlns:europass2="http://europass.cedefop.europa.eu/Europass/V3.2">
    
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
        <xsl:attribute name="xsi:schemaLocation">http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/xml/v3.2.0/EuropassSchema.xsd</xsl:attribute>
    </xsl:template>
    
    <xsl:template match="europass:XSDVersion">  
        <xsl:element name="XSDVersion">V3.2</xsl:element>
    </xsl:template>

    <xsl:template match="europass:Field">
        <xsl:choose>
            <xsl:when test="boolean(@format)=false and boolean(@position)=false and boolean(@order)=false and @name!='LearnerInfo.CEFLanguageLevelsGrid'"></xsl:when>
            <xsl:when test="substring(@name, 0, 10)='CoverLetter' "></xsl:when>
            <xsl:otherwise>
                
                    <xsl:element name="Field">
                        <xsl:apply-templates select="@*|node()"/>
                    </xsl:element>
                
            </xsl:otherwise>
        </xsl:choose>
        
        
    </xsl:template>  
    
</xsl:stylesheet>