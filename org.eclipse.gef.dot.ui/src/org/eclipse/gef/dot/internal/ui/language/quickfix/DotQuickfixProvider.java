/*******************************************************************************
 * Copyright (c) 2010, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation
 *     Tamas Miklossy (itemis AG) - Add quickfix support for all dot attributes (bug #513196)
 *     Zoey Gerrit Prigge (itemis AG) - quickfix to remove redundant attributes (bug #540330)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.quickfix;

import static org.eclipse.gef.dot.internal.language.validation.DotJavaValidator.INVALID_EDGE_OPERATOR;
import static org.eclipse.gef.dot.internal.language.validation.DotJavaValidator.REDUNDANT_ATTRIBUTE;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.arrowtype.DeprecatedShape;
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.dir.DirType;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.EdgeOp;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsSubgraph;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode;
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.ranktype.RankType;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedNodeShape;
import org.eclipse.gef.dot.internal.language.shape.RecordBasedNodeShape;
import org.eclipse.gef.dot.internal.language.style.ClusterStyle;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;

/**
 * A quick-fix provider for Dot.
 * 
 * @author anyssen
 */
public class DotQuickfixProvider extends DefaultQuickfixProvider {

	@Fix(INVALID_EDGE_OPERATOR)
	public void fixInvalidEdgeOperator(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		String[] issueData = issue.getData();
		if (issueData != null && issueData.length > 0) {
			String invalidEdgeOperator = issueData[0];
			EdgeOp validEdgeOperator = invalidEdgeOperator
					.equals(EdgeOp.DIRECTED.toString()) ? EdgeOp.UNDIRECTED
							: EdgeOp.DIRECTED;
			provideQuickfix(validEdgeOperator.toString(), "edge operator", //$NON-NLS-1$
					issue, acceptor, new ISemanticModification() {

						@Override
						public void apply(EObject element,
								IModificationContext context) {
							if (element instanceof EdgeRhsNode) {
								EdgeRhsNode edge = (EdgeRhsNode) element;
								edge.setOp(validEdgeOperator);
							}
							if (element instanceof EdgeRhsSubgraph) {
								EdgeRhsSubgraph edge = (EdgeRhsSubgraph) element;
								edge.setOp(validEdgeOperator);
							}
						}
					});
		}
	}

	@Fix(REDUNDANT_ATTRIBUTE)
	public void fixRedundantAttribute(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		if (issue.getData() == null || issue.getData().length == 0) {
			return;
		}

		String attributeName = issue.getData()[0];

		String label = "Remove '" + attributeName + "' attribute."; //$NON-NLS-1$ //$NON-NLS-2$
		String description = "Remove the redundant '" + attributeName //$NON-NLS-1$
				+ "' attribute."; //$NON-NLS-1$
		ISemanticModification semanticModification = (EObject element,
				IModificationContext context) -> EcoreUtil.remove(element);

		acceptor.accept(issue, label, description, null, semanticModification);
	}

	@Fix(DotAttributes.ARROWHEAD__E)
	public void fixArrowheadAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		String[] issueData = issue.getData();
		if (issueData != null && issueData.length > 0) {
			String deprecatedShapeString = issueData[0];
			String validArrowShape = getValidArrowShape(deprecatedShapeString);
			if (validArrowShape != null) {
				provideQuickfix(validArrowShape, "edge arrowhead", issue, //$NON-NLS-1$
						acceptor);
			}
		}
	}

	@Fix(DotAttributes.ARROWTAIL__E)
	public void fixArrowtailAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		String[] issueData = issue.getData();
		if (issueData != null && issueData.length > 0) {
			String deprecatedShapeString = issueData[0];
			String validArrowShape = getValidArrowShape(deprecatedShapeString);
			if (validArrowShape != null) {
				provideQuickfix(validArrowShape, "edge arrowtail", issue, //$NON-NLS-1$
						acceptor);
			}
		}
	}

	@Fix(DotAttributes.CLUSTERRANK__G)
	public void fixClusterRankAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(ClusterMode.values(), "graph clusterMode", issue, //$NON-NLS-1$
				acceptor);
	}

	@Fix(DotAttributes.COLORSCHEME__GCNE)
	public void fixColorschemeAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		// TODO: use "graph colorscheme", "node colorscheme", "edge colorscheme"
		// as suffix.
		provideQuickfixes(DotColors.getColorSchemes(), "colorscheme", //$NON-NLS-1$
				issue, acceptor);
	}

	@Fix(DotAttributes.DIR__E)
	public void fixDirAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(DirType.values(), "edge dir", issue, acceptor); //$NON-NLS-1$
	}

	@Fix(DotAttributes.LAYOUT__G)
	public void fixLayoutAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(Layout.values(), "graph layout", issue, acceptor); //$NON-NLS-1$
	}

	@Fix(DotAttributes.OUTPUTORDER__G)
	public void fixOutputOrderAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(OutputMode.values(), "graph outputMode", issue, //$NON-NLS-1$
				acceptor);
	}

	@Fix(DotAttributes.PAGEDIR__G)
	public void fixPagedirAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(Pagedir.values(), "graph pagedir", issue, //$NON-NLS-1$
				acceptor);
	}

	@Fix(DotAttributes.RANK__S)
	public void fixRankAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(RankType.values(), "subgraph rankType", issue, //$NON-NLS-1$
				acceptor);
	}

	@Fix(DotAttributes.RANKDIR__G)
	public void fixRankdirAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(Rankdir.values(), "graph rankdir", issue, //$NON-NLS-1$
				acceptor);
	}

	@Fix(DotAttributes.SHAPE__N)
	public void fixShapeAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfixes(PolygonBasedNodeShape.VALUES, "node shape", issue, //$NON-NLS-1$
				acceptor);
		provideQuickfixes(RecordBasedNodeShape.VALUES, "node shape", issue, //$NON-NLS-1$
				acceptor);
	}

	@Fix(DotAttributes.STYLE__GCNE)
	public void fixStyleAttributeValue(final Issue issue,
			IssueResolutionAcceptor acceptor) {
		// currently there is no support for quick fixing a style warning
		if (issue.getSeverity() == Severity.WARNING) {
			return;
		}

		String[] issueData = issue.getData();
		if (issueData == null || issueData.length < 2) {
			return;
		}

		DotAttributes.Context attributeContext = DotAttributes.Context
				.valueOf(issueData[1]);
		switch (attributeContext) {
		case GRAPH:
		case SUBGRAPH:
		case CLUSTER:
			provideQuickfixes(ClusterStyle.VALUES, "graph style", issue, //$NON-NLS-1$
					acceptor);
			break;
		case NODE:
			provideQuickfixes(NodeStyle.VALUES, "node style", issue, acceptor); //$NON-NLS-1$
			break;
		case EDGE:
			provideQuickfixes(EdgeStyle.VALUES, "edge style", issue, acceptor); //$NON-NLS-1$
			break;
		default:
			break;
		}
	}

	private String getValidArrowShape(String deprecatedShapeString) {
		DeprecatedShape deprecatedShape = DeprecatedShape
				.get(deprecatedShapeString);
		if (deprecatedShape == null) {
			return null;
		}
		switch (deprecatedShape) {
		case EDIAMOND:
			return "odiamond"; //$NON-NLS-1$
		case OPEN:
			return "vee"; //$NON-NLS-1$
		case HALFOPEN:
			return "lvee"; //$NON-NLS-1$
		case EMPTY:
			return "onormal"; //$NON-NLS-1$
		case INVEMPTY:
			return "oinv"; //$NON-NLS-1$
		default:
			return null;
		}
	}

	private void provideQuickfixes(Object[] validValues, String suffix,
			Issue issue, IssueResolutionAcceptor acceptor) {
		provideQuickfixes(Arrays.asList(validValues), suffix, issue, acceptor);
	}

	private void provideQuickfixes(List<?> validValues, String suffix,
			Issue issue, IssueResolutionAcceptor acceptor) {
		for (Object validValue : validValues) {
			provideQuickfix(validValue.toString(), suffix, issue, acceptor);
		}
	}

	private void provideQuickfix(String validValue, String suffix, Issue issue,
			IssueResolutionAcceptor acceptor) {
		provideQuickfix(validValue, suffix, issue, acceptor,
				new ISemanticModification() {
					@Override
					public void apply(EObject element,
							IModificationContext context) {
						Attribute attribute = (Attribute) element;
						Type type = attribute.getValue().getType();
						ID validValueAsID = ID.fromValue(validValue, type);
						attribute.setValue(validValueAsID);
					}
				});
	}

	private void provideQuickfix(String validValue, String suffix, Issue issue,
			IssueResolutionAcceptor acceptor,
			ISemanticModification semanticModification) {
		acceptor.accept(issue,
				"Replace '" + issue.getData()[0] + "' with '" + validValue //$NON-NLS-1$ //$NON-NLS-2$
						+ "'.", //$NON-NLS-1$
				"Use valid '" + validValue + "' instead of invalid '" //$NON-NLS-1$ //$NON-NLS-2$
						+ issue.getData()[0] + "' " + suffix + ".", //$NON-NLS-1$ //$NON-NLS-2$
				null, semanticModification);
	}
}
