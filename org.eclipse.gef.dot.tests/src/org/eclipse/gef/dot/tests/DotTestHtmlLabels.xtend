/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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
	
	public static val COMMENT_WITH_HYPHEN= '''
		<!-- This-is-a-bold-label -->
			<B>Bold Label</B>
	'''
	
	public static val COMMENT_WITH_NESTED_TAG= '''
		<!-- <nested>
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
	
	public static val FONT_TAG_CONTAINS_TABLE_TAG = '''
		<font color="green">
			<table>
				<tr>
					<td>text</td>
				</tr>
			</table>
		</font>
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
	
	public static val TAG_WITH_ATTRIBUTE = '''
		<BR ALIGN="LEFT"/>
	'''	
}