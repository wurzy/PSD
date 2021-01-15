-module(priv_notification_manager).
-export([bind/1,loop/1]).

bind(Port) ->
    {ok, Context} = erlzmq:context(1),
    {ok, DistSocket} = erlzmq:socket(Context, [pull, {active, false}]),
    ok = erlzmq:bind(DistSocket, "tcp://localhost:" ++ integer_to_list(Port)),
    register(?MODULE, spawn(fun() -> loop(DistSocket) end)).

% processo em loop à espera de mensagens dos servidores distritais
loop(Socket) ->
    case erlzmq:recv(Socket) of
        {ok, Bin} ->
            Msg = messages:decode_msg(Bin,'Message'),
            io:fwrite("Message from district: ~p\n", [Msg]),
            loop(Socket);
        {error, _} ->
            loop(Socket)
    end.