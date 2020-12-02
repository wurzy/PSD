-module(lock1_0).
-export([create/0, release/1, acquire/2]).

create()->
    spawn(fun()->released() end).

release(Pid) ->
    Pid ! {release,self()},
    receive
        ok_released -> ok; 
        _ -> invalid
    end.

acquire(Pid, Mode) ->
    Pid ! {Mode,self()},
    receive
        invalid -> invalid;
        _ -> ok
    end.

released()->
    receive
        {release,From} -> 
            From ! not_acquired,
            released();
        {read,From}->
            From ! ok_read,
            reading([From]);
        {write,From}->
            From ! ok_write,
            writing(From)
    end.

reading([]) -> 
    released(); % salta para o estado default, pode vir qualquer coisa porque nao ha mais ninguem a usar o lock
reading(Readers) -> 
    receive 
        {release,From} -> 
            From ! ok_released,
            reading(Readers -- [From]); % se for um release, o leitor é libertado da lista.
        {read,From} -> 
            From ! ok_read,
            reading([From|Readers]); % se for um read, o leitor é adicionado a lista. Starvation de writers.
        {_,From} ->
            From ! invalid,
            reading(Readers)
    end.

writing(From) ->
    receive
        {release,From} -> % aqui só pode dar release o que obteu o writing lock
            From ! ok_released,
            released();
        _ -> 
            From ! invalid,
            writing(From)
    end.    