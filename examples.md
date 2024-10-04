# Ascetic by Example

Code is executed from the global scope, so you don’t need a `main()` function.
This should be familiar to people who have written Python, Javascript, or Swift in the past.

## Comments

Ascetic supports single-line and multi-line comments.
Unlike Swift, Haskell, Scala, or Rust, nested comments are not supported.
This is to maintain compatibility with comments from C based languages.

```C
// Single-line comment

/*
    Multi-line comment 
    that can span 
    several lines
*/
```


## Variables

Variable names must start with a letter or underscore, they may not clash with predefined keywords, they may include numbers.

### Immutable variable declaration

Use `let` to declare immutable variables. 

```Swift
let toggle = false;
let number = 42; 
let decimal = 3.14;
let message = "Hello world";
```

### Optional type annotations 

Type annotations are usually optional. 
Functions are an exception, but we will get to that later.

```Swift
let toggle: Bool = false;
let number: Int = 42; // i32
let decimal: Float = 3.14; // f32
let message: String = "Hello world";
```

## Control Flow

### Anonymous Code Blocks

Anonymous code blocks makes scoping easy. 
If an expression is an anonymous code block, it will be executed on site.

```Swift
{
    let x = 0;
};

x; // error: x is not defined
```

### Return Statements

Return statements will `return` the expression that follows to the last returnable scope. 
This means that it should always be the last statement in a code block. 
If no return statement is found within a code block, it automatically returns the primitive value `void`.

```Swift
let zero = { return 0; };
```

### Guard Statements

Guard statements allow for early returns from returnable scopes.
It is similar to if expressions, except it is a statement (cannot be assigned to a variable) and does not have `else if` or `else` branches. 
Use the `guard` keyword followed by a clause that, when evaluated to `true`, runs the trailing anonymous code block.

```Swift
let one = {
    guard true { return 1; };
    return 0;
};
```

### If Expressions

Use `if`, `else if`, and `else` to create branching values. 
Each clause should evaluate to a `Bool`. 
The code block that corresponds to the first clause to evaluate `true` is executed.
If all clauses evaluate to `false`, the `void` value is returned.

```Swift
let two = if true { return 2; } 
          else { return 0; };

let unit: Void = if false {};
```

## Operations

Ascetic supports all basic operations. 
However, operations only work on identical types.

```Swift
/* Bool operations */
!true; // false
true && false; // false
true || false; // true

/* Int/Float operations */
-1 + 2; // 1
9 - 3 * 3; // 0
5 / 2; // 2
5 % 2; // 1
5.0 / 2.0; // 2.5
5.0 / 2; // error: division cannot operate on Float and Int

/* String operations */
"Hello" + "World"; // HelloWorld
```

## Functions

Function declarations use the same syntax as variable declarations. 
However, the compiler needs ample information about the function's type signature. 

```Swift
/* ok! */
let f = (a: Int, b: Int) -> Int {
    return a + b;
};

/* also ok! */
let g: (Int, Int) -> Int = (a, b) -> {
    return a + b;
};
```

Because functions are variables, they are first class! 
This means that they can be passed as parameters to other functions and returned from other functions.

```Swift
let wrapAdder = (adder: (Int, Int) -> Int) -> (Int, Int) -> Int {
    var callCounter = 0;
    return (a, b) -> {
        callCounter += 1;
        adder(a, b);    
    };
};
```

Recursion is also supported.

```Swift
let fib = (n: Int) -> Int {
    guard n <= 1 { return n; };
    return fib(n - 1) + fib(n - 2);
};

fib(10); // 55
```