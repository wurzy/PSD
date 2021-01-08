-module(exchangeManager).
-export([run/0, lookup_exchange/1]).

run() -> 
	exchangeConsumer:run("localhost", 3332),
	register(?MODULE, spawn ( fun() -> exchangeManager(#{}, #{}) end) ).

lookup_exchange(Company) ->
	?MODULE ! {lookup, Company, self()},
	receive
		{ok, Pid} ->
			{ok, Pid}
	end.

% Keeps two maps. One maps Exchange's name with the responsible actor's pid
% and the other a cache, that maps a Company's name with the responsible actor's pid 
exchangeManager(Exchanges, Cache) ->
	receive
		{lookup, Company, From} ->
			io:format("looking for ~p\n", [Company]),
			case maps:find(Company, Cache) of
				error -> 
					EInfo = ask_directory(Company),
					Name = maps:get(name, EInfo),
					case maps:find(Name, Exchanges) of
						{ok, Pid} ->
							maps:update(Company, Pid, Cache),
							From ! {ok, Pid};
						_ ->
							Host = maps:get(host, EInfo),
							Port = maps:get(port, EInfo),
							Pid = exchangeProducer:run(Host, Port),
							io:format("Creating producer for ~p:~p\n", [Host, Port]),
							From ! {ok, Pid}
					end;
				{ok, Pid} ->
					From ! {ok, Pid}
			end,
			exchangeManager(Exchanges, Cache)
	end.

% Returns a Map with the exchange info
% #{ name => "Gualtar", host => "localhost", port => "2222" }
ask_directory(Company) ->
	inets:start(),
	{ok, {_, _, Result}} = httpc:request("http://localhost:8080/company/" ++ Company),
	inets:stop(),
	{struct, Json} = mochijson:decode(Result),
	{_, Exchange} = proplists:get_value("exchange", Json), 
	proplists_to_map(Exchange).

proplists_to_map(Prop) ->
	Name = proplists:get_value("name", Prop),
	Host = proplists:get_value("host", Prop),
	Port = proplists:get_value("port", Prop),
	#{name => Name, host => Host, port => Port}.
