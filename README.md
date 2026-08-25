# 🍺 Piwo

**Piwo** is a small beer-themed programming language front end written in Java.
It includes a custom **lexer**, **parser**, **AST (Abstract Syntax Tree)** model, and **JSON AST printer**.

The language uses Serbian beer-inspired keywords and even replaces the traditional semicolon with a beer emoji: `🍺`.

> The current project performs lexical and syntax analysis and prints the resulting AST as JSON. It is not an interpreter or compiler that executes Piwo programs.

## Features

* Custom lexer written from scratch
* Recursive-descent parser
* Abstract Syntax Tree representation
* Pretty-printed JSON output
* Variables and assignments
* Functions with parameters and optional return types
* Arrays and array indexing
* `if / else` statements
* `while` loops
* Return statements
* Input and output statements
* Arithmetic, comparison, equality, and logical operators
* Integer, floating-point, boolean, and string literals
* Single-line and block comments
* Lexer and parser error reporting
* Included valid and invalid example programs
* Beer emoji `🍺` as the statement terminator

## Example

A minimal Piwo program:

```text
pivnica main() {
    nazdravi("Ziveli!")🍺
}
```

Running the parser produces a JSON representation of the program's AST.

## Language Syntax

### Functions

Functions are declared with `pivnica`:

```text
pivnica main() {
    nazdravi("Ziveli!")🍺
}
```

Functions can have parameters and an optional return type:

```text
pivnica saberi(casa a, casa b): casa {
    ziveli a + b🍺
}
```

### Variables

Variable declarations use one of Piwo's built-in type keywords:

```text
casa broj = 10🍺
limenka vrednost = 3.14🍺
pijan punoletan = tacno🍺
```

Arrays are declared using `[]` after the type:

```text
casa[] brojevi🍺
casa[][] matrica🍺
```

### Conditionals

```text
ako (broj > 5) {
    nazdravi("Veliki broj")🍺
} inace {
    nazdravi("Mali broj")🍺
}
```

### Loops

Piwo uses the two-word keyword `dok toci` for a `while` loop:

```text
dok toci (broj > 0) {
    broj = broj - 1🍺
}
```

### Input and Output

```text
sipaj(broj)🍺
nazdravi(broj)🍺
```

### Return

```text
ziveli broj🍺
```

### Booleans

```text
tacno
netacno
```

### Comments

Single-line comments:

```text
// Ovo je komentar
```

Block comments:

```text
/*
   Ovo je
   blok komentar
*/
```

## Keywords

| Piwo keyword | Purpose                |
| ------------ | ---------------------- |
| `pivnica`    | Function declaration   |
| `casa`       | Type                   |
| `limenka`    | Type                   |
| `krigla`     | Type                   |
| `bomba`      | Type                   |
| `pinta`      | Type                   |
| `pijan`      | Type                   |
| `kap`        | Character type keyword |
| `ako`        | `if`                   |
| `inace`      | `else`                 |
| `dok toci`   | `while`                |
| `ziveli`     | `return`               |
| `sipaj`      | Input                  |
| `nazdravi`   | Output                 |
| `tacno`      | Boolean `true`         |
| `netacno`    | Boolean `false`        |
| `🍺`         | Statement terminator   |

## Operators

Piwo supports common expression operators:

```text
+  -  *  /  %
!  !=
=  ==
>  >=  <  <=
&&  ||
```

The parser also supports function calls, parentheses, assignments, and array indexing.

## Project Structure

```text
Piwo/
├── src/
│   └── main/
│       ├── java/
│       │   └── org/example/
│       │       ├── lexer/
│       │       │   ├── Lexer.java
│       │       │   ├── Token.java
│       │       │   └── TokenType.java
│       │       ├── parser/
│       │       │   ├── AST.java
│       │       │   └── Parser.java
│       │       ├── main/
│       │       │   └── Main.java
│       │       └── JsonPrinter.java
│       └── resources/
│           └── examples/
├── manifest.txt
├── piwo.jar
└── pom.xml
```

### Main Components

**Lexer**
Reads Piwo source code and converts it into a stream of tokens. It also tracks line and column information and reports lexical errors.

**Parser**
Consumes the token stream and builds an Abstract Syntax Tree using recursive-descent parsing.

**AST**
Contains the node classes used to represent functions, variable declarations, control flow, expressions, calls, literals, assignments, and other language constructs.

**JsonPrinter**
Serializes the generated AST into readable JSON.

**Main**
Reads a source file, runs the lexer and parser, and prints the resulting AST.

## Requirements

* Java **23**
* Maven

Check your Java installation with:

```bash
java -version
```

Check Maven with:

```bash
mvn -version
```

## Running the Project

### Using the Included JAR

The repository already contains `piwo.jar`.

Run a Piwo source file with:

```bash
java -jar piwo.jar path/to/program.piwo
```

For example:

```bash
java -jar piwo.jar src/main/resources/examples/example1
```

The program expects exactly one source-file argument.

### From Source

Clone the repository:

```bash
git clone https://github.com/Vaske10024/Piwo.git
cd Piwo
```

Compile it with Maven:

```bash
mvn clean compile
```

Then run the main class:

```bash
java -cp target/classes org.example.main.Main path/to/program.piwo
```

Example:

```bash
java -cp target/classes org.example.main.Main src/main/resources/examples/example1
```

## Processing Pipeline

```text
.piwo source
     │
     ▼
   Lexer
     │
     ▼
   Tokens
     │
     ▼
   Parser
     │
     ▼
    AST
     │
     ▼
JsonPrinter
     │
     ▼
 JSON output
```

If lexical analysis fails, the lexer reports a lexical error.

If the token sequence does not match the Piwo grammar, the parser reports a syntax error.

## Examples

The `src/main/resources/examples` directory contains several test programs.

It includes:

* valid Piwo programs
* a lexical-error example
* multiple syntax-error examples

These files are useful for testing both successful parsing and error handling.

## Technologies

* Java 23
* Maven
* Regular expressions
* Recursive-descent parsing
* Abstract Syntax Trees
* JSON serialization

## Purpose

Piwo was created as an educational lexer/parser project demonstrating the main stages of a programming-language front end:

1. Lexical analysis
2. Tokenization
3. Syntax analysis
4. AST construction
5. Structured AST output

The beer-themed syntax makes the language a little more memorable. 🍻

## Author

**Vasilije Stanković**

GitHub: [@Vaske10024](https://github.com/Vaske10024)
