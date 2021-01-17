-module(response_manager).

-export([sendResponse/3, sendUserLocation/3, sendSickPing/2, sendLocationToCountPeople/3, sendInfectionWarning/1]).

% envia uma resposta tcp ao pedido do cliente
sendResponse(Socket,Result,Msg) ->
    Reply = messages:encode_msg(#{type=>'REPLY', reply => #{result=>Result, message=>Msg}}, 'Message'),
    gen_tcp:send(Socket,Reply).

% envia uma mensagem tcp ao cliente com o nr de pessoas na localização que ele indicou
sendUserLocation(Socket,Username,Location) ->
    X = maps:get(coordx,Location),
    Y = maps:get(coordy,Location),
    Msg = messages:encode_msg(#{type=>'LOCATION_PING', location => #{username=>Username, coordx=>X, coordy=>Y}}, 'Message'),
    gen_tcp:send(Socket,Msg).

% envia uma mensagem tcp ao servidor distrital com o nome do cliente infetado
sendSickPing(Socket,Username) ->
    Msg = messages:encode_msg(#{type=>'SICK_PING', sickPing => #{username=>Username}}, 'Message'),
    gen_tcp:send(Socket,Msg).

% envia uma mensagem tcp ao servidor distrital com uma localização para contar o nr de pessoas
sendLocationToCountPeople(Socket,X,Y) ->
    Msg = messages:encode_msg(#{type=>'NR_PEOPLE', location => #{coordx=>X, coordy=>Y}}, 'Message'),
    gen_tcp:send(Socket,Msg).

% envia uma notificação privada tcp ao cliente a avisá-lo que pode estar contagiado
sendInfectionWarning(Socket) ->
    Warning = "Esteve em contacto com um utilizador que está doente!",
    Msg = messages:encode_msg(#{type=>'NOTIFICATION', notification => #{notification=>Warning}}, 'Message'),
    gen_tcp:send(Socket,Msg).