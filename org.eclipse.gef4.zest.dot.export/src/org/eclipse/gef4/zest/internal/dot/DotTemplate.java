package org.eclipse.gef4.zest.internal.dot;

import org.eclipse.gef4.zest.core.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.gef4.zest.layouts.algorithms.*;
import org.eclipse.gef4.zest.layouts.*;

public class DotTemplate
{
  protected static String nl;
  public static synchronized DotTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    DotTemplate result = new DotTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = " ";
  protected final String TEXT_4 = "{" + NL + "" + NL + "\t/* Global settings */" + NL + "\tgraph[layout=";
  protected final String TEXT_5 = "]" + NL + "\tnode[shape=box] //more like the Zest default node look" + NL + "\trankdir=";
  protected final String TEXT_6 = NL + "\t" + NL + "\t/* Nodes */" + NL + "\t";
  protected final String TEXT_7 = " " + NL + "\t";
  protected final String TEXT_8 = "[label=\"";
  protected final String TEXT_9 = "\"];";
  protected final String TEXT_10 = NL + "\t";
  protected final String TEXT_11 = " " + NL + "\t  ";
  protected final String TEXT_12 = " ";
  protected final String TEXT_13 = NL + "        ";
  protected final String TEXT_14 = "[label=\"";
  protected final String TEXT_15 = "\"];";
  protected final String TEXT_16 = NL + "\t";
  protected final String TEXT_17 = NL + "\t";
  protected final String TEXT_18 = NL + "\t" + NL + "\t/* Edges */" + NL + "\t";
  protected final String TEXT_19 = " " + NL + "\t";
  protected final String TEXT_20 = " " + NL + "\t";
  protected final String TEXT_21 = " ";
  protected final String TEXT_22 = " ";
  protected final String TEXT_23 = "[style=";
  protected final String TEXT_24 = " label=\"";
  protected final String TEXT_25 = "\"];" + NL + "\t";
  protected final String TEXT_26 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    /*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and 
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
     Graph graph = (Graph) argument; 
     boolean small = graph.getNodes().size() < 100; 
     LayoutAlgorithm algo = graph.getLayoutAlgorithm() != null ? graph.getLayoutAlgorithm() : new TreeLayoutAlgorithm(); 
     boolean digraph = graph.getConnectionStyle()==ZestStyles.CONNECTIONS_DIRECTED; 
     String simpleClassName = graph.getClass().getSimpleName(); 
     /* The exact name 'Graph' is not valid for rendering with Graphviz: */ 
     simpleClassName = simpleClassName.equals("Graph") ? "Zest" + simpleClassName : simpleClassName; 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    stringBuffer.append( digraph ? "digraph" : "graph" );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(simpleClassName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append((algo.getClass() == RadialLayoutAlgorithm.class) ? "twopi" : (algo.getClass() == GridLayoutAlgorithm.class) ? "osage" : (algo.getClass() == SpringLayoutAlgorithm.class) ? (small ? "fdp" : "sfdp") : "dot");
    stringBuffer.append(TEXT_5);
    stringBuffer.append((graph.getLayoutAlgorithm() != null && graph.getLayoutAlgorithm().getClass() == TreeLayoutAlgorithm.class && ((TreeLayoutAlgorithm)graph.getLayoutAlgorithm()).getDirection() == TreeLayoutAlgorithm.LEFT_RIGHT)?"LR":"TD");
    stringBuffer.append(TEXT_6);
     for(Object nodeObject : graph.getNodes()){ GraphNode node = (GraphNode) nodeObject; 
    stringBuffer.append(TEXT_7);
     if(!(node instanceof GraphContainer)) {
    stringBuffer.append(node.hashCode());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(node.getText());
    stringBuffer.append(TEXT_9);
    }
    stringBuffer.append(TEXT_10);
     if(node instanceof GraphContainer) {
    stringBuffer.append(TEXT_11);
     for(Object o : ((GraphContainer) node ).getNodes()){ GraphNode n = (GraphNode) o; 
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(n.hashCode());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(n.getText());
    stringBuffer.append(TEXT_15);
     }
    stringBuffer.append(TEXT_16);
     }
    stringBuffer.append(TEXT_17);
     }
    stringBuffer.append(TEXT_18);
     for(Object edgeObject : graph.getConnections()){ GraphConnection edge = (GraphConnection) edgeObject; 
    stringBuffer.append(TEXT_19);
    boolean dashed = edge.getLineStyle() == SWT.LINE_DASH; boolean dotted = edge.getLineStyle() == SWT.LINE_DOT;
    stringBuffer.append(TEXT_20);
    stringBuffer.append(edge.getSource().hashCode());
    stringBuffer.append(TEXT_21);
    stringBuffer.append( digraph ? "->" : "--" );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(edge.getDestination().hashCode());
    stringBuffer.append(TEXT_23);
    stringBuffer.append(dashed?"dashed":dotted?"dotted":"solid");
    stringBuffer.append(TEXT_24);
    stringBuffer.append(edge.getText());
    stringBuffer.append(TEXT_25);
     }
    stringBuffer.append(TEXT_26);
    return stringBuffer.toString();
  }
}
