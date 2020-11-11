-module(room_manager).
-export([start/0]).


start() -> 
    register(?MODULE, spawn(fun()->loop(#{}) end)).

loop(Rooms) ->
    receive
        {{create,Room},From} -> 
            case maps:find(Room,Rooms) of 
                error -> 
                    From ! {Pid,?MODULE}, % aqui tem de retornar o pid da room criada
                    loop(maps:put(Room,[From]))
            end
    end.