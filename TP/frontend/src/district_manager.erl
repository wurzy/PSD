-module(district_manager).

-export([start/0, verifyDistrict/1, countPeopleInLocation/3]).

% inicia o gestor de distritos e liga-se ao socket de cada servidor distrital
% guarda os sockets num mapa com os nomes dos distritos
start() ->
    Districts = #{
        aveiro => {element(2,gen_tcp:connect("localhost", 8100, [binary, {packet, 0}])), #{}},
        beja => {element(2,gen_tcp:connect("localhost", 8101, [binary, {packet, 0}])), #{}},
        braga => {element(2,gen_tcp:connect("localhost", 8102, [binary, {packet, 0}, {active, false}, {reuseaddr, true}])), #{}},
        braganca => {element(2,gen_tcp:connect("localhost", 8103, [binary, {packet, 0}])), #{}},
        castelo_branco => {element(2,gen_tcp:connect("localhost", 8104, [binary, {packet, 0}])), #{}},
        coimbra => {element(2,gen_tcp:connect("localhost", 8105, [binary, {packet, 0}])), #{}},
        evora => {element(2,gen_tcp:connect("localhost", 8106, [binary, {packet, 0}])), #{}},
        faro => {element(2,gen_tcp:connect("localhost", 8107, [binary, {packet, 0}])), #{}},
        guarda => {element(2,gen_tcp:connect("localhost", 8108, [binary, {packet, 0}])), #{}},
        leiria => {element(2,gen_tcp:connect("localhost", 8109, [binary, {packet, 0}])), #{}},
        lisboa => {element(2,gen_tcp:connect("localhost", 8110, [binary, {packet, 0}])), #{}},
        portalegre => {element(2,gen_tcp:connect("localhost", 8111, [binary, {packet, 0}])), #{}},
        porto => {element(2,gen_tcp:connect("localhost", 8112, [binary, {packet, 0}])), #{}},
        santarem => {element(2,gen_tcp:connect("localhost", 8113, [binary, {packet, 0}])), #{}},
        setubal => {element(2,gen_tcp:connect("localhost", 8114, [binary, {packet, 0}])), #{}},
        viana_do_castelo => {element(2,gen_tcp:connect("localhost", 8115, [binary, {packet, 0}])), #{}},
        vila_real => {element(2,gen_tcp:connect("localhost", 8116, [binary, {packet, 0}])), #{}},
        viseu => {element(2,gen_tcp:connect("localhost", 8117, [binary, {packet, 0}])), #{}}
    },
    io:fwrite("Districts: ~p\n", [Districts]),
    register(?MODULE, spawn(fun() -> loop(Districts) end)).


rpc(Request) ->
    ?MODULE ! {Request,self()},
    receive
        {?MODULE, Result} -> Result
    end.


verifyDistrict(District) ->
    rpc({verify_district,District}).

countPeopleInLocation(District,X,Y) ->
    rpc({nr_people,District,X,Y}).


% Districts -> #{district_name -> {district_socket, #{username -> notif_socket}}}
loop(Districts) ->
    receive
        % retorna o nome do distrito correspondente ao código recebido
        {{verify_district, DistrictCode}, From} -> 
            case DistrictCode of
                '1' -> From ! {?MODULE, {ok, aveiro}};
                '2' -> From ! {?MODULE, {ok, beja}};
                '3' -> From ! {?MODULE, {ok, braga}};
                '4' -> From ! {?MODULE, {ok, braganca}};
                '5' -> From ! {?MODULE, {ok, castelo_branco}};
                '6' -> From ! {?MODULE, {ok, coimbra}};
                '7' -> From ! {?MODULE, {ok, evora}};
                '8' -> From ! {?MODULE, {ok, faro}};
                '9' -> From ! {?MODULE, {ok, guarda}};
                '10' -> From ! {?MODULE, {ok, leiria}};
                '11' -> From ! {?MODULE, {ok, lisboa}};
                '12' -> From ! {?MODULE, {ok, portalegre}};
                '13' -> From ! {?MODULE, {ok, porto}};
                '14' -> From ! {?MODULE, {ok, santarem}};
                '15' -> From ! {?MODULE, {ok, setubal}};
                '16' -> From ! {?MODULE, {ok, viana_do_castelo}};
                '17' -> From ! {?MODULE, {ok, vila_real}};
                '18' -> From ! {?MODULE, {ok, viseu}};               
                _ -> From ! {?MODULE, invalid}
            end,
            loop(Districts);

        % liga-se ao socket privado do cliente na porta dada e guarda o socket no mapa do respetivo distrito
        {register_user, District, Username, Port} ->
            {ok, NotifSocket} = gen_tcp:connect("localhost", Port, [binary, {packet, 0}, {active, false}, {reuseaddr, true}]),
            {DistSocket,Users} = maps:get(District,Districts),
            NewUsers = maps:put(Username,NotifSocket,Users),
            io:fwrite("Connected to user notification socket: ~p ~p.\n", [Username, NotifSocket]),
            loop(maps:update(District,{DistSocket,NewUsers},Districts));
        
        % envia a nova localização do cliente para o respetivo servidor distrital
        {location, District, Username, Location} ->
            {DistSocket,_} = maps:get(District,Districts),
            response_manager:sendUserLocation(DistSocket,Username,Location),
            io:fwrite("Sent new location to district server: ~p ~p ~p.\n", [Username, District,Location]),
            loop(Districts);

        % envia mensagem ao servidor distrital a avisar que o cliente está doente
        % fecha o socket privado do cliente e apaga-o do mapa do distrito
        {sick_user, District, Username} ->
            {DistSocket,_} = maps:get(District,Districts),
            response_manager:sendSickPing(DistSocket,Username),
            io:fwrite("Flagged sick user: ~p ~p.\n", [Username, District]),
            close_socket(Username,District,Districts);

        % envia pedido ao servidor distrital para contar o nr de pessoas na localização dada
        % recebe a contagem e envia-a para o client_manager para responder ao cliente
        {{nr_people, District, X, Y}, From} ->
            {DistSocket,_} = maps:get(District,Districts),
            response_manager:sendLocationToCountPeople(DistSocket,X,Y),
            {ok, Bin} = gen_tcp:recv(DistSocket,0),
            Msg = messages:decode_msg(Bin,'Message'),
            Total = maps:get(total,maps:get(nrPeopleReply, Msg)),
            From ! {?MODULE, {ok, Total}},
            loop(Districts);

        % notifica todos os clientes que tenham estado em contacto com o cliente infetado
        % envia-lhes mensagens pelos sockets privados
        {notify_users, District, Usernames} ->
            {_,UserSockets} = maps:get(District,Districts),
            case Usernames of
                [""] -> 
                    io:fwrite("No other users possibly infected.\n");
                _ ->
                    notify_loop(Usernames, UserSockets)
            end,
            loop(Districts);

        % fecha o socket privado do cliente e apaga-o do mapa do distrito
        {remove_user, District, Username} ->
            close_socket(Username,District,Districts)
    end.

% fecha o socket de notificações privadas do cliente
close_socket(Username,District,Districts) ->
    {DistSocket,Users} = maps:get(District,Districts),
    NotifSocket = maps:get(Username,Users),
    gen_tcp:close(NotifSocket),
    io:fwrite("Closed user notification socket: ~p ~p.\n", [Username, NotifSocket]),
    loop(maps:update(District,{DistSocket,maps:remove(Username,Users)},Districts)).

% envia uma notificação privada a cada cliente potencialmente contagiado
notify_loop([], _) -> io:fwrite("Users notified.\n");
notify_loop([Username|T], UserSockets) ->
    io:fwrite("Notifying possibly infected user: ~p.\n", [Username]), 
    Sock = maps:get(Username,UserSockets),
    response_manager:sendInfectionWarning(Sock),
    notify_loop(T,UserSockets).