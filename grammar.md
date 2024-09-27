## Formal Language Grammar represented in EBNF dialect

### Table modified from [The Rust Reference](https://doc.rust-lang.org/reference/notation.html):
*Changes inspired by the [Extended Backus–Naur Form (EBNF) notation guidelines](https://matt.might.net/articles/grammars-bnf-ebnf/)*

Notation     | Examples             | Meaning
------------ | -------------------- | -------
lower_snake  | letter, digit        | Common aliases
CAPITAL_SNAKE| PLUS, OPEN_PAREN     | A token type produced by the lexer
PascalCase   | VariableDeclaration  | A syntactical production, aka an Abstract Syntax Tree node
"string"     | "x", "abc", "\n"     | The exact character(s), except for escaped characters, within the "double quotes"
x*           | letter*              | 0 or more of x
x+           | whitespace+          | 1 or more of x
\|           | "+" \| "-"           | Either one or another
,            | "let", IDENTIFIER    | Concatenation of notations, optional whitespace depending on if it can be tokenized without whitespace
[ , ]        | [STRING_LITERAL]     | Optional of notations listed within [square brackets]
[ \| ]        | ["a" \| "A"]          | Any of the characters listed
[ - ]        | ["a"-"z"]            | Any of the characters in the range, according to the characters' standard ASCII value
~            | ~["a"-"z"]           | Any characters, except those that comply with the successive notation
( )          | ("abc", PLUS, "def") \| ("def", PLUS, "abc) | Groups items
{ }          | { Statement }        | Repetition, 0 or more, of the notation within the { curly braces }
? ?          | ? special case ?     | Plain english describing some special case

## Common aliases: 
```EBNF
letter := ["a"-"z"] | ["A"-"Z"]
digit := ["0"-"9"]
whitespace := [" " | "\t" | "\r" | "\u000B" | "u001C" | "u001D" | "u001E" | "u001F"]+
```

# Lexer defined, token type aliases:
```EBNF
TOKEN := ? anything listed below ?

END := "\0"
INVALID := ~(TOKEN ? excluding INVALID ?), END

COMMENT := ("//", ~["\n"]*, "\n") | ("/*", ~["*/"]*, "*/")

LET := "let"
RETURN := "return"
IF := "if"
ELSE := "else"
GUARD := "guard"

IDENTIFIER := letter [letter | digit]*
PRIMITIVE_TYPE := "Bool" | "Int" | "Float" | "String" | "Void" | "Any"
ASSIGNMENT := "="

BOOLEAN_LITERAL := "true" | "false"
STRING_LITERAL := "\"", ~["\""]*, "\"" 
INTEGER_LITERAL := digit+
FLOAT_LITERAL := [digit]+, PERIOD, digit+

OPEN_PAREN := "("
CLOSED_PAREN := ")"
OPEN_CURLY := "{"
CLOSED_CURLY := "}"
OPEN_BRACKET := "["
CLOSED_BRACKET := "]"

COLON := ":"
SEMICOLON := ";"
PERIOD := "."
COMMA := ","
BANG := "!"
ARROW := "->"
PLUS := "+"
MINUS := "-"
MULTIPLY := "*"
DIVIDE := "/"
MODULO := "%"

AND := "&&"
OR := "||"

EQUALS_TO := "=="
NOT_EQUALS_TO := "!="
LESS_THAN := "<"
LESS_THAN_OR_EQUALS_TO := "<="
GREATER_THAN := ">"
GREATER_THAN_OR_EQUALS_TO := ">="
```

## Statements:
```EBNF
Program := { Statement }

Statement := CommentStatement
    | ExpressionStatement 
    | ReturnStatement 
    | GuardStatement 
    | VariableDeclaration 
    | StructDeclaration

CommentStatement := COMMENT

ExpressionStatement := Expression, SEMICOLON

ReturnStatement := RETURN, Expression, SEMICOLON

GuardStatement := GUARD, Expression, OPEN_CURLY, { Statement }, CLOSED_CURLY, SEMICOLON

VariableDeclarator := LET, IDENTIFIER, [TypeAnnotation], 
    ASSIGNMENT, Expression, SEMICOLON
```

## Expressions:
```EBNF
Expression := ? anything listed below ?

LiteralExpression := BOOLEAN_LITERAL | STRING_LITERAL | INTEGER_LITERAL | FLOAT_LITERAL

IdentifierExpression := IDENTIFIER, [TypeAnnotation]

PrefixExpression := BANG | MINUS, Expression

PostfixExpression := Expression, BANG

OperationExpression := Expression, 
    (PLUS | MINUS | MULTIPLY | DIVIDE | MODULO | AND | OR 
          | EQUALS_TO | NOT_EQUALS_TO | LESS_THAN | LESS_THAN_OR_EQUALS_TO | GREATER_THAN | GREATER_THAN_OR_EQUALS_TO), 
    Expression

AssignmentExpression := Expression, ASSIGNMENT, Expression

CodeBlockExpression := OPEN_CURLY, { Statement }, CLOSED_CURLY

IfExpression := IF, Expression, CodeBlockExpression, 
    { ELSE, IF, Expression, CodeBlockExpression }, 
    [ELSE, CodeBlockExpression]

FunctionExpression := OPEN_PAREN, 
    [IdentifierExpression, { COMMA, IdentifierExpression }], CLOSED_PAREN, 
    ARROW, [TypeSignature], CodeBlockExpression

FunctionCallExpression := IDENTIFIER, OPEN_PAREN, [Expression, { COMMA, Expression }], CLOSED_PAREN

TypeAnnotation := COLON, TypeSignature
```

## Type Signatures:
```EBNF
TypeSignature := ? anything listed below ?

PrimitiveType := PRIMITIVE_TYPE

FunctionType := OPEN_PAREN, 
    [TypeSignature, { COMMA, TypeSignature }], CLOSED_PAREN, 
    ARROW, TypeSignature
```