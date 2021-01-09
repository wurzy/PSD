-module(response_manager).

-export([sendResponse/3, sendUserLocation/3, sendSickPing/2, sendLocationToCountPeople/2]).

sendResponse(Socket,Result,Msg) ->
    Reply = messages:encode_msg(#{type=>'REPLY', reply => #{result=>Result, message=>Msg}}, 'Message'),
    gen_tcp:send(Socket,Reply).

sendUserLocation(Socket,Username,Location) ->
    X = maps:get(coordx,Location),
    Y = maps:get(coordy,Location),
    Msg = messages:encode_msg(#{type=>'LOCATION_PING', locationPing => #{username=>Username, coordx=>X, coordy=>Y}}, 'Message'),
    gen_tcp:send(Socket,Msg).

sendSickPing(Socket,Username) ->
    Msg = messages:encode_msg(#{type=>'SICK_PING', sickPing => #{username=>Username}}, 'Message'),
    gen_tcp:send(Socket,Msg).

sendLocationToCountPeople(Socket,Location) ->
    X = maps:get(coordx,Location),
    Y = maps:get(coordy,Location),
    Msg = messages:encode_msg(#{type=>'NR_PEOPLE_PING', location => #{coordx=>X, coordy=>Y}}, 'Message'),
    gen_tcp:send(Socket,Msg).