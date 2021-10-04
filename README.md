Takmela: a parsing engine and Datalog query engine
=========

Takmela is a new experimental (i.e not yet mathematically proven) general parsing algorithm which can parse any context-free grammar --including those with left recursion and ambiguity. The same algoritm with modification can be used to run Datalog queries, hinting that the technique may be possible to generalize to various other computation tasks. This project is a demo for both Takmela the parser and Takmelogic the Datalog query engine.

Besides simply running the algorithms, this demo includes support for tracing/visualization of parses and queries. Example traces are included and the user can add their own grammars/programs for tracing.

For an overview of Takmela's algorithm and many trace visualizations, see https://samiz.github.io/takmela/

Where are the main algorithms?
=========

The parser is in the Java class [takmela.engine.ParseEngine](src/takmela/engine/ParseEngine.java)

The Datalog query executor is in [takmelogic.engine.TakmelaDatalogEngine](src/takmelogic/engine/TakmelaDatalogEngine.java)

Each is about 500 lines of code.

Dependencies
=========

In order to generate graphical traces, this project uses *GraphViz* as an external tool. The path to GraphViz's *dot* program is hardcoded as `/usr/bin/dot` on the user's computer, and you'll need to change it on non-Linux operating systems. You'll find the place to change the path at the top of the file [src/takmela/viz/StagedGraphRender.java](src/takmela/viz/StagedGraphRender.java)


Parsing with Takmela
=========

Takmela's grammar format is described in the document [Writing Takmela grammars](doc/WritingTakmelaGrammars.md), and to learn how to use the parse trees given by Takmela, check out [Using Takmela parse trees](doc/UsingTakmelasParseTrees.md)

The [takmela.tool.Parse](src/takmela/tool/Parse.java) class contains lots of helper functions to help you easily load grammars and parse text with them, it is recommended to check it out.

Here's the simplest program to parse some input with Takmela:

```
List<String> outGrammarParseErrors = new ArrayList<>();
takmela.ast.Module mod = takmela.tool.Parse.parseGrammar(grammarCode, outGrammarParseErrors);

if(outGrammarParseErrors.size() == 0)
{
    takmela.tool.Parse.parse(mod, inputStr, startSymbol, 
    (tree)-> {
	    System.out.println(tree);
    },
    (expected, given, pos) -> {
        System.err.println(takmela.tool.Parse.defaultFailMessage(expected, given, pos));
    });
}
```

If you're looking for an example which parses more than toy grammars, this project uses Takmela itself to parse a subset of GraphViz's dot format, with the code found here: [takmela.viz.graphicsElements.ReadDot](src/takmela/viz/graphicsElements/ReadDot.java) and the grammar in the file [dot.takmela](./dot.takmela)

Running Datalog queries with Takmelogic
=========

The Datalog program syntax is described in the document [Writing Datalog Programs](./doc/WritingDatalogPrograms.md)

The [takmelogic.tool.RunDatalog](src/takmelogic/tool/RunDatalog.java) class contains helper functions to load Datalog programs and run queries on them.

The smallest program to run a query:
```
    String query = "ancestor";
    Object[] queryArgs = new Object[] {null, "c"}; // null -> output parameter

    RunDatalog.runStdOut("./takmelogic_examples/ancestors/ancestors.dlog", query, queryArgs);
```

**A note about data types**

Datalog/Takmela supports values of type string, like `"hello world"` or symbols, like `a` or `name` or `candy`. In order to pass symbols (rather than strings) as arguments to the query, use the class `takmelogic.engine.S` , for example 
```
Object[] queryArgs = new Object[] {null, 12, new S("candy")};
RunDatalog.runStdOut("./sample.dlog", "myQuery", queryArgs);
```

Output that includes symbols will also wrap them in `S` objects, whose data you can read from their `Value` field.

Playing with Takmela's examples
=========

If you want to run a sample trace, you'll find them in the pacakage `examples.takmela.parser` or `examples.takmela.datalog`, just run the main class and you'll find its output in `./trace_html/*`

To rebuild HTML traces from all packaged examples, run the `examples.takmela.build.BuildAllHtml` class

By convention all examples use these paths:

* HTML Trace output: `./trace_html/*`
* Example source code (Grammar & sample input) : `./takmela_examples/*`
* Example source code (Datalog program)        : `./takmelogic_examples/*`
* Note that Grammars include a sample input file with them, but running Datalog programs needs a query that's embedded in the Java code of the example.

Some examples use templates to generate an example's trace with some additional documentation. All such templates are located in `./trace_templates/`

The easiest way to create your own traces is to copy & paste one of the existing classes in the `examples.takmela.*.viz` package and change the parameters and configuration as needed.

What does "Takmela" mean?
=========
It's an Arabic word that means "completion". A program's (full) continuation is its completion!

License
=========
(c) 2018-2021 Mohamed Samy, samy2004@gmail.com
This software is released under the Apache Software License version 2.0
