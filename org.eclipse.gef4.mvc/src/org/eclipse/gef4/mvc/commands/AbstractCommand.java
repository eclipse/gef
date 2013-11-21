/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.commands;

/**
 * An Abstract implementation of {@link AbstractCommand}.
 * 
 * @author hudsonr
 * @since 2.0
 */
public abstract class AbstractCommand implements ICommand {

	private String label;

	private String debugLabel;

	/**
	 * Constructs a Command with no label.
	 */
	public AbstractCommand() {
	}

	/**
	 * Constructs a Command with the specified label.
	 * 
	 * @param label
	 *            the Command's label
	 */
	public AbstractCommand(String label) {
		setLabel(label);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.ICommand#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.ICommand#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.ICommand#chain(org.eclipse.gef.commands.ICommand)
	 */
	@Override
	public ICommand chain(ICommand command) {
		if (command == null)
			return this;
		class ChainedCompoundCommand extends CompoundCommand {
			public AbstractCommand chain(AbstractCommand c) {
				add(c);
				return this;
			}
		}
		CompoundCommand result = new ChainedCompoundCommand();
		result.setDebugLabel("Chained Commands"); //$NON-NLS-1$
		result.add(this);
		result.add(command);
		return result;
	}

	/**
	 * This is called to indicate that the <code>Command</code> will not be used
	 * again. The Command may be in any state (executed, undone or redone) when
	 * dispose is called. The Command should not be referenced in any way after
	 * it has been disposed.
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.ICommand#execute()
	 */
	@Override
	public void execute() {
	}

	/**
	 * @return a String used to describe this command to the User
	 */
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.ICommand#redo()
	 */
	@Override
	public void redo() {
		execute();
	}

	/**
	 * Sets the debug label for this command
	 * 
	 * @param label
	 *            a description used for debugging only
	 */
	public void setDebugLabel(String label) {
		debugLabel = label;
	}

	/**
	 * Sets the label used to describe this command to the User.
	 * 
	 * @param label
	 *            the label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.ICommand#undo()
	 */
	@Override
	public void undo() {
	}

}
