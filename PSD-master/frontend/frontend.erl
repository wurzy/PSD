-module(frontend).
-export([server/1]).

server(Port) ->
	loginManager:run(), 
	exchangeManager:run(),
    {ok, LSock} = gen_tcp:listen(Port, [binary,{packet, 0}, {reuseaddr, true}, {active, true}]),
    acceptor(LSock).


acceptor(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    spawn(fun() -> acceptor(LSock) end),
    wait_login(Sock).


wait_login(Sock) ->
    receive
        {tcp, Sock, Bin} ->
            Msg = protocol:decode_msg(Bin, 'Message'),
          	Cli = maps:get(user, Msg),
          	User = maps:get(username, Cli),
          	Pass = maps:get(password, Cli),

            case maps:get(type, Msg) of
            	"REGISTER" ->
					case loginManager:create_account(User, Pass) of
            			created ->
            				ResBin = protocol:encode_msg(#{dest => User, type => "RESPONSE", response => #{result => "OK", description => "SUCCESSFUL REGISTER"}}, 'Message'),
            				gen_tcp:send(Sock, ResBin),
            		  		wait_login(Sock);
            			user_exists ->
            		  		ResBin = protocol:encode_msg(#{dest => User, type => "RESPONSE", response => #{result => "EXCEPTION", description => "USER EXISTS"}}, 'Message'),
							wait_login(Sock)
          			end;	
				"LOGIN" ->
					case loginManager:login(User, Pass) of
						logged ->
							ResBin = protocol:encode_msg(#{dest => User, type => "RESPONSE", response => #{result => "OK", description => "SUCCESSFUL LOGIN"}}, 'Message'),
							gen_tcp:send(Sock, ResBin),
							userSession:run(Sock, User);
            			invalid ->
            		  		ResBin = protocol:encode_msg(#{dest => User, type => "RESPONSE", response => #{result => "EXCEPTION", description => "INVALID LOGIN"}}, 'Message'),
              				gen_tcp:send(Sock, ResBin),
            		  		wait_login(Sock)
          			end
			end
    end.    
