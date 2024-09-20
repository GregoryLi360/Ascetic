# Ascetic
[əˈset̬.ɪk] Adjective  
Austere in appearance, manner, or attitude. 
Marked by the deliberate rejection of excesses and luxuries in favor of inner growth.

---

Ascetic was named for its simplicity in design and semi-consistent syntatic aesthetic. 
It borrows concepts from and is heavily inspired by Swift, Haskell, Scala, Kotlin, Go, and Rust. 
Ascetic may be ran by the standard interpreter or transpiled into C++ code. 

## Table of Contents

<!-- no toc -->
- [Design Philosophy](#design-philosophy)
- [Feature Roadmap](#feature-roadmap)
- [Code Examples](#code-examples)
- [About the Author](#about-the-author)

## Design Philosophy

### Expressions vs. Statements
An expression is a snippet of code that evaluates to a value. 
On the other hand, a statement is a snippet of code that does something and does not evaluate to a value. 
This distinction is important to make because all expressions can be assigned to a variable. 
However, statements cannot be assigned to a variable because they have no value.  

Like Rust, Ascetic aligns with the expression-oriented design philosophy and aims to make expressions out of as much code as possible. 
The reasoning behind this approach is that expressions are much easier to work with than statements. 

A top-level program is made of a list of statements which fall into three major categories:
declaration statements, control flow statements, and expression statements.

1. Declaration Statements  

Declarations include variable declarations and type declarations (eg. structs). 
These declaration statements assign an identifier, a.k.a. a variable name, to a type of value or type.

2. Control Flow Statements

The most important control flow statement is the `return` statement. 
Ascetic opts for explicit returns unlike languages like Rust and Kotlin to communicate clear intentions and to keep syntatic consistency.  

The other control flow statement is the `guard` statement. 
Because regular control flow (if-else) statements in other languages are expressions in this language, the guard statement is the only way to return early within a nested code block.

3. Expression Statements  

Any expression can be made into a statement by appending a semicolon to it. 
This eliminates the need to assign unused values to a variable.

### Value vs. Reference Types
Part of the core philosophy of the functional programming paradigm is "immutablity". 
This idea indicates that values cannot be mutated for the duration of their lifetime.
Functional-leaning programming languages (eg. Rust and OCaml) default to assigning variables immutably. 
This is a neat idea when debugging since you know that every variable references only one value which cannot be changed by extraneous code such as an outside function.

On the other hand, most low-level (Rust being an exception, as usual) and object-oriented programming languages rely heavily on pointers and reference types respectively. 
This philosophy allows for in-place mutation which is much more efficient than creating copies of all immutable values.
For example, Java has its set of immutable primitive/default types, but all Objects are passed by reference. 
This default behavior is much more pragmatic but sacrifices safety as Objects can be freely altered by any reference to it.

Swift's creator Chris Lattner introduces a concept that strikes a balance between reference types and immutable values: Value types.
Essentially acting as Java primitives, value types are copied on assignment, meaning every variable holds a unique reference to the snapshot of data. 
This idea becomes powerful when compound types such as structs are passed by value, enabling mutability checks of all data types and their non-reference members. 

Ascetic is designed so that all types that can be value types are value types. 
The major exception will be closures as they are commonly used to capture mutable state. 

### Nullability
Tony Hoare the creator of "null" once said, "I call it my billion-dollar mistake. It was the invention of the null reference." 
Nullability is a tricky subject in the design of type systems. 
On one hand, null comes in handy to represent the absence of value or nothing and is also required to create useful data structures such as lists and trees.
On the other, it has costed many programmers the frustration of NullPointerExceptions because the programming language's type system fails to inform the programmer of a variable's nullability.  

The solution is to use `Optional` values and a `Unit` or `Nothing` type. 
This way, the compiler can — statically (during compile time) rather than dynamically (during runtime) — catch accesses to "null" values.
Ascetic uses something similar to:
- Swift's `Optional` type and `Void` return values
- Haskell's `Maybe` type and `Nothing` value
- Rust's `Optional` type and `Unit` type

The `Unit` type represents a variable that will never have a useful value while the `Optional` type represents a variable that could have a useful value or nothing.

Ascetic uses `Void` instead of `Unit` because it is more consistent with the idea of void in other programming languages. 
`Void` should only be used if a function has no expected return value. 
`Optional` should be used when a function can traditionally return a null in place of its declared return type.

<!-- 

### Error Handling 

### Protocols/Interfaces vs. Inheritance

Swift protocols (or interfaces in other languages) are contracts that structures may fulfill. 
Ascetic aims to offer something with the portability of Swift protocols but also features of abstract classes. 
While inheritance can theoretically do everything protocols can, the orthogonality of protocol composition offers a more concise and flat (as opposed to nested) way of requesting values that fulfill multiple contracts. 

Ascetic's structs are also final meaning structs may not extend/inherent from other structs. 
This promotes the philosophy of using protocols for both contracts and repeated code.

-->

## Feature Roadmap
More *'s = higher priority

- [ ] Lexer
  - [ ] Convert source files to token streams***
  - [ ] Syntax highlighting
  - [ ] Standardized code formatter
- [ ] Parser
  - [ ] Creates standardized abstract syntax tree***
  - [ ] Expressions
    - [ ] Literal expressions***
    - [ ] Identifier expressions***
    - [ ] Unary operator expressions***
    - [ ] Binary operator expressions***
    - [ ] Assignment expressions***
    - [ ] Grouped expressions***
    - [ ] Conditional/Control flow expressions**
    - [ ] Function expressions**
  - [ ] Statements
    - [ ] Expression statements***
    - [ ] Return statements***
    - [ ] Guard statements
    - [ ] Declaration statements
      - [ ] Variable declarations***
      - [ ] Struct declarations
- [ ] Compiler Backends
  - [ ] Interpreter**
  - [ ] C++*
  - [ ] LLVM?

- [ ] Immutable variables**
- [ ] Mutable variables
- [ ] Mutability checks
- [ ] Closures
  - [ ] Pure and non-capturing**
  - [ ] Capturing immutable variables**
  - [ ] Escaping*
  - [ ] Capturing mutable variables
  - [ ] Recursive**
  - [ ] Mutually recursive*
- [ ] Compound types (structs)
- [ ] Optional type*

<!--

---
- [ ] Array/List collection
- [ ] Enums
- [ ] Generics
- [ ] Protocols

- [ ] Modules
- [ ] A standard library
  - [ ] Collections Module
  - [ ] Foreign Function Interface Module
  - [ ] Filesystem Module
  - [ ] Graphics Module
  - [ ] GUI Module
  - [ ] Networking Module
  - [ ] Audio Module
  - [ ] Type Reflection Module
  - [ ] Memory Management Module

-->

## Code Examples
