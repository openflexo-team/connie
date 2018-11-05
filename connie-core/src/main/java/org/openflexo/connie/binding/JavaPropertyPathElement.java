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

package org.openflexo.connie.binding;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.annotations.NotificationUnsafe;
import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.kvc.KeyValueLibrary;
import org.openflexo.kvc.KeyValueProperty;
import org.openflexo.toolbox.ToolBox;

/**
 * Modelize a Java simple get/set access through a property<br>
 * The path element may be settable or not.
 * 
 * @author sylvain
 * 
 */
public class JavaPropertyPathElement extends SimplePathElement {

	private static final Logger LOGGER = Logger.getLogger(DataBinding.class.getPackage().getName());

	private final KeyValueProperty keyValueProperty;

	public JavaPropertyPathElement(IBindingPathElement parent, String propertyName) {
		super(parent, propertyName, Object.class);
		keyValueProperty = KeyValueLibrary.getKeyValueProperty(parent.getType(), propertyName);

		if (keyValueProperty != null) {
			setType(keyValueProperty.getType());
			warnWhenInconsistentData(parent, propertyName);
		}
		else {
			LOGGER.warning("cannot find property " + propertyName + " for " + parent + " which type is " + parent.getType());
		}
	}

	public JavaPropertyPathElement(IBindingPathElement parent, KeyValueProperty property) {
		super(parent, property.getName(), property.getType());
		keyValueProperty = property;

		warnWhenInconsistentData(parent, property.getName());
	}

	private void warnWhenInconsistentData(IBindingPathElement parent, String propertyName) {

		if (keyValueProperty.getGetMethod() != null) {
			Type declaringType = keyValueProperty.getGetMethod().getDeclaringClass();
			Type parentType = getParent().getType();
			if (!(TypeUtils.isTypeAssignableFrom(declaringType, parentType))) {
				LOGGER.warning("Inconsistent data: " + getParent().getType() + " is not an instance of "
						+ keyValueProperty.getGetMethod().getDeclaringClass());
				/*System.out.println("propertyName=" + propertyName);
				System.out.println("parent=" + parent);
				System.out.println("parent type=" + parent.getType());
				System.out.println("bc = " + TypeUtils.getBaseClass(parent.getType()));
				System.out.println("keyValueProperty=" + keyValueProperty);
				System.out.println("keyValueProperty.getGetMethod()=" + keyValueProperty.getGetMethod());
				System.out.println("keyValueProperty.getType()=" + keyValueProperty.getType());
				System.out.println("keyValueProperty.getDeclaringClass()=" + keyValueProperty.getDeclaringClass());
				System.out.println("keyValueProperty.getDeclaringType()=" + keyValueProperty.getDeclaringType());
				System.out.println("Types incompatibles:");
				System.out.println("declaringType=" + declaringType);
				System.out.println("parentType=" + parentType);*/
			}
		}
	}

	@Override
	public Type getType() {
		// IMPORTANT:
		// If declared type is a CustomType, don't try to instanciate the one from the keyValueProperty
		// which can also shadow a more specialized type encoded by a CustomType
		if (!(super.getType() instanceof CustomType) && keyValueProperty != null) {
			return TypeUtils.makeInstantiatedType(keyValueProperty.getType(), getParent().getType());
		}
		return super.getType();
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	public KeyValueProperty getKeyValueProperty() {
		return keyValueProperty;
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

		KeyValueProperty kvProperty = getKeyValueProperty();
		Method m = kvProperty.getGetMethod();
		if (m == null || m.getAnnotation(NotificationUnsafe.class) != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSettable() {
		return keyValueProperty != null && keyValueProperty.isSettable();
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
		Object obj = keyValueProperty.getObjectValue(target);
		return obj;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
		keyValueProperty.setObjectValue(value, target);
	}

	@Override
	public String toString() {
		return "JavaPropertyPathElement " + getParent().getType() + "#" + getPropertyName();
	}
}
