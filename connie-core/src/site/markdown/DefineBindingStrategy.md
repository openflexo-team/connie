# How to define a BindingStrategy

  You should consider which binding strategy will apply to objects and types you will connect.
  This is highly dependant of underlying technology.
  
  This means that you have to choose a [BindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingFactory.html)
  
  You might also define your own BindingStrategy.
  To do so, you have to define your own [BindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingFactory.html).
  
  This is generally performed with your own implementation of the whole interface of ```BindingFactory```.
  You can find inspiration and an example in the provided [JavaBindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/JavaBindingFactory.html).
  
  This interface implies implementing twose 5 methods:
  
```
  public interface BindingFactory {

	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent);
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent);
	public SimplePathElement makeSimplePathElement(BindingPathElement father, String propertyName);
	public Function retrieveFunction(Type parentType, String functionName, List<DataBinding<?>> args);
	public FunctionPathElement makeFunctionPathElement(BindingPathElement father, Function function, List<DataBinding<?>> args);
}
```

Native JavaBindingFactory

  For using with native Java objects, simply use  [JavaBindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/JavaBindingFactory.html)
  
Reference documentation

  (overview) [Introduction to CONNIE](index.md)
  
  (advanced) [Defining a binding strategy](DefineBindingStrategy.md)

  (programmer) [Defining bindable context](DefineBindableContext.md)

  (user) [Defining DataBinding objects](DefineDataBinding.md)

  