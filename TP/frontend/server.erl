-module(server).
-export ([start_server/1]).

% começa a correr o servidor da frontend
start_server(Port) ->
    account_manager:start(),
    country_manager:start(),
    {ok, LSock} = gen_tcp:listen(Port, [binary, {active, once}, {packet, 0}, {reuseaddr, true}]),
    spawn(fun() -> acceptor(LSock) end),
    ok.

% processo em loop a aceitar novas conexões, cria um processo por cada socket (cliente) que se conecta
acceptor(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    io:fwrite("Connected socket ~p.\n", [Sock]),
    spawn(fun() -> acceptor(LSock) end),
    gen_tcp:controlling_process(Sock, self()),
    authenticator:authentication(Sock).