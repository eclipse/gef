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
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *                                - Improve the content assistant support (bug #498324)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.contentassist;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotAttributes.Context;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.dir.DirType;
import org.eclipse.gef.dot.internal.language.dot.AttrList;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.language.dot.EdgeOp;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode;
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess;
import org.eclipse.gef.dot.internal.language.splines.Splines;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
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
	private DotGrammarAccess dotGrammarAccess;

	private static Map<Context, List<String>> dotAttributeNames;
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
			Context attributeContext = DotAttributes.getContext(model);
			proposeAttributeNames(attributeContext, contentAssistContext,
					acceptor);
		} else if (model instanceof DotGraph || model instanceof NodeStmt) {
			proposeAttributeNames(Context.GRAPH, contentAssistContext,
					acceptor);
		}
	}

	@Override
	public void completeAttribute_Value(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if (model instanceof Attribute) {
			Attribute attribute = (Attribute) model;
			if (DotAttributes.getContext(attribute) == Context.EDGE) {
				switch (attribute.getName().toValue()) {
				case DotAttributes.ARROWHEAD__E:
				case DotAttributes.ARROWTAIL__E:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTARROWTYPE,
							context, acceptor);
					break;
				case DotAttributes.COLOR__CNE:
					proposeColorListAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.EDGETOOLTIP__E:
				case DotAttributes.HEADTOOLTIP__E:
				case DotAttributes.LABELTOOLTIP__E:
				case DotAttributes.TAILTOOLTIP__E:
				case DotAttributes.TOOLTIP__CNE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTESCSTRING,
							context, acceptor);
					break;
				case DotAttributes.FILLCOLOR__CNE:
					proposeColorAttributeValues(attribute, context, acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
				case DotAttributes.LABELFONTCOLOR__E:
					proposeColorAttributeValues(attribute, context, acceptor);
					break;
				case DotAttributes.COLORSCHEME__GCNE:
					proposeColorSchemeAttributeValues(context, acceptor);
					break;
				case DotAttributes.DIR__E:
					proposeAttributeValues(DirType.values(), context, acceptor);
					break;
				case DotAttributes.HEAD_LP__E:
				case DotAttributes.LP__GCE:
				case DotAttributes.TAIL_LP__E:
				case DotAttributes.XLP__NE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTPOINT,
							context, acceptor);
					break;
				case DotAttributes.HEADLABEL__E:
				case DotAttributes.LABEL__GCNE:
				case DotAttributes.TAILLABEL__E:
				case DotAttributes.XLABEL__NE:
					proposeHtmlLabelAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.HEADPORT__E:
				case DotAttributes.TAILPORT__E:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTPORTPOS,
							context, acceptor);
					break;
				case DotAttributes.POS__NE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTSPLINETYPE,
							context, acceptor);
					break;
				case DotAttributes.STYLE__GCNE:
					proposeAttributeValues(EdgeStyle.VALUES, context, acceptor);
					break;
				default:
					super.completeAttribute_Value(model, assignment, context,
							acceptor);
					break;
				}
			} else if (DotAttributes.getContext(attribute) == Context.GRAPH) {
				switch (attribute.getName().toValue()) {
				case DotAttributes.BGCOLOR__GC:
					proposeColorListAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
					proposeColorAttributeValues(attribute, context, acceptor);
					break;
				case DotAttributes.CLUSTERRANK__G:
					proposeAttributeValues(ClusterMode.values(), context,
							acceptor);
					break;
				case DotAttributes.COLORSCHEME__GCNE:
					proposeColorSchemeAttributeValues(context, acceptor);
					break;
				case DotAttributes.FORCELABELS__G:
					proposeAttributeValues(booleanAttributeValuesProposals,
							context, acceptor);
					break;
				case DotAttributes.LABEL__GCNE:
					proposeHtmlLabelAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.LAYOUT__G:
					proposeAttributeValues(Layout.values(), context, acceptor);
					break;
				case DotAttributes.LP__GCE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTPOINT,
							context, acceptor);
					break;
				case DotAttributes.OUTPUTORDER__G:
					proposeAttributeValues(OutputMode.values(), context,
							acceptor);
					break;
				case DotAttributes.PAGEDIR__G:
					proposeAttributeValues(Pagedir.values(), context, acceptor);
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
			} else if (DotAttributes.getContext(attribute) == Context.NODE) {
				switch (attribute.getName().toValue()) {
				case DotAttributes.COLOR__CNE:
					proposeColorAttributeValues(attribute, context, acceptor);
					break;
				case DotAttributes.FILLCOLOR__CNE:
					proposeColorListAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
					proposeColorAttributeValues(attribute, context, acceptor);
					break;
				case DotAttributes.COLORSCHEME__GCNE:
					proposeColorSchemeAttributeValues(context, acceptor);
					break;
				case DotAttributes.FIXEDSIZE__N:
					proposeAttributeValues(booleanAttributeValuesProposals,
							context, acceptor);
					break;
				case DotAttributes.LABEL__GCNE:
				case DotAttributes.XLABEL__NE:
					proposeHtmlLabelAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.POS__NE:
				case DotAttributes.XLP__NE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTPOINT,
							context, acceptor);
					break;
				case DotAttributes.SHAPE__N:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTSHAPE,
							context, acceptor);
					break;
				case DotAttributes.STYLE__GCNE:
					proposeAttributeValues(NodeStyle.VALUES, context, acceptor);
					break;
				case DotAttributes.TOOLTIP__CNE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTESCSTRING,
							context, acceptor);
				default:
					break;
				}
			} else if (DotAttributes.getContext(attribute) == Context.CLUSTER
					|| DotAttributes
							.getContext(attribute) == Context.SUBGRAPH) {
				switch (attribute.getName().toValue()) {
				case DotAttributes.BGCOLOR__GC:
					proposeColorListAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.COLOR__CNE:
					proposeColorAttributeValues(attribute, context, acceptor);
					break;
				case DotAttributes.COLORSCHEME__GCNE:
					proposeColorSchemeAttributeValues(context, acceptor);
					break;
				case DotAttributes.FILLCOLOR__CNE:
					proposeColorListAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.FONTCOLOR__GCNE:
					proposeColorAttributeValues(attribute, context, acceptor);
					break;
				case DotAttributes.LABEL__GCNE:
					proposeHtmlLabelAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.TOOLTIP__CNE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTESCSTRING,
							context, acceptor);
				}
			}

			else {
				super.completeAttribute_Value(model, assignment, context,
						acceptor);
			}
		} else {
			super.completeAttribute_Value(model, assignment, context, acceptor);
		}
	}

	private void proposeAttributeNames(Context attributeContext,
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
			text = ID.fromString(text, Type.QUOTED_STRING).toValue();
			context = context.copy().setPrefix(text).toContext();
		}

		if (text.startsWith("<")) { //$NON-NLS-1$
			text = text.substring(1);
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
			final String proposedValue = ID.fromValue(value.toString())
					.toString();
			acceptor.accept(createCompletionProposal(proposedValue, context));
		}
	}

	private void proposeColorAttributeValues(Attribute attribute,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		// give the DotColorProposalProvider the necessary 'global' information
		DotColorProposalProvider.globalColorScheme = DotAstHelper
				.getColorSchemeAttributeValue(attribute);

		// propose color values based on the DotColor sub-grammar
		proposeAttributeValues(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTCOLOR,
				context, acceptor);

		// reset the state of the DotColorProposalProvider
		DotColorProposalProvider.globalColorScheme = null;
	}

	private void proposeColorListAttributeValues(Attribute attribute,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		// give the DotColorProposalProvider the necessary 'global' information
		DotColorProposalProvider.globalColorScheme = DotAstHelper
				.getColorSchemeAttributeValue(attribute);

		// propose color values based on the DotColorList sub-grammar
		proposeAttributeValues(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTCOLORLIST,
				context, acceptor);

		// reset the state of the DotColorProposalProvider
		DotColorProposalProvider.globalColorScheme = null;
	}

	private void proposeColorSchemeAttributeValues(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		proposeAttributeValues(DotColors.getColorSchemes(), context, acceptor);
	}

	private void proposeHtmlLabelAttributeValues(Attribute attribute,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if (attribute.getValue() != null
				&& attribute.getValue().getType() == Type.HTML_STRING) {
			proposeAttributeValues(
					DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL,
					context, acceptor);
		}
	}

	/**
	 * Calculates the valid dot attribute names within a given {@link Context}.
	 * 
	 * @return a map mapping the {@link Context} elements such as
	 *         {@link Context#EDGE}, {@link Context#GRAPH}, {@link Context#NODE}
	 *         to the valid dot attribute names.
	 */
	private Map<Context, List<String>> getDotAttributeNames() {
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

		Map<Context, List<String>> dotAttributeNames = new HashMap<>();
		dotAttributeNames.put(Context.EDGE, edgeAttributeNames);
		dotAttributeNames.put(Context.GRAPH, graphAttributeNames);
		dotAttributeNames.put(Context.NODE, nodeAttributeNames);
		return dotAttributeNames;
	}
}
