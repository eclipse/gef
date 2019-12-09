/*******************************************************************************
 * Copyright (c) 2010, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - initial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *                                - Improve the content assistant support (bug #498324)
 *     Zoey Prigge (itemis AG)    - Improve quoted attribute CA support (bug #545801)
 *                                - Add FontName content assist support (bug #542663)
 *                                - Add subgraph content assist support (bug #547639)
 *                                - Add support for listed (style) attribute (bug #549393)
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
import org.eclipse.gef.dot.internal.language.dot.AttributeType;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.language.dot.EdgeOp;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.dot.Subgraph;
import org.eclipse.gef.dot.internal.language.fontname.PostScriptFontAlias;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode;
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.ranktype.RankType;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess;
import org.eclipse.gef.dot.internal.language.splines.Splines;
import org.eclipse.gef.dot.internal.language.style.ClusterStyle;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.DotActivatorEx;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal.IReplacementTextApplier;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;

import com.google.inject.Inject;

/**
 * A proposal provider for Dot.
 * 
 * @author anyssen
 */
public class DotProposalProvider extends AbstractDotProposalProvider {

	@Inject
	private DotGrammarAccess dotGrammarAccess;

	@Inject
	private IImageHelper imageHelper;

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

		EObject currentModel = context.getCurrentModel();
		DotGraph dotGraph = EcoreUtil2.getContainerOfType(currentModel,
				DotGraph.class);

		if (dotGraph != null) {
			GraphType graphType = dotGraph.getType();
			if (EdgeOp.get(proposal) != null) {
				if (graphType == GraphType.DIGRAPH
						&& proposal.equals(EdgeOp.UNDIRECTED.toString())) {
					// do not propose the undirected edge operator in case of a
					// directed graph
					return null;
				}
				if (graphType == GraphType.GRAPH
						&& proposal.equals(EdgeOp.DIRECTED.toString())) {
					// do not propose the directed edge operator in case of an
					// undirected graph
					return null;
				}
				String format = "%s: Edge"; //$NON-NLS-1$
				displayString = DotEditorUtils.style(format, proposal);
			}
		}

		if (prefix.equals("=") && proposal.equals("=")) { //$NON-NLS-1$//$NON-NLS-2$
			// do not propose the "=" symbol if it is already included in the
			// text as prefix
			return null;
		}

		if (prefix.equals("[") && proposal.equals("[")) { //$NON-NLS-1$ //$NON-NLS-2$
			// do not propose the "[" symbol if it is already included in the
			// text as prefix
			return null;
		}

		if ("subgraph".equals(proposal)) { //$NON-NLS-1$
			String format = "%s: Subgraph"; //$NON-NLS-1$
			displayString = DotEditorUtils.style(format, proposal);
		}

		ICompletionProposal completionProposal = super.createCompletionProposal(
				proposal, displayString, image, priority, prefix, context);

		if (completionProposal instanceof ConfigurableCompletionProposal) {
			ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;

			// ensure that an empty attribute list is created by after the
			// 'graph', 'node', 'edge' attribute statements when applying the
			// proposal
			if (dotGraph != null) {
				if (AttributeType.get(proposal) != null && !context
						.getLastCompleteNode().getText().equals("strict")) { //$NON-NLS-1$
					String format = "%s[]: Attributes"; //$NON-NLS-1$
					StyledString newDisplayString = DotEditorUtils.style(format,
							displayString);
					configurableCompletionProposal
							.setDisplayString(newDisplayString);
					configurableCompletionProposal
							.setReplacementString(configurableCompletionProposal
									.getReplacementString() + "[]"); //$NON-NLS-1$
					configurableCompletionProposal.setCursorPosition(
							configurableCompletionProposal.getCursorPosition()
									+ 1);
				}
			}

			if (context.getMatcher() instanceof AttributeValueMatcher) {
				// avoid confusing empty proposal display string
				if (configurableCompletionProposal.getReplacementString()
						.length() == 0) {
					configurableCompletionProposal.setDisplayString("\"\""); //$NON-NLS-1$
				}

				configurableCompletionProposal
						.setTextApplier(new IReplacementTextApplier() {
							final private IReplacementTextApplier initialTextApplier = configurableCompletionProposal
									.getTextApplier();

							@Override
							public void apply(IDocument document,
									ConfigurableCompletionProposal proposal)
									throws BadLocationException {
								String original = document.get(
										proposal.getReplacementOffset(),
										proposal.getReplacementLength());

								String replacement = proposal
										.getReplacementString();
								int replacementOffset = proposal
										.getReplacementOffset();
								int replacementLength = proposal
										.getReplacementLength();

								// ensure that the double quote at the beginning
								// of an attribute value is not overridden when
								// applying the proposal
								// However, if final quote is to be overridden,
								// too (text selection), ignore.
								if (original.startsWith("\"") //$NON-NLS-1$
										&& (original.length() == 1
												|| !original.endsWith("\""))) { //$NON-NLS-1$

									// check if a list separator is set (list
									// case)
									Character listSeparator = ((AttributeValueMatcher) context
											.getMatcher()).getListSeparator();
									if (listSeparator != null
											&& prefix.contains(
													listSeparator.toString())) {
										proposal.setReplacementOffset(
												replacementOffset
														+ original.lastIndexOf(
																listSeparator,
																prefix.length()
																		- 1)
														+ 1);
										proposal.setReplacementLength(
												replacementLength
														- original.lastIndexOf(
																listSeparator,
																prefix.length()
																		- 1)
														- 1);
									} else {
										proposal.setReplacementOffset(
												replacementOffset + 1);
										proposal.setReplacementLength(
												replacementLength - 1);
									}
								} else {
									String idValue = ID.fromValue(replacement)
											.toString();
									boolean idIsQuoted = idValue
											.startsWith("\""); //$NON-NLS-1$
									// Check if ID representation is quoted to
									// account for valid DOT grammar
									if (idIsQuoted) {
										proposal.setReplacementString(idValue);
										proposal.setCursorPosition(
												idValue.length());
									}
								}
								if (initialTextApplier == null) {
									// adapted from
									// ConfigurableCompletionProposal::apply
									document.replace(
											proposal.getReplacementOffset(),
											proposal.getReplacementLength(),
											proposal.getReplacementString());
								} else {
									initialTextApplier.apply(document,
											proposal);
								}
							}
						});
			}
		}

		return completionProposal;
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

		EObject modelCompleted = model instanceof NodeStmt ? model.eContainer()
				: model;

		if (modelCompleted instanceof AttrList
				|| modelCompleted instanceof DotGraph
				|| modelCompleted instanceof Subgraph) {
			Context attributeContext = DotAttributes.getContext(modelCompleted);
			proposeAttributeNames(attributeContext, contentAssistContext,
					acceptor);
		}
	}

	@Override
	public void completeAttribute_Value(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if (model instanceof Attribute) {
			Attribute attribute = (Attribute) model;

			context = context.copy()
					.setMatcher(new AttributeValueMatcher(context.getMatcher()))
					.toContext();

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
				case DotAttributes.FONTNAME__GCNE:
				case DotAttributes.LABELFONTNAME__E:
					proposeFontNameAttributeValues(context, acceptor);
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
					((AttributeValueMatcher) context.getMatcher())
							.setListSeparator(',');
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
				case DotAttributes.FONTNAME__GCNE:
					proposeFontNameAttributeValues(context, acceptor);
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
				case DotAttributes.FONTNAME__GCNE:
					proposeFontNameAttributeValues(context, acceptor);
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
					((AttributeValueMatcher) context.getMatcher())
							.setListSeparator(',');
					proposeAttributeValues(NodeStyle.VALUES, context, acceptor);
					break;
				case DotAttributes.TOOLTIP__CNE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTESCSTRING,
							context, acceptor);
				default:
					break;
				}
			} else if (DotAttributes.getContext(attribute) == Context.CLUSTER) {
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
				case DotAttributes.FONTNAME__GCNE:
					proposeFontNameAttributeValues(context, acceptor);
					break;
				case DotAttributes.LABEL__GCNE:
					proposeHtmlLabelAttributeValues(attribute, context,
							acceptor);
					break;
				case DotAttributes.STYLE__GCNE:
					((AttributeValueMatcher) context.getMatcher())
							.setListSeparator(',');
					proposeAttributeValues(ClusterStyle.VALUES, context,
							acceptor);
					break;
				case DotAttributes.TOOLTIP__CNE:
					proposeAttributeValues(
							DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTESCSTRING,
							context, acceptor);
				}
			} else if (DotAttributes
					.getContext(attribute) == Context.SUBGRAPH) {
				switch (attribute.getName().toValue()) {
				case DotAttributes.RANK__S:
					proposeAttributeValues(RankType.values(), context,
							acceptor);
					break;
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

		Image attributeImage = imageHelper.getImage("attribute.png"); //$NON-NLS-1$
		String format = "%s: Attribute"; //$NON-NLS-1$

		for (String attributeName : dotAttributeNames.get(attributeContext)) {
			StyledString displayString = DotEditorUtils.style(format,
					attributeName);
			ICompletionProposal completionProposal = createCompletionProposal(
					attributeName, displayString, attributeImage,
					contentAssistContext);
			if (completionProposal instanceof ConfigurableCompletionProposal) {
				ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;

				// ensure that the '=' symbol is inserted after the attribute
				// name when applying the proposal
				configurableCompletionProposal.setReplacementString(
						configurableCompletionProposal.getReplacementString()
								+ "="); //$NON-NLS-1$
				configurableCompletionProposal.setCursorPosition(
						configurableCompletionProposal.getCursorPosition() + 1);
			}
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
		values.forEach(value -> acceptor
				.accept(createCompletionProposal(value.toString(), context)));
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

	private void proposeFontNameAttributeValues(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		proposeAttributeValues(PostScriptFontAlias.values(), context, acceptor);
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
		List<String> clusterAttributeNames = new ArrayList<>();
		List<String> edgeAttributeNames = new ArrayList<>();
		List<String> graphAttributeNames = new ArrayList<>();
		List<String> nodeAttributeNames = new ArrayList<>();
		List<String> subgraphAttributeNames = new ArrayList<>();

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
						DotActivatorEx.logError(e);
					}
					if (dotAttributeName != null) {
						if (dotClassifier.contains("C")) { //$NON-NLS-1$
							clusterAttributeNames.add(dotAttributeName);
						}
						if (dotClassifier.contains("E")) { //$NON-NLS-1$
							edgeAttributeNames.add(dotAttributeName);
						}
						if (dotClassifier.contains("G")) { //$NON-NLS-1$
							graphAttributeNames.add(dotAttributeName);
						}
						if (dotClassifier.contains("N")) { //$NON-NLS-1$
							nodeAttributeNames.add(dotAttributeName);
						}
						if (dotClassifier.contains("S")) { //$NON-NLS-1$
							subgraphAttributeNames.add(dotAttributeName);
						}
					}
				}
			}
		}

		Map<Context, List<String>> dotAttributeNames = new HashMap<>();
		dotAttributeNames.put(Context.CLUSTER, clusterAttributeNames);
		dotAttributeNames.put(Context.EDGE, edgeAttributeNames);
		dotAttributeNames.put(Context.GRAPH, graphAttributeNames);
		dotAttributeNames.put(Context.NODE, nodeAttributeNames);
		dotAttributeNames.put(Context.SUBGRAPH, subgraphAttributeNames);
		return dotAttributeNames;
	}

	private static class AttributeValueMatcher extends PrefixMatcher {
		private final PrefixMatcher originalMatcher;
		private Character listSeparator = null;

		public AttributeValueMatcher(PrefixMatcher originalMatcher) {
			this.originalMatcher = originalMatcher;
		}

		@Override
		public boolean isCandidateMatchingPrefix(String name, String prefix) {
			if (prefix.trim().startsWith("\"")) { //$NON-NLS-1$
				if (listSeparator != null && prefix.contains(",")) { //$NON-NLS-1$
					return listMatch(name, prefix);
				}
				return quoteMatch(name, prefix);
			}
			return standardMatch(name, prefix);
		}

		public void setListSeparator(char listSeparator) {
			this.listSeparator = listSeparator;
		}

		/**
		 * Returns the character separating a list of values, not a list if null
		 * 
		 * @return separating character, may be null
		 */
		public Character getListSeparator() {
			return listSeparator;
		}

		private boolean standardMatch(String name, String prefix) {
			return originalMatcher.isCandidateMatchingPrefix(name, prefix);
		}

		private boolean quoteMatch(String name, String prefix) {
			return originalMatcher.isCandidateMatchingPrefix(name,
					prefix.substring(1));
		}

		private boolean listMatch(String name, String prefix) {
			return originalMatcher.isCandidateMatchingPrefix(name,
					prefix.substring(prefix.lastIndexOf(',') + 1).trim());
		}
	}
}
