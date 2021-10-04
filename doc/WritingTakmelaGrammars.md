A simple example grammar
====
```
expr : expr '+' term
    | term ;

term: 
    NUM
    ;
    
term: '(' expr ')';
    
NUM: [0-9]+;

WS skip: (' ' | [\n\r\t])+;
```

A more detailed example grammar
====
Please check out [dot.takmela](../dot.takmela) at the project's root directory, which is used as part of the project itself.

Basic Layout & Syntax rules
====
A grammar is composed of two parts:

```
<grammar rules>
<lexer rules>
```

Grammar rules take this form:
`name: choice0 | choice1 | .... ;`

The name must start with a small letter. A choice can be empty (indicating a null production).

Choices can also have labels (which would then be used to label parse trees instead of naming them by the rule name). A rule must have either all choices labelled or none labelled. Labels must start with a capital letter.

```
name:
      choice0     #Label0
    | choice1     #Label1
    ;
```
    
Terminals in rules can be either embedded strings `'like this'` or tokens defined in the lexer rules section, which must start with a capital letter (and by convention are usually ALL CAPS).

Takmela grammars support EBNF operators `*`, `?`, `+` and `(` `)` in grammar rules.

Grammar layout is not whitespace sensitive.

Comments in grammars are C++ style comments: `/*  comment */` or `// line comment`


Lexical Rules
====
A lexer rule takes this form:

`Name <optional modifiers> : <regexp> ;`

For example:

```
ID: [a-zA-Z] [a-zA-Z0-9]*;
SPACING skip: ' ';
```

Currently supported modifiers are `skip` , `within`, `pushes` and `pops`.

* `skip` makes the resulting Tokens' boolean method `skip()` return true, allowing consumers of the lexer to skip them.
* `within`, `pushes` and `pops` are used to manage *lexing scopes*.
** A rule like `Condition within EmbeddedSql: ....;` means that this rule applies only when the lexer is in the `EmbeddedSql` scope
** A rule like `Rule1 pushes Scope1: ...;` or `Rule2 pops Scope2, Scope3: ...;` will -- if it succeeds and generates a token -- push or pop its given scopes from a scope stack that is part of the lexer's state. The topmost scope in the stack is the active one. The default scope has no defined name.
** Scope names always start with a capital letter
** A rule can have multiple pushed or popped scope names (separated by commas), and `pushes` and `pops` can be in the same rule. In this case all pushed scopes are handled, then all popped scopes. Multiple operations are run according to their order in the rule.

Tokenizing chooses the successfully accepted rule by longest match. In case of multiple longest match ties are broken by order in the grammar listing (earlier = higher priority). All embedded terminals take higher priority than named tokens, and the order among embedded terminals is currently undefined.

A RegExp in Takmela supports the following operations:
Sequencing: `partA partB`
Repetition: `part+` or `part*`
Optional parts: `part?`
String literals: `'hello'` or `"hello"`
Character classes: `[a-h] [abcdx-z] [a-z2-5] [\n] [\n\r\t\'\"\\\-]`

Sub-expressions can be grouped using parentheses.

A character class can contain a mix of char ranges (i.e `from-to`) and single characters. Char ranges are inclusive from both sides.

These character escapes are supported in single characters, character ranges, and string literals:
`\n`        New line
`\r`        Carriage return
`\t`        Tab
`\\`        Literal backslash
`\"`        Double quote
`\'`        Single quote
`\-`        Dash (in char classes but NOT string literal)

Notably lacking right now is literals for Unicode code points (Unicode support in general is lacking right now).

