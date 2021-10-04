Datalog programs in this demo interpreter are really simple! A program is a sequence of facts and rules.

Facts are composed of a fact name and terms, and are written like this:
```
f1(t1, t2, t3).
```

The fact name must start with a small letter and can include underscores, while a term can be:
* An integer like `145`
* A symbol, always starting with a small letter, like `a` or `name` or `fuji`
* A string, surrounded by double or single quotes (quotes obviously must match). Supported escapes are `\n` , `\r` , `\t` , `\"` and `\'`
* A variable, which must start with a capital letter.

Rules have a head and body, separated by `:-` , the head (like facts) is composed of a head name and terms, while the body is composed of subgoals.

```
r1(t1, t2) :- sg1(A), sg2(b), sg3(12, c).
```

Currently the only subgoals supported are predicate calls and comparisons.

Predicate calls are a rule or fact name with some terms:
```
grand_parent(X, Y) :- parent(X, Z), parent(Z, Y).
```
Unlike other Datalog variants or Prolog, the parentheses are mandatory; you cannot use a fact or rule name like so `somename` but must use `somename()`

Comparisons take the following forms: `term == term` , `term != term` , `term < term` , `term <= term` , `term > term` , `term >= term` where a term is an integer, symbol, string, or variable. Please note that `==` is *not* Prolog-like unification! A subgoal like `X==Y` will assume that both `X` and `Y` are already bound to ground terms and will not try to bind them together.

Comments are C++ style comments: `/*  comment */` or `// line comment`

Running Datalog queries
=========

The `takmelogic.tool.RunDatalog` class contains helper functions to load Datalog programs and run queries on them.

The smallest program to run a query:
```
    String query = "ancestor";
    Object[] queryArgs = new Object[] {null, "c"}; // null -> output parameter

    RunDatalog.runStdOut("./takmelogic_examples/ancestors/ancestors.dlog", query, queryArgs);
```

**A note about data types**

Datalog/Takmela supports values of type string, like `"hello world"` or symbols, like `a` or `name` or `candy`. In order to pass symbols (rather than strings) as arguments to the query, use the class `takmelogic.engine.S` ; for example 

```
// equivalent to running myQuery(V0, 12, candy);
Object[] queryArgs = new Object[] {null, 12, new S("candy")};
RunDatalog.runStdOut("./sample.dlog", "myQuery", queryArgs);
```

Output that includes symbols will also wrap them in `S` objects, whose data you can read from their `Value` field.

