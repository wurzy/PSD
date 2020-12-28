-module(authenticator).

% API
-export([authentication/1]).

authentication(Socket) ->
    receive
        {tcp, _, Bin} ->
            Msg = messages:decode_msg(Bin, 'Message'),
            case maps:get(type, Msg) of
                'REGISTER' ->
                    Data = maps:get(registerData, Msg),
                    registerHandler(Socket, Data);
                'LOGIN' ->
                    Data = maps:get(loginData, Msg),
                    loginHandler(Socket, Data);
                'LOGOUT' ->
                    Data = maps:get(logoutData, Msg),
                    logoutHandler(Socket, Data)
            end;
        _ -> %{tcp_closed, _} e {tcp_error, _, _} || o que fazer aqui?
            io:fwrite("invalid~n"),
            invalid
    end.

registerHandler(Socket, {Username, Password, District}) ->
    io:fwrite("Register request. ~p ~p ~p\n", [Username, Password, District]),
    case client_manager:register(Username, Password, District) of
        ok ->
            io:fwrite("Successfully registered. ~p ~p ~p\n", [Username, Password, District]),
            Reply = messages:encode_msg(#{type=>'REPLY', replyData => #{result=>true, message=>"Successfully registered."}}, 'Message'),
            gen_tcp:send(Socket,Reply);
        _ ->
            io:fwrite("Username already taken. ~p ~p ~p\n", [Username, Password, District]),
            Reply = messages:encode_msg(#{type=>'REPLY', replyData => #{result=>false, message=>"Username already taken."}}, 'Message'),
            gen_tcp:send(Socket,Reply)
    end.

loginHandler(Socket, {Username, Password}) ->
    io:fwrite("Login request. ~p ~p\n", [Username, Password]),
    case client_manager:login(Username, Password) of
        ok ->
            io:fwrite("Successfully logged in. ~p ~p\n", [Username, Password]),
            Reply = messages:encode_msg(#{type=>'REPLY', replyData => #{result=>true, message=>"Successfully logged in."}}, 'Message'),
            gen_tcp:send(Socket,Reply);
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            Reply = messages:encode_msg(#{type=>'REPLY', replyData => #{result=>false, message=>ErrorMsg}}, 'Message'),
            gen_tcp:send(Socket,Reply)
    end.

logoutHandler(Socket, {Username, Password}) ->
    io:fwrite("Logout request. ~p ~p\n", [Username, Password]),
    case client_manager:login(Username, Password) of
        ok ->
            io:fwrite("Successfully logged out. ~p ~p\n", [Username, Password]),
            Reply = messages:encode_msg(#{type=>'REPLY', replyData => #{result=>true, message=>"Successfully logged out."}}, 'Message'),
            gen_tcp:send(Socket,Reply);
        {error, ErrorMsg} ->
            io:fwrite("~p ~p\n", [ErrorMsg, Username]),
            Reply = messages:encode_msg(#{type=>'REPLY', replyData => #{result=>false, message=>ErrorMsg}}, 'Message'),
            gen_tcp:send(Socket,Reply)
    end.    