-module(client_manager).

-export([loop/2]).

% ÁREA AUTENTICADA

% processo em loop à espera de pedidos do user
loop(Socket,Username) ->
    receive
        {tcp, Socket, Bin} ->
            Msg = messages:decode_msg(Bin,'Message'),
            case maps:get(type,Msg) of
                'SICK' -> sickHandler(Socket, Username);
                'LOGOUT' -> logoutHandler(Socket, Username)
            end
    end.

sickHandler(Socket, Username) ->
    case account_manager:sick(Username) of
        ok ->
            io:fwrite("Successfully logged out. ~p\n", [Username]),
            response_manager:sendAuthResponse(Socket,"Successfully logged out."),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendAuthResponse(Socket,ErrorMsg),
            loop(Socket,Username)
    end. 

logoutHandler(Socket, Username) ->
    case account_manager:logout(Username) of
        ok ->
            io:fwrite("Successfully flagged. ~p\n", [Username]),
            response_manager:sendAuthResponse(Socket,"Successfully flagged."),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendAuthResponse(Socket,ErrorMsg),
            loop(Socket,Username)
    end. 