-module(demo).
-export([factorial/1]).

% modulos uteis: lists, dict, gb_trees (arvores com ordem)

% c(demo). ... demo:factorial(5).
factorial(0) -> 1;
factorial(N) when N<0 -> 0;
factorial(N) when N>0 -> N * factorial(N-1).

