package org.eclipse.gef4.mvc.fx.tests;

import static org.junit.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;

public class FXTypeToolTests {

	/**
	 * Ensure all tests are executed on the JavaFX application thread (and the
	 * JavaFX toolkit is properly initialized).
	 */
	@Rule
	public FxApplicationThreadRule fxApplicationThreadRule = new FxApplicationThreadRule();

	private class FXDomainDriver extends FXDomain {
		protected int openedExecutionTransactions = 0;
		protected int closedExecutionTransactions = 0;

		@Override
		public void openExecutionTransaction(ITool<Node> tool) {
			super.openExecutionTransaction(tool);
			openedExecutionTransactions++;
		}

		public void closeExecutionTransaction(ITool<Node> tool) {
			closedExecutionTransactions++;
			super.closeExecutionTransaction(tool);
		};
	}

	/**
	 * It is important that a (single) execution transaction (see
	 * {@link IDomain#openExecutionTransaction(org.eclipse.gef4.mvc.tools.ITool)}
	 * ) is used for a complete press/drag interaction gesture, because
	 * otherwise the transactional results of the gesture could not be undone.
	 */
	@Test
	public void singleExecutionTransactionUsedForInteraction()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new MvcFxModule() {
			@Override
			protected void bindIDomain() {
				// stub the domain to be able to keep track of opened execution
				// transactions
				binder().bind(new TypeLiteral<IDomain<Node>>() {
				}).to(FXDomainDriver.class);
			}
		});

		// create domain (i.e. FXDomainDriver stub)
		final FXDomainDriver domain = (FXDomainDriver) injector.getInstance(IDomain.class);

		// hook viewer to scene
		Scene scene = new Scene(domain.<FXViewer> getAdapter(IViewer.class).getScrollPane(), 100, 100);
		JFXPanel panel = new JFXPanel();
		panel.setScene(scene);

		// activate domain, so tool gets activated and can register listeners
		domain.activate();

		// create robot to simulate events
		Robot robot = new Robot();

		// simulate press/release gesture
		domain.openedExecutionTransactions = 0;
		domain.closedExecutionTransactions = 0;
		assertEquals("No execution transaction should have been opened", 0, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
		robot.keyPress(KeyEvent.VK_K);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
		robot.keyRelease(KeyEvent.VK_K);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("A single execution transaction should have been closed", 1, domain.closedExecutionTransactions);
	}

}
