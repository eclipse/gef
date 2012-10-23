/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
 
/* A simple, exemplaric parser for a specified context-free grammar, implemented
in Prolog using the built-in syntax for specifying definite clause grammars
(DCG). Further reference on DCGs can be found in Clocksin & Mellish (2003:213).*/

/* Implementing a recognizer that will decide if an input is described by 
the grammar without assigning a structure to it is very simple: the grammar 
and the lexicon are specified using a production rule notation, describing 
the structure of sentences described by the grammar: */

s   --> np, vp. 
np  --> det, n.
np  --> n.
vp  --> v, n.
vp  --> v.
det --> [the].
det --> [a].
n   --> [agent].
n   --> [martinis].
v   --> [likes].
v   --> [drinks].

/* This grammar describes sentences like "the agent likes martinis":

digraph the_agent_likes_martinis {

	S; NP; V; VP; DET; N1[label="N"]; N2[label="N"]
	the; agent;	likes; martinis
	
	S -> NP; NP -> DET; NP -> N1 
	DET -> the[style=dashed]; N1 -> agent[style=dashed]
	
	S -> VP; VP -> V; VP -> N2
	V -> likes[style=dashed]; N2 -> martinis[style=dashed]
}

When queried like ''recognize([the, agent, likes, dry, martinis]).'' the 
Prolog interpreter analyses the given list and replies "Yes." if the input is 
described by the grammar (which it is for in this case) or "No.", if it isn't.
The predicate we called simply wraps the syntax that uses the predicates 
defined by the DCG: */

recognize(Input) :- s(Input,[]).

/*
Through Prolog's built-in backtracking mechanism all sentences that can be 
described by the grammar can be generated. Calling ''generate(Sentence).'' 
and telling Prolog to backtrack results in the following outputs:

Sentence = [the, agent, likes, the, agent] ;

Sentence = [the, agent, likes, the, hero] ;

Sentence = [the, agent, likes, the, martinis] ;

...

Here again, the predicate we called simply wraps the syntax that uses the 
predicates defined by the DCG directly:
*/

generate(Sentence) :- s(Sentence,[]).

/* 
The grammar description is valid Prolog code, so we can expand on it using 
variables in order to save the structure for actually parsing the structure 
of an input instead of only recognizing it:
*/

s(s(NP, VP))   --> np(NP), vp(VP).
np(np(DET, N)) --> det(DET), n(N).
vp(vp(V, NP))  --> v(V), np(NP).
vp(vp(V))      --> v(V).
det(the)       --> [the].
det(a)         --> [a].
det(dry)       --> [dry].
n(agent)       --> [agent].
n(hero)        --> [hero].
n(martinis)    --> [martinis].
v(likes)       --> [likes].
v(drinks)      --> [drinks].

/* Sample usage, as a parser: calling ''parse(Structure, [the,agent,likes,dry,martinis]).'' 
results in the output: ''Structure = s(np(the, agent), vp(likes, np(dry, martinis)))''. 
As above, the syntax of the predicates defined by the DCG is wrapped by a predicate 
which we called: */

parse(Structure, Input) :- s(Structure, Input, []).

/* Through Prolog's built-in backtracking mechanism all sentences and structures 
that are described by the grammar can be generated. Calling ''generate(Structure, Sentence).'' 
and telling Prolog to backtrack results in the following outputs:

Structure = s(np(the, agent), vp(likes, np(the, agent)))
Sentence = [the, agent, likes, the, agent] ;

Structure = s(np(the, agent), vp(likes, np(the, hero)))
Sentence = [the, agent, likes, the, hero] ;

Structure = s(np(the, agent), vp(likes, np(the, martinis)))
Sentence = [the, agent, likes, the, martinis] 

...

Finally, as before, the predicate we called, which itself calls the predicate defined by the DCG:*/

generate(Structure, Sentence) :- s(Structure, Sentence, []).