-module(server).
-export ([start/2]).

% começa a correr o servidor da frontend
start(Port1,Port2) ->
    account_manager:start(),
    district_manager:start(),
    {ok, LSock} = gen_tcp:listen(Port1, [binary, {active, once}, {packet, 0}, {reuseaddr, true}]),
    {ok, LSock2} = gen_tcp:listen(Port2, [binary, {active, once}, {packet, 0}, {reuseaddr, true}]),
    spawn(fun() -> acceptor(LSock) end),
    spawn(fun() -> notif_acceptor(LSock2) end),
    ok.

% processo em loop a aceitar novas conexões, cria um processo por cada socket (cliente) que se conecta
acceptor(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    io:fwrite("Connected socket ~p.\n", [Sock]),
    spawn(fun() -> acceptor(LSock) end),
    gen_tcp:controlling_process(Sock, self()),
    authenticator:authentication(Sock).

% processo em loop a aceitar novas conexões, cria um processo por cada socket (cliente) que se conecta
notif_acceptor(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    io:fwrite("Connected socket ~p.\n", [Sock]),
    spawn(fun() -> notif_acceptor(LSock) end),
    gen_tcp:controlling_process(Sock, self()),
    priv_notification_manager:loop(Sock).