-module(authenticator).

-export([authentication/1]).

% ÁREA NÃO AUTENTICADA
% processa pedidos de registo e login

% à espera de pedidos de autenticação do user
authentication(Socket) ->
    receive
        {tcp, Socket, Bin} ->
            inet:setopts(Socket, [{active, once}]),
            Msg = messages:decode_msg(Bin,'Message'),
            case maps:get(type,Msg) of
                'REGISTER' -> registerHandler(Socket, maps:get(register,Msg));
                'LOGIN' -> loginHandler(Socket, maps:get(login,Msg));
                _ ->
                    response_manager:sendResponse(Socket,false,"Pedido inválido, registe-se ou entre na sua conta primeiro"),
                    authentication(Socket)
            end;
        _ -> % fim do processo do cliente antes de estar autenticado
            bye
    end.


% handlers de registo e login
% se for bem sucedido, passa o controlo para o módulo client_manager (área autenticada)
% caso contrário, dá mensagem de erro e continua à espera de pedidos

registerHandler(Socket, Data) ->
    Username = maps:get(username, Data),
    Password = maps:get(password, Data),
    DistrictCode = erlang:list_to_atom(maps:get(district, Data)),
    io:fwrite("\nREGISTER REQ: ~p ~p ~p.\n", [Username, Password, DistrictCode]),
    case district_manager:verifyDistrict(DistrictCode) of
        {ok, District} ->
            case account_manager:signup(Username, Password, District) of
                ok ->
                    io:fwrite("Registered user: ~p ~p ~p.\n", [Username, Password, District]),
                    response_manager:sendResponse(Socket,true,"Registado com sucesso"),
                    authentication(Socket);
                _ ->
                    io:fwrite("Username already taken: ~p.\n", [Username]),
                    response_manager:sendResponse(Socket,false,"Username não disponível"),
                    authentication(Socket)
            end;
        invalid ->
            io:fwrite("Invalid district: ~p\n.", [Username]),
            response_manager:sendResponse(Socket,false,"Distrito inválido"),
            authentication(Socket)
    end.

loginHandler(Socket, Data) ->
    Username = maps:get(username, Data),
    Password = maps:get(password, Data),
    io:fwrite("\nLOGIN REQ: ~p ~p.\n", [Username, Password]),
    case account_manager:login(Username, Password) of
        {ok, District} ->
            io:fwrite("Logged in user: ~p ~p.\n", [Username, Password]),
            response_manager:sendResponse(Socket,true,"Bem-vindo"),
            client_manager:loop(Socket,Username,District);
        {error, TypeError, ErrorMsg} ->
            case TypeError of
                quarantined -> io:fwrite("Can't login while quarantined: ~p.\n", [Username]);
                login_status -> io:fwrite("User already logged in: ~p.\n", [Username]);
                wrong_password -> io:fwrite("Wrong password: ~p.\n", [Username]);
                wrong_username -> io:fwrite("Username doesn't exist: ~p.\n", [Username])
            end,
            response_manager:sendResponse(Socket,false,ErrorMsg),
            authentication(Socket)
    end.