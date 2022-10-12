# Define bindable context

A binding is defined in the context of a [Bindable](/connie/javadoc/connie/connie-core/org/openflexo/connie/Bindable.html). 
A ```Bindable``` basically references :
  
* a ```BindingFactory``` (which defines how bindings are defined) and 
    
* a ```BindingModel``` (which defines the evaluation context, also called environment). The [BindingModel](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingModel.html)
     is the environment beeing considered at run-time. It defines :
     
* a list of  [BindingVariable](/connie/javadoc/connie/connie-core/org/openflexo/connie/BindingVariable.html). A ```BindingVariable``` basically has a name and a type.
      
Example

  -- to do

Reference documentation

  (overview) [Introduction to CONNIE](index.md)
  
  (advanced) [Defining a binding strategy](DefineBindingStrategy.md)

  (programmer) [Defining bindable context](DefineBindableContext.md)

  (user) [Defining DataBinding objects](DefineDataBinding.md)