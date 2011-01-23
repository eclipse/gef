/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attr List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.zest.internal.dot.parser.dot.AttrList#getA_list <em>Alist</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getAttrList()
 * @model
 * @generated
 */
public interface AttrList extends EObject
{
  /**
   * Returns the value of the '<em><b>Alist</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.zest.internal.dot.parser.dot.AList}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Alist</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Alist</em>' containment reference list.
   * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage#getAttrList_A_list()
   * @model containment="true"
   * @generated
   */
  EList<AList> getA_list();

} // AttrList
