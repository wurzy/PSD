-module(district_manager).

- export([start/2]).

% Para ja guardar o historico todo de cada user, eventualmente deixar só a posição atual
% Locations => Map{Username => list[(x,y)}

% regista o processo deste district_manager com o nome do distrito (átomo)
% inicializa o mapa do distrito com um user
start(Name,Username) -> 
    register(Name, spawn(fun() -> loop(#{Username => []}) end)).

loop(Locations) ->
    receive
        % novo user registado, inicializar a lista de localizações dele no distrito
        {{new_user,Username},From} ->
            case maps:find(Username,Locations) of
                error ->
                    From ! ok,
                    loop(maps:put(Username, [], Locations));
                _ -> % à partida nunca acontece
                    From ! {error, username_taken},
                    loop(Locations)
            end;
        {{location,Username,X,Y},From} ->
            UserLocs = maps:get(Username,Locations),
            case lists:last(UserLocs) of
                {X,Y} -> loop(Locations); % se a localização enviada for a atual, não insere repetida
                _ -> loop(maps:update(Username, UserLocs++{X,Y}, Locations))
            end,
            From ! ok;
        {{nr_people,X,Y},From} ->
            Pred = fun(_,V) -> {X,Y} = lists:last(V) end,
            From ! {ok, maps:size(maps:filter(Pred,Locations))},
            loop(Locations)
    end.