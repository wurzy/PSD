-module(client_manager).

-export([loop/3]).

% ÁREA AUTENTICADA

% processo em loop à espera de pedidos do user
loop(Socket,Username,District) ->
    receive
        {tcp, Socket, Bin} ->
            inet:setopts(Socket, [{active, once}]),
            Msg = messages:decode_msg(Bin,'Message'),
            io:fwrite("Authenticated message: ~p\n", [Msg]),
            case maps:get(type,Msg) of
                'PORT' -> notificationsPortHandler(Socket, Username, District, maps:get(port,Msg));
                'LOCATION' -> updateUserLocation(Socket, Username, District, maps:get(location,Msg));
                'NR_PEOPLE' -> getNrPeopleInLocation(Socket, Username, District, maps:get(location,Msg));
                'SICK' -> sickHandler(Socket, Username, District);
                'LOGOUT' -> logoutHandler(Socket, Username, District)
            end;
        _ -> logoutHandler(Socket,Username,District)
    end.

notificationsPortHandler(Socket, Username, District, Data) ->
    Port = maps:get(port, Data),
    io:fwrite("Register private notification socket request: ~p ~p\n", [Username, Port]),
    district_manager ! {register_user,Socket,District,Username,Port},
    loop(Socket,Username,District).

updateUserLocation(Socket, Username, District, Location) ->
    district_manager ! {location,District,Username,Location},
    io:fwrite("Update user location request: ~p ~p\n", [Username, District]),
    loop(Socket,Username,District).

sickHandler(Socket, Username, District) ->
    case account_manager:sick(Username) of
        ok ->
            district_manager ! {sick_user,District,Username},
            io:fwrite("Successfully flagged. ~p\n", [Username]),
            response_manager:sendResponse(Socket,true,"Successfully flagged."),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendResponse(Socket,false,ErrorMsg),
            loop(Socket,Username,District)
    end.

getNrPeopleInLocation(Socket, Username, District, Location) ->
    district_manager:countPeopleInLocation(District,Location),
    io:fwrite("Count people in location request: ~p\n", [District]),
    loop(Socket,Username,District).

logoutHandler(Socket, Username, District) ->
    case account_manager:logout(Username) of
        ok ->
            district_manager ! {remove_user, District, Username},
            io:fwrite("Successfully logged out. ~p\n", [Username]),
            response_manager:sendResponse(Socket,true,"Successfully logged out."),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} -> % nunca deve acontecer
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendResponse(Socket,false,ErrorMsg),
            loop(Socket,Username,District)
    end. 