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

import java.util.ArrayList;
import java.util.List;

/**
 * An aggregation of multiple <code>Commands</code>. A
 * <code>CompoundCommand</code> is executable if all of its contained Commands
 * are executable, and it has at least one contained Command. The same is true
 * for undo. When undo is called, the contained Commands are undone in the
 * reverse order in which they were executed.
 * <P>
 * An empty CompoundCommand is <em>not</em> executable.
 * <P>
 * A CompoundCommand can be {@link #unwrap() unwrapped}. Unwrapping returns the
 * simplest equivalent form of the CompoundCommand. So, if a CompoundCommand
 * contains just one Command, that Command is returned.
 */
public class CompoundCommand extends Command {

	private List commandList = new ArrayList();

	/**
	 * Constructs an empty CompoundCommand
	 * 
	 * @since 2.0
	 */
	public CompoundCommand() {
	}

	/**
	 * Constructs an empty CompoundCommand with the specified label.
	 * 
	 * @param label
	 *            the label for the Command
	 */
	public CompoundCommand(String label) {
		super(label);
	}

	/**
	 * Adds the specified command if it is not <code>null</code>.
	 * 
	 * @param command
	 *            <code>null</code> or a Command
	 */
	public void add(ICommand command) {
		if (command != null)
			commandList.add(command);
	}

	/**
	 * @see org.eclipse.gef4.mvc.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if (commandList.size() == 0)
			return false;
		for (int i = 0; i < commandList.size(); i++) {
			ICommand cmd = (ICommand) commandList.get(i);
			if (cmd == null)
				return false;
			if (!cmd.canExecute())
				return false;
		}
		return true;
	}

	/**
	 * @see org.eclipse.gef4.mvc.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		if (commandList.size() == 0)
			return false;
		for (int i = 0; i < commandList.size(); i++) {
			ICommand cmd = (ICommand) commandList.get(i);
			if (cmd == null)
				return false;
			if (!cmd.canUndo())
				return false;
		}
		return true;
	}

	/**
	 * Disposes all contained Commands.
	 * 
	 * @see org.eclipse.gef4.mvc.commands.Command#dispose()
	 */
	public void dispose() {
		for (int i = 0; i < commandList.size(); i++)
			((Command) getCommands().get(i)).dispose();
	}

	/**
	 * Execute the command.For a compound command this means executing all of
	 * the commands that it contains.
	 */
	public void execute() {
		for (int i = 0; i < commandList.size(); i++) {
			ICommand cmd = (ICommand) commandList.get(i);
			cmd.execute();
		}
	}

	/**
	 * This is useful when implementing
	 * {@link org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)}
	 * to display the Command's nested structure.
	 * 
	 * @return returns the Commands as an array of Objects.
	 */
	public Object[] getChildren() {
		return commandList.toArray();
	}

	/**
	 * @return the List of contained Commands
	 */
	public List getCommands() {
		return commandList;
	}

	/**
	 * @see org.eclipse.gef4.mvc.commands.Command#getLabel()
	 */
	public String getLabel() {
		String label = super.getLabel();
		if (label == null)
			if (commandList.isEmpty())
				return null;
		if (label != null)
			return label;
		return ((Command) commandList.get(0)).getLabel();
	}

	/**
	 * @return <code>true</code> if the CompoundCommand is empty
	 */
	public boolean isEmpty() {
		return commandList.isEmpty();
	}

	/**
	 * @see org.eclipse.gef4.mvc.commands.Command#redo()
	 */
	public void redo() {
		for (int i = 0; i < commandList.size(); i++)
			((ICommand) commandList.get(i)).redo();
	}

	/**
	 * @return the number of contained Commands
	 */
	public int size() {
		return commandList.size();
	}

	/**
	 * @see org.eclipse.gef4.mvc.commands.Command#undo()
	 */
	public void undo() {
		for (int i = commandList.size() - 1; i >= 0; i--)
			((ICommand) commandList.get(i)).undo();
	}

	/**
	 * Returns the simplest form of this Command that is equivalent. This is
	 * useful for removing unnecessary nesting of Commands.
	 * 
	 * @return the simplest form of this Command that is equivalent
	 */
	public ICommand unwrap() {
		switch (commandList.size()) {
		case 0:
			return UnexecutableCommand.INSTANCE;
		case 1:
			return (ICommand) commandList.get(0);
		default:
			return this;
		}
	}

}
