/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.common.collect.Lists
import com.google.inject.Inject
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IMarker
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.MarkerTypes
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.editor.quickfix.MarkerResolutionGenerator
import org.eclipse.xtext.ui.testing.AbstractEditorTest
import org.eclipse.xtext.ui.testing.util.AnnotatedTextToString
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.addNature
import static extension org.eclipse.xtext.util.Strings.toUnixLineSeparator

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotMultiQuickfixTest extends AbstractEditorTest {

	@Inject extension FileExtensionProvider
	@Inject extension MarkerResolutionGenerator

	@Test def single_quickfix__graph_contains_directed_edge_to_node() {
		'''
			graph {
				1->2
			}
		'''.testSingleQuickfix('''
			graph {
				1<0<->>0>2
			}
			--------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1--2
			}
			--------
			(no markers found)
		''')
	}

	@Test def single_quickfix__graph_contains_directed_multi_edge_to_node_1() {
		'''
			graph {
				1->2--3
			}
		'''.testSingleQuickfix('''
			graph {
				1<0<->>0>2--3
			}
			-----------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1--2--3
			}
			-----------
			(no markers found)
		''')
	}

	@Test def single_quickfix__graph_contains_directed_multi_edge_to_node_2() {
		'''
			graph {
				1--2->3
			}
		'''.testSingleQuickfix('''
			graph {
				1--2<0<->>0>3
			}
			-----------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1--2--3
			}
			-----------
			(no markers found)
		''')
	}

	@Test def single_quickfix__graph_contains_directed_edge_to_subgraph() {
		'''
			graph {
				1 -> subgraph {
					2 3
				}
			}
		'''.testSingleQuickfix('''
			graph {
				1 <0<->>0> subgraph {
					2 3
				}
			}
			-------------------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1 -- subgraph {
					2 3
				}
			}
			-------------------
			(no markers found)
		''')
	}

	@Test def single_quickfix__graph_contains_directed_multi_edge_to_subgraph_1() {
		'''
			graph {
				1 -> subgraph {
					2 3
				} -- subgraph { 4 }
			}
		'''.testSingleQuickfix('''
			graph {
				1 <0<->>0> subgraph {
					2 3
				} -- subgraph { 4 }
			}
			-------------------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1 -- subgraph {
					2 3
				} -- subgraph { 4 }
			}
			-----------------------
			(no markers found)
		''')
	}

	@Test def single_quickfix__graph_contains_directed_multi_edge_to_subgraph_2() {
		'''
			graph {
				1 -- subgraph {
					2 3
				} -> subgraph { 4 }
			}
		'''.testSingleQuickfix('''
			graph {
				1 -- subgraph {
					2 3
				} <0<->>0> subgraph { 4 }
			}
			-----------------------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1 -- subgraph {
					2 3
				} -- subgraph { 4 }
			}
			-----------------------
			(no markers found)
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_edge_to_node() {
		'''
			digraph {
				1--2
			}
		'''.testSingleQuickfix('''
			digraph {
				1<0<-->0>2
			}
			--------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1->2
			}
			---------
			(no markers found)
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_multi_edge_to_node_1() {
		'''
			digraph {
				1--2->3
			}
		'''.testSingleQuickfix('''
			digraph {
				1<0<-->0>2->3
			}
			-----------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1->2->3
			}
			-----------
			(no markers found)
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_multi_edge_to_node_2() {
		'''
			digraph {
				1->2--3
			}
		'''.testSingleQuickfix('''
			digraph {
				1->2<0<-->0>3
			}
			-----------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1->2->3
			}
			-----------
			(no markers found)
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_edge_to_subgraph() {
		'''
			digraph {
				1 -- subgraph {
					2 3
				}
			}
		'''.testSingleQuickfix('''
			digraph {
				1 <0<-->0> subgraph {
					2 3
				}
			}
			-------------------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1 -> subgraph {
					2 3
				}
			}
			-------------------
			(no markers found)
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_multi_edge_to_subgraph_1() {
		'''
			digraph {
				1 -- subgraph {
					2 3
				} -> subgraph { 4 }
			}
		'''.testSingleQuickfix('''
			digraph {
				1 <0<-->0> subgraph {
					2 3
				} -> subgraph { 4 }
			}
			-------------------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1 -> subgraph {
					2 3
				} -> subgraph { 4 }
			}
			-----------------------
			(no markers found)
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_multi_edge_to_subgraph_2() {
		'''
			digraph {
				1 -> subgraph {
					2 3
				} -- subgraph { 4 }
			}
		'''.testSingleQuickfix('''
			digraph {
				1 -> subgraph {
					2 3
				} <0<-->0> subgraph { 4 }
			}
			-----------------------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1 -> subgraph {
					2 3
				} -> subgraph { 4 }
			}
			-----------------------
			(no markers found)
		''')
	}

	@Test def single_quickfix__graph_contains_directed_edges() {
		'''
			graph {
				1->2
				2->3
			}
		'''.testSingleQuickfix('''
			graph {
				1<0<->>0>2
				2<1<->>1>3
			}
			--------------
			0: message=EdgeOp '->' may only be used in directed graphs.
			1: message=EdgeOp '->' may only be used in directed graphs.
		''', [firstMarker], '''
			graph {
				1--2
				2<0<->>0>3
			}
			--------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''')
	}

	@Test def single_quickfix__graph_contains_directed_multi_edges() {
		'''
			graph {
				1->2->3
			}
		'''.testSingleQuickfix('''
			graph {
				1<0<->>0>2<1<->>1>3
			}
			-----------------------
			0: message=EdgeOp '->' may only be used in directed graphs.
			1: message=EdgeOp '->' may only be used in directed graphs.
		''', [firstMarker], '''
			graph {
				1--2<0<->>0>3
			}
			-----------------
			0: message=EdgeOp '->' may only be used in directed graphs.
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_edges() {
		'''
			digraph {
				1--2
				2--3
			}
		'''.testSingleQuickfix('''
			digraph {
				1<0<-->0>2
				2<1<-->1>3
			}
			--------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
			1: message=EdgeOp '--' may only be used in undirected graphs.
		''', [firstMarker], '''
			digraph {
				1->2
				2<0<-->0>3
			}
			--------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''')
	}

	@Test def single_quickfix__digraph_contains_undirected_multi_edges() {
		'''
			digraph {
				1--2--3
			}
		'''.testSingleQuickfix('''
			digraph {
				1<0<-->0>2<1<-->1>3
			}
			-----------------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
			1: message=EdgeOp '--' may only be used in undirected graphs.
		''', [firstMarker], '''
			digraph {
				1->2<0<-->0>3
			}
			-----------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
		''')
	}

	@Test def multi_quickfix__graph_contains_directed_edges() {
		'''
			graph {
				1->2
				2->3
			}
		'''.testMultiQuickfix('''
			graph {
				1<0<->>0>2
				2<1<->>1>3
			}
			--------------
			0: message=EdgeOp '->' may only be used in directed graphs.
			1: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1--2
				2--3
			}
			--------
			(no markers found)
		''')
	}

	@Test def multi_quickfix__graph_contains_directed_multi_edges() {
		'''
			graph {
				1->2->3
			}
		'''.testMultiQuickfix('''
			graph {
				1<0<->>0>2<1<->>1>3
			}
			-----------------------
			0: message=EdgeOp '->' may only be used in directed graphs.
			1: message=EdgeOp '->' may only be used in directed graphs.
		''', '''
			graph {
				1--2--3
			}
			-----------
			(no markers found)
		''')
	}

	@Test def multi_quickfix__digraph_contains_undirected_edges() {
		'''
			digraph {
				1--2
				2--3
			}
		'''.testMultiQuickfix('''
			digraph {
				1<0<-->0>2
				2<1<-->1>3
			}
			--------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
			1: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1->2
				2->3
			}
			---------
			(no markers found)
		''')
	}

	@Test def multi_quickfix__digraph_contains_undirected_multi_edges() {
		'''
			digraph {
				1--2--3
			}
		'''.testMultiQuickfix('''
			digraph {
				1<0<-->0>2<1<-->1>3
			}
			-----------------------
			0: message=EdgeOp '--' may only be used in undirected graphs.
			1: message=EdgeOp '--' may only be used in undirected graphs.
		''', '''
			digraph {
				1->2->3
			}
			-----------
			(no markers found)
		''')
	}

	private def testSingleQuickfix(CharSequence it, CharSequence initialWithMarkers, CharSequence resultWithMarkers) {
		testSingleQuickfix(initialWithMarkers, [firstMarker], resultWithMarkers)
	}

	private def testSingleQuickfix(CharSequence it, CharSequence initialWithMarkers, extension (IMarker[])=>IMarker markerProvider, CharSequence resultWithMarkers) {
		// Given
		val file = dslFile
		file.hasContentWithMarkers(initialWithMarkers)

		// When
		val marker = file.markers.apply
		applyQuickfixOnSingleMarker(marker)

		// Then
		file.hasContentWithMarkers(resultWithMarkers)
	}

	private def testMultiQuickfix(CharSequence it, CharSequence initialWithMarkers, CharSequence resultWithMarkers) {
		// Given
		val file = dslFile
		file.hasContentWithMarkers(initialWithMarkers)

		// When
		applyQuickfixOnMultipleMarkers(file.markers)

		// Then
		file.hasContentWithMarkers(resultWithMarkers)
	}

	private def dslFile(CharSequence content) {
		val file = IResourcesSetupUtil.createFile(projectName, fileName, fileExtension, content.toString)

		val project = file.project
		if(!project.hasNature(XtextProjectHelper.NATURE_ID)) {
			project.addNature(XtextProjectHelper.NATURE_ID)
		}

		file
	}

	private def hasContentWithMarkers(IFile file, CharSequence expectation) {
		val actual = new AnnotatedTextToString().withFile(file).withMarkers(file.markers).toString.trim
		val expected = expectation.toString.trim
		assertEquals(expected.toUnixLineSeparator(), actual.toUnixLineSeparator)
	}

	private def void applyQuickfixOnSingleMarker(IMarker marker) {
		val resolutions = marker.resolutions
		assertEquals(1, resolutions.length)
		val resolution = resolutions.head
		resolution.run(marker)
	}

	private def void applyQuickfixOnMultipleMarkers(IMarker[] markers) {
		val primaryMarker = markers.head
		val resolutions = primaryMarker.resolutions
		Assert.assertEquals(1, resolutions.length)
		val resolution = resolutions.head 
		assertTrue(resolution instanceof WorkbenchMarkerResolution)
		val workbenchMarkerResolution = resolution as WorkbenchMarkerResolution
		val others = Lists.newArrayList(workbenchMarkerResolution.findOtherMarkers(markers))
		assertFalse(others.contains(primaryMarker))
		assertEquals(markers.length - 1, others.size)
		others.add(primaryMarker)
		workbenchMarkerResolution.run(others, new NullProgressMonitor)
	}

	private def IMarker getFirstMarker(IMarker[] markers) {
		markers.sortBy[it.getAttribute(IMarker.CHAR_START) as Integer].head
	}

	private def IMarker[] getMarkers(IFile file) {
		IResourcesSetupUtil.waitForBuild
		file.findMarkers(MarkerTypes.FAST_VALIDATION, true,	IResource.DEPTH_INFINITE)
	}

	private def getProjectName() {
		"MultiQuickfixTestProject"
	}

	private def getFileName() {
		"test"
	}

	private def getFileExtension() {
		primaryFileExtension
	}
}