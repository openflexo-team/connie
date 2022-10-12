# Define DataBinding objects

  A [DataBinding](/connie/javadoc/connie/connie-core/org/openflexo/connie/DataBinding.html). is the abstraction of an evaluable
  expression which might be executed in a [BindingEvaluationContext](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingEvaluationContext.html).
  
  A DataBinding is defined in the context of a [Bindable](/connie/javadoc/connie/connie-core/org/openflexo/connie/Bindable.html)., which is the owner of ```DataBinding``` 
  (remember that ```Bindable``` references both a ```BindingFactory``` and a ```BindingModel```)
  
  A [DataBinding](/connie/javadoc/connie/connie-core/org/openflexo/connie/DataBinding.html) might be created using this constructor:
  
```
public DataBinding(String unparsed, Bindable owner, Type declaredType, BindingDefinitionType bdType);
```  

  For example:
  
```
Bindable myBindable = ...; // myBindable should be somewhere initialized
DataBinding<String> myBinding = new DataBinding<String>("person.name",myBindable,String.class,BindingDefinitionType.GET_SET);
```  
  
  Initialize a ```DataBinding```:
  
  * whose owner is the ```myBindable``` variable (a ```Bindable``` instance)
  
  * where declared type (type of expression defined by the binding) is ```String```
  
  * where binding is executable both as readable and settable
  
  * where expression is set to ```person.name```
  
 To be valid, ```myBindable``` variable should define:
  
  * a ```BindingModel``` declaring a ```BindingVariable``` called ```'person'``` (suppose that type of this variable is reflected by ```Person``` Java class)
  
  * a suitable ```BindingFactory``` allowing to browse ```'name'``` from a ```Person``` type (this might be implemented by ```Person``` Java class if this class defines both ```getName()``` and ```setName(String)``` methods, and if ```JavaBindingFactory``` is used)

 Validity of a ```DataBinding``` might be tested using ```isValid()``` method.

Reference documentation

  (overview) [Introduction to CONNIE](index.md)
  
  (advanced) [Defining a binding strategy](DefineBindingStrategy.md)

  (programmer) [Defining bindable context](DefineBindableContext.md)

  (user) [Defining DataBinding objects](DefineDataBinding.md)