/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.connie.expr;

import java.lang.reflect.Type;
import java.util.Vector;

import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;

public class CastExpression extends Expression {

	private final TypeReference castType;
	private Expression argument;

	public CastExpression(TypeReference castType, Expression argument) {
		super();
		this.castType = castType;
		this.argument = argument;
	}

	@Override
	public int getDepth() {
		return argument.getDepth() + 1;
	}

	public TypeReference getCastType() {
		return castType;
	}

	public Expression getArgument() {
		return argument;
	}

	public void setArgument(Expression argument) {
		this.argument = argument;
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		Expression expression = this;
		Expression transformedArgument = argument.transform(transformer);

		if (!transformedArgument.equals(argument)) {
			expression = new CastExpression(castType, transformedArgument);
		}

		return transformer.performTransformation(expression);
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		argument.visit(visitor);
		visitor.visit(this);
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return TypeUtils.kindOfType(castType.getType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		Vector<Expression> returned = new Vector<>();
		returned.add(getArgument());
		return returned;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CastExpression) {
			CastExpression e = (CastExpression) obj;
			return castType.equals(e.getCastType()) && getArgument().equals(e.getArgument());
		}
		return super.equals(obj);
	}

	@Override
	public boolean isSettable() {
		return getArgument().isSettable();
	}

	@Override
	public Type getAccessedType() {
		return getCastType().getType();
	}

}
