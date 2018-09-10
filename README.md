# fling

*fling* (**fl**uent **in**terfaces **g**enerator) is a parser generator.
Instead of generating the code of a real-time parser, *fling* generates Java fluent interfaces,
which implement a compile-time parser of the given language.

*fling* accepts, currently, *LL(1)* (left-to-right, leftmost, using 1 lookahead token) grammars.
