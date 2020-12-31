-module(authenticator).

-export([authentication/1]).

% ÁREA NÃO AUTENTICADA
% Processa pedidos de registo e login

% à espera de pedidos de autenticação do user
authentication(Socket) ->
    receive
        {tcp, Socket, Bin} ->
            Msg = messages:decode_msg(Bin,'Message'),
            io:fwrite("Message: ~p\n", [Msg]),
            case maps:get(type,Msg) of
                'REGISTER' -> registerHandler(Socket, maps:get(register,Msg));
                'LOGIN' -> loginHandler(Socket, maps:get(login,Msg));
                _ ->
                    response_manager:sendResponse(Socket,false,"Invalid request, please signup or login first."),
                    authentication(Socket)
            end;
        _ -> %{tcp_closed, _} e {tcp_error, _, _} -> fim do processo do cliente antes de estar autenticado
            bye
    end.


% handlers de registo e login
% se for bem sucedido, passa o controlo para o módulo client_manager (área autenticada)
% caso contrário, dá mensagem de erro e continua à espera de pedidos

registerHandler(Socket, Data) ->
    Username = maps:get(username, Data),
    Password = maps:get(password, Data),
    District = maps:get(district, Data),
    io:fwrite("Register request: ~p ~p ~p\n", [Username, Password, District]),
    case account_manager:register(Username, Password, District) of
        ok ->
            case districts_manager:registerUserInDistrict(District,Username) of
                ok ->
                    io:fwrite("Successfully registered: ~p ~p ~p\n", [Username, Password, District]),
                    response_manager:sendResponse(Socket,true,"Successfully registered."),
                    authentication(Socket);
                _ -> % nunca deve acontecer
                    io:fwrite("Error registering user in district: ~p ~p\n", [Username, District]),
                    response_manager:sendResponse(Socket,false,"Error registering user in district."),
                    authentication(Socket)
            end;
        _ ->
            io:fwrite("Username already taken: ~p\n", [Username]),
            response_manager:sendResponse(Socket,false,"Username already taken."),
            authentication(Socket)
    end.

loginHandler(Socket, Data) ->
    Username = maps:get(username, Data),
    Password = maps:get(password, Data),
    io:fwrite("Login request: ~p ~p\n", [Username, Password]),
    case account_manager:login(Username, Password) of
        {ok, District} ->
            io:fwrite("Successfully logged in. ~p ~p\n", [Username, Password]),
            response_manager:sendResponse(Socket,true,"Successfully logged in."),
            client_manager:loop(Socket,Username,District);
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendResponse(Socket,false,ErrorMsg),
            authentication(Socket)
    end.