package org.eclipse.gef4.mvc.tools;


public interface ICompositeTool<V> extends ITool<V> {
	
	public void add(ITool<V> tool);
	
	public void add(int intex, ITool<V> tool);
	
	public void remove(ITool<V> tool);
	
	public void remove(int index);
	
}
