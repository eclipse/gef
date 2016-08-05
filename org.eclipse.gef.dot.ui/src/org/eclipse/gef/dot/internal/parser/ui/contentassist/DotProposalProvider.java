/*******************************************************************************
 * Copyright (c) 2010, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *                                - Improve the content assistant support (bug #498324)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.parser.ui.contentassist;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotAttributes.AttributeContext;
import org.eclipse.gef.dot.internal.parser.conversion.DotTerminalConverters;
import org.eclipse.gef.dot.internal.parser.dir.DirType;
import org.eclipse.gef.dot.internal.parser.dot.AttrList;
import org.eclipse.gef.dot.internal.parser.dot.Attribute;
import org.eclipse.gef.dot.internal.parser.dot.DotGraph;
import org.eclipse.gef.dot.internal.parser.dot.EdgeOp;
import org.eclipse.gef.dot.internal.parser.dot.GraphType;
import org.eclipse.gef.dot.internal.parser.dot.NodeStmt;
import org.eclipse.gef.dot.internal.parser.layout.Layout;
import org.eclipse.gef.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.parser.services.DotGrammarAccess;
import org.eclipse.gef.dot.internal.parser.splines.Splines;
import org.eclipse.gef.dot.internal.parser.style.EdgeStyle;
import org.eclipse.gef.dot.internal.parser.style.NodeStyle;
import org.eclipse.gef.dot.internal.parser.ui.internal.DotActivator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.google.inject.Inject;

/**
 * A proposal provider for Dot.
 * 
 * @author anyssen
 */
public class DotProposalProvider extends AbstractDotProposalProvider {

	@Inject
	DotGrammarAccess dotGrammarAccess;

	private static Map<AttributeContext, List<String>> dotAttributeNames;
	private String[] booleanAttributeValuesProposals = { "true", "false" }; //$NON-NLS-1$ //$NON-NLS-2$

	public DotProposalProvider() {
		// collect the dot attribute names on demand
		if (dotAttributeNames == null) {
			dotAttributeNames = getDotAttributeNames();
		}
	}

	@Override
	protected ICompletionProposal createCompletionProposal(String proposal,
			StyledString displayString, Image image, int priority,
			String prefix, ContentAssistContext context) {

		if (context.getCurrentModel() instanceof DotGraph
				&& ((DotGraph) context.getCurrentModel())
						.getType() == GraphType.DIGRAPH
				&& proposal.equals(EdgeOp.UNDIRECTED.toString())) {
			// do not propose the undirected edge operator in case of a directed
			// graph
			return null;
		}

		if (context.getCurrentModel() instanceof DotGraph
				&& ((DotGraph) context.getCurrentModel())
						.getType() == GraphType.GRAPH
				&& proposal.equals(EdgeOp.DIRECTED.toString())) {
			// do not propose the directed edge operator in case of an
			// undirected graph
			return null;
		}

		if (context.getPrefix().equals("=") && proposal.equals("=")) { //$NON-NLS-1$//$NON-NLS-2$
			// do not propose the "=" symbol if it is already included in the
			// text as prefix
			return null;
		}

		if (context.getPrefix().equals("[") && proposal.equals("[")) { //$NON-NLS-1$ //$NON-NLS-2$
			// do not propose the "[" symbol if it is already included in the
			// text as prefix
			return null;
		}

		ICompletionProposal completionProposal = super.createCompletionProposal(
				proposal, displayString, image, priority, prefix, context);

		// ensure that the double quote at the beginning of an attribute value
		// is not overridden when applying the proposal
		if (completionProposal instanceof ConfigurableCompletionProposal
				&& context.getCurrentModel() instanceof Attribute) {
			INode currentNode = context.getCurrentNode();
			String text = currentNode.getText();
			if (text.startsWith("\"")) { //$NON-NLS-1$
				ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;
				configurableCompletionProposal.setReplacementOffset(
						configurableCompletionProposal.getReplacementOffset()
								+ 1);
				configurableCompletionProposal.setReplacementLength(
						configurableCompletionProposal.getReplacementLength()
								- 1);
			}
		}
		return completionProposal;
	}

	@Override
	protected boolean isValidProposal(String proposal, String prefix,
			ContentAssistContext context) {
		// consider a double quote as a valid prefix for the attribute values
		if (context.getCurrentModel() instanceof Attribute
				&& prefix.startsWith("\"")) { //$NON-NLS-1$
			return super.isValidProposal(proposal, prefix.substring(1),
					context);
		}

		return super.isValidProposal(proposal, prefix, context);
	}

	@Override
	public void completePort_Compass_pt(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		for (Keyword k : EcoreUtil2.getAllContentsOfType(
				dotGrammarAccess.getCOMPASS_PTRule().getAlternatives(),
				Keyword.class)) {
			acceptor.accept(createCompletionProposal(k.getValue(), context));
		}
		super.completePort_Compass_pt(model, assignment, context, acceptor);
	}

	@Override
	public void completeAttribute_Name(EObject model, Assignment assignment,
			ContentAssistContext contentAssistContext,
			ICompletionProposalAcceptor acceptor) {

		super.completeAttribute_Name(model, assignment, contentAssistContext,
				acceptor);

		if (model instanceof AttrList) {
			AttributeContext attributeContext = DotAttributes.getContext(model);
			proposeAttributeNames(attributeContext, contentAssistContext,
					acceptor);
		} else if (model instanceof DotGraph || model instanceof NodeStmt) {
			proposeAttributeNames(AttributeContext.GRAPH, contentAssistContext,
					acceptor);
		}
	}

	@Override
	public void completeAttribute_Value(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if (model instanceof Attribute) {
			Attribute attribute = (Attribute) model;
			if (DotAttributes.isEdgeAttribute(attribute)) {
				switch (attribute.getName()) {
				case DotAttributes.ARROWHEAD__E:
				case DotAttributes.ARROWTAIL__E:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_PARSER_DOTARROWTYPE,
							context, acceptor);
					break;
				case DotAttributes.DIR__E:
					proposeAttributeValues(DirType.values(), context, acceptor);
					break;
				case DotAttributes.HEAD_LP__E:
				case DotAttributes.LP__GE:
				case DotAttributes.TAIL_LP__E:
				case DotAttributes.XLP__NE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_PARSER_DOTPOINT,
							context, acceptor);
					break;
				case DotAttributes.POS__NE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_PARSER_DOTSPLINETYPE,
							context, acceptor);
					break;
				case DotAttributes.STYLE__GNE:
					proposeAttributeValues(EdgeStyle.VALUES, context, acceptor);
					break;
				default:
					super.completeAttribute_Value(model, assignment, context,
							acceptor);
					break;
				}
			} else if (DotAttributes.isGraphAttribute(attribute)) {
				switch (attribute.getName()) {
				case DotAttributes.FORCELABELS__G:
					proposeAttributeValues(booleanAttributeValuesProposals,
							context, acceptor);
					break;
				case DotAttributes.LAYOUT__G:
					proposeAttributeValues(Layout.values(), context, acceptor);
					break;
				case DotAttributes.LP__GE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_PARSER_DOTPOINT,
							context, acceptor);
					break;
				case DotAttributes.RANKDIR__G:
					proposeAttributeValues(Rankdir.values(), context, acceptor);
					break;
				case DotAttributes.SPLINES__G:
					proposeAttributeValues(Splines.values(), context, acceptor);
					break;
				default:
					super.completeAttribute_Value(model, assignment, context,
							acceptor);
					break;
				}
			} else if (DotAttributes.isNodeAttribute(attribute)) {
				switch (attribute.getName()) {
				case DotAttributes.FIXEDSIZE__N:
					proposeAttributeValues(booleanAttributeValuesProposals,
							context, acceptor);
					break;
				case DotAttributes.POS__NE:
				case DotAttributes.XLP__NE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_PARSER_DOTPOINT,
							context, acceptor);
					break;
				case DotAttributes.SHAPE__N:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_PARSER_DOTSHAPE,
							context, acceptor);
					break;
				case DotAttributes.STYLE__GNE:
					proposeAttributeValues(NodeStyle.VALUES, context, acceptor);
					break;
				default:
					break;
				}
			} else {
				super.completeAttribute_Value(model, assignment, context,
						acceptor);
			}
		} else {
			super.completeAttribute_Value(model, assignment, context, acceptor);
		}
	}

	private void proposeAttributeNames(AttributeContext attributeContext,
			ContentAssistContext contentAssistContext,
			ICompletionProposalAcceptor acceptor) {

		for (String attributeName : dotAttributeNames.get(attributeContext)) {
			ICompletionProposal completionProposal = createCompletionProposal(
					attributeName, contentAssistContext);
			acceptor.accept(completionProposal);
		}
	}

	private void proposeAttributeValues(String subgrammarName,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {

		String text = context.getPrefix();

		if (text.startsWith("\"")) { //$NON-NLS-1$
			text = DotTerminalConverters.unquote(text);
			context = context.copy().setPrefix(text).toContext();
		}

		List<ConfigurableCompletionProposal> configurableCompletionProposals = new DotProposalProviderDelegator(
				subgrammarName).computeConfigurableCompletionProposals(text,
						text.length());

		for (ConfigurableCompletionProposal configurableCompletionProposal : configurableCompletionProposals) {
			// adapt the replacement offset determined within the
			// sub-grammar context to be valid within the context of the
			// original text
			configurableCompletionProposal.setReplacementOffset(
					context.getOffset() - configurableCompletionProposal
							.getReplaceContextLength());

			acceptor.accept(configurableCompletionProposal);
		}
	}

	private void proposeAttributeValues(Object[] values,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		proposeAttributeValues(Arrays.asList(values), context, acceptor);
	}

	private void proposeAttributeValues(List<?> values,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		for (Object value : values) {
			// quote attribute value only if needed
			final String proposedValue = DotTerminalConverters
					.needsToBeQuoted(value.toString())
							? DotTerminalConverters.quote(value.toString())
							: value.toString();
			acceptor.accept(createCompletionProposal(proposedValue, context));
		}
	}

	/**
	 * Calculates the valid dot attribute names within a given
	 * {@link AttributeContext}.
	 * 
	 * @return a map mapping the {@link AttributeContext} elements such as
	 *         {@link AttributeContext#EDGE}, {@link AttributeContext#GRAPH},
	 *         {@link AttributeContext#NODE} to the valid dot attribute names.
	 */
	private Map<AttributeContext, List<String>> getDotAttributeNames() {
		List<String> edgeAttributeNames = new ArrayList<>();
		List<String> graphAttributeNames = new ArrayList<>();
		List<String> nodeAttributeNames = new ArrayList<>();

		Field[] declaredFields = DotAttributes.class.getDeclaredFields();
		for (Field field : declaredFields) {
			int modifiers = field.getModifiers();
			if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
					&& Modifier.isFinal(modifiers)
					&& field.getType().equals(String.class)) {
				String fieldName = field.getName();
				String dotClassifier = fieldName
						.substring(fieldName.lastIndexOf("_") + 1); //$NON-NLS-1$
				if (!fieldName.startsWith("_")) { //$NON-NLS-1$
					String dotAttributeName = null;
					try {
						dotAttributeName = (String) field.get(null);
					} catch (IllegalArgumentException
							| IllegalAccessException e) {
						e.printStackTrace();
					}
					if (dotAttributeName != null) {
						if (dotClassifier.contains("E")) { //$NON-NLS-1$
							edgeAttributeNames.add(dotAttributeName);
						}
						if (dotClassifier.contains("G")) { //$NON-NLS-1$
							graphAttributeNames.add(dotAttributeName);
						}
						if (dotClassifier.contains("N")) { //$NON-NLS-1$
							nodeAttributeNames.add(dotAttributeName);
						}
					}
				}
			}
		}

		Map<AttributeContext, List<String>> dotAttributeNames = new HashMap<>();
		dotAttributeNames.put(AttributeContext.EDGE, edgeAttributeNames);
		dotAttributeNames.put(AttributeContext.GRAPH, graphAttributeNames);
		dotAttributeNames.put(AttributeContext.NODE, nodeAttributeNames);
		return dotAttributeNames;
	}
}
