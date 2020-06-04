<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
	xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="text xlink">

	<xsl:output method="xml" omit-xml-declaration="yes"
		encoding="UTF-8" indent="no" />

	<!-- Textarea Tab -->
	<xsl:template match="span[@class='tab']">
		<xsl:element name="text:tab" />
		<xsl:apply-templates />
	</xsl:template>
	<!-- Textarea Space -->
	<xsl:template match="span[@class='space']">
		<xsl:element name="text:s" />
		<xsl:apply-templates />
	</xsl:template>

	<!-- Link -->
	<xsl:template
		match="a[ancestor::p or ancestor::blockquote or ancestor::li or ancestor::div]">
		<xsl:call-template name="aTemplate" />
	</xsl:template>
	<xsl:template
		match="a[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div)) or (parent::div[@class='dummy-root'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
			<xsl:call-template name="aTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="a[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div)) or (parent::div[@class='dummy-root ECL'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
			<xsl:call-template name="aTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="a[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div)) or (parent::div[@class='dummy-root ELP'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
			<xsl:call-template name="aTemplate" />
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="aTemplate">
		<xsl:element name="text:a">
			<xsl:attribute name="xlink:type">simple</xsl:attribute>
			<xsl:attribute name="xlink:href"><xsl:value-of select="@href" /></xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<!-- Bold -->
	<xsl:template
		match="b[ancestor::p or ancestor::blockquote or ancestor::li or ancestor::div]">
		<xsl:call-template name="boldTemplate" />
	</xsl:template>
	<xsl:template
		match="b[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root']) or (parent::div[@class='dummy-root'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
			<xsl:call-template name="boldTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="b[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ECL']) or (parent::div[@class='dummy-root ECL'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
			<xsl:call-template name="boldTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="b[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ELP']) or (parent::div[@class='dummy-root ELP'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
			<xsl:call-template name="boldTemplate" />
		</xsl:element>
	</xsl:template>
	<!-- Emphasized -->
	<xsl:template
		match="strong[ancestor::p or ancestor::blockquote or ancestor::li or ancestor::div]">
		<xsl:call-template name="boldTemplate" />
	</xsl:template>
	<xsl:template
		match="strong[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root']) or (parent::div[@class='dummy-root'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
			<xsl:call-template name="boldTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="strong[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ECL']) or (parent::div[@class='dummy-root ECL'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
			<xsl:call-template name="boldTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="strong[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ELP']) or (parent::div[@class='dummy-root ELP'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
			<xsl:call-template name="boldTemplate" />
		</xsl:element>
	</xsl:template>

	<xsl:template name="boldTemplate">
		<xsl:element name="text:span">
			<xsl:choose>
				<!--xsl:when test="(ancestor::u or ancestor::em) and ancestor::i"-->
				<xsl:when test="(ancestor::u and ( ancestor::em or ancestor::i))">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold_5f_And_5f_Underline_5f_And_5f_Italics</xsl:attribute>
				</xsl:when>
				<!--xsl:when test="(ancestor::u or ancestor::em) and not(ancestor::i)"-->
				<xsl:when test="ancestor::u and not(ancestor::em or ancestor::i)">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold_5f_And_5f_Underline</xsl:attribute>
				</xsl:when>
				<!--xsl:when test="not(ancestor::u) and not(ancestor::em) and ancestor::i"-->
				<xsl:when test="(ancestor::em or ancestor::i) and not(ancestor::u)">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold_5f_And_5f_Italics</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Underline -->
	<xsl:template
		match="u[ancestor::p or ancestor::blockquote or ancestor::li or ancestor::div]">
		<xsl:call-template name="underlineTemplate" />
	</xsl:template>
	<xsl:template
		match="u[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root']) or (parent::div[@class='dummy-root'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
			<xsl:call-template name="underlineTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="u[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ECL']) or (parent::div[@class='dummy-root ECL'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
			<xsl:call-template name="underlineTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="u[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ELP']) or (parent::div[@class='dummy-root ELP'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
			<xsl:call-template name="underlineTemplate" />
		</xsl:element>
	</xsl:template>

	<xsl:template name="underlineTemplate">
		<xsl:element name="text:span">
			<xsl:choose>
				<xsl:when
					test="(ancestor::b or ancestor::strong) and (ancestor::i or ancestor::em)">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold_5f_And_5f_Underline_5f_And_5f_Italics</xsl:attribute>
				</xsl:when>
				<xsl:when
					test="(ancestor::b or ancestor::strong) and not(ancestor::i) and not(ancestor::em)">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold_5f_And_5f_Underline</xsl:attribute>
				</xsl:when>
				<xsl:when
					test="not(ancestor::b) and not(ancestor::strong) and (ancestor::i or ancestor::em)">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Underline_5f_And_5f_Italics</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Underline</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Italics -->
	<xsl:template
		match="i[ancestor::p or ancestor::blockquote or ancestor::li or ancestor::div]">
		<xsl:call-template name="italicsTemplate" />
	</xsl:template>
	<xsl:template
		match="i[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root']) or (parent::div[@class='dummy-root'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
			<xsl:call-template name="italicsTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="i[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ECL']) or (parent::div[@class='dummy-root ECL'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
			<xsl:call-template name="italicsTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="i[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ELP']) or (parent::div[@class='dummy-root ELP'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
			<xsl:call-template name="italicsTemplate" />
		</xsl:element>
	</xsl:template>
	<!-- Emphasized -->
	<xsl:template
		match="em[ancestor::p or ancestor::blockquote or ancestor::li or ancestor::div]">
		<xsl:call-template name="italicsTemplate" />
	</xsl:template>
	<xsl:template
		match="em[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root']) or (parent::div[@class='dummy-root'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
			<xsl:call-template name="italicsTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="em[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ECL']) or (parent::div[@class='dummy-root ECL'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
			<xsl:call-template name="italicsTemplate" />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="em[(not(ancestor::p) and not(ancestor::blockquote) and not(ancestor::li) and not(ancestor::div) and ancestor::div[@class='dummy-root ELP']) or (parent::div[@class='dummy-root ELP'])]">
		<xsl:element name="text:p">
			<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
			<xsl:call-template name="italicsTemplate" />
		</xsl:element>
	</xsl:template>

	<xsl:template name="italicsTemplate">
		<xsl:element name="text:span">
			<xsl:choose>
				<xsl:when test="(ancestor::b or ancestor::strong) and ancestor::u">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold_5f_And_5f_Underline_5f_And_5f_Italics</xsl:attribute>
				</xsl:when>
				<xsl:when test="(ancestor::b or ancestor::strong) and not(ancestor::u)">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Bold_5f_And_5f_Italics</xsl:attribute>
				</xsl:when>
				<xsl:when test="not(ancestor::b) and not(ancestor::strong) and ancestor::u">
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Underline_5f_And_5f_Italics</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_Text_5f_Italics</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>


	<!-- Paragraph -->
	<xsl:template match="p[parent::blockquote]">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template
		match="p[not(parent::blockquote) and not(parent::li)]">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="p[parent::li[parent::ol]]">
                <xsl:element name="text:p">
                        <xsl:choose>
                                <xsl:when test="ancestor::div[@class='dummy-root ECL']">
                                    <xsl:attribute name="text:style-name">europass_5f_numbered_5f_list_ECL</xsl:attribute>
                                </xsl:when>
                                <xsl:when test="ancestor::div[@class='dummy-root ELP']">
                                    <xsl:attribute name="text:style-name">europass_5f_numbered_5f_list_ELP</xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="text:style-name">europass_5f_numbered_5f_list</xsl:attribute>
                                </xsl:otherwise>
                        </xsl:choose>
                    <xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="p[parent::li[parent::ul]]">
                <xsl:element name="text:p">
                        <xsl:choose>
                                <xsl:when test="ancestor::div[@class='dummy-root ECL']">
                                    <xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ECL</xsl:attribute>
                                </xsl:when>
                                <xsl:when test="ancestor::div[@class='dummy-root ELP']">
                                    <xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ELP</xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list</xsl:attribute>
                                </xsl:otherwise>
                        </xsl:choose>
                    <xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Paragraph indentation -->
	<xsl:template match="p[@class='indent1']">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template match="p[@class='indent2']">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template match="p[@class='indent3']">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Paragraph text-align justify -->
	<xsl:template match="p[@class='align-justify']">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_align_justify_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_align_justify_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_align_justify</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Paragraph text-align justify and indentation -->
	<xsl:template
		match="p[@class='indent1 align-justify'] | p[@class='align-justify indent1']">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_justify_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_justify_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_justify</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="p[@class='indent2 align-justify'] | p[@class='align-justify indent2']">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2_justify_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2_justify_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent2_justify</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="p[@class='indent3 align-justify'] | p[@class='align-justify indent3']">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3_justify_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3_justify_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent3_justify</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>


	<xsl:template match="sup">
		<xsl:element name="text:span">
			<xsl:attribute name="text:style-name">europass_5f_Text_5f_Superscript</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="sub">
		<xsl:element name="text:span">
			<xsl:attribute name="text:style-name">europass_5f_Text_5f_Subscript</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Blockquote -->
	<xsl:template match="blockquote">
		<!-- This will only add a tab in the first line -->
		<xsl:element name="text:line-break" />
		<xsl:element name="text:tab" />
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template
		match="blockquote[not(parent::li) and not(parent::p) and not(parent::blockquote)]">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_paragraph_indent1</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- DIV -->
	<!-- The use of section does not go well with the produced odt and results 
		in parts of the content (e.g. last list item to be removed) <xsl:template 
		match="div[@class='dummy-root']" > <xsl:element name="text:section"> <xsl:apply-templates 
		/> </xsl:element> </xsl:template> -->
	<xsl:template match="div[ancestor::*]">
		<xsl:element name="text:p">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Break -->
	<xsl:template match="br">
		<xsl:element name="text:line-break" />
	</xsl:template>

	<!-- Ordered List -->
	<xsl:template match="ol">
		<xsl:element name="text:list">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_numbered_5f_list_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_numbered_5f_list_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_numbered_5f_list</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Unordered List -->
        <xsl:template match="ul">
		<xsl:element name="text:list">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ECL</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ELP</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template match="ul[@class='indent1']">
		<xsl:element name="text:list">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ECL_indent1</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ELP_indent1</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_indent1</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template match="ul[@class='indent2']">
		<xsl:element name="text:list">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ECL_indent2</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ELP_indent2</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_indent2</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template match="ul[@class='indent3']">
		<xsl:element name="text:list">
			<xsl:choose>
				<xsl:when test="ancestor::div[@class='dummy-root ECL']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ECL_indent3</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::div[@class='dummy-root ELP']">
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ELP_indent3</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_indent3</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Ordered List Item -->
	<xsl:template match="ol/li">
		<xsl:element name="text:list-item">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template match="ol/li[not(child::p)]">
		<xsl:element name="text:list-item">
			<text:p>
				<xsl:choose>
					<xsl:when test="ancestor::div[@class='dummy-root ECL']">
						<xsl:attribute name="text:style-name">europass_5f_numbered_5f_list_ECL</xsl:attribute>
					</xsl:when>
					<xsl:when test="ancestor::div[@class='dummy-root ELP']">
						<xsl:attribute name="text:style-name">europass_5f_numbered_5f_list_ELP</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="text:style-name">europass_5f_numbered_5f_list</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="a|b|strong|u|i|em|sup|sub|blockquote|text()"/>
                        </text:p>
                    <xsl:apply-templates select="ol|ul"/>
		</xsl:element>
	</xsl:template>

	<!-- Unordered List Item -->
	<xsl:template match="ul/li">
		<xsl:element name="text:list-item">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<!-- Add a text:p where applicable -->
	<xsl:template match="ul/li[not(child::p)]">
		<xsl:element name="text:list-item">
                        <text:p>
                                <xsl:choose>
                                        <xsl:when test="ancestor::div[@class='dummy-root ECL']">
                                            <xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ECL</xsl:attribute>
                                        </xsl:when>
                                        <xsl:when test="ancestor::div[@class='dummy-root ELP']">
                                            <xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list_ELP</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="text:style-name">europass_5f_bulleted_5f_list</xsl:attribute>
                                        </xsl:otherwise>
                                </xsl:choose>
                                <xsl:apply-templates select="a|b|strong|u|i|em|sup|sub|blockquote|text()"/>
                        </text:p>
                        <xsl:apply-templates select="ol|ul"/>
		</xsl:element>
	</xsl:template>

	<xsl:template
		match="div[@class='dummy-root']/text()">
		<xsl:if test="normalize-space(current()) != '' ">
			<xsl:element name="text:p">
				<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
				<xsl:copy></xsl:copy>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template
		match="div[@class='dummy-root ECL']/text()">
		<xsl:if test="normalize-space(current()) != '' ">
			<xsl:element name="text:p">
				<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
				<xsl:copy></xsl:copy>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template
		match="div[@class='dummy-root ELP']/text()">
		<xsl:if test="normalize-space(current()) != '' ">
			<xsl:element name="text:p">
				<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
				<xsl:copy></xsl:copy>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<!-- Match the text under a li, which does not have a sibling ol or ul -->
	<xsl:template
		match="ul/li/text()  |  ol/li/text() ">
		<xsl:if test="normalize-space(current()) != '' ">
			<xsl:element name="text:span">
				<xsl:copy></xsl:copy>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="/div[@class='dummy-text']/text() ">
		<xsl:if test="normalize-space(current()) != '' ">
			<xsl:element name="text:p">
				<xsl:attribute name="text:style-name">europass_5f_SectionDetails</xsl:attribute>
				<xsl:copy></xsl:copy>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="/div[@class='dummy-text ECL']/text() ">
		<xsl:if test="normalize-space(current()) != '' ">
			<xsl:element name="text:p">
				<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ECL</xsl:attribute>
				<xsl:copy></xsl:copy>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="/div[@class='dummy-text ELP']/text() ">
		<xsl:if test="normalize-space(current()) != '' ">
			<xsl:element name="text:p">
				<xsl:attribute name="text:style-name">europass_5f_SectionDetails_ELP</xsl:attribute>
				<xsl:copy></xsl:copy>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
