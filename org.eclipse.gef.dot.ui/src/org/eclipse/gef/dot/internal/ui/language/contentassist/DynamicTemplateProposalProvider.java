/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #321775)
 *******************************************************************************/

package org.eclipse.gef.dot.internal.ui.language.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateAcceptor;
import org.eclipse.xtext.ui.editor.templates.ContextTypeIdHelper;
import org.eclipse.xtext.ui.editor.templates.DefaultTemplateProposalProvider;

import com.google.inject.Inject;

public class DynamicTemplateProposalProvider
		extends DefaultTemplateProposalProvider {

	@Inject
	public DynamicTemplateProposalProvider(TemplateStore templateStore,
			ContextTypeRegistry registry, ContextTypeIdHelper helper) {
		super(templateStore, registry, helper);
	}

	@Override
	protected void createTemplates(TemplateContext templateContext,
			ContentAssistContext context, ITemplateAcceptor acceptor) {

		EObject currentModel = context.getCurrentModel();
		if (currentModel == null) {
			super.createTemplates(templateContext, context, acceptor);
		}
		if (currentModel instanceof Attribute) {
			ID attributeNameID = ((Attribute) currentModel).getName();
			if (attributeNameID != null) {
				String attributeName = attributeNameID.toValue();
				switch (attributeName) {
				case DotAttributes.HEADLABEL__E:
				case DotAttributes.LABEL__GCNE:
				case DotAttributes.TAILLABEL__E:
				case DotAttributes.XLABEL__NE:
					super.createTemplates(templateContext, context, acceptor);
				}
			}
		}
	}
}
