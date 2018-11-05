package org.openflexo.connie;

import java.util.List;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public class DataBindingFactory {

	public static DataBinding<Boolean> makeTrueBinding() {
		return new DataBinding<>("true");
	}

	public static DataBinding<Boolean> makeFalseBinding() {
		return new DataBinding<>("false");
	}

	public static DataBinding<List<?>> makeListBinding(Bindable owner) {
		DataBinding<List<?>> result = new DataBinding<>(owner, new TypeToken<List<?>>() {
		}.getType(), DataBinding.BindingDefinitionType.GET);
		result.setBindingName("list");
		return result;
	}

	public static DataBinding<Object[]> makeArrayBinding(Bindable owner) {
		DataBinding<Object[]> result = new DataBinding<>(owner, new TypeToken<Object[]>() {
		}.getType(), DataBinding.BindingDefinitionType.GET);
		result.setBindingName("array");
		return result;
	}

	public static DataBinding<Object[]> makeArrayBinding(Bindable owner, String unparsed) {
		DataBinding<Object[]> result = new DataBinding<>(unparsed, owner, new TypeToken<Object[]>() {
		}.getType(), DataBinding.BindingDefinitionType.GET);
		result.setBindingName("array");
		return result;
	}
}
