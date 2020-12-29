-module(authenticator).

-export([authentication/1]).

% ÁREA NÃO AUTENTICADA
% Processa pedidos de registo e login

% à espera de pedidos de autenticação do user
authentication(Socket) ->
    receive
        {tcp, Socket, Bin} ->
            Msg = messages:decode_msg(Bin,'Message'),
            case maps:get(type,Msg) of
                'REGISTER' -> registerHandler(Socket, maps:get(register,Msg));
                'LOGIN' -> loginHandler(Socket, maps:get(login,Msg));
                _ ->
                    Reply = messages:encode_msg(#{type=>'REPLY', reply => 
                                                #{result=>false, message=>"Invalid request, please signup or login first."}}, 'Message'),
                    gen_tcp:send(Socket,Reply),
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
            io:fwrite("Successfully registered: ~p ~p ~p\n", [Username, Password, District]),
            response_manager:sendAuthResponse(Socket,"Successfully registered."),
            client_manager:loop(Socket,Username);
        _ ->
            io:fwrite("Username already taken: ~p\n", [Username]),
            response_manager:sendAuthResponse(Socket,"Username already taken."),
            authentication(Socket)
    end.

loginHandler(Socket, Data) ->
    Username = maps:get(username, Data),
    Password = maps:get(password, Data),
    io:fwrite("Login request: ~p ~p\n", [Username, Password]),
    case account_manager:login(Username, Password) of
        ok ->
            io:fwrite("Successfully logged in. ~p ~p\n", [Username, Password]),
            response_manager:sendAuthResponse(Socket,"Successfully logged in."),
            client_manager:loop(Socket,Username);
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendAuthResponse(Socket,ErrorMsg),
            authentication(Socket)
    end.