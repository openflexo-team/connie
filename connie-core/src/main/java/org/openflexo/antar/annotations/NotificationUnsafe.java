package org.openflexo.antar.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DataBinding;

/**
 * When used as an annotation of a method, indicates that related getter or method used in a {@link DataBinding} as
 * {@link BindingPathElement} is not notification-safe<br>
 * 
 * This means that when the caching strategy of a {@link DataBinding} is set to CachingStrategy.PRAGMATIC_CACHE, involving of this method
 * will cause the execution of the binding to be recomputed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface NotificationUnsafe {

}
