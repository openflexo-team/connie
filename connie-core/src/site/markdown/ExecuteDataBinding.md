# Execute DataBinding objects

  Once your ```DataBinding``` is defined, you can "execute" it in a [BindingEvaluationContext](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingEvaluationContext.html).
  
  ```BindingEvaluationContext``` is the run-time environment and must implement variable resolving. 
  Only this method need to be implemented:

```
public Object getValue(BindingVariable variable);
```  
   
  Evaluation of ```DataBinding``` is typically performed using this code:

```
DataBinding<String> myBinding = ...; // myBinding has been initialized above
BindingEvaluationContext evaluationContext = ...; // evaluationContext should be somewhere initialized
String value = myBinding.getBindingValue(evaluationContext);
```  
  
  This code computes the value of ```"person.name"```.
  
  Supposing that this binding has been declared as settable (here with ```BindingDefinitionType.GET_SET```), we might assign a value to the name of a ```Person``` while invoking this code:
  
```
DataBinding<String> myBinding = ...; // myBinding has been initialized above
BindingEvaluationContext evaluationContext = ...; // evaluationContext should be somewhere initialized
myBinding.setBindingValue("aName",evaluationContext);
```  

  General execution:
 
```
DataBinding<String> myBinding = ...; // myBinding has been initialized above
BindingEvaluationContext evaluationContext = ...; // evaluationContext should be somewhere initialized
Object value = myBinding.execute(evaluationContext);
```  
  
Reference documentation

  (overview) [Introduction to CONNIE](index.md)
  
  (advanced) [Defining a binding strategy](DefineBindingStrategy.md)

  (programmer) [Defining bindable context](DefineBindableContext.md)

  (user) [Defining DataBinding objects](DefineDataBinding.md)