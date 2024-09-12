# Ascetic
[əˈset̬.ɪk] Adjective  
Austere in appearance, manner, or attitude. 
Marked by the deliberate rejection of excesses and luxuries in favor of inner growth.

---

Ascetic was named for its simplicity in design and semi-consistent syntatic aesthetic. 
It borrows concepts from and was heavily inspired by Swift, Haskell, Scala, Kotlin, Go, and Rust. 
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
