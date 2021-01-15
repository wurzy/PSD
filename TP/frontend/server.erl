-module(server).
-export ([start/2]).

% começa a correr o servidor da frontend
start(Port1,Port2) ->
    account_manager:start(),
    district_manager:start(),
    {ok, LSock} = gen_tcp:listen(Port1, [binary, {active, once}, {packet, 0}, {reuseaddr, true}]),
    priv_notification_manager:bind(Port2),
    spawn(fun() -> client_acceptor(LSock) end),
    ok.

% processo em loop a aceitar novas conexões, cria um processo por cada socket (cliente) que se conecta
client_acceptor(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    io:fwrite("Connected client socket: ~p.\n", [Sock]),
    spawn(fun() -> client_acceptor(LSock) end),
    gen_tcp:controlling_process(Sock, self()),
    authenticator:authentication(Sock).