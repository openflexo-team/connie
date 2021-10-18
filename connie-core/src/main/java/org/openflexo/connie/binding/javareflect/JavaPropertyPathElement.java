/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.connie.binding.javareflect;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.annotations.NotificationUnsafe;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Modelize a Java simple get/set access through a property<br>
 * The path element may be settable or not.
 * 
 * @author sylvain
 * 
 */
public class JavaPropertyPathElement extends SimplePathElement<KeyValueProperty> {

	private static final Logger logger = Logger.getLogger(DataBinding.class.getPackage().getName());

	public JavaPropertyPathElement(IBindingPathElement parent, String propertyName) {
		super(parent, propertyName, Object.class);
	}

	public JavaPropertyPathElement(IBindingPathElement parent, KeyValueProperty property) {
		super(parent, property.getName(), property.getType());
		setProperty(property);
	}

	@Override
	public BindingPathCheck checkBindingPathIsValid(IBindingPathElement parentElement, Type parentType) {

		BindingPathCheck check = super.checkBindingPathIsValid(parentElement, parentType);

		if (getProperty() != null) {
			if (getProperty().getGetMethod() != null) {
				Type declaringType = getProperty().getGetMethod().getDeclaringClass();
				if (!(TypeUtils.isTypeAssignableFrom(declaringType, parentType))) {
					check.invalidBindingReason = "Inconsistent data: " + getParent().getType() + " is not an instance of "
							+ getProperty().getGetMethod().getDeclaringClass();
					check.valid = false;
				}
			}
		}
		else {
			check.invalidBindingReason = "Unresolved path element: " + getParsed();
			check.valid = false;
		}

		return check;
	}

	@Override
	public Type getType() {
		// IMPORTANT:
		// If declared type is a CustomType, don't try to instanciate the one from the keyValueProperty
		// which can also shadow a more specialized type encoded by a CustomType
		if (!(super.getType() instanceof CustomType) && getProperty() != null) {
			return TypeUtils.makeInstantiatedType(getProperty().getType(), getParent().getType());
		}
		return super.getType();
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	/**
	 * Return boolean indicating if this {@link BindingPathElement} is notification-safe (all modifications of data are notified using
	 * {@link PropertyChangeSupport} scheme)<br>
	 * 
	 * A {@link JavaPropertyPathElement} is notification-safe when related get method is not tagged with {@link NotificationUnsafe}
	 * annotation
	 * 
	 * Otherwise return true
	 * 
	 * @return
	 */
	@Override
	public boolean isNotificationSafe() {

		KeyValueProperty kvProperty = getProperty();
		Method m = kvProperty.getGetMethod();
		if (m == null || m.getAnnotation(NotificationUnsafe.class) != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSettable() {
		return getProperty() != null && getProperty().isSettable();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (resultingType != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(resultingType);
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		}
		else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getPropertyName() + "</b></p>";
		// returned +=
		// "<p><i>"+(property.getDescription()!=null?property.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) {
		Object obj = getProperty().getObjectValue(target);
		return obj;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
		getProperty().setObjectValue(value, target);
	}

	/*@Override
	public String toString() {
		return getSerializationRepresentation();
		// return "JavaPropertyPathElement " + getParent().getType() + "#" + getPropertyName();
	}*/

	@Override
	public boolean isResolved() {
		return getProperty() != null;
	}

	@Override
	public void resolve() {
		if (getParent() != null) {
			KeyValueProperty keyValueProperty = KeyValueLibrary.getKeyValueProperty(getParent().getType(), getParsed());
			if (keyValueProperty != null) {
				// setType(keyValueProperty.getType());
				// warnWhenInconsistentData(getParent(), getParsed());
				setProperty(keyValueProperty);
			}
			else {
				logger.warning("cannot find property " + getParsed() + " for " + getParent() + " which type is " + getParent().getType());
			}
		}
		else {
			logger.warning("cannot find parent for " + this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		if (isResolved()) {
			result = prime * result + Objects.hash(getProperty());
		}
		else {
			result = prime * result + Objects.hash(getParsed());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		JavaPropertyPathElement other = (JavaPropertyPathElement) obj;
		if (isResolved() != other.isResolved()) {
			return false;
		}
		if (isResolved()) {
			if (!Objects.equals(getProperty(), other.getProperty())) {
			}
			return Objects.equals(getProperty(), other.getProperty());
		}
		else {
			if (!Objects.equals(getParsed(), other.getParsed())) {
			}
			return Objects.equals(getParsed(), other.getParsed());
		}
	}

}
