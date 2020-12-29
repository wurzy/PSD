-module(client).

-export([register/3, login/2, logout/1]).

register(Username,Password,District) -> 
    {ok, Sock} = gen_tcp:connect("localhost", 1234, [binary, {packet, 0}]),
    gen_tcp:send(Sock, messages:encode_msg(
                #{type=>'REGISTER', 
                  registerData => #{
                        username=>Username, 
                        password=>Password, 
                        district=>District}}, 'Message')),
    receive
        {tcp, _, Bin} ->
            Msg = messages:decode_msg(Bin, 'Message'),
            io:fwrite("Reply: ~p\n", [Msg])
    end,
    gen_tcp:close(Sock).

login(Username,Password) -> 
    {ok, Sock} = gen_tcp:connect("localhost", 1234, [binary, {packet, 0}]),
    gen_tcp:send(Sock, messages:encode_msg(
                #{type=>'LOGIN', 
                  loginData => #{
                        username=>Username, 
                        password=>Password}}, 'Message')),
    receive
        {tcp, _, Bin} ->
            Msg = messages:decode_msg(Bin, 'Message'),
            io:fwrite("Reply: ~p\n", [Msg])
    end,
    gen_tcp:close(Sock).

logout(Username) -> 
    {ok, Sock} = gen_tcp:connect("localhost", 1234, [binary, {packet, 0}]),
    gen_tcp:send(Sock, messages:encode_msg(
                #{type=>'LOGOUT',
                  logoutData => #{
                        username=>Username}}, 'Message')),
    receive
        {tcp, _, Bin} ->
            Msg = messages:decode_msg(Bin, 'Message'),
            io:fwrite("Reply: ~p\n", [Msg])
    end,
    gen_tcp:close(Sock).