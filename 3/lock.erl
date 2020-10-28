-module(lock).
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
        ok_write -> ok_write;
        ok_read -> ok_read;
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
            reading([From|Readers]); % se for um read, o leitor é adicionado a lista.
        {write, From} ->
            reading(Readers,From);
        {_,From} ->
            From ! invalid,
            reading(Readers)
    end.

reading([],From) ->
    From ! ok_write,
    writing(From);

reading(Readers,From) ->
    receive
        {release,FromSomeoneElse} -> reading(Readers--[FromSomeoneElse],From); % exaustar os leitores.
        _ -> reading(Readers,From)
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