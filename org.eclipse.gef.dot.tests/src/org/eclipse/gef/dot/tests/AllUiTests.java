/*******************************************************************************
 * Copyright (c) 2015, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - merge DotInterpreter into DotImport (bug #491261)
 *                                 - implement additional test cases
 *     Zoey Prigge (itemis AG)     - implement additional test cases
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ // JUnit Tests
		Dot2ZestAttributesConverterTest.class,
		Dot2ZestEdgeAttributesConversionTest.class,
		Dot2ZestGraphCopierTest.class,
		Dot2ZestNodeAttributesConversionTest.class, DotArrowTypeTest.class,
		DotAstTest.class, DotAttributeActiveAnnotationTest.class,
		DotAttributesTest.class, DotBSplineInterpolatorTest.class,
		DotColorListLexerTest.class, DotColorListTest.class,
		DotColorUtilTest.class, DotEscStringTest.class,
		DotExecutableUtilsTest.class, DotExportTest.class,
		DotExtractorTest.class, DotFontNameTest.class, DotFormatterTest.class,
		DotHtmlLabelFormatterTest.class, DotHtmlLabelLexerTest.class,
		DotHtmlLabelParserTest.class, DotHtmlLabelValidatorTest.class,
		DotImportTest.class, DotLexerTest.class, DotParserTest.class,
		DotPortPosTest.class, DotQualifiedNameProviderTest.class,
		DotRecordLabelTest.class, DotStyleTest.class, DotValidatorTest.class,
		GraphvizConfigurationDialogTest.class,

		// JUnit Plug-in Tests
		DotAutoEditTest.class, DotColorListContentAssistLexerTest.class,
		DotColorListHighlightingLexerTest.class, DotContentAssist2Test.class,
		DotContentAssistLexerTest.class, DotContentAssistTest.class,
		DotEditorDoubleClickingTest.class, DotEditorTest.class,
		DotEditorToDotEditorHyperlinkingTest.class, DotFoldingTest.class,
		DotGraphViewTest.class, DotHighlightingCalculatorTest.class,
		DotHighlightingLexerTest.class, DotHighlightingTest.class,
		DotHoverTest.class, DotHtmlLabelContentAssistLexerTest.class,
		DotHtmlLabelContentAssistTest.class,
		DotHtmlLabelHighlightingLexerTest.class, DotHtmlLabelQuickfixTest.class,
		DotHtmlLabelRenameRefactoringTest.class,
		DotHtmlLabelTokenTypeToPartitionMapperTest.class,
		DotLabelProviderTest.class, DotMarkingOccurrencesTest.class,
		DotOutlineViewTest.class, DotQuickfixTest.class,
		DotReferenceFinderTest.class, DotRenameRefactoringTest.class,
		DotToggleCommentTest.class, DotTokenTypeToPartitionMapperTest.class,
		SyncGraphvizExportHandlerTest.class })
public class AllUiTests {
}
