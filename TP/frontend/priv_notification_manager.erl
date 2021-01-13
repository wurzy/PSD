-module(priv_notification_manager).
-export([loop/0]).

% processo em loop à espera de conexões de clientes
% processo em loop à espera de pedidos do user
loop() ->
    receive
        {tcp, Socket, Bin} ->
            inet:setopts(Socket, [{active, once}]),
            Msg = messages:decode_msg(Bin,'Message'),
            io:fwrite("Private notification message: ~p\n", [Msg])
    end.