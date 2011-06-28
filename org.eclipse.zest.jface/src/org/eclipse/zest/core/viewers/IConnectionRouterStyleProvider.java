/**
 * 
 */
package org.eclipse.zest.core.viewers;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.zest.core.widgets.Graph;

/**
 * Extension connection style provider that supports setting connection routers.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public interface IConnectionRouterStyleProvider {

	/**
	 * Calculates the connection router of the single relation.
	 * 
	 * @param rel
	 * @return the calculated connection router
	 * @see Graph#setDefaultConnectionRouter(ConnectionRouter) to set the
	 *      default router
	 */
	public ConnectionRouter getConnectionRouter(Object rel);
}
