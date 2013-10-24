/*******************************************************************************
* Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
* the accompanying materials are made available under the terms of the Eclipse
* Public License v1.0 which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* <p/>
* Contributors: Stephan Schwiebert - initial API and implementation
*******************************************************************************/
package org.eclipse.gef4.cloudio.examples.application.ui.customization;

import org.eclipse.gef4.cloudio.Word;
import org.eclipse.gef4.cloudio.layout.DefaultLayouter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * An example to show how to modify a layouter
 * @author sschwieb
 *
 */
public class CharacterLayouter extends DefaultLayouter {
	
	public CharacterLayouter(int x, int y) {
		super(x,y);
	}

	public Point getInitialOffset(Word word, Rectangle cloudArea) {
		Point parentOffsets = super.getInitialOffset(word, new Rectangle(cloudArea.x, cloudArea.y, cloudArea.width/4, cloudArea.height/4));
		char firstChar = Character.toLowerCase(word.string.charAt(0));
		int x=cloudArea.width/4;
		int y = cloudArea.height/4;
		if(firstChar < 's') {
			x = 0;
			y = 0;
		}
		if(firstChar < 'm') {
			x = cloudArea.width/4;
			y = 0;
		}
		if(firstChar < 'g') {
			x = 0;
			y = cloudArea.height/4;
		}
		return new Point(x+parentOffsets.x, y+parentOffsets.y);
	}

}
