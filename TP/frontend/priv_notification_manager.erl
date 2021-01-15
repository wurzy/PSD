-module(priv_notification_manager).
-export([bind/1]).

bind(Port) ->
    application:start(chumak),
    {ok, DistSocket} = chumak:socket(pull),

    case chumak:bind(DistSocket, tcp, "localhost", Port) of
        {ok, _BindPid} ->
            io:format("Binding OK with Pid: ~p\n", [DistSocket]);
        {error, Reason} ->
            io:format("Connection Failed for this reason: ~p\n", [Reason]);
        X ->
            io:format("Unhandled reply for bind ~p \n", [X])
    end,
    loop(DistSocket).

loop(Socket) ->
    {ok, Data} = chumak:recv(Socket),
    io:format("Received ~p\n", [Data]),
    loop(Socket).