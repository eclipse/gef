/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Subgraph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.Subgraph#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.Subgraph#getStmts <em>Stmts</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getSubgraph()
 * @model
 * @generated
 */
public interface Subgraph extends Stmt
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getSubgraph_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.zest.internal.dot.parser.dot.Subgraph#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Stmts</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.zest.internal.dot.parser.dot.Stmt}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Stmts</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Stmts</em>' containment reference list.
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getSubgraph_Stmts()
   * @model containment="true"
   * @generated
   */
  EList<Stmt> getStmts();

} // Subgraph
