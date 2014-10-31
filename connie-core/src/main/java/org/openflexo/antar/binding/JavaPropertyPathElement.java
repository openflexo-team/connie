package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

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

	public JavaPropertyPathElement(BindingPathElement parent, String propertyName) {
		super(parent, propertyName, Object.class);
		keyValueProperty = KeyValueLibrary.getKeyValueProperty(parent.getType(), propertyName);

		if (keyValueProperty != null) {
			setType(keyValueProperty.getType());
		} else {
			LOGGER.warning("cannot find property " + propertyName + " for " + parent + " which type is " + parent.getType());
		}

		warnWhenInconsistentData(parent, propertyName);

	}

	public JavaPropertyPathElement(BindingPathElement parent, KeyValueProperty property) {
		super(parent, property.getName(), property.getType());
		keyValueProperty = property;

		warnWhenInconsistentData(parent, property.getName());
	}

	private void warnWhenInconsistentData(BindingPathElement parent, String propertyName) {

		if (keyValueProperty.getGetMethod() != null
				&& !TypeUtils.isTypeAssignableFrom(keyValueProperty.getGetMethod().getDeclaringClass(), getParent().getType())) {
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
			*/

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
		} else {
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
		return "JavaProperty " + getParent().getType() + "#" + getPropertyName();
	}
}
