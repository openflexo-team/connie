package org.openflexo.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class OrderedRunner extends BlockJUnit4ClassRunner {

	public OrderedRunner(Class klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List computeTestMethods() {
		List list = super.computeTestMethods();
		List copy = new ArrayList(list);
		Collections.sort(copy, new Comparator<FrameworkMethod>() {
			@Override
			public int compare(FrameworkMethod o1, FrameworkMethod o2) {
				return getTestOrder(o1) - getTestOrder(o2);
			}
		});
		return copy;
	}

	protected int getTestOrder(FrameworkMethod fm) {
		TestOrder to = fm.getMethod().getAnnotation(TestOrder.class);
		if (to != null) {
			return to.value();
		}
		return -1;
	}

}