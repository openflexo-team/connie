package org.openflexo.connie;

import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings("serial")
public class DataBindingFactory {

	/** Holder used to get the generic type {@code List<?>} from the JDK */
	private static class GenericTypes {
		@SuppressWarnings("unused")
		List<?> list;
	}

	private static final Type LIST_TYPE;

	static {
		try {
			LIST_TYPE = GenericTypes.class.getDeclaredField("list").getGenericType();
		} catch (NoSuchFieldException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static DataBinding<Boolean> makeTrueBinding() {
		return new DataBinding<>("true");
	}

	public static DataBinding<Boolean> makeFalseBinding() {
		return new DataBinding<>("false");
	}

	public static DataBinding<List<?>> makeListBinding(Bindable owner) {
		DataBinding<List<?>> result = new DataBinding<>(owner, LIST_TYPE, DataBinding.BindingDefinitionType.GET);
		result.setBindingName("list");
		return result;
	}

	public static DataBinding<Object[]> makeArrayBinding(Bindable owner) {
		DataBinding<Object[]> result = new DataBinding<>(owner, Object[].class, DataBinding.BindingDefinitionType.GET);
		result.setBindingName("array");
		return result;
	}

	public static DataBinding<Object[]> makeArrayBinding(Bindable owner, String unparsed) {
		DataBinding<Object[]> result = new DataBinding<>(unparsed, owner, Object[].class, DataBinding.BindingDefinitionType.GET);
		result.setBindingName("array");
		return result;
	}
}
