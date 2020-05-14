/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *    Zoey Prigge (itemis AG)    - include parsedAsAttribute (bug #548911)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.xtend.core.compiler.batch.XtendCompilerTester
import org.junit.Test

class DotAttributeActiveAnnotationTest {

	extension XtendCompilerTester = XtendCompilerTester.newXtendCompilerTester(class.classLoader)

	@Test def subgraph_rank() {
		'''
			package org.eclipse.gef.dot.internal
			
			import org.eclipse.gef.dot.internal.generator.DotAttribute
			import org.eclipse.gef.dot.internal.language.ranktype.RankType
			import org.eclipse.gef.dot.internal.language.terminals.ID
			
			class DotAttributes {
				static enum Context {
					GRAPH, EDGE, NODE, SUBGRAPH, CLUSTER
				}
				
				@DotAttribute(rawType="STRING", parsedType=RankType)
				public static val String RANK__S = "rank"
				
				//method body is generated using @DotAttribute active annotation
				public static def Object parsedAsAttribute(ID valueRaw, String attrName, Context context){}
			}
		'''.assertCompilesTo('''
			package org.eclipse.gef.dot.internal;
			
			import org.eclipse.gef.dot.internal.language.ranktype.RankType;
			import org.eclipse.gef.dot.internal.language.terminals.ID;
			import org.eclipse.gef.graph.Graph;
			
			@SuppressWarnings("all")
			public class DotAttributes {
			  public enum Context {
			    GRAPH,
			    
			    EDGE,
			    
			    NODE,
			    
			    SUBGRAPH,
			    
			    CLUSTER;
			  }
			  
			  /**
			   * The 'rank' attribute, which is used by: Subgraph.
			   */
			  public static final String RANK__S = "rank";
			  
			  public static Object parsedAsAttribute(final ID valueRaw, final String attrName, final DotAttributes.Context context) {
			    switch (context) {
			    case GRAPH:
			      switch (attrName.toLowerCase(java.util.Locale.ENGLISH)) {
			      case RANK__S:
			        return parseAttributeValue(RANKTYPE_PARSER, valueRaw != null ? valueRaw.toValue() : null);
			      }
			    case NODE:
			      switch (attrName.toLowerCase(java.util.Locale.ENGLISH)) {
			      }
			    case EDGE:
			      switch (attrName.toLowerCase(java.util.Locale.ENGLISH)) {
			      }
			    case SUBGRAPH:
			      switch (attrName.toLowerCase(java.util.Locale.ENGLISH)) {
			      }
			    case CLUSTER:
			      switch (attrName.toLowerCase(java.util.Locale.ENGLISH)) {
			      }
			    default:
			      return valueRaw != null ? valueRaw.toValue() : null;
			    }
			  }
			  
			  /**
			   * Returns the (raw) value of the {@link #RANK__S} attribute of the given {@link Graph}.
			   *     @param graph
			   *                The {@link Graph} for which to return the value of the
			   *                {@link #RANK__S} attribute.
			   *     @return The (raw) value of the {@link #RANK__S} attribute of the given
			   *             {@link Graph}.
			   * 
			   */
			  public static ID getRankRaw(final Graph graph) {
			    return (ID) graph.attributesProperty().get(RANK__S);
			  }
			  
			  /**
			   * Sets the (raw) value of the {@link #RANK__S} attribute of the given {@link Graph}
			   * to the given <i>rank</i> value.
			   *     @param graph
			   *                The {@link Graph} for which to change the value of the
			   *                {@link #RANK__S} attribute.
			   *     @param rank
			   *                The new (raw) value of the {@link #RANK__S} attribute.
			   *     @throws IllegalArgumentException
			   *                when the given <i>rank</i> value is not supported.
			   * 
			   */
			  public static void setRankRaw(final Graph graph, final ID rank) {
			    checkAttributeRawValue(Context.GRAPH, RANK__S, rank);
			    graph.attributesProperty().put(RANK__S, rank);
			  }
			  
			  /**
			   * Returns the value of the {@link #RANK__S} attribute of the given {@link Graph}.
			   *     @param graph
			   *            The {@link Graph} for which to return the value of the {@link #RANK__S} attribute.
			   *     @return The value of the {@link #RANK__S} attribute of the given {@link Graph}.
			   * 
			   */
			  public static String getRank(final Graph graph) {
			    ID rankRaw = getRankRaw(graph);
			    return rankRaw != null ? rankRaw.toValue() : null;
			  }
			  
			  /**
			   * Sets the value of the {@link #RANK__S} attribute of the given {@link Graph} to the given <i>rank</i> value.
			   *     @param graph
			   *                The {@link Graph} for which to change the value of the {@link #RANK__S} attribute.
			   *     @param rank
			   *            The new value of the {@link #RANK__S} attribute.
			   *     @throws IllegalArgumentException
			   *            when the given <i>rank</i> value is not supported.
			   * 
			   */
			  public static void setRank(final Graph graph, final String rank) {
			    setRankRaw(graph, ID.fromValue(rank, org.eclipse.gef.dot.internal.language.terminals.ID.Type.STRING));
			  }
			  
			  /**
			   * Returns the (parsed) value of the {@link #RANK__S} attribute of the given {@link Graph}.
			   *     @param graph
			   *                 The {@link Graph} for which to return the value of the {@link #RANK__S} attribute.
			   *     @return The (parsed) value of the {@link #RANK__S} attribute of the given {@link Graph}.
			   * 
			   */
			  public static RankType getRankParsed(final Graph graph) {
			    return parseAttributeValue(RANKTYPE_PARSER, getRank(graph));
			  }
			  
			  /**
			   * Sets the (parsed) value of the {@link #RANK__S} attribute of the given {@link Graph} to the given <i>rank</i> value.
			   *     @param graph
			   *                The {@link Graph} for which to change the value of the {@link #RANK__S} attribute.
			   *     @param rank
			   *                The new (parsed) value of the {@link #RANK__S} attribute.
			   *     @throws IllegalArgumentException
			   *                when the given <i>rank</i> value is not supported.
			   * 
			   */
			  public static void setRankParsed(final Graph graph, final RankType rank) {
			    setRank(graph, serializeAttributeValue(RANKTYPE_SERIALIZER, rank));
			  }
			}
		''')
	}
}