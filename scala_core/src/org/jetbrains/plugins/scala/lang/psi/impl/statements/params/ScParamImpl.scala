package org.jetbrains.plugins.scala.lang.psi.impl.statements.params

import _root_.org.jetbrains.plugins.scala.lang.psi.types.{ScType, ScFunctionType}
import api.base._
import api.expr.ScFunctionExpr
import api.statements.params._
import api.statements._
import icons.Icons
import lexer.ScalaTokenTypes
import psi.ScalaPsiElementImpl
import com.intellij.lang.ASTNode
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util._
import psi.stubs.ScParameterStub
import toplevel.synthetic.JavaIdentifier
import com.intellij.psi._

/**
 * @author Alexander Podkhalyuzin
 * Date: 22.02.2008
 */

class ScParameterImpl extends ScalaStubBasedElementImpl[ScParameter] with ScParameter {
  def this(node: ASTNode) = {this (); setNode(node)}

  def this(stub: ScParameterStub) = {this (); setStub(stub); setNode(null)}

  override def toString: String = "Parameter"

  override def getTextOffset: Int = nameId.getTextRange.getStartOffset

  override def getNameIdentifier: PsiIdentifier = new JavaIdentifier(nameId)

  def nameId = {
    val id = findChildByType(ScalaTokenTypes.tIDENTIFIER)
    if (id == null) findChildByType(ScalaTokenTypes.tUNDER) else id
  }

  def paramType = findChild(classOf[ScParameterType])

  def getDeclarationScope = PsiTreeUtil.getParentOfType(this, classOf[ScParameterOwner])

  def getAnnotations = PsiAnnotation.EMPTY_ARRAY

  def getTypeElement = null

  def typeElement = paramType match {
    case Some(x) if x.typeElement != null => Some(x.typeElement)
    case _ => None
  }

  override def getUseScope = new LocalSearchScope(getDeclarationScope)

  def calcType() = typeElement match {
    case None => expectedType match {
      case Some(t) => t
      case None => lang.psi.types.Nothing
    }
    case Some(e) => e.getType
  }

  def isVarArgs = false

  def computeConstantValue = null

  def normalizeDeclaration() = false

  def hasInitializer = false

  def getInitializer = null

  def getType: PsiType = ScType.toPsi(calcType, getProject, getResolveScope)

  def getModifierList = findChildByClass(classOf[ScModifierList])

  def expectedType: Option[ScType] = getParent match {
    case cl: ScParameterClause => cl.getParent.getParent match {
      case f: ScFunctionExpr => f.expectedType.map({
        case ScFunctionType(_, params) =>
          val i = cl.parameters.indexOf(this)
          if (i >= 0 && i < params.length) params(i) else psi.types.Nothing
        case _ => psi.types.Nothing
      })
      case _ => None
    }
  }

}