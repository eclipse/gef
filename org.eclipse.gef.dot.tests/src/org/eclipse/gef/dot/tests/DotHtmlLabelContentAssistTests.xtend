/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial implementation (bug #321775)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.Injector
import java.util.List
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.gef.dot.internal.language.DotHtmlLabelUiInjectorProvider
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder
import org.eclipse.xtext.resource.IResourceFactory
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.fail

@SuppressWarnings("restriction")
@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelUiInjectorProvider)
class DotHtmlLabelContentAssistTests extends AbstractContentAssistTest {
	
	@Inject Injector injector
	@Inject IResourceFactory resourceFactory

	// cursor position marker
	val c = '''<|>'''

	@Before def void before() {
		Resource.Factory.Registry.INSTANCE.extensionToFactoryMap.put("dothtmllabel", resourceFactory)
	}

	@Test def empty() {
		'''
			«c»
		'''.testContentAssistant(#[
			"<B></B>",
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<O></O>",
			"<S></S>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<TABLE></TABLE>",
			"<U></U>"
		], "<B></B>", '''
			<B></B>
		''')
	}

	@Test def openingTag() {
		'''
			<«c»
		'''
		.testContentAssistant(#[
			"<B></B>",
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<O></O>",
			"<S></S>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<TABLE></TABLE>",
			"<U></U>"
		], "<TABLE></TABLE>", '''
			<TABLE></TABLE>
		''')
	}

	@Test def childrenTagOfTagFONT() {
		'''
			<FONT>«c»</FONT>
		'''
		.testContentAssistant(#[
			"<TABLE></TABLE>",
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<S></S>",
			"<SUB></SUB>",
			"<SUP></SUP>"
		], "<S></S>", '''
			<FONT><S></S></FONT>
		''')
	}

	@Test def childrenTagOfTagI() {
		'''
			<I>«c»</I>
		'''.testContentAssistant(#[
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",		
			"<S></S>",		
			"<TABLE></TABLE>"
		], "<BR/>",	'''
			<I><BR/></I>
		''')
	}

	@Test def childrenTagOfTagB() {
		'''
			<B>«c»</B>
		'''.testContentAssistant(#[
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<S></S>",
			"<TABLE></TABLE>"
		], "<U></U>", '''
			<B><U></U></B>
		''')
	}

	@Test def childrenTagOfTagU() {
		'''
			<U>«c»</U>
		'''.testContentAssistant(#[
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<S></S>",
			"<TABLE></TABLE>"
		], "<O></O>", '''
			<U><O></O></U>
		''')
	}

	@Test def childrenTagOfTagO() {
		'''
			<O>«c»</O>
		'''.testContentAssistant(#[
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<S></S>",
			"<TABLE></TABLE>"
		], "<B></B>", '''
			<O><B></B></O>
		''')
	}

	@Test def childrenTagOfTagSUB() {
		'''
			<SUB>«c»</SUB>
		'''.testContentAssistant(#[
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<S></S>"
		], "<B></B>", '''
			<SUB><B></B></SUB>
		''')
	}

	@Test def childrenTagOfTagSUP() {
		'''
			<SUP>«c»</SUP>
		'''.testContentAssistant(#[
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<S></S>"
		], "<I></I>", '''
			<SUP><I></I></SUP>
		''')
	}

	@Test def childrenTagOfTagS() {
		'''
			<S>«c»</S>
		'''.testContentAssistant(#[
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<S></S>"
		], "<I></I>", '''
			<S><I></I></S>
		''')
	}

	@Test def childrenTagOfTagTABLE() {
		'''
			<TABLE>«c»</TABLE>
		'''.testContentAssistant(#[
			"<HR/>",
			"<TR></TR>"
		], "<TR></TR>", '''
			<TABLE><TR></TR></TABLE>
		''')
	}

	@Test def childrenTagOfTagTR() {
		'''
			<TR>«c»</TR>
		'''.testContentAssistant(#[
			"<VR/>",
			"<TD></TD>"
		], "<TD></TD>", '''
			<TR><TD></TD></TR>
		''')
	}

	@Test def childrenTagOfTagTD() {
		'''
			<TD>«c»</TD>
		'''.testContentAssistant(#[
			"<IMG/>",
			"<BR/>",
			"<FONT></FONT>",
			"<I></I>",
			"<B></B>",
			"<U></U>",
			"<O></O>",
			"<SUB></SUB>",
			"<SUP></SUP>",
			"<S></S>",
			"<TABLE></TABLE>"
		], "<TABLE></TABLE>", '''
			<TD><TABLE></TABLE></TD>
		''')
	}

	@Test def attributesOfTagTABLE() {
		'''
			<TABLE «c»></TABLE>
		'''.testContentAssistant(#[
			"ALIGN",
			"BGCOLOR",
			"BORDER",
			"CELLBORDER",
			"CELLPADDING",
			"CELLSPACING",
			"COLOR",
			"COLUMNS",
			"FIXEDSIZE",
			"GRADIENTANGLE",
			"HEIGHT",
			"HREF",
			"ID",
			"PORT",
			"ROWS",
			"SIDES",
			"STYLE",
			"TARGET",
			"TITLE",
			"TOOLTIP",
			"VALIGN",
			"WIDTH"
		], "ALIGN", '''
			<TABLE ALIGN=""></TABLE>
		''')
	}

	@Test def attributesOfTagTD() {
		'''
			<TD «c»></TD>
		'''.testContentAssistant(#[
			"ALIGN",
			"BALIGN",
			"BGCOLOR",
			"BORDER",
			"CELLPADDING",
			"CELLSPACING",
			"COLOR",
			"COLSPAN",
			"FIXEDSIZE",
			"GRADIENTANGLE",
			"HEIGHT",
			"HREF",
			"ID",
			"PORT",
			"ROWSPAN",
			"SIDES",
			"STYLE",
			"TARGET",
			"TITLE",
			"TOOLTIP",
			"VALIGN",
			"WIDTH"
		], "BALIGN", '''
			<TD BALIGN=""></TD>
		''')
	}

	@Test def attributesOfTagFONT() {
		'''
			<FONT «c»></FONT>
		'''.testContentAssistant(#[
			"COLOR",
			"FACE",
			"POINT-SIZE"
		], "POINT-SIZE", '''
			<FONT POINT-SIZE=""></FONT>
		''')
	}

	@Test def attributesOfTagBR() {
		'''
			<BR «c»/>
		'''.testContentAssistant(#[
			"ALIGN"
		], "ALIGN", '''
			<BR ALIGN=""/>
		''')
	}

	@Test def attributesOfTagIMG() {
		'''
			<IMG «c»/>
		'''.testContentAssistant(#[
			"SCALE",
			"SRC"
		], "SRC", '''
			<IMG SRC=""/>
		''')
	}

	@Test def attributesOfTagB() {
		'''<B «c»></B>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagHR() {
		'''<HR «c»/>'''.testEmptyContentAssistant // no attributes are allowed 
	}

	@Test def attributesOfTagI() {
		'''<I «c»></I>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagO() {
		'''<O «c»></O>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagS() {
		'''<S «c»></S>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagSUB() {
		'''<SUB «c»></SUB>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagSUP() {
		'''<SUP «c»></SUP>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagTR() {
		'''<TR «c»></TR>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagU() {
		'''<U «c»></U>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributesOfTagVR() {
		'''<VR «c»/>'''.testEmptyContentAssistant // no attributes are allowed
	}

	@Test def attributeValuesOfTagBR_ALIGN() {
		'''
			<BR ALIGN="«c»" />
		'''.testContentAssistant(#[
			"CENTER",
			"LEFT",
			"RIGHT"
		], "CENTER", '''
			<BR ALIGN="CENTER" />
		''')
	}

	@Test def attributeValuesOfTagBR_ALIGN_single_quoted() {
		'''
			<BR ALIGN='«c»' />
		'''.testContentAssistant(#[
			"CENTER",
			"LEFT",
			"RIGHT"
		], "CENTER", '''
			<BR ALIGN='CENTER' />
		''')
	}

	@Test def attributeValuesOfTagFONT_COLOR() {
		'''<FONT COLOR="«c»" ></FONT>'''.testEmptyContentAssistant // TODO implement "color"
	}

	@Test def attributeValuesOfTagFONT_FACE() {
		'''<FONT FACE="«c»" ></FONT>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagFONT_POINTSIZE() {
		'''<FONT POINT-SIZE="«c»" ></FONT>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagIMG_SCALE() {
		'''
			<IMG SCALE="«c»" />
		'''.testContentAssistant(#[
			"FALSE",
			"TRUE",
			"WIDTH",
			"HEIGHT",
			"BOTH"
		], "TRUE", '''
			<IMG SCALE="TRUE" />
		''')
	}

	@Test def attributeValuesOfTagIMG_SRC() {
		'''<IMG SRC="«c»" />'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_ALIGN() {
		'''
			<TABLE ALIGN="«c»" ></TABLE>
		'''.testContentAssistant(#[
			"CENTER",
			"LEFT",
			"RIGHT"
		], "LEFT", '''
			<TABLE ALIGN="LEFT" ></TABLE>
		''')
	}

	@Test def attributeValuesOfTagTABLE_BGCOLOR() {
		'''<TABLE BGCOLOR="«c»" ></TABLE>'''.testEmptyContentAssistant // TODO implement "color"
	}

	@Test def attributeValuesOfTagTABLE_BORDER() {
		'''<TABLE BORDER="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_CELLBORDER() {
		'''<TABLE CELLBORDER="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_CELLPADDING() {
		'''<TABLE CELLPADDING="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_CELLSPACING() {
		'''<TABLE CELLSPACING="«c»" ></TABLE>"'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_COLOR() {
		'''<TABLE COLOR="«c»" ></TABLE>"'''.testEmptyContentAssistant // TODO implement "color"
	}

	@Test def attributeValuesOfTagTABLE_COLUMNS() {
		'''
			<TABLE COLUMNS="«c»" ></TABLE>
		'''.testContentAssistant(#[
			"*"
		], "*", '''
			<TABLE COLUMNS="*" ></TABLE>
		''')
	}

	@Test def attributeValuesOfTagTABLE_FIXEDSIZE() {
		'''
			<TABLE FIXEDSIZE="«c»" ></TABLE>
		'''.testContentAssistant(#[
			"FALSE",
			"TRUE"
		], "FALSE", '''
			<TABLE FIXEDSIZE="FALSE" ></TABLE>
		''')
	}

	@Test def attributeValuesOfTagTABLE_GRADIENTANGLE() {
		'''<TABLE GRADIENTANGLE="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_HEIGHT() {
		'''<TABLE HEIGHT="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_HREF() {
		'''<TABLE HREF="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_ID() {
		'''<TABLE ID="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_PORT() {
		'''<TABLE PORT="«c»" ></TABLE>'''.testEmptyContentAssistant // TODO implement "portName"
	}

	@Test def attributeValuesOfTagTABLE_ROWS() {
		'''
			<TABLE ROWS="«c»" ></TABLE>
		'''.testContentAssistant(#[
			"*"
		], "*", '''
			<TABLE ROWS="*" ></TABLE>
		''')
	}

	@Test def attributeValuesOfTagTABLE_SIDES() {
		'''
			<TABLE SIDES="«c»" ></TABLE>
		'''.testContentAssistant(#[
			"L",
			"T",
			"R",
			"B",
			"LT",
			"LR",
			"LB",
			"TR",
			"TB",
			"RB",
			"LTR",
			"TRB",
			"LRB",
			"LTB",
			"LTRB"
		], "TB", '''
			<TABLE SIDES="TB" ></TABLE>
		''')
	}

	@Test def attributeValuesOfTagTABLE_STYLE() {
		'''<TABLE STYLE="«c»" ></TABLE>'''.testEmptyContentAssistant // TODO implement "value"
	}

	@Test def attributeValuesOfTagTABLE_TARGET() {
		'''<TABLE TARGET="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_TITLE() {
		'''<TABLE TITLE="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_TOOLTIP() {
		'''<TABLE TOOLTIP="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTABLE_VALIGN() {
		'''
			<TABLE VALIGN="«c»" ></TABLE>
		'''.testContentAssistant(#[
			"MIDDLE",
			"BOTTOM",
			"TOP"
		], "MIDDLE", '''
			<TABLE VALIGN="MIDDLE" ></TABLE>
		''')
	}

	@Test def attributeValuesOfTagTABLE_WIDTH() {
		'''<TABLE WIDTH="«c»" ></TABLE>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_ALIGN() {
		'''
			<TD ALIGN="«c»" ></TD>
		'''.testContentAssistant(#[
			"CENTER",
			"LEFT",
			"RIGHT",
			"TEXT"
		], "TEXT", '''
			<TD ALIGN="TEXT" ></TD>
		''')
	}

	@Test def attributeValuesOfTagTD_BALIGN() {
		'''
			<TD BALIGN="«c»" ></TD>
		'''.testContentAssistant(#[
			"CENTER",
			"LEFT",
			"RIGHT"
		], "RIGHT", '''
			<TD BALIGN="RIGHT" ></TD>
		''')
	}

	@Test def attributeValuesOfTagTD_BGCOLOR() {
		'''<TD BGCOLOR="«c»" ></TD>'''.testEmptyContentAssistant // TODO implement "color"
	}

	@Test def attributeValuesOfTagTD_BORDER() {
		'''<TD BORDER="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_CELLPADDING() {
		'''<TD CELLPADDING="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_CELLSPACING() {
		'''<TD CELLSPACING="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_COLOR() {
		'''<TD COLOR="«c»" ></TD>'''.testEmptyContentAssistant // TODO implement "color"
	}

	@Test def attributeValuesOfTagTD_COLSPAN() {
		'''<TD COLSPAN="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_FIXEDSIZE() {
		'''
			<TD FIXEDSIZE="«c»" ></TD>
		'''.testContentAssistant(#[
			"FALSE",
			"TRUE"
		], "FALSE", '''
			<TD FIXEDSIZE="FALSE" ></TD>
		''')
	}

	@Test def attributeValuesOfTagTD_GRADIENTANGLE() {
		'''<TD GRADIENTANGLE="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_HEIGHT() {
		'''<TD HEIGHT="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_HREF() {
		'''<TD HREF="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_ID() {
		'''<TD ID="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_PORT() {
		'''<TD PORT="«c»" ></TD>'''.testEmptyContentAssistant // TODO implement "portName"
	}

	@Test def attributeValuesOfTagTD_ROWSPAN() {
		'''<TD ROWSPAN="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_SIDES() {
		'''
			<TD SIDES="«c»" ></TD>
		'''.testContentAssistant(#[
			"L",
			"T",
			"R",
			"B",
			"LT",
			"LR",
			"LB",
			"TR",
			"TB",
			"RB",
			"LTR",
			"TRB",
			"LRB",
			"LTB",
			"LTRB"
		], "LTRB", '''
			<TD SIDES="LTRB" ></TD>
		''')
	}

	@Test def attributeValuesOfTagTD_STYLE() {
		'''<TD STYLE="«c»" ></TD>'''.testEmptyContentAssistant // TODO implement "value"
	}

	@Test def attributeValuesOfTagTD_TARGET() {
		'''<TD TARGET="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_TITLE() {
		'''<TD TITLE="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_TOOLTIP() {
		'''<TD TOOLTIP="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def attributeValuesOfTagTD_VALIGN() {
		'''
			<TD VALIGN="«c»" ></TD>
		'''.testContentAssistant(#[
			"MIDDLE",
			"BOTTOM",
			"TOP"
		], "BOTTOM", '''
			<TD VALIGN="BOTTOM" ></TD>
		''')
	}

	@Test def attributeValuesOfTagTD_WIDTH() {
		'''<TD WIDTH="«c»" ></TD>'''.testEmptyContentAssistant
	}

	@Test def siblingOfTableTagOnTheRootLevel() {
		// do not offer any tags
		'''«c»<TABLE></TABLE>'''.testEmptyContentAssistant
		'''<TABLE></TABLE>«c»'''.testEmptyContentAssistant
	}

	@Test def siblingOfTableTagOnANestedLevel() {
		// do not offer any tags
		'''<TABLE><TR><TD>«c»<TABLE></TABLE></TD></TR></TABLE>'''.testEmptyContentAssistant
		'''<TABLE><TR><TD><TABLE></TABLE>«c»</TD></TR></TABLE>'''.testEmptyContentAssistant
	}

	@Test def siblingOfTextOnTheRootLevel() {
		val testData = #["text", "<B>b</B>", "<BR/>", "<FONT>font</FONT>", "<I>i</I>",
			"<O>o</O>", "<S>s</S>", "<SUB>sub</SUB>", "<SUP>sup</SUP>", "<U>u</U>"]
		
		// do not offer the TABLE tag
		val expectations = #["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>",
			"<O></O>", "<S></S>", "<SUB></SUB>", "<SUP></SUP>", "<U></U>"]

		testData.forEach[htmlLabel|
			'''«c»«htmlLabel»'''.testContentAssistant(expectations, "<B></B>", '''<B></B>«htmlLabel»''')
			'''«htmlLabel»«c»'''.testContentAssistant(expectations, "<B></B>", '''«htmlLabel»<B></B>''')
		]
	}

	@Test def siblingOfTextOnANestedLevel() {
		val testData = #["text", "<B>b</B>", "<BR/>", "<FONT>font</FONT>", "<I>i</I>",
			"<O>o</O>", "<S>s</S>", "<SUB>sub</SUB>", "<SUP>sup</SUP>", "<U>u</U>"]
		
		// do not offer the TABLE tag
		val expectations = #["<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>",
			"<O></O>", "<S></S>", "<SUB></SUB>", "<SUP></SUP>", "<U></U>"]
		
		testData.forEach[htmlLabel|
			'''
				<TABLE><TR><TD>«c»«htmlLabel»</TD></TR></TABLE>
			'''.testContentAssistant(expectations, "<B></B>", '''
				<TABLE><TR><TD><B></B>«htmlLabel»</TD></TR></TABLE>
			''')

			'''
				<TABLE><TR><TD>«htmlLabel»«c»</TD></TR></TABLE>
			'''.testContentAssistant(expectations, "<B></B>", '''
				<TABLE><TR><TD>«htmlLabel»<B></B></TD></TR></TABLE>
			''')
		]
	}

	private def testEmptyContentAssistant(CharSequence text) {
		testContentAssistant(text, #[], null, null)
	}

	private def void testContentAssistant(CharSequence text, List<String> expectedProposals,
		String proposalToApply, String expectedContent) {
		
		val cursorPosition = text.toString.indexOf(c)
		if(cursorPosition == -1) {
			fail('''Can't locate cursor position symbols '«c»' in the input text.''')
		}
		
		val content = text.toString.replace(c, "")
		
		val builder = newBuilder.append(content).
		assertTextAtCursorPosition(cursorPosition, expectedProposals)
		
		if(proposalToApply!==null) {
			builder.applyProposal(cursorPosition, proposalToApply).
			expectContent(expectedContent)
		}
	}

	override protected ContentAssistProcessorTestBuilder newBuilder() {
		return new ContentAssistProcessorTestBuilder(injector, this) {
			/*
			 * configure the ContentAssistProcessorTestBuilder to consider only
			 * the displayString of a proposal and ignore its replacement
			 * strings when determining the proposed text.
			 */
			override protected getProposedText(ICompletionProposal proposal) {
				return proposal.displayString
			}
		}
	}
}
