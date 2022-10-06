[/images/components/connie/ConnieScreenshot] Connie

## Introduction to CONNIE

  CONNIE is a Java library allowing to define and execute an expression-based language over a generic API.

  This language is used to connect *any* technologies (for instance EMF, excel, OWL, etc.) with the common connie API. 
  It allows to navigate through a graph of objects mixing these heterogeneous technologies.

  The purpose of the connie API is to offer the ability to:
  
    * Define strong-typed paths (sequence of names and dots as in *group.persons.first.age*) and expressions (as *group.persons.first.age + 1*).

    * Execute (evaluate) strong-typed paths and expressions.
  
  There are 3 kinds of use:
  
    1. Connection developper: defines the binding strategy and develop [BindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingFactory.html)
  
    2. Programmer or modeller: uses BindingFactories and defines models and expressions (instances of 
    [BindingModel](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingModel.html), [BindingVariable](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingVariable.html), 
    [Bindable](/connie/javadoc/connie/connie-core/org/openflexo/connie/Bindable.html) and [DataBinding](/connie/javadoc/connie/connie-core/org/openflexo/connie/DataBinding.html))
  
    3. User: populates models and evaluates expressions
  
  CONNIE is delivered with an embedded ```BindingFactory``` dedicated to handle some kind of Plain Old Java Objects. 
  It allows to define and evaluate expressions with Java object instances of classes that define accessors (set and get methods) for attributes. 
  
Examples

1. The connection
  
    The current implementation proposes a [JavaBindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingFactory.html) that allows to navigate among 
    Java objects using a (key, value) coding protocol.
  
    For instance, in order to define or evaluate *person.age* the JavaBindingFactory assumes that Java objects are instances of classes that offers 
    getAge() and setAge(int) methods to read and write the property "age".
  
    We could have imagine other binding strategies for Java (using annotation or the java reflect interface).
  
    For the following examples, we assume that the binding strategy ExcelBindingFactory exists for excel that allows to define expressions like *sheet.row(12).col(4).content*
    
1. The programmer
  
    Imagine a programmer wants to allow the computation of *person.age + sheet.row(12).col(4).content*.
    
    Assuming the binding strategies are defined, s/he needs a context to be able to define the ```DataBinding``` "*person.age + sheet.row(12).col(4).content*".
    
    The context is an instance of the interface ```Bindable``` that requires the BindingFactories (here JavaBindingFactory and ExcelBindingFactory) and a ```BindingModel```.
    
    The ```BindingModel``` defines the ```BindingVariable```s used as the roots of paths or expressions (here person of type Person and sheet of type Sheet).
    No actual instance is needed. Instances are required by the user only at evaluation time.
    
    Now, a ```DataBinding``` can be defined. The String "person.age + sheet.row(12).col(4).content" can now be check against 
    the navigability and typing rules to know if it [isValid](/connie/javadoc/connie/connie-core/org/openflexo/connie/DataBinding.html#isValid).
  
1. The user
  
    The user can now define a ```BindingEvaluationContext``` where the ```BindingModel``` is instantiated with actual objects. 
    
    If the ```DataBinding``` isValid(), it can be evaluated().
    
  Note that, sophisticated observing and notifying services allow re-evaluating of all stages.
  
  * If the actual instance changes, the ```DataBinding``` is re-evaluated
  
  * If the ```DataBinding``` changes (to "person.age + sheet.row(12).col(4).content +1"), the ```DataBinding``` is re-evaluated
  
  * If the accesors are changed, the ```DataBinding``` is re-evaluated

Getting started with Connie

* 1. Define a binding strategy (advanced)
  
  You should first think of how to navigate among objects of the technologies you want connect.
  
  To do so, you have to choose an existing [BindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingFactory.html) or to define a new one.
  
  A good practice is to define a [BindingFactory](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingFactory.html) per technology.

  More about defining a binding strategy in [Defining a binding strategy](DefineBindingStrategy.md)

* 2. Define a bindable context (programmer)
  
  A binding is defined in the context of a [Bindable](/connie/javadoc/connie/connie-core/org/openflexo/connie/Bindable.html). 
  A ```Bindable``` basically references a ```BindingFactory``` (which defines how bindings are defined) and a ```BindingModel``` (which defines the evaluation context, also called environment).
  
  You have to define a [BindingModel](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingModel.html), which is the environment beeing considered at run-time.
  A ```BindingModel``` defines a list of [BindingVariable](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingVariable.html). A ```BindingVariable``` basically has a name and a type.
  
  More about defining a Bindable context in [Defining a bindable context](DefineBindableContext.md)
  
* 3. Define ```DataBinding``` objects (user)

  A [DataBinding](/connie/javadoc/connie/connie-core/org/openflexo/connie/DataBinding.html) is the abstraction of an evaluable 
  expression which might be executed in a [BindingEvaluationContext](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingEvaluationContext.html).
  
  A DataBinding is defined in the context of a [Bindable](/connie/javadoc/connie/connie-core/org/openflexo/connie/Bindable.html), which is the owner of ```DataBinding``` 
  (remember that ```Bindable``` references both a ```BindingFactory``` and a ```BindingModel```)
  
  A [DataBinding](/connie/javadoc/connie/connie-core/org/openflexo/connie/DataBinding.html) might be created using this constructor:
  
```
 public DataBinding(String unparsed, Bindable owner, Type declaredType, BindingDefinitionType bdType);
```

  Note that the ```BindingDefinitionType``` is an enum (SET, GET, GET_SET, EXECUTE) that 
  defines the access type of a binding, which is generally related to the purpose of the binding.

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

  More about defining ```DataBinding``` objects in [Defining DataBinding objects](DefineDataBinding.md).

* 4. Execute DataBinding objects

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
  
  More about executing ```DataBinding``` objects in {{{./ExecuteDataBinding.html}Executing DataBinding objects}}
   
Reference documentation

  (advanced) [Defining a binding strategy](DefineBindingStrategy.md)

  (programmer) [Defining bindable context](DefineBindableContext.md)

  (user) [Defining DataBinding objects](DefineDataBinding.md)

  (user) [Executing DataBinding objects](ExecuteDataBinding.md)
