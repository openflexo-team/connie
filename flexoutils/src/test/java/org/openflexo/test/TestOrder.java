package org.openflexo.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to define a customized Junit execution order, in a class<br>
 * This annotation should be used in combination with OrdererRunner
 * 
 * @author sylvain
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface TestOrder {

	/**
	 * Order of JUnit test to run
	 */
	int value() default 0;
}
