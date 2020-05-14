/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #321775)
 *******************************************************************************/

package org.eclipse.gef.dot.internal.ui.language.contentassist;

import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.DotGraph;
import org.eclipse.gef.dot.internal.language.dot.EdgeOp;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.templates.ContextTypeIdHelper;
import org.eclipse.xtext.ui.editor.templates.DefaultTemplateProposalProvider;

import com.google.inject.Inject;

public class DotTemplateProposalProvider
		extends DefaultTemplateProposalProvider {

	@Inject
	public DotTemplateProposalProvider(TemplateStore templateStore,
			ContextTypeRegistry registry, ContextTypeIdHelper helper) {
		super(templateStore, registry, helper);
	}

	protected TemplateProposal doCreateProposal(Template template,
			TemplateContext templateContext, ContentAssistContext context,
			Image image, int relevance) {
		EObject currentModel = context.getCurrentModel();

		if (isEdgeTemplate(template)) {
			template = replaceOpVariable(currentModel, template);
		}

		if (isHtmlLabelTemplate(template)) {
			if (currentModel instanceof Attribute) {
				ID attributeNameID = ((Attribute) currentModel).getName();
				if (attributeNameID != null) {
					String attributeName = attributeNameID.toValue();
					switch (attributeName) {
					case DotAttributes.HEADLABEL__E:
					case DotAttributes.LABEL__GCNE:
					case DotAttributes.TAILLABEL__E:
					case DotAttributes.XLABEL__NE:
						return super.doCreateProposal(template, templateContext,
								context, image, relevance);
					default:
						return null;
					}
				}
			} else {
				return null;
			}
		}

		return super.doCreateProposal(template, templateContext, context, image,
				relevance);
	}

	private boolean isEdgeTemplate(Template template) {
		return "edge".equals(template.getName()); //$NON-NLS-1$
	}

	private boolean isHtmlLabelTemplate(Template template) {
		return "HTMLLabel".equals(template.getName()); //$NON-NLS-1$
	}

	private Template replaceOpVariable(EObject currentModel,
			Template edgeTemplate) {
		DotGraph dotGraph = EcoreUtil2.getContainerOfType(currentModel,
				DotGraph.class);
		boolean isDirected = dotGraph.getType() == GraphType.DIGRAPH;
		String edgeOp = isDirected ? EdgeOp.DIRECTED.toString()
				: EdgeOp.UNDIRECTED.toString();

		return new Template(edgeTemplate.getName(),
				edgeTemplate.getDescription(), edgeTemplate.getContextTypeId(),
				edgeTemplate.getPattern().replaceAll(Pattern.quote("${op}"), //$NON-NLS-1$
						edgeOp),
				edgeTemplate.isAutoInsertable());
	}
}
