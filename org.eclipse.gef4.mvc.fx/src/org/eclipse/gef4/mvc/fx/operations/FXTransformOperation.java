/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.operations;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

public class FXTransformOperation extends AbstractOperation {

	public static final String TRANSFORMATION_PROVIDER_ROLE = "transformationProvider";

	private final IVisualPart<Node, ? extends Node> part;
	private Affine oldTransform;
	private Affine newTransform;

	public FXTransformOperation(IVisualPart<Node, ? extends Node> part) {
		super("Transform");
		this.part = part;
		// TODO: Remove this method call from this constructor.
		this.oldTransform = setAffine(new Affine(), getNodeTransform());
		this.newTransform = setAffine(new Affine(), oldTransform);
	}

	public FXTransformOperation(IVisualPart<Node, ? extends Node> part,
			Affine newTransform) {
		super("Transform");
		this.part = part;
		this.newTransform = newTransform;
		// TODO: Remove this method call from this constructor.
		this.oldTransform = setAffine(new Affine(), getNodeTransform());
	}

	public FXTransformOperation(IVisualPart<Node, ? extends Node> part,
			Affine oldTransform, Affine newTransform) {
		super("Transform");
		this.part = part;
		this.oldTransform = oldTransform;
		this.newTransform = newTransform;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		setAffine(getNodeTransform(), newTransform);
		return Status.OK_STATUS;
	}

	public Affine getNewTransform() {
		return newTransform;
	}

	protected Affine getNodeTransform() {
		@SuppressWarnings("serial")
		Provider<Affine> affineProvider = part.getAdapter(AdapterKey
				.<Provider<? extends Affine>> get(
						new TypeToken<Provider<? extends Affine>>() {
						}, TRANSFORMATION_PROVIDER_ROLE));
		if (affineProvider == null) {
			throw new IllegalStateException(
					"Part <"
							+ part
							+ "> is missing an adapter for Provider<Affine> under role <"
							+ TRANSFORMATION_PROVIDER_ROLE + ">.");
		}
		return affineProvider.get();
	}

	public Affine getOldTransform() {
		return oldTransform;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	protected Affine setAffine(Affine dst, Affine src) {
		dst.setMxx(src.getMxx());
		dst.setMxy(src.getMxy());
		dst.setMxz(src.getMxz());
		dst.setMyx(src.getMyx());
		dst.setMyy(src.getMyy());
		dst.setMyz(src.getMyz());
		dst.setMzx(src.getMzx());
		dst.setMzy(src.getMzy());
		dst.setMzz(src.getMzz());
		dst.setTx(src.getTx());
		dst.setTy(src.getTy());
		dst.setTz(src.getTz());
		return dst;
	}

	public void setNewTransform(Affine newTransform) {
		this.newTransform = newTransform;
	}

	public void setOldTransform(Affine oldTransform) {
		this.oldTransform = oldTransform;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		setAffine(getNodeTransform(), oldTransform);
		return Status.OK_STATUS;
	}

}
