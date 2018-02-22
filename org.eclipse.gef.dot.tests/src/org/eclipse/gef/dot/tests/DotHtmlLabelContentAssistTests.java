/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
package org.eclipse.gef.dot.tests;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.dot.internal.language.DotHtmlLabelUiInjectorProvider;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;

@SuppressWarnings("restriction")
@RunWith(XtextRunner.class)
@InjectWith(DotHtmlLabelUiInjectorProvider.class)
public class DotHtmlLabelContentAssistTests extends AbstractContentAssistTest {

	@Inject
	private Injector injector;

	@Inject
	private IResourceFactory resourceFactory;

	@Before
	public void before() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
				.put("dothtmllabel", resourceFactory);
	}

	@Test
	public void empty() throws Exception {
		newBuilder().assertText("<B></B>", "<BR/>", "<FONT></FONT>", "<I></I>",
				"<O></O>", "<S></S>", "<SUB></SUB>", "<SUP></SUP>",
				"<TABLE></TABLE>", "<U></U>");
	}

	@Test
	public void openingTag() throws Exception {
		newBuilder().append("<").assertText("<B></B>", "<BR/>", "<FONT></FONT>",
				"<I></I>", "<O></O>", "<S></S>", "<SUB></SUB>", "<SUP></SUP>",
				"<TABLE></TABLE>", "<U></U>");
	}

	@Test
	public void childrenTagOfTagFONT() throws Exception {
		newBuilder().append("<FONT></FONT>").assertTextAtCursorPosition(6,
				"<TABLE></TABLE>", "<BR/>", "<FONT></FONT>", "<I></I>",
				"<B></B>", "<U></U>", "<O></O>", "<S></S>", "<SUB></SUB>",
				"<SUP></SUP>");
	}

	@Test
	public void childrenTagOfTagI() throws Exception {
		newBuilder().append("<I></I>").assertTextAtCursorPosition(3, "<BR/>",
				"<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>", "<O></O>",
				"<SUB></SUB>", "<SUP></SUP>", "<S></S>");
	}

	@Test
	public void childrenTagOfTagB() throws Exception {
		newBuilder().append("<B></B>").assertTextAtCursorPosition(3, "<BR/>",
				"<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>", "<O></O>",
				"<SUB></SUB>", "<SUP></SUP>", "<S></S>");
	}

	@Test
	public void childrenTagOfTagU() throws Exception {
		newBuilder().append("<U></U>").assertTextAtCursorPosition(3, "<BR/>",
				"<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>", "<O></O>",
				"<SUB></SUB>", "<SUP></SUP>", "<S></S>");
	}

	@Test
	public void childrenTagOfTagO() throws Exception {
		newBuilder().append("<O></O>").assertTextAtCursorPosition(3, "<BR/>",
				"<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>", "<O></O>",
				"<SUB></SUB>", "<SUP></SUP>", "<S></S>");
	}

	@Test
	public void childrenTagOfTagSUB() throws Exception {
		newBuilder().append("<SUB></SUB>").assertTextAtCursorPosition(5,
				"<BR/>", "<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>",
				"<O></O>", "<SUB></SUB>", "<SUP></SUP>", "<S></S>");
	}

	@Test
	public void childrenTagOfTagSUP() throws Exception {
		newBuilder().append("<SUP></SUP>").assertTextAtCursorPosition(5,
				"<BR/>", "<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>",
				"<O></O>", "<SUB></SUB>", "<SUP></SUP>", "<S></S>");
	}

	@Test
	public void childrenTagOfTagS() throws Exception {
		newBuilder().append("<S></S>").assertTextAtCursorPosition(3, "<BR/>",
				"<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>", "<O></O>",
				"<SUB></SUB>", "<SUP></SUP>", "<S></S>");
	}

	@Test
	public void childrenTagOfTagTABLE() throws Exception {
		newBuilder().append("<TABLE></TABLE>").assertTextAtCursorPosition(7,
				"<HR/>", "<TR></TR>");
	}

	@Test
	public void childrenTagOfTagTR() throws Exception {
		newBuilder().append("<TR></TR>").assertTextAtCursorPosition(4, "<VR/>",
				"<TD></TD>");
	}

	@Test
	public void childrenTagOfTagTD() throws Exception {
		newBuilder().append("<TD></TD>").assertTextAtCursorPosition(4, "<IMG/>",
				"<BR/>", "<FONT></FONT>", "<I></I>", "<B></B>", "<U></U>",
				"<O></O>", "<SUB></SUB>", "<SUP></SUP>", "<S></S>",
				"<TABLE></TABLE>");
	}

	@Test
	public void attributesOfTagTABLE() throws Exception {
		newBuilder().append("<TABLE ></TABLE>")
				.assertTextAtCursorPosition(7, "ALIGN", "BGCOLOR", "BORDER",
						"CELLBORDER", "CELLPADDING", "CELLSPACING", "COLOR",
						"COLUMNS", "FIXEDSIZE", "GRADIENTANGLE", "HEIGHT",
						"HREF", "ID", "PORT", "ROWS", "SIDES", "STYLE",
						"TARGET", "TITLE", "TOOLTIP", "VALIGN", "WIDTH")
				.applyProposal(7, "ALIGN")
				.expectContent("<TABLE ALIGN=\"\"></TABLE>");
	}

	@Test
	public void attributesOfTagTD() throws Exception {
		newBuilder().append("<TD ></TD>")
				.assertTextAtCursorPosition(4, "ALIGN", "BALIGN", "BGCOLOR",
						"BORDER", "CELLPADDING", "CELLSPACING", "COLOR",
						"COLSPAN", "FIXEDSIZE", "GRADIENTANGLE", "HEIGHT",
						"HREF", "ID", "PORT", "ROWSPAN", "SIDES", "STYLE",
						"TARGET", "TITLE", "TOOLTIP", "VALIGN", "WIDTH")
				.applyProposal(4, "BALIGN")
				.expectContent("<TD BALIGN=\"\"></TD>");
	}

	@Test
	public void attributesOfTagFONT() throws Exception {
		newBuilder().append("<FONT ></FONT>")
				.assertTextAtCursorPosition(6, "COLOR", "FACE", "POINT-SIZE")
				.applyProposal(6, "POINT-SIZE")
				.expectContent("<FONT POINT-SIZE=\"\"></FONT>");
	}

	@Test
	public void attributesOfTagBR() throws Exception {
		newBuilder().append("<BR />").assertTextAtCursorPosition(4, "ALIGN")
				.applyProposal(4, "ALIGN").expectContent("<BR ALIGN=\"\"/>");
	}

	@Test
	public void attributesOfTagIMG() throws Exception {
		newBuilder().append("<IMG />")
				.assertTextAtCursorPosition(5, "SCALE", "SRC")
				.applyProposal(5, "SRC").expectContent("<IMG SRC=\"\"/>");
	}

	@Test
	public void attributesOfTagB() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<B ></B>").assertTextAtCursorPosition(3, "");
	}

	@Test
	public void attributesOfTagHR() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<HR />").assertTextAtCursorPosition(4, "");
	}

	@Test
	public void attributesOfTagI() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<I ></I>").assertTextAtCursorPosition(3, "");
	}

	@Test
	public void attributesOfTagO() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<O ></O>").assertTextAtCursorPosition(3, "");
	}

	@Test
	public void attributesOfTagS() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<S ></S>").assertTextAtCursorPosition(3, "");
	}

	@Test
	public void attributesOfTagSUB() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<SUB ></SUB>").assertTextAtCursorPosition(5, "");
	}

	@Test
	public void attributesOfTagSUP() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<SUP ></SUP>").assertTextAtCursorPosition(5, "");
	}

	@Test
	public void attributesOfTagTR() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<TR ></TR>").assertTextAtCursorPosition(4, "");
	}

	@Test
	public void attributesOfTagU() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<U ></U>").assertTextAtCursorPosition(3, "");
	}

	@Test
	public void attributesOfTagVR() throws Exception {
		/* no attributes are allowed */
		newBuilder().append("<VR />").assertTextAtCursorPosition(4, "");
	}

	@Test
	public void attributeValuesOfTagBR_ALIGN() throws Exception {
		newBuilder().append("<BR ALIGN=\"\" />")
				.assertTextAtCursorPosition(11, "CENTER", "LEFT", "RIGHT")
				.applyProposal(11, "CENTER")
				.expectContent("<BR ALIGN=\"CENTER\" />");
	}

	@Test
	public void attributeValuesOfTagFONT_COLOR() throws Exception {
		// TODO implement "color"
		newBuilder().append("<FONT COLOR=\"\" ></FONT>")
				.assertTextAtCursorPosition(13, "");
	}

	@Test
	public void attributeValuesOfTagFONT_FACE() throws Exception {
		newBuilder().append("<FONT FACE=\"\" ></FONT>")
				.assertTextAtCursorPosition(12, "");
	}

	@Test
	public void attributeValuesOfTagFONT_POINTSIZE() throws Exception {
		newBuilder().append("<FONT POINT-SIZE=\"\" ></FONT>")
				.assertTextAtCursorPosition(18, "");
	}

	@Test
	public void attributeValuesOfTagIMG_SCALE() throws Exception {
		newBuilder().append("<IMG SCALE=\"\" />")
				.assertTextAtCursorPosition(12, "FALSE", "TRUE", "WIDTH",
						"HEIGHT", "BOTH")
				.applyProposal(12, "TRUE")
				.expectContent("<IMG SCALE=\"TRUE\" />");
	}

	@Test
	public void attributeValuesOfTagIMG_SRC() throws Exception {
		newBuilder().append("<IMG SRC=\"\" />").assertTextAtCursorPosition(10,
				"");
	}

	@Test
	public void attributeValuesOfTagTABLE_ALIGN() throws Exception {
		newBuilder().append("<TABLE ALIGN=\"\" ></TABLE>")
				.assertTextAtCursorPosition(14, "CENTER", "LEFT", "RIGHT")
				.applyProposal(14, "LEFT")
				.expectContent("<TABLE ALIGN=\"LEFT\" ></TABLE>");
	}

	@Test
	public void attributeValuesOfTagTABLE_BGCOLOR() throws Exception {
		// TODO implement "color"
		newBuilder().append("<TABLE BGCOLOR=\"\" ></TABLE>")
				.assertTextAtCursorPosition(16, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_BORDER() throws Exception {
		newBuilder().append("<TABLE BORDER=\"\" ></TABLE>")
				.assertTextAtCursorPosition(15, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_CELLBORDER() throws Exception {
		newBuilder().append("<TABLE CELLBORDER=\"\" ></TABLE>")
				.assertTextAtCursorPosition(19, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_CELLPADDING() throws Exception {
		newBuilder().append("<TABLE CELLPADDING=\"\" ></TABLE>")
				.assertTextAtCursorPosition(20, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_CELLSPACING() throws Exception {
		newBuilder().append("<TABLE CELLSPACING=\"\" ></TABLE>")
				.assertTextAtCursorPosition(20, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_COLOR() throws Exception {
		// TODO implement "color"
		newBuilder().append("<TABLE COLOR=\"\" ></TABLE>")
				.assertTextAtCursorPosition(14, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_COLUMNS() throws Exception {
		newBuilder().append("<TABLE COLUMNS=\"\" ></TABLE>")
				.assertTextAtCursorPosition(16, "*")
				.applyProposal(16, "<TABLE COLUMNS=\"*\" ></TABLE>");
	}

	@Test
	public void attributeValuesOfTagTABLE_FIXEDSIZE() throws Exception {
		newBuilder().append("<TABLE FIXEDSIZE=\"\" ></TABLE>")
				.assertTextAtCursorPosition(18, "FALSE", "TRUE")
				.applyProposal(18, "FALSE")
				.expectContent("<TABLE FIXEDSIZE=\"FALSE\" ></TABLE>");
	}

	@Test
	public void attributeValuesOfTagTABLE_GRADIENTANGLE() throws Exception {
		newBuilder().append("<TABLE GRADIENTANGLE=\"\" ></TABLE>")
				.assertTextAtCursorPosition(22, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_HEIGHT() throws Exception {
		newBuilder().append("<TABLE HEIGHT=\"\" ></TABLE>")
				.assertTextAtCursorPosition(15, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_HREF() throws Exception {
		newBuilder().append("<TABLE HREF=\"\" ></TABLE>")
				.assertTextAtCursorPosition(13, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_ID() throws Exception {
		newBuilder().append("<TABLE ID=\"\" ></TABLE>")
				.assertTextAtCursorPosition(11, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_PORT() throws Exception {
		// TODO implement "portName"
		newBuilder().append("<TABLE PORT=\"\" ></TABLE>")
				.assertTextAtCursorPosition(13, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_ROWS() throws Exception {
		newBuilder().append("<TABLE ROWS=\"\" ></TABLE>")
				.assertTextAtCursorPosition(13, "*").applyProposal(13, "*")
				.expectContent("<TABLE ROWS=\"*\" ></TABLE>");
	}

	@Test
	public void attributeValuesOfTagTABLE_SIDES() throws Exception {
		newBuilder().append("<TABLE SIDES=\"\" ></TABLE>")
				.assertTextAtCursorPosition(14, "L", "T", "R", "B", "LT", "LR",
						"LB", "TR", "TB", "RB", "LTR", "TRB", "LRB", "LTB",
						"LTRB")
				.applyProposal(14, "TB")
				.expectContent("<TABLE SIDES=\"TB\" ></TABLE>");
	}

	@Test
	public void attributeValuesOfTagTABLE_STYLE() throws Exception {
		// TODO implement "value"
		newBuilder().append("<TABLE STYLE=\"\" ></TABLE>")
				.assertTextAtCursorPosition(14, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_TARGET() throws Exception {
		newBuilder().append("<TABLE TARGET=\"\" ></TABLE>")
				.assertTextAtCursorPosition(15, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_TITLE() throws Exception {
		newBuilder().append("<TABLE TITLE=\"\" ></TABLE>")
				.assertTextAtCursorPosition(14, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_TOOLTIP() throws Exception {
		newBuilder().append("<TABLE TOOLTIP=\"\" ></TABLE>")
				.assertTextAtCursorPosition(16, "");
	}

	@Test
	public void attributeValuesOfTagTABLE_VALIGN() throws Exception {
		newBuilder().append("<TABLE VALIGN=\"\" ></TABLE>")
				.assertTextAtCursorPosition(15, "MIDDLE", "BOTTOM", "TOP")
				.applyProposal(15, "MIDDLE")
				.expectContent("<TABLE VALIGN=\"MIDDLE\" ></TABLE>");
	}

	@Test
	public void attributeValuesOfTagTABLE_WIDTH() throws Exception {
		newBuilder().append("<TABLE WIDTH=\"\" ></TABLE>")
				.assertTextAtCursorPosition(14, "");
	}

	@Test
	public void attributeValuesOfTagTD_ALIGN() throws Exception {
		newBuilder().append("<TD ALIGN=\"\" ></TD>")
				.assertTextAtCursorPosition(11, "CENTER", "LEFT", "RIGHT",
						"TEXT")
				.applyProposal(11, "TEXT")
				.expectContent("<TD ALIGN=\"TEXT\" ></TD>");
	}

	@Test
	public void attributeValuesOfTagTD_BALIGN() throws Exception {
		newBuilder().append("<TD BALIGN=\"\" ></TD>")
				.assertTextAtCursorPosition(12, "CENTER", "LEFT", "RIGHT")
				.applyProposal(12, "RIGHT")
				.expectContent("<TD BALIGN=\"RIGHT\" ></TD>");
	}

	@Test
	public void attributeValuesOfTagTD_BGCOLOR() throws Exception {
		// TODO implement "color"
		newBuilder().append("<TD BGCOLOR=\"\" ></TD>")
				.assertTextAtCursorPosition(13, "");
	}

	@Test
	public void attributeValuesOfTagTD_BORDER() throws Exception {
		newBuilder().append("<TD BORDER=\"\" ></TD>")
				.assertTextAtCursorPosition(12, "");
	}

	@Test
	public void attributeValuesOfTagTD_CELLPADDING() throws Exception {
		newBuilder().append("<TD CELLPADDING=\"\" ></TD>")
				.assertTextAtCursorPosition(17, "");
	}

	@Test
	public void attributeValuesOfTagTD_CELLSPACING() throws Exception {
		newBuilder().append("<TD CELLSPACING=\"\" ></TD>")
				.assertTextAtCursorPosition(17, "");
	}

	@Test
	public void attributeValuesOfTagTD_COLOR() throws Exception {
		// TODO implement "color"
		newBuilder().append("<TD COLOR=\"\" ></TD>")
				.assertTextAtCursorPosition(11, "");
	}

	@Test
	public void attributeValuesOfTagTD_COLSPAN() throws Exception {
		newBuilder().append("<TD COLSPAN=\"\" ></TD>")
				.assertTextAtCursorPosition(13, "");
	}

	@Test
	public void attributeValuesOfTagTD_FIXEDSIZE() throws Exception {
		newBuilder().append("<TD FIXEDSIZE=\"\" ></TD>")
				.assertTextAtCursorPosition(15, "FALSE", "TRUE")
				.applyProposal(15, "FALSE")
				.expectContent("<TD FIXEDSIZE=\"FALSE\" ></TD>");
	}

	@Test
	public void attributeValuesOfTagTD_GRADIENTANGLE() throws Exception {
		newBuilder().append("<TD GRADIENTANGLE=\"\" ></TD>")
				.assertTextAtCursorPosition(19, "");
	}

	@Test
	public void attributeValuesOfTagTD_HEIGHT() throws Exception {
		newBuilder().append("<TD HEIGHT=\"\" ></TD>")
				.assertTextAtCursorPosition(12, "");
	}

	@Test
	public void attributeValuesOfTagTD_HREF() throws Exception {
		newBuilder().append("<TD HREF=\"\" ></TD>")
				.assertTextAtCursorPosition(10, "");
	}

	@Test
	public void attributeValuesOfTagTD_ID() throws Exception {
		newBuilder().append("<TD ID=\"\" ></TD>").assertTextAtCursorPosition(8,
				"");
	}

	@Test
	public void attributeValuesOfTagTD_PORT() throws Exception {
		// TODO implement "portName"
		newBuilder().append("<TD PORT=\"\" ></TD>")
				.assertTextAtCursorPosition(10, "");
	}

	@Test
	public void attributeValuesOfTagTD_ROWSPAN() throws Exception {
		newBuilder().append("<TD ROWSPAN=\"\" ></TD>")
				.assertTextAtCursorPosition(13, "");
	}

	@Test
	public void attributeValuesOfTagTD_SIDES() throws Exception {
		newBuilder().append("<TD SIDES=\"\" ></TD>")
				.assertTextAtCursorPosition(11, "L", "T", "R", "B", "LT", "LR",
						"LB", "TR", "TB", "RB", "LTR", "TRB", "LRB", "LTB",
						"LTRB")
				.applyProposal(11, "LTRB")
				.expectContent("<TD SIDES=\"LTRB\" ></TD>");
	}

	@Test
	public void attributeValuesOfTagTD_STYLE() throws Exception {
		// TODO implement "value"
		newBuilder().append("<TD STYLE=\"\" ></TD>")
				.assertTextAtCursorPosition(11, "");
	}

	@Test
	public void attributeValuesOfTagTD_TARGET() throws Exception {
		newBuilder().append("<TD TARGET=\"\" ></TD>")
				.assertTextAtCursorPosition(12, "");
	}

	@Test
	public void attributeValuesOfTagTD_TITLE() throws Exception {
		newBuilder().append("<TD TITLE=\"\" ></TD>")
				.assertTextAtCursorPosition(11, "");
	}

	@Test
	public void attributeValuesOfTagTD_TOOLTIP() throws Exception {
		newBuilder().append("<TD TOOLTIP=\"\" ></TD>")
				.assertTextAtCursorPosition(13, "");
	}

	@Test
	public void attributeValuesOfTagTD_VALIGN() throws Exception {
		newBuilder().append("<TD VALIGN=\"\" ></TD>")
				.assertTextAtCursorPosition(12, "MIDDLE", "BOTTOM", "TOP")
				.applyProposal(12, "BOTTOM")
				.expectContent("<TD VALIGN=\"BOTTOM\" ></TD>");
	}

	@Test
	public void attributeValuesOfTagTD_WIDTH() throws Exception {
		newBuilder().append("<TD WIDTH=\"\" ></TD>")
				.assertTextAtCursorPosition(11, "");
	}

	@Test
	public void siblingOfTableTagOnTheRootLevel() throws Exception {
		// do not offer any tag
		newBuilder().append("<TABLE></TABLE>").assertTextAtCursorPosition(0);
		newBuilder().append("<TABLE></TABLE>").assertTextAtCursorPosition(15);
	}

	@Test
	public void siblingOfTableTagOnANestedLevel() throws Exception {
		// do not offer any tag
		newBuilder().append("<TABLE><TR><TD><TABLE></TABLE></TD></TR></TABLE>")
				.assertTextAtCursorPosition(16);

		newBuilder().append("<TABLE><TR><TD><TABLE></TABLE></TD></TR></TABLE>")
				.assertTextAtCursorPosition(30);
	}

	@Test
	public void siblingOfTextOnTheRootLevel() throws Exception {
		String[] testData = { "text", "<B>b</B>", "<BR/>", "<FONT>font</FONT>",
				"<I>i</I>", "<O>o</O>", "<S>s</S>", "<SUB>sub</SUB>",
				"<SUP>sup</SUP>", "<U>u</U>" };

		// do not offer the TABLE tag
		String[] expectations = { "<B></B>", "<BR/>", "<FONT></FONT>",
				"<I></I>", "<O></O>", "<S></S>", "<SUB></SUB>", "<SUP></SUP>",
				"<U></U>" };

		for (int i = 0; i < testData.length; i++) {
			String htmlLabel = testData[i];
			// the cursor is located before the html label
			newBuilder().append(htmlLabel).assertTextAtCursorPosition(0,
					expectations);
			// the cursor is located after the html label
			newBuilder().append(htmlLabel).assertTextAtCursorPosition(
					htmlLabel.length(), expectations);
		}
	}

	@Test
	public void siblingOfTextOnANestedLevel() throws Exception {
		String[] testData = { "text", "<B>b</B>", "<BR/>", "<FONT>font</FONT>",
				"<I>i</I>", "<O>o</O>", "<S>s</S>", "<SUB>sub</SUB>",
				"<SUP>sup</SUP>", "<U>u</U>" };

		// do not offer the TABLE tag
		String[] expectations = { "<B></B>", "<BR/>", "<FONT></FONT>",
				"<I></I>", "<O></O>", "<S></S>", "<SUB></SUB>", "<SUP></SUP>",
				"<U></U>" };

		for (int i = 0; i < testData.length; i++) {
			String htmlLabel = "<TABLE><TR><TD>" + testData[i]
					+ "</TD></TR></TABLE>";
			// the cursor is located before the html label
			newBuilder().append(htmlLabel).assertTextAtCursorPosition(15,
					expectations);
			// the cursor is located after the html label
			newBuilder().append(htmlLabel).assertTextAtCursorPosition(
					15 + testData[i].length(), expectations);
		}
	}

	@Override
	protected ContentAssistProcessorTestBuilder newBuilder() throws Exception {
		return new ContentAssistProcessorTestBuilder(injector, this) {

			/*
			 * configure the ContentAssistProcessorTestBuilder to consider only
			 * the displayString of a proposal and ignore its replacement
			 * strings when determining the proposed text.
			 */
			@Override
			protected String getProposedText(ICompletionProposal proposal) {
				return proposal.getDisplayString();
			}
		};
	}
}
