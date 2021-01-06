-module(client_manager).

-export([loop/3]).

% ÁREA AUTENTICADA

% processo em loop à espera de pedidos do user
loop(Socket,Username,District) ->
    io:fwrite("authenticated loop: ~p ~p\n", [Username, District]),
    receive
        {tcp, Socket, Bin} ->
            inet:setopts(Socket, [{active, once}]),
            Msg = messages:decode_msg(Bin,'Message'),
            io:fwrite("Authenticated message: ~p\n", [Msg]),
            case maps:get(type,Msg) of
                'LOCATION' -> updateUserLocation(Socket, Username, District, maps:get(location,Msg));
                'NR_PEOPLE' -> getNrPeopleInLocation(Socket, Username, District, maps:get(location,Msg));
                'SICK' -> sickHandler(Socket, Username, District);
                'LOGOUT' -> logoutHandler(Socket, Username, District)
            end;
        _ -> logoutHandler(Socket,Username,District)
    end.

updateUserLocation(Socket, Username, District, Location) ->
    X = maps:get(coordx,Location),
    Y = maps:get(coordy,Location),
    District ! {{location,Username,X,Y},self()},
    receive
        ok ->
            io:fwrite("Updated user location. ~p ~p (~p,~p)\n", [Username, District, X, Y]),
            response_manager:sendResponse(Socket,true,"Updated user location."),
            loop(Socket,Username,District)
    end.

getNrPeopleInLocation(Socket, Username, District, Location) ->
    X = maps:get(coordx,Location),
    Y = maps:get(coordy,Location),
    District ! {{nr_people,X,Y},self()},
    receive
        {ok, Nr_People} ->
            Msg = "~p people in location (~p,~p) of district ~p.\n", [Nr_People,X,Y,District],
            io:fwrite(Msg),
            response_manager:sendResponse(Socket,true,Nr_People),
            loop(Socket,Username,District)
    end.

sickHandler(Socket, Username, District) ->
    case account_manager:sick(Username) of
        ok ->
            io:fwrite("Successfully flagged. ~p\n", [Username]),
            response_manager:sendResponse(Socket,true,"Successfully flagged."),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendResponse(Socket,false,ErrorMsg),
            loop(Socket,Username,District)
    end. 

logoutHandler(Socket, Username, District) ->
    case account_manager:logout(Username) of
        ok ->
            io:fwrite("Successfully logged out. ~p\n", [Username]),
            response_manager:sendResponse(Socket,true,"Successfully logged out."),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} -> % nunca deve acontecer
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendResponse(Socket,false,ErrorMsg),
            loop(Socket,Username,District)
    end. 