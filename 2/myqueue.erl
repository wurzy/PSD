-module(myqueue).
-export([create/0, enqueue/2, dequeue/1]).

create() -> []. %decidir que é uma lista, encapsular o que é "Queue"

% é eficiente na cabeça e ineficiente na cauda, porque é tudo copiado
enqueue(Queue, Elem) -> Queue ++ [Elem]. % convem ser algo pequeno do lado direito, p.e L ++ [1]

dequeue([]) -> empty;
dequeue([H|T]) -> {T,H}. 