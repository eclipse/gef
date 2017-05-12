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
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
@RunWith(XtextRunner.class)
@InjectWith(DotHtmlLabelUiInjectorProvider.class)
public class DotHtmlLabelContentAssistTests extends AbstractContentAssistTest {

	@Inject
	IResourceFactory resourceFactory;

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
		newBuilder().append("<TABLE ></TABLE>").assertTextAtCursorPosition(7,
				"ALIGN", "BGCOLOR", "BORDER", "CELLBORDER", "CELLPADDING",
				"CELLSPACING", "COLOR", "COLUMNS", "FIXEDSIZE", "GRADIENTANGLE",
				"HEIGHT", "HREF", "ID", "PORT", "ROWS", "SIDES", "STYLE",
				"TARGET", "TITLE", "TOOLTIP", "VALIGN", "WIDTH");
	}

	@Test
	public void attributesOfTagTD() throws Exception {
		newBuilder().append("<TD ></TD>").assertTextAtCursorPosition(4, "ALIGN",
				"BALIGN", "BGCOLOR", "BORDER", "CELLPADDING", "CELLSPACING",
				"COLOR", "COLSPAN", "FIXEDSIZE", "GRADIENTANGLE", "HEIGHT",
				"HREF", "ID", "PORT", "ROWSPAN", "SIDES", "STYLE", "TARGET",
				"TITLE", "TOOLTIP", "VALIGN", "WIDTH");
	}

	@Test
	public void attributesOfTagFONT() throws Exception {
		newBuilder().append("<FONT ></FONT>").assertTextAtCursorPosition(6,
				"COLOR", "FACE", "POINT-SIZE");
	}

	@Test
	public void attributesOfTagBR() throws Exception {
		newBuilder().append("<BR />").assertTextAtCursorPosition(4, "ALIGN");
	}

	@Test
	public void attributesOfTagIMG() throws Exception {
		newBuilder().append("<IMG />").assertTextAtCursorPosition(5, "SCALE",
				"SRC");
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

}
