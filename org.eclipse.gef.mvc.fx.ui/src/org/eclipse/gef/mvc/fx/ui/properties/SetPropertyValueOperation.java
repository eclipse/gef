/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.properties;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.ui.Messages;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;

/**
 * An {@link ITransactionalOperation} used to set or reset the value of a
 * property.
 *
 * @author pshah
 * @author anyssen
 *
 */
public class SetPropertyValueOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * Value constant to indicate that the property is to be reset to its
	 * default value during execute/redo and undo.
	 */
	protected static final Object DEFAULT_VALUE = new Object();

	private static IPropertyDescriptor getPropertyDescriptor(
			IPropertySource propertySource, Object propertyId) {
		for (IPropertyDescriptor propertyDescriptor : propertySource
				.getPropertyDescriptors()) {
			if (propertyDescriptor.getId().equals(propertyId)) {
				return propertyDescriptor;
			}
		}
		return null;
	}

	private static String getValueLabel(IPropertySource propertySource,
			Object propertyId, Object newValue) {
		IPropertyDescriptor propertyDescriptor = getPropertyDescriptor(
				propertySource, propertyId);
		return propertyDescriptor.getLabelProvider().getText(newValue);
	}

	/** content-relevant-property */
	private boolean isContentRelevant = true;
	/** the value to set for the property */
	private Object newValue;
	/** the old value of the property prior to executing this command */
	private Object oldValue;
	/** the id of the property whose value has to be set */
	private Object propertyId;
	/** the property source whose property has to be set */
	private IPropertySource propertySource;

	/**
	 * Constructs a new {@link SetPropertyValueOperation}.
	 *
	 * @param propertyLabel
	 *            A label to identify the property whose value is set by this
	 *            command.
	 * @param propertySource
	 *            The property source which provides the property, whose value
	 *            is to be set.
	 * @param propertyId
	 *            The id of the property whose value is to be set.
	 * @param newValue
	 *            The new value to set for the property or
	 *            {@link #DEFAULT_VALUE} to indicate that the property should be
	 *            reset.
	 */
	public SetPropertyValueOperation(String propertyLabel,
			IPropertySource propertySource, Object propertyId,
			Object newValue) {
		super(MessageFormat
				.format(Messages.SetPropertyValueCommand_Label,
						new Object[] { propertyLabel, getValueLabel(
								propertySource, propertyId, newValue) })
				.trim());
		this.propertySource = propertySource;
		this.propertyId = propertyId;
		this.newValue = newValue;
	}

	@Override
	public boolean canExecute() {
		if (propertySource == null || propertyId == null) {
			return false;
		}
		if (newValue == DEFAULT_VALUE) {
			// we may only reset a property to its default value if it supports
			// the notion of a default value and it does not already have this
			// value
			boolean canExecute = propertySource.isPropertySet(propertyId);
			if (propertySource instanceof IPropertySource2) {
				canExecute &= (((IPropertySource2) propertySource)
						.isPropertyResettable(propertyId));
			}
			return canExecute;
		}
		return true;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		/*
		 * Fix for bug #54250 IPropertySource.isPropertySet(String) returns
		 * false both when there is no default value, and when there is a
		 * default value and the property is set to that value. To correctly
		 * determine if a reset should be done during undo, we compare the
		 * return value of isPropertySet(String) before and after
		 * setPropertyValue(...) is invoked. If they are different (it must have
		 * been false before and true after -- it cannot be the other way
		 * around), then that means we need to reset.
		 */
		boolean wasPropertySet = propertySource.isPropertySet(propertyId);
		oldValue = unwrapValue(propertySource.getPropertyValue(propertyId));

		// set value of property to new value or reset the value, if specified
		if (newValue == DEFAULT_VALUE) {
			propertySource.resetPropertyValue(propertyId);
		} else {
			propertySource.setPropertyValue(propertyId, unwrapValue(newValue));
		}

		// check if property was set to its default value before (so it will
		// have to be resetted during undo); note that if the new value is
		// DEFAULT_VALUE the old value may not have been the default value as
		// well, as the command would not be executable in this case.
		if (propertySource instanceof IPropertySource2) {
			if (!wasPropertySet && ((IPropertySource2) propertySource)
					.isPropertyResettable(propertyId)) {
				oldValue = DEFAULT_VALUE;
			}
		} else {
			if (!wasPropertySet && propertySource.isPropertySet(propertyId)) {
				oldValue = DEFAULT_VALUE;
			}
		}
		// TODO: infer a proper status
		return Status.OK_STATUS;
	}

	/**
	 * Returns the new value to be set for the property when executing or
	 * redoing.
	 *
	 * @return the new value or {@link #DEFAULT_VALUE} to indicate that the
	 *         default value should be set as the new value.
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * After the command has been executed or redone, returns the old value of
	 * the property or {@link #DEFAULT_VALUE} if the property did not have a
	 * value before.
	 *
	 * @return the old value of the property or {@link #DEFAULT_VALUE}.
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * Returns the id by which to identify the property whose value is to be
	 * set.
	 *
	 * @return the id of the property whose value is to be set.
	 */
	public Object getPropertyId() {
		return propertyId;
	}

	/**
	 * Returns the {@link IPropertySource} which provides the property, whose
	 * value is to be set.
	 *
	 * @return the {@link IPropertySource} which provides the property.
	 */
	public IPropertySource getPropertySource() {
		return propertySource;
	}

	@Override
	public boolean isContentRelevant() {
		return isContentRelevant;
	}

	@Override
	public boolean isNoOp() {
		return oldValue == newValue
				|| (oldValue != null && oldValue.equals(newValue));
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the content-relevancy of this operation to the given value.
	 *
	 * @param isContentRelevant
	 *            <code>true</code> if this operation is content-relevant,
	 *            <code>false</code> otherwise.
	 */
	public void setContentRelevant(boolean isContentRelevant) {
		this.isContentRelevant = isContentRelevant;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (oldValue == DEFAULT_VALUE) {
			propertySource.resetPropertyValue(propertyId);
		} else {
			propertySource.setPropertyValue(propertyId, oldValue);
		}
		return Status.OK_STATUS;
	}

	private Object unwrapValue(Object value) {
		if (value instanceof IPropertySource) {
			return ((IPropertySource) value).getEditableValue();
		}
		return value;
	}
}
