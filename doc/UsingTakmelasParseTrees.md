Consider the simplest program to parse some text from a Takmela grammar:

```
List<String> outGrammarParseErrors = new ArrayList<>();
takmela.ast.Module mod = takmela.tool.Parse.parseGrammar(grammarCode, outGrammarParseErrors);

if(outGrammarParseErrors.size() == 0)
{
    takmela.tool.Parse.parse(mod, inputStr, startSymbol, 
    (tree)-> {
	    // What to do with 'tree'?
    },
    (expected, given, pos) -> {
        System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
    });
}
```
Each time the parser finds a parse tree, it will call the lambda and pass that tree to it. So how do we use that tree?

The building blocks of Takmela's trees are `takmela.tree.Leaf` and `takmela.tree.Tree`, both implementing the `takmela.tree.Node` interface.

A leaf contains a `takmela.lexer.Token` object passed from the lexical analyzer, while a tree contains a label, a choice number (zero-based, in the order a rule's alternatives appear in the grammar) and a collection of child nodes.

If a rule in the grammar contains a label for each alternative, these labels are used in the `Label` field in the tree so that a user can programatically determine which alternative was parsed, otherwise the rule name is used. Labels are written in the grammer like this:

```
rule1:
      a b C     #Label1
    | d e       #Label2
    | f G       #Label3
    ;
```
    
Note that labels must start with a capital letter, and that if a rule decides to label its alternatives it must label all of them.

The class `takmela.tree.TreeUtils` contains various helper functions to deal with parse trees, some of which are particularly useful when the grammar contains EBNF notation, as explained below

Trees and EBNF
===
Takmela support extra syntactic sugar: zero-or-more repetition `*` , one-or-more repetition `+` , optional `?` and grouping `(a b c)`. All of them are implemented by simple syntax transformations; e.g a rule like 
```
myRule : (a)? ;
``` 
...will become
```			
myRule: %question_0;

%question_0: a 
            |  
            ;
```

Grouping doesn't cause problems with using the parse trees, the grouped items become their own subtree which a programmer can just traverse.

Repetitions and optional items are more complex so they have their own helper functions in the `takmela.tree.TreeUtils` package; the functions are as follows:

```
public static void enumStar(Tree t, BiConsumer<Integer, Node> fn) { ... }
public static void enumPlus(Tree t, BiConsumer<Integer, Node> fn) { ... }
public static void enumQuestion(Tree t, BiConsumer<Integer, Node> fn)  { ... }

public static<T> void collect(List<T> collectInto, Node node, Predicate<Tree> test, Function<Tree, T> extract) { ... }
```
Each of the `enum...` function takes a tree and a callback, and invokes the callback with each repeated item and its index. Optional items are also treated as 'enumerated', and the item if exists will have an index of zero.

`collect` is the lazy programmer's weapon; to use when one just wants to collect some data from the tree without worrying about structure. It traverses the whole tree, and whatever sub-trees (but not leaves) satisfy the given predicate `test` will have the `extract` function applied to them. All extracted values will be added to the `collectInto` parameter.

Real-world example
===
For an example of real-world parse tree processing, check out [ReadDot](../src/takmela/viz/graphicsElements/ReadDot.java) in the same project.
