package org.eclipse.gef4.mvc.commands;

public interface ICommand {
	
	public abstract String getLabel();

	/**
	 * @return <code>true</code> if the command can be executed
	 */
	public abstract boolean canExecute();

	/**
	 * @return <code>true</code> if the command can be undone. This method
	 *         should only be called after <code>execute()</code> or
	 *         <code>redo()</code> has been called.
	 */
	public abstract boolean canUndo();

	/**
	 * Returns a Command that represents the chaining of a specified Command to
	 * this Command. The Command being chained will <code>execute()</code> after
	 * this command has executed, and it will <code>undo()</code> before this
	 * Command is undone.
	 * 
	 * @param command
	 *            <code>null</code> or the Command being chained
	 * @return a Command representing the union
	 */
	public abstract ICommand chain(ICommand command);

	/**
	 * executes the Command. This method should not be called if the Command is
	 * not executable.
	 */
	public abstract void execute();

	/**
	 * Re-executes the Command. This method should only be called after
	 * <code>undo()</code> has been called.
	 */
	public abstract void redo();

	/**
	 * Undoes the changes performed during <code>execute()</code>. This method
	 * should only be called after <code>execute</code> has been called, and
	 * only when <code>canUndo()</code> returns <code>true</code>.
	 * 
	 * @see #canUndo()
	 */
	public abstract void undo();

}