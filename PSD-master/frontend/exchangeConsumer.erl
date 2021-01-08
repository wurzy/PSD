-module(exchangeConsumer).
-export([run/2]).

run(Host, Port) -> 
	{ok, Context} = erlzmq:context(),
	{ok, Sock} = erlzmq:socket(Context, [pull, {active, false}]),
	register(?MODULE, spawn ( fun() -> exchangeConsumer(Sock) end) ).

exchangeConsumer(Sock) ->
	case erlzmq:recv(Sock) of
		{ok, Bin} ->
			Msg = protocol:decode_msg(Bin, 'Message'),
			io:format("Incoming message: ~p\n", [Msg]),
			User = maps:get(dest, Msg),
			{ok, Pid} = loginManager:user_pid(User),
			userSession:send(Msg, Pid),
			exchangeConsumer(Sock);
		{error, _} -> 
			exchangeConsumer(Sock)
	end.
