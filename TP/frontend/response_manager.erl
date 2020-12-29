-module(response_manager).

-export([sendAuthResponse/2]).

sendAuthResponse(Socket,Msg) ->
    Reply = messages:encode_msg(#{type=>'REPLY', replyData => #{result=>true, message=>Msg}}, 'Message'),
    gen_tcp:send(Socket,Reply).