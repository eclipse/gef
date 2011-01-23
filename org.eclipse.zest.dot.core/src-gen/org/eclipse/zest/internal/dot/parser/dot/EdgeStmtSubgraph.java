/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge Stmt Subgraph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getSubgraph <em>Subgraph</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getEdgeRHS <em>Edge RHS</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtSubgraph()
 * @model
 * @generated
 */
public interface EdgeStmtSubgraph extends Stmt
{
  /**
   * Returns the value of the '<em><b>Subgraph</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Subgraph</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Subgraph</em>' containment reference.
   * @see #setSubgraph(Subgraph)
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtSubgraph_Subgraph()
   * @model containment="true"
   * @generated
   */
  Subgraph getSubgraph();

  /**
   * Sets the value of the '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getSubgraph <em>Subgraph</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Subgraph</em>' containment reference.
   * @see #getSubgraph()
   * @generated
   */
  void setSubgraph(Subgraph value);

  /**
   * Returns the value of the '<em><b>Edge RHS</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Edge RHS</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Edge RHS</em>' containment reference.
   * @see #setEdgeRHS(EdgeRhs)
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtSubgraph_EdgeRHS()
   * @model containment="true"
   * @generated
   */
  EdgeRhs getEdgeRHS();

  /**
   * Sets the value of the '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph#getEdgeRHS <em>Edge RHS</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Edge RHS</em>' containment reference.
   * @see #getEdgeRHS()
   * @generated
   */
  void setEdgeRHS(EdgeRhs value);

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.zest.internal.dot.parser.dot.AttrList}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' containment reference list.
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getEdgeStmtSubgraph_Attributes()
   * @model containment="true"
   * @generated
   */
  EList<AttrList> getAttributes();

} // EdgeStmtSubgraph
