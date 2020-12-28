-module(server).
-export ([start_server/1]).

% começa a correr o servidor da frontend
start_server(Port) ->
    client_manager:start(),
    {ok, LSock} = gen_tcp:listen(Port, [binary, {active, once}, {packet, 0}, {reuseaddr, true}]),
    spawn(fun() -> acceptor(LSock) end),
    ok.

% cria um processo de Erlang por cliente e fica em loop a aceitar novas conexões
acceptor(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    spawn(fun() -> acceptor(LSock) end),
    gen_tcp:controlling_process(Sock, self()),
    authenticator:authentication(Sock).