/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

class DotTestHtmlLabels {

	public static val COMMENT = '''
		<!-- This is a bold label -->
			<B>Bold Label</B>
	'''

	public static val COMMENT_WITH_CLOSE_TAG = '''
		<!-- </tags> -->
		<B>Bold Label</B>
	'''

	public static val COMMENT_WITH_HYPHEN = '''
		<!-- This-is-a-bold-label -->
			<B>Bold Label</B>
	'''

	public static val COMMENT_WITH_NESTED_TAG = '''
		<!--
			<nested>
				<tags/>
			</nested>
			<t>
		-->
		<B>Bold Label</B>
	'''

	public static val COMMENT_WITH_OPEN_TAG = '''
		<!-- <tags> -->
		<B>Bold Label</B>
	'''

	public static val COMMENT_WITHIN_TABLE_TAG = '''
			<TABLE>
				<!-- HTML comment-->
				<TR>
					<TD>left</TD>
				</TR>
			</TABLE>
	'''

	public static val COMMENT_WITHIN_TEXT= '''
			<TABLE>
				<TR>
					<TD>
						left  <!-- HTML comment 1 -->
						      <!-- HTML comment 2 -->
						right <!-- HTML comment 3 -->
					</TD>
				</TR>
			</TABLE>
	'''

	public static val FONT_TAG_CONTAINS_TABLE_TAG = '''
		<!--
			Html label with custom font
		-->
		<font color="green">
			<table>
				<tr>
					<td>text</td>
				</tr>
			</table>
		</font>
	'''

	public static val I_TAG_CONTAINS_TABLE_TAG = '''
		<!--
			Html table with italic syled text
		-->
		<i>
			<table>
				<tr>
					<td>text</td>
				</tr>
			</table>
		</i>
	'''

	public static val B_TAG_CONTAINS_TABLE_TAG = '''
		<!--
			Html table with bold syled text
		-->
		<b>
			<table>
				<tr>
					<td>text</td>
				</tr>
			</table>
		</b>
	'''

	public static val U_TAG_CONTAINS_TABLE_TAG = '''
		<!--
			Html table with underlined text
		-->
		<u>
			<table>
				<tr>
					<td>text</td>
				</tr>
			</table>
		</u>
	'''

	public static val O_TAG_CONTAINS_TABLE_TAG = '''
		<!--
			Html table with overlined text
		-->
		<o>
			<table>
				<tr>
					<td>text</td>
				</tr>
			</table>
		</o>
	'''

	public static val FONT_TAG_WITH_POINT_SIZE_ATTRIBUTE = '''
		<FONT POINT-SIZE="24.0">
			line3
		</FONT>
	'''

	public static val MIXED_LOWER_CASE_AND_UPPER_CASE = '''
		<b>
			string
		</B>
	'''

	public static val NESTED_TAGS = '''
		<table>
			<tr><td>first</td></tr>
			<tr><td><table><tr><td><b>second</b></td></tr></table></td></tr>
		</table>'''

	public static val NESTED_TAGS2 = '''
		<!-- no nesting -->
		<font>font</font>
		<i>i</i>
		<b>b</b>
		<u>u</u>
		<o>o</o>
		<sub>sub</sub>
		<sup>sup</sup>
		<s>s</s>

		<br/><br/><!-- only placeholder -->
		
		<!-- nested within font -->
		<font><br/>fontbr</font>
		<font><font>fontfont</font></font>
		<font><i>fonti</i></font>
		<font><b>fontb</b></font>
		<font><u>fontu</u></font>
		<font><o>fonto</o></font>
		<font><sub>fontsub</sub></font>
		<font><sup>fontsup</sup></font>
		<font><s>fonts</s></font>

		<br/><br/><!-- only placeholder -->
		
		<!-- nested within i -->
		<i><br/>ibr</i>
		<i><font>ifont</font></i>
		<i><i>ii</i></i>
		<i><b>ib</b></i>
		<i><u>iu</u></i>
		<i><o>io</o></i>
		<i><sub>isub</sub></i>
		<i><sup>isup</sup></i>
		<i><s>is</s></i>

		<br/><br/><!-- only placeholder -->

		<!-- nested within b -->
		<b><br/>bbr</b>
		<b><font>bfont</font></b>
		<b><i>bi</i></b>
		<b><b>bb</b></b>
		<b><u>bu</u></b>
		<b><o>bo</o></b>
		<b><sub>bsub</sub></b>
		<b><sup>bsup</sup></b>
		<b><s>bs</s></b>

		<br/><br/><!-- only placeholder -->

		<!-- nested within u -->
		<u><br/>ubr</u>
		<u><font>ufont</font></u>
		<u><i>ui</i></u>
		<u><b>ub</b></u>
		<u><u>uu</u></u>
		<u><o>uo</o></u>
		<u><sub>usub</sub></u>
		<u><sup>usup</sup></u>
		<u><s>us</s></u>

		<br/><br/><!-- only placeholder -->

		<!-- nested within o -->
		<o><br/>bbr</o>
		<o><font>ofont</font></o>
		<o><i>oi</i></o>
		<o><b>ob</b></o>
		<o><u>ou</u></o>
		<o><o>oo</o></o>
		<o><sub>osub</sub></o>
		<o><sup>osup</sup></o>
		<o><s>os</s></o>

		<br/><br/><!-- only placeholder -->

		<!-- nested within sub -->
		<sub><br/>subbr</sub>
		<sub><font>subfont</font></sub>
		<sub><i>subi</i></sub>
		<sub><b>subb</b></sub>
		<sub><u>subu</u></sub>
		<sub><o>subo</o></sub>
		<sub><sub>subsub</sub></sub>
		<sub><sup>subsup</sup></sub>
		<sub><s>subs</s></sub>

		<br/><br/><!-- only placeholder -->

		<!-- nested within sup -->
		<sup><br/>subbr</sup>
		<sup><font>supfont</font></sup>
		<sup><i>supi</i></sup>
		<sup><b>supb</b></sup>
		<sup><u>supu</u></sup>
		<sup><o>supo</o></sup>
		<sup><sub>supsub</sub></sup>
		<sup><sup>supsup</sup></sup>
		<sup><s>sups</s></sup>

		<br/><br/><!-- only placeholder -->

		<!-- nested within s -->
		<s><br/>sbr</s>
		<s><font>sfont</font></s>
		<s><i>si</i></s>
		<s><b>sb</b></s>
		<s><u>su</u></s>
		<s><o>so</o></s>
		<s><sub>ssub</sub></s>
		<s><sup>ssup</sup></s>
		<s><s>ss</s></s>
	'''

	public static val SELF_CLOSING_TAGS = '''
		<TABLE>
			<TR>
				<TD>nord<BR/>west</TD>
				<VR/>
				<TD>nord<BR/>east</TD>
			</TR>
			<HR/>
			<TR>
				<TD>south<BR/>west</TD>
				<VR/>
				<TD>south<BR/>east</TD>
			</TR>
		</TABLE>
	'''

	public static val TAG_WITH_SINGLE_QUTOED_ATTRIBUTE_VALUE = '''
		<BR ALIGN='LEFT'/>
	'''

	public static val TAG_WITH_DOUBLE_QUOTED_ATTRIBUTE_VALUE = '''
		<BR ALIGN="LEFT"/>
	'''

	public static val QUOTES_IN_HTML_TEXT = '''
		"text"
	'''
}