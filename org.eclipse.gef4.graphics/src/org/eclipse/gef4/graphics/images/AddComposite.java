package org.eclipse.gef4.graphics.images;

public class AddComposite implements ICompositionRule {

	public void compose(int[] a, int[] b, int[] r) {
		for (int i = 0; i < 4; i++) {
			r[i] = Math.min(255, a[i] + b[i]);
		}
	}

}
