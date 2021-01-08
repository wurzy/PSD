-module(userSession).
-export([run/2, send/2]).

send(Msg, User) ->
	User ! {transaction, Msg}.

run(Sock, User) ->
	receive
    	{tcp, Sock, Bin} ->
			Msg = protocol:decode_msg(Bin, 'Message'),
			Order = maps:get(order, Msg),
			Company = maps:get(company, Order),
			{ok, Pid} = exchangeManager:lookup_exchange(Company),
			io:format("Message = ~p\n", [Msg]),
			ok = exchangeProducer:new_order(Bin, Pid),
			run(Sock, User);
		{transaction, Bin} ->
            gen_tcp:send(Sock, Bin),
			run(Sock, User);
		{tcp_closed, _} ->
			loginManager:logout(User)
	end.
