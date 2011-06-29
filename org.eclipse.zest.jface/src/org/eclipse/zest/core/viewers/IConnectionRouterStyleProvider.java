/**
 * 
 */
package org.eclipse.zest.core.viewers;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.ui.services.IDisposable;

/**
 * Extension connection style provider that supports setting connection routers.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public interface IConnectionRouterStyleProvider extends IDisposable {

	/**
	 * Returns the connection router of the single relation.
	 * 
	 * @param rel
	 * @return the connection router for rel
	 */
	public ConnectionRouter getRouter(Object rel);
}
