package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class TryOutSwtFocusTraversal {
	public static void main(String[] args) {
		new TryOutSwtFocusTraversal();
	}

	Display display = new Display();

	Shell shell = new Shell(display);

	public TryOutSwtFocusTraversal() {
		init();

		shell.pack();
		shell.open();

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				// If no more entries in event queue
				display.sleep();
			}
		}

		display.dispose();
	}

	private void init() {
		shell.setLayout(new RowLayout());
		Composite composite1 = new Composite(shell, SWT.BORDER);
		composite1.setLayout(new RowLayout());
		composite1.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		Button button1 = new Button(composite1, SWT.PUSH);
		button1.setText("Button1");
		List list = new List(composite1, SWT.MULTI | SWT.BORDER);
		list.setItems(new String[] { "Item-1", "Item-2", "Item-3" });

		Button radioButton1 = new Button(composite1, SWT.RADIO);
		radioButton1.setText("radio-1");
		Button radioButton2 = new Button(composite1, SWT.RADIO);
		radioButton2.setText("radio-2");
		Composite composite2 = new Composite(shell, SWT.BORDER);
		composite2.setLayout(new RowLayout());
		composite2.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
		Button button2 = new Button(composite2, SWT.PUSH);
		button2.setText("Button2");
		final Canvas canvas = new Canvas(composite2, SWT.NULL);
		canvas.setSize(50, 50);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));

		Combo combo = new Combo(composite2, SWT.DROP_DOWN);
		combo.add("combo");
		combo.select(0);
		canvas.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				GC gc = new GC(canvas);
				// Erase background first.
				Rectangle rect = canvas.getClientArea();
				gc.fillRectangle(rect.x, rect.y, rect.width, rect.height);
				Font font = new Font(display, "Arial", 32, SWT.BOLD);
				gc.setFont(font);
				gc.drawString("" + e.character, 15, 10);
				gc.dispose();
				font.dispose();
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		canvas.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT
						|| e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
					e.doit = true;
				}
			}
		});
		composite1.setTabList(new Control[] { button1, list });
		composite2.setTabList(new Control[] { button2, canvas, combo });
		shell.setTabList(new Control[] { composite2, composite1 });
	}
}
