-module(client_manager).

-export([loop/3]).

% ÁREA AUTENTICADA

% processo em loop à espera de pedidos do user
loop(Socket,Username,District) ->
    receive
        {tcp, Socket, Bin} ->
            inet:setopts(Socket, [{active, once}]),
            Msg = messages:decode_msg(Bin,'Message'),
            case maps:get(type,Msg) of
                'PORT' -> notificationsPortHandler(Socket, Username, District, maps:get(port,Msg));
                'LOCATION' -> updateUserLocation(Socket, Username, District, maps:get(location,Msg));
                'NR_PEOPLE' -> getNrPeopleInLocation(Socket, Username, District, maps:get(location,Msg));
                'SICK' -> sickHandler(Socket, Username, District);
                'LOGOUT' -> logoutHandler(Socket, Username, District)
            end;
        % se o socket for fechado inesperadamente, dá logout ao user
        _ -> logoutHandler(Socket,Username,District) 
    end.

% liga-se ao socket privado do cliente na porta recebida
notificationsPortHandler(Socket, Username, District, Data) ->
    Port = maps:get(port, Data),
    io:fwrite("\nPORT REQ: ~p ~p ~p.\n", [Username, District, Port]),
    district_manager ! {register_user, District, Username, Port},
    loop(Socket,Username,District).

% envia a nova posição do utilizador para o respetivo servidor distrital
updateUserLocation(Socket, Username, District, Location) ->
    io:fwrite("\nLOCATION REQ: ~p ~p ~p.\n", [Username, District, Location]),
    district_manager ! {location,District,Username,Location},
    loop(Socket,Username,District).

% dá flag ao cliente como doente
% fecha o seu socket de notificações privadas
% dá-lhe logout e não deixar voltar (não pode interagir mais com o sistema em quarentena)
sickHandler(Socket, Username, District) ->
    io:fwrite("\nSICK REQ: ~p ~p.\n", [Username, District]),
    case account_manager:sick(Username) of
        ok ->
            district_manager ! {sick_user,District,Username},
            response_manager:sendResponse(Socket,true,"Foi sinalizado com sucesso, boa recuperação"),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendResponse(Socket,false,ErrorMsg),
            loop(Socket,Username,District)
    end.

% redireciona o pedido para o respetivo servidor distrital
% recebe a contagem e envia a resposta para o cliente
getNrPeopleInLocation(Socket, Username, District, Location) ->
    X = maps:get(coordx,Location),
    Y = maps:get(coordy,Location),
    io:fwrite("\nNR_PEOPLE REQ: ~p\n", [District]),
    case district_manager:countPeopleInLocation(District,X,Y) of
        {ok, Total} ->
            io:fwrite("Number of people in ~p (~p,~p): ~p.\n", [District,X,Y,Total]),
            Reply = io_lib:format("Total de pessoas em ~p (~p,~p): ~p", [District,X,Y,Total]),
            response_manager:sendResponse(Socket,true,Reply)
    end,
    loop(Socket,Username,District).

% dá logout ao user e fecha o seu socket de notificações privadas
logoutHandler(Socket, Username, District) ->
    io:fwrite("\nLOGOUT REQ: ~p\n", [District]),
    case account_manager:logout(Username) of
        ok ->
            district_manager ! {remove_user, District, Username},
            io:fwrite("Logged out user: ~p.\n", [Username]),
            response_manager:sendResponse(Socket,true,"Até à próxima"),
            authenticator:authentication(Socket); % redireciona para a área não autenticada
        {error, ErrorMsg} -> % nunca deve acontecer
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            response_manager:sendResponse(Socket,false,ErrorMsg),
            loop(Socket,Username,District)
    end. 