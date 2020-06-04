<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
                xmlns:xlink="http://www.w3.org/1999/xlink" 
                exclude-result-prefixes="text xlink">
    
    <xsl:output method="xml" omit-xml-declaration="yes" encoding="UTF-8" indent="no"/>
    
    <!-- Textarea Tab -->
    <xsl:template match="span[@class='tab']" >
    	<!-- This will only add a tab in each span.tab  -->
        <xsl:element name="text:tab"/>
        <xsl:apply-templates/>
    </xsl:template>
    
    <!-- Link -->
    <xsl:template match="a[ancestor::p or ancestor::li or ancestor::div]">
       <xsl:element name="text:a">
            <xsl:attribute name="xlink:type">simple</xsl:attribute>
            <xsl:attribute name="xlink:href"><xsl:value-of select="@href"/></xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="a[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='dummy-root'])) and (parent::div[@class='dummy-root'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:element name="text:a">
                <xsl:attribute name="xlink:type">simple</xsl:attribute>
                <xsl:attribute name="xlink:href"><xsl:value-of select="@href"/></xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="a[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-heading'])) and (parent::div[@class='left-heading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
            <xsl:element name="text:a">
                <xsl:attribute name="xlink:type">simple</xsl:attribute>
                <xsl:attribute name="xlink:href"><xsl:value-of select="@href"/></xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="a[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-subheading'])) and (parent::div[@class='left-subheading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftDetails</xsl:attribute>
            <xsl:element name="text:a">
                <xsl:attribute name="xlink:type">simple</xsl:attribute>
                <xsl:attribute name="xlink:href"><xsl:value-of select="@href"/></xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Subscript/Superscript support -->
    <xsl:template match="sup" >
	    <xsl:element name="text:span">
	         <xsl:attribute name="text:style-name">_5f_ECV_5f_Text_5f_Superscript</xsl:attribute>
	         <xsl:apply-templates />
	    </xsl:element>
    </xsl:template>
    
    <xsl:template match="sub" >
	    <xsl:element name="text:span">
	         <xsl:attribute name="text:style-name">_5f_ECV_5f_Text_5f_Subscript</xsl:attribute>
	         <xsl:apply-templates />
	    </xsl:element>
    </xsl:template>
    
    <!-- Bold -->
    <xsl:template match="b[ancestor::p or ancestor::li or ancestor::div[@class!='dummy-root']]" >
        <xsl:element name="text:span">
            <xsl:attribute name="text:style-name">europass_bold</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="b[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='dummy-root'])) and (parent::div[@class='dummy-root'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_bold</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="b[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-heading'])) and (parent::div[@class='left-heading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_bold</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="b[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-subheading'])) and (parent::div[@class='left-subheading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_bold</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Underline -->
    <xsl:template match="u[ancestor::p or ancestor::li or ancestor::div]" >
        <xsl:element name="text:span">
            <xsl:attribute name="text:style-name">europass_underline</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="u[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='dummy-root'])) and (parent::div[@class='dummy-root'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_underline</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="u[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-heading'])) and (parent::div[@class='left-heading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_underline</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="u[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-subheading'])) and (parent::div[@class='left-subheading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_underline</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Italics -->
    <xsl:template match="i[ancestor::p or ancestor::li or ancestor::div]" >
        <xsl:element name="text:span">
            <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="i[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='dummy-root'])) and (parent::div[@class='dummy-root'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="i[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-heading'])) and (parent::div[@class='left-heading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="i[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-subheading'])) and (parent::div[@class='left-subheading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Emphasized -->
    <xsl:template match="em[ancestor::p or ancestor::li or ancestor::div]" >
        <xsl:element name="text:span">
            <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="em[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='dummy-root'])) and (parent::div[@class='dummy-root'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="em[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class!='left-heading'])) and (parent::div[@class='left-heading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="em[(not(ancestor::p) and not(ancestor::li) and not(ancestor::div[@class='left-subheading'])) and (parent::div[@class='left-subheading'])]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftDetails</xsl:attribute>
            <xsl:element name="text:span">
                <xsl:attribute name="text:style-name">europass_italic</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Paragraph -->
    <xsl:template match="p[parent::li]" >
       <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="p[not(parent::li) and (parent::div[@class='dummy-root'])]" >
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
       
    <!--  Paragraph LeftHeading -->
    <xsl:template match="p[not(parent::li) and (parent::div[@class='left-heading'])]" >
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
   <!--  Paragraph LeftSubHeading -->
    <xsl:template match="p[not(parent::li) and (parent::div[@class='left-subheading'])]" >
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftDetails</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    
    
    <!-- DIV -->
   <!-- The use of section does not go well with the produced odt
       and results in parts of the content (e.g. last list item to be removed)
    <xsl:template match="div[@class='dummy-root']" >
        <xsl:element name="text:section">
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template> "div[ancestor::*]"-->
    <xsl:template match="div[@class!='dummy-root' and ancestor::div[@class='dummy-root']]" >
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="div[ancestor::div[@class='left-heading'] and ancestor::div[@class='left-heading']]" >
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="div[ancestor::div[@class='left-subheading'] and ancestor::div[@class='left-subheading']]" >
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_ECV_LeftSubHeading</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <!-- Paragraph indentation -->
    <xsl:template match="p[@class='indent1']" >
    	<xsl:element name="text:p">
       		<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1</xsl:attribute>
       		<xsl:apply-templates />
       </xsl:element>
    </xsl:template>
    <xsl:template match="p[@class='indent2']" >
    	<xsl:element name="text:p">
       		<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2</xsl:attribute>
       		<xsl:apply-templates />
       </xsl:element>
    </xsl:template>
    <xsl:template match="p[@class='indent3']" >
    	<xsl:element name="text:p">
       		<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3</xsl:attribute>
       		<xsl:apply-templates />
       </xsl:element>
    </xsl:template>
    
    <!-- Paragraph text-align justify -->
    <xsl:template match="p[@class='align-justify']" >
    	<xsl:element name="text:p">
       		<xsl:attribute name="text:style-name">europass_5f_paragraph_align_justify</xsl:attribute>
       		<xsl:apply-templates />
       </xsl:element>
    </xsl:template>
    
    <!-- Paragraph text-align justify and indentation -->
    <xsl:template match="p[@class='indent1 align-justify'] | p[@class='align-justify indent1']" >
    	<xsl:element name="text:p">
       		<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_justify</xsl:attribute>
       		<xsl:apply-templates />
       </xsl:element>
    </xsl:template>
    <xsl:template match="p[@class='indent2 align-justify'] | p[@class='align-justify indent2']" >
    	<xsl:element name="text:p">
       		<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2_justify</xsl:attribute>
       		<xsl:apply-templates />
       </xsl:element>
    </xsl:template>
    <xsl:template match="p[@class='indent3 align-justify'] | p[@class='align-justify indent3']" >
    	<xsl:element name="text:p">
       		<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3_justify</xsl:attribute>
       		<xsl:apply-templates />
       </xsl:element>
    </xsl:template>
    
    <!-- Break -->
    <xsl:template match="br">
        <xsl:element name="text:line-break"/>
    </xsl:template>
    <xsl:template match="br[( 
    				parent::div[@class='dummy-root'] 
    				or parent::div[@class='left-heading'] 
    				or parent::div[@class='left-subheading'] 
    				)]">
        <xsl:element name="text:p">
            <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
            <xsl:element name="text:line-break"/>
        </xsl:element>
    </xsl:template>
 
    
    <!-- Ordered List -->
    <xsl:template match="ol" >
        <xsl:element name="text:list">
            <xsl:attribute name="text:style-name">europass_simple_content_numbered_list</xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <!-- Unordered List -->
    <xsl:template match="ul" >
        <xsl:element name="text:list">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_CV_5f_Bullets</xsl:attribute>
                <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <!-- Ordered List Item -->
    <xsl:template match="ol/li" >
        <xsl:element name="text:list-item">
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="ol/li[not(child::ol) and not(child::ul) and ancestor::div[@class='dummy-root']]" >
        <xsl:element name="text:list-item">
            <xsl:element name="text:h">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionBullet</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="ol/li[not(child::ol) and not(child::ul) and ancestor::div[@class='left-heading']]" >
        <xsl:element name="text:list-item">
            <xsl:element name="text:h">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_HeadingBullet</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="ol/li[not(child::ol) and not(child::ul) and ancestor::div[@class='left-subheading']]" >
        <xsl:element name="text:list-item">
            <xsl:element name="text:h">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_SubHeadingBullet</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Unordered List Item -->
    <xsl:template match="ul/li" >
        <xsl:element name="text:list-item">
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <!-- Add a text:p only if the children of li are anything but ol, ul -->
    <xsl:template match="ul/li[not(child::ol) and not(child::ul) and ancestor::div[@class='dummy-root']]" >
        <xsl:element name="text:list-item">
            <xsl:element name="text:h">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionBullet</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="ul/li[not(child::ol) and not(child::ul) and ancestor::div[@class='left-heading']]" >
        <xsl:element name="text:list-item">
            <xsl:element name="text:h">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_HeadingBullet</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="ul/li[not(child::ol) and not(child::ul) and ancestor::div[@class='left-subheading']]" >
        <xsl:element name="text:list-item">
            <xsl:element name="text:h">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_SubHeadingBullet</xsl:attribute>
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="ul/li/text() | ol/li/text() | div[@class='dummy-root']/text() | div[@class='left-heading']/text() | div[@class='left-subheading']/text()">
        <xsl:if test="normalize-space(current()) != '' ">
            <xsl:element name="text:span">
                <xsl:copy></xsl:copy>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <!-- Match the text under a li, which does not have a sibling ol or ul -->
    <xsl:template match="ul/li[not(child::ol) and not(child::ul)]/text()  |  ol/li[not(child::ol) and not(child::ul)]/text() " >
        <xsl:if test="normalize-space(current()) != '' ">
          <xsl:element name="text:span">
              <xsl:copy></xsl:copy>
          </xsl:element>
        </xsl:if>
    </xsl:template>
    <!-- /div[@class='dummy-text']/text() -->
    <xsl:template match="div[@class='left-heading']/text()" >
        <xsl:if test="normalize-space(current()) != '' ">
            <xsl:element name="text:p">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftHeading</xsl:attribute>
                <xsl:copy></xsl:copy>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="div[@class='left-subheading']/text()" >
        <xsl:if test="normalize-space(current()) != '' ">
            <xsl:element name="text:p">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_LeftDetails</xsl:attribute>
                <xsl:copy></xsl:copy>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="div[@class='dummy-root']/text()" >
        <xsl:if test="normalize-space(current()) != '' ">
            <xsl:element name="text:p">
                <xsl:attribute name="text:style-name">_5f_ECV_5f_SectionDetails</xsl:attribute>
                <xsl:copy></xsl:copy>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
