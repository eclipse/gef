/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.zest.internal.dot.parser.dot.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage
 * @generated
 */
public class DotSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static DotPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DotSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = DotPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @parameter ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case DotPackage.GRAPHVIZ_MODEL:
      {
        GraphvizModel graphvizModel = (GraphvizModel)theEObject;
        T result = caseGraphvizModel(graphvizModel);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.MAIN_GRAPH:
      {
        MainGraph mainGraph = (MainGraph)theEObject;
        T result = caseMainGraph(mainGraph);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.STMT:
      {
        Stmt stmt = (Stmt)theEObject;
        T result = caseStmt(stmt);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.EDGE_STMT_NODE:
      {
        EdgeStmtNode edgeStmtNode = (EdgeStmtNode)theEObject;
        T result = caseEdgeStmtNode(edgeStmtNode);
        if (result == null) result = caseStmt(edgeStmtNode);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.EDGE_STMT_SUBGRAPH:
      {
        EdgeStmtSubgraph edgeStmtSubgraph = (EdgeStmtSubgraph)theEObject;
        T result = caseEdgeStmtSubgraph(edgeStmtSubgraph);
        if (result == null) result = caseStmt(edgeStmtSubgraph);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.NODE_STMT:
      {
        NodeStmt nodeStmt = (NodeStmt)theEObject;
        T result = caseNodeStmt(nodeStmt);
        if (result == null) result = caseStmt(nodeStmt);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.ATTRIBUTE:
      {
        Attribute attribute = (Attribute)theEObject;
        T result = caseAttribute(attribute);
        if (result == null) result = caseStmt(attribute);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.ATTR_STMT:
      {
        AttrStmt attrStmt = (AttrStmt)theEObject;
        T result = caseAttrStmt(attrStmt);
        if (result == null) result = caseStmt(attrStmt);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.ATTR_LIST:
      {
        AttrList attrList = (AttrList)theEObject;
        T result = caseAttrList(attrList);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.ALIST:
      {
        AList aList = (AList)theEObject;
        T result = caseAList(aList);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.SUBGRAPH:
      {
        Subgraph subgraph = (Subgraph)theEObject;
        T result = caseSubgraph(subgraph);
        if (result == null) result = caseStmt(subgraph);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.EDGE_RHS:
      {
        EdgeRhs edgeRhs = (EdgeRhs)theEObject;
        T result = caseEdgeRhs(edgeRhs);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.EDGE_RHS_NODE:
      {
        EdgeRhsNode edgeRhsNode = (EdgeRhsNode)theEObject;
        T result = caseEdgeRhsNode(edgeRhsNode);
        if (result == null) result = caseEdgeRhs(edgeRhsNode);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.EDGE_RHS_SUBGRAPH:
      {
        EdgeRhsSubgraph edgeRhsSubgraph = (EdgeRhsSubgraph)theEObject;
        T result = caseEdgeRhsSubgraph(edgeRhsSubgraph);
        if (result == null) result = caseEdgeRhs(edgeRhsSubgraph);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case DotPackage.NODE_ID:
      {
        NodeId nodeId = (NodeId)theEObject;
        T result = caseNodeId(nodeId);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Graphviz Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Graphviz Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGraphvizModel(GraphvizModel object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Main Graph</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Main Graph</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMainGraph(MainGraph object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Stmt</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Stmt</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStmt(Stmt object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Edge Stmt Node</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Edge Stmt Node</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEdgeStmtNode(EdgeStmtNode object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Edge Stmt Subgraph</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Edge Stmt Subgraph</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEdgeStmtSubgraph(EdgeStmtSubgraph object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Node Stmt</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Node Stmt</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNodeStmt(NodeStmt object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttribute(Attribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attr Stmt</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attr Stmt</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttrStmt(AttrStmt object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attr List</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attr List</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttrList(AttrList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>AList</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>AList</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAList(AList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Subgraph</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Subgraph</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSubgraph(Subgraph object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Edge Rhs</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Edge Rhs</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEdgeRhs(EdgeRhs object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Edge Rhs Node</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Edge Rhs Node</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEdgeRhsNode(EdgeRhsNode object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Edge Rhs Subgraph</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Edge Rhs Subgraph</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEdgeRhsSubgraph(EdgeRhsSubgraph object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Node Id</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Node Id</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNodeId(NodeId object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //DotSwitch
