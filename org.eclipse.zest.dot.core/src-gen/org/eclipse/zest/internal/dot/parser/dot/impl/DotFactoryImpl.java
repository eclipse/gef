/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.zest.internal.dot.parser.dot.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.zest.internal.dot.parser.dot.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DotFactoryImpl extends EFactoryImpl implements DotFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static DotFactory init()
  {
    try
    {
      DotFactory theDotFactory = (DotFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/zest/internal/dot/parser/Dot"); 
      if (theDotFactory != null)
      {
        return theDotFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new DotFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DotFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case DotPackage.GRAPHVIZ_MODEL: return createGraphvizModel();
      case DotPackage.MAIN_GRAPH: return createMainGraph();
      case DotPackage.STMT: return createStmt();
      case DotPackage.EDGE_STMT_NODE: return createEdgeStmtNode();
      case DotPackage.EDGE_STMT_SUBGRAPH: return createEdgeStmtSubgraph();
      case DotPackage.NODE_STMT: return createNodeStmt();
      case DotPackage.ATTRIBUTE: return createAttribute();
      case DotPackage.ATTR_STMT: return createAttrStmt();
      case DotPackage.ATTR_LIST: return createAttrList();
      case DotPackage.ALIST: return createAList();
      case DotPackage.SUBGRAPH: return createSubgraph();
      case DotPackage.EDGE_RHS: return createEdgeRhs();
      case DotPackage.EDGE_RHS_NODE: return createEdgeRhsNode();
      case DotPackage.EDGE_RHS_SUBGRAPH: return createEdgeRhsSubgraph();
      case DotPackage.NODE_ID: return createNodeId();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case DotPackage.EDGE_OP:
        return createEdgeOpFromString(eDataType, initialValue);
      case DotPackage.GRAPH_TYPE:
        return createGraphTypeFromString(eDataType, initialValue);
      case DotPackage.ATTRIBUTE_TYPE:
        return createAttributeTypeFromString(eDataType, initialValue);
      case DotPackage.COMPASS_PT:
        return createCompassPtFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case DotPackage.EDGE_OP:
        return convertEdgeOpToString(eDataType, instanceValue);
      case DotPackage.GRAPH_TYPE:
        return convertGraphTypeToString(eDataType, instanceValue);
      case DotPackage.ATTRIBUTE_TYPE:
        return convertAttributeTypeToString(eDataType, instanceValue);
      case DotPackage.COMPASS_PT:
        return convertCompassPtToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GraphvizModel createGraphvizModel()
  {
    GraphvizModelImpl graphvizModel = new GraphvizModelImpl();
    return graphvizModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MainGraph createMainGraph()
  {
    MainGraphImpl mainGraph = new MainGraphImpl();
    return mainGraph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Stmt createStmt()
  {
    StmtImpl stmt = new StmtImpl();
    return stmt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeStmtNode createEdgeStmtNode()
  {
    EdgeStmtNodeImpl edgeStmtNode = new EdgeStmtNodeImpl();
    return edgeStmtNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeStmtSubgraph createEdgeStmtSubgraph()
  {
    EdgeStmtSubgraphImpl edgeStmtSubgraph = new EdgeStmtSubgraphImpl();
    return edgeStmtSubgraph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NodeStmt createNodeStmt()
  {
    NodeStmtImpl nodeStmt = new NodeStmtImpl();
    return nodeStmt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Attribute createAttribute()
  {
    AttributeImpl attribute = new AttributeImpl();
    return attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttrStmt createAttrStmt()
  {
    AttrStmtImpl attrStmt = new AttrStmtImpl();
    return attrStmt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttrList createAttrList()
  {
    AttrListImpl attrList = new AttrListImpl();
    return attrList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AList createAList()
  {
    AListImpl aList = new AListImpl();
    return aList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Subgraph createSubgraph()
  {
    SubgraphImpl subgraph = new SubgraphImpl();
    return subgraph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeRhs createEdgeRhs()
  {
    EdgeRhsImpl edgeRhs = new EdgeRhsImpl();
    return edgeRhs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeRhsNode createEdgeRhsNode()
  {
    EdgeRhsNodeImpl edgeRhsNode = new EdgeRhsNodeImpl();
    return edgeRhsNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeRhsSubgraph createEdgeRhsSubgraph()
  {
    EdgeRhsSubgraphImpl edgeRhsSubgraph = new EdgeRhsSubgraphImpl();
    return edgeRhsSubgraph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NodeId createNodeId()
  {
    NodeIdImpl nodeId = new NodeIdImpl();
    return nodeId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeOp createEdgeOpFromString(EDataType eDataType, String initialValue)
  {
    EdgeOp result = EdgeOp.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertEdgeOpToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GraphType createGraphTypeFromString(EDataType eDataType, String initialValue)
  {
    GraphType result = GraphType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertGraphTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeType createAttributeTypeFromString(EDataType eDataType, String initialValue)
  {
    AttributeType result = AttributeType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertAttributeTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CompassPt createCompassPtFromString(EDataType eDataType, String initialValue)
  {
    CompassPt result = CompassPt.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertCompassPtToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DotPackage getDotPackage()
  {
    return (DotPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static DotPackage getPackage()
  {
    return DotPackage.eINSTANCE;
  }

} //DotFactoryImpl
