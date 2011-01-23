/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.zest.internal.dot.parser.dot.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.zest.internal.dot.parser.dot.DotPackage
 * @generated
 */
public class DotAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static DotPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DotAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = DotPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DotSwitch<Adapter> modelSwitch =
    new DotSwitch<Adapter>()
    {
      @Override
      public Adapter caseGraphvizModel(GraphvizModel object)
      {
        return createGraphvizModelAdapter();
      }
      @Override
      public Adapter caseMainGraph(MainGraph object)
      {
        return createMainGraphAdapter();
      }
      @Override
      public Adapter caseStmt(Stmt object)
      {
        return createStmtAdapter();
      }
      @Override
      public Adapter caseEdgeStmtNode(EdgeStmtNode object)
      {
        return createEdgeStmtNodeAdapter();
      }
      @Override
      public Adapter caseEdgeStmtSubgraph(EdgeStmtSubgraph object)
      {
        return createEdgeStmtSubgraphAdapter();
      }
      @Override
      public Adapter caseNodeStmt(NodeStmt object)
      {
        return createNodeStmtAdapter();
      }
      @Override
      public Adapter caseAttribute(Attribute object)
      {
        return createAttributeAdapter();
      }
      @Override
      public Adapter caseAttrStmt(AttrStmt object)
      {
        return createAttrStmtAdapter();
      }
      @Override
      public Adapter caseAttrList(AttrList object)
      {
        return createAttrListAdapter();
      }
      @Override
      public Adapter caseAList(AList object)
      {
        return createAListAdapter();
      }
      @Override
      public Adapter caseSubgraph(Subgraph object)
      {
        return createSubgraphAdapter();
      }
      @Override
      public Adapter caseEdgeRhs(EdgeRhs object)
      {
        return createEdgeRhsAdapter();
      }
      @Override
      public Adapter caseEdgeRhsNode(EdgeRhsNode object)
      {
        return createEdgeRhsNodeAdapter();
      }
      @Override
      public Adapter caseEdgeRhsSubgraph(EdgeRhsSubgraph object)
      {
        return createEdgeRhsSubgraphAdapter();
      }
      @Override
      public Adapter caseNodeId(NodeId object)
      {
        return createNodeIdAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.GraphvizModel <em>Graphviz Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.GraphvizModel
   * @generated
   */
  public Adapter createGraphvizModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.MainGraph <em>Main Graph</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.MainGraph
   * @generated
   */
  public Adapter createMainGraphAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.Stmt <em>Stmt</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.Stmt
   * @generated
   */
  public Adapter createStmtAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode <em>Edge Stmt Node</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtNode
   * @generated
   */
  public Adapter createEdgeStmtNodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph <em>Edge Stmt Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeStmtSubgraph
   * @generated
   */
  public Adapter createEdgeStmtSubgraphAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.NodeStmt <em>Node Stmt</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.NodeStmt
   * @generated
   */
  public Adapter createNodeStmtAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.Attribute
   * @generated
   */
  public Adapter createAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.AttrStmt <em>Attr Stmt</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttrStmt
   * @generated
   */
  public Adapter createAttrStmtAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.AttrList <em>Attr List</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.AttrList
   * @generated
   */
  public Adapter createAttrListAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.AList <em>AList</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.AList
   * @generated
   */
  public Adapter createAListAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.Subgraph <em>Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.Subgraph
   * @generated
   */
  public Adapter createSubgraphAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhs <em>Edge Rhs</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhs
   * @generated
   */
  public Adapter createEdgeRhsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsNode <em>Edge Rhs Node</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhsNode
   * @generated
   */
  public Adapter createEdgeRhsNodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph <em>Edge Rhs Subgraph</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.EdgeRhsSubgraph
   * @generated
   */
  public Adapter createEdgeRhsSubgraphAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.zest.internal.dot.parser.dot.NodeId <em>Node Id</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.zest.internal.dot.parser.dot.NodeId
   * @generated
   */
  public Adapter createNodeIdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //DotAdapterFactory
