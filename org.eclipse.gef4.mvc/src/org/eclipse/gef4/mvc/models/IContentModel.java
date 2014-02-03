package org.eclipse.gef4.mvc.models;

import java.util.List;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;

public interface IContentModel extends IPropertyChangeSupport {

	public static final String CONTENTS_PROPERTY = "contents";
	
	public void setContents(List<Object> contents);
	
	public List<Object> getContents();
	
}
