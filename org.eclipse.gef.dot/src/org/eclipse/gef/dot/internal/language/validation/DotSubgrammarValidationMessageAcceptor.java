/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge  - initial API and implementation (bug #454629)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

/**
 * This class provides a ValidationMessageAcceptor to be used with sub grammars.
 * It can be used when validating the main grammar to translate validation
 * warnings/errors.
 *
 * @author zgerritprigge
 *
 */
public class DotSubgrammarValidationMessageAcceptor
		implements ValidationMessageAcceptor {

	private final Attribute attribute;
	private final ValidationMessageAcceptor hostMessageAcceptor;
	private final String userReadableIdentifier;
	private final int initialOffset;

	/**
	 * Constructs a Validation Message acceptor to 'translate' issues while
	 * validating a sub grammar.
	 *
	 * @param attribute
	 *            The attribute in the main grammar that hosts the sub grammar
	 * @param hostingFeature
	 *            Can be null. If set, needs to be unique within hostingEObject
	 *            (not a list, ...).
	 * @param userReadableIdentifier
	 *            A name for the sub grammar understood by the user
	 * @param hostMessageAcceptor
	 *            The validationMessageAcceptor of the host grammar
	 * @param internalOffset
	 *            Offset from begin of feature
	 */
	public DotSubgrammarValidationMessageAcceptor(Attribute attribute,
			EStructuralFeature hostingFeature, String userReadableIdentifier,
			ValidationMessageAcceptor hostMessageAcceptor, int internalOffset) {

		this.attribute = attribute;
		this.hostMessageAcceptor = hostMessageAcceptor;
		this.userReadableIdentifier = userReadableIdentifier;

		this.initialOffset = calculateInitialOffset(hostingFeature,
				internalOffset);
	}

	/**
	 * Helper Method to allow this message acceptor to handle syntax errors
	 * issued while parsing the sub grammar as validation issues of the main
	 * grammar.
	 *
	 * @param error
	 *            Error node from parsing.
	 */
	public void acceptSyntaxError(INode error) {
		SyntaxErrorMessage errorMessage = error.getSyntaxErrorMessage();
		hostMessageAcceptor.acceptError(
				buildSyntaxErrorMessage(errorMessage.getMessage()), attribute,
				calculateOffset(error.getOffset()), error.getLength(),
				errorMessage.getIssueCode(), errorMessage.getIssueData());
	}

	@Override
	public void acceptError(String message, EObject object,
			EStructuralFeature feature, int index, String code,
			String... issueData) {
		for (INode node : getNodesForEObject(object, feature))
			hostMessageAcceptor.acceptError(buildSemanticErrorMessage(message),
					attribute, calculateOffset(node.getOffset()),
					node.getLength(), code, issueData);
	}

	@Override
	public void acceptError(String message, EObject object, int offset,
			int length, String code, String... issueData) {
		hostMessageAcceptor.acceptError(buildSemanticErrorMessage(message),
				attribute, calculateOffset(offset), length, code, issueData);
	}

	@Override
	public void acceptInfo(String message, EObject object,
			EStructuralFeature feature, int index, String code,
			String... issueData) {
		for (INode node : getNodesForEObject(object, feature))
			hostMessageAcceptor.acceptInfo(buildSemanticErrorMessage(message),
					attribute, calculateOffset(node.getOffset()),
					node.getLength(), code, issueData);
	}

	@Override
	public void acceptInfo(String message, EObject object, int offset,
			int length, String code, String... issueData) {
		hostMessageAcceptor.acceptInfo(buildSemanticErrorMessage(message),
				attribute, calculateOffset(offset), length, code, issueData);
	}

	@Override
	public void acceptWarning(String message, EObject object,
			EStructuralFeature feature, int index, String code,
			String... issueData) {
		for (INode node : getNodesForEObject(object, feature))
			hostMessageAcceptor.acceptWarning(
					buildSemanticErrorMessage(message), attribute,
					calculateOffset(node.getOffset()), node.getLength(), code,
					issueData);

	}

	@Override
	public void acceptWarning(String message, EObject object, int offset,
			int length, String code, String... issueData) {
		hostMessageAcceptor.acceptWarning(buildSemanticErrorMessage(message),
				attribute, calculateOffset(offset), length, code, issueData);
	}

	private int calculateInitialOffset(EStructuralFeature hostingFeature,
			int internalOffset) {
		List<INode> nodes = getNodesForEObject(attribute, hostingFeature);
		if (nodes.size() != 1)
			throw new RuntimeException(
					"EObject has no node or feature not unique");
		return nodes.get(0).getOffset() + internalOffset;
	}

	private int calculateOffset(int offset) {
		// in theory, initialOffset and offset should always be positive.
		// but if one is negative, adding them seems useless.
		// TODO checked if and when and how to handle
		return offset >= 0 && initialOffset >= 0 ? offset + initialOffset
				: offset;
	}

	private List<INode> getNodesForEObject(EObject eObject,
			EStructuralFeature eStructuralFeature) {
		List<INode> nodes = NodeModelUtils.findNodesForFeature(eObject,
				eStructuralFeature);
		// if the result is empty, possibly the feature is not set
		// hence we try to find the note for the entire eObject
		if (nodes.size() == 0) {
			nodes = new ArrayList<INode>();
			nodes.add(NodeModelUtils.findActualNodeFor(eObject));
		}
		return nodes;
	}

	private String buildSyntaxErrorMessage(String errorMessage) {
		StringBuilder message = new StringBuilder();
		message.append("The value '" + attribute.getValue().toValue()
				+ "' is not a syntactically correct " + userReadableIdentifier
				+ ": ").append(errorMessage);
		return message.toString();
	}

	private String buildSemanticErrorMessage(String errorMessage) {
		StringBuilder message = new StringBuilder();
		message.append("The " + userReadableIdentifier + " '"
				+ attribute.getValue().toValue()
				+ "' is not semantically correct: ").append(errorMessage);
		return message.toString();
	}

}
