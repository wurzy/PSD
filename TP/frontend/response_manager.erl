-module(response_manager).

-export([sendResponse/3]).

sendResponse(Socket,Result,Msg) ->
    Reply = messages:encode_msg(#{type=>'REPLY', reply => #{result=>Result, message=>Msg}}, 'Message'),
    gen_tcp:send(Socket,Reply).