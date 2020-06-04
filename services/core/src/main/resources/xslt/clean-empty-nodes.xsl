<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
        version="1.0"
        xmlns="http://europass.cedefop.europa.eu/Europass"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:europass="http://europass.cedefop.europa.eu/Europass"
        >
        
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
        
        <xsl:template match="*[not(normalize-space()) and not(.//@*) and not(name()='LearnerInfo') ]"/>
        
</xsl:stylesheet>