-module(priorityqueue).
-export([create/0, enqueue/2, dequeue/1]).


% pensar nisto como duas pilha de papeis, uma incoming e outgoing. A outgoing sai sempre pela cabeça, a Incoming quando a Outgoing acaba tem de ser reversed porque
% o ultimo elemento é o mais recente.
create () -> {[],[]}.

enqueue({In,Out}, Item) -> {[Item|In], Out}.

dequeue({[],[]}) -> empty;
dequeue({In,[]}) -> dequeue({[],list:reverse(In)});
dequeue({In, [H|T]}) -> {{In, T},H}.
