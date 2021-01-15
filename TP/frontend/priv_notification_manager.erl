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
    register(?MODULE, spawn(fun() -> loop(DistSocket) end)).

loop(Socket) ->
    {ok, Bin} = chumak:recv(Socket),
    Msg = messages:decode_msg(Bin,'Message'),
    case maps:get(type, Msg) of
        'NOTIFY_USERS' ->
            MsgBody = maps:get(notifyUsers, Msg),
            District = maps:get(district, MsgBody),
            Usernames = string:split(maps:get(users, MsgBody), ",", all),
            district_manager ! {notify_users, District, Usernames}
    end,
    loop(Socket).