-module(district_manager).

-export([start/0, verifyDistrict/1, countPeopleInLocation/2]).

start() -> 
    Districts = #{},
    Districts = maps:put(aveiro,{8100, #{}},Districts),
    Districts = maps:put(beja,{8101, #{}},Districts),
    Districts = maps:put(braga,{8102, #{}},Districts),
    Districts = maps:put(bragança,{8103, #{}},Districts),
    Districts = maps:put(castelo_branco,{8104, #{}},Districts),
    Districts = maps:put(coimbra,{8105, #{}},Districts),
    Districts = maps:put(evora,{8106, #{}},Districts),
    Districts = maps:put(faro,{8107, #{}},Districts),
    Districts = maps:put(guarda,{8108, #{}},Districts),
    Districts = maps:put(leiria,{8109, #{}},Districts),
    Districts = maps:put(lisboa,{8110, #{}},Districts),
    Districts = maps:put(portalegre,{8111, #{}},Districts),
    Districts = maps:put(porto,{8112, #{}},Districts),
    Districts = maps:put(santarem,{8113, #{}},Districts),
    Districts = maps:put(setubal,{8114, #{}},Districts),
    Districts = maps:put(viana_do_castelo,{8115, #{}},Districts),
    Districts = maps:put(vila_real,{8116, #{}},Districts),
    Districts = maps:put(viseu,{8117, #{}},Districts),
    register(?MODULE, spawn(fun() -> loop(Districts) end)).

rpc(Request) ->
    ?MODULE ! {Request,self()},
    receive
        {?MODULE, Result} -> Result
    end.

verifyDistrict(District) ->
    rpc({verify_district,District}).

countPeopleInLocation(District,Location) ->
    rpc({nr_people,District,Location}).

% Districts -> #{district_name -> {district_socket, #{username -> notif_socket}}}
loop(Districts) ->
    io:fwrite("Districts list: ~p\n", [Districts]),
    receive
        {{verify_district, District}, From} ->
            case maps:is_key(District,Districts) of
                true -> From ! {?MODULE, ok};
                false -> From ! {?MODULE, invalid}
            end,
            loop(Districts);

        {register_user, Socket, District, Username, Port} ->
            {DistSocket,Users} = maps:get(District,Districts),
            NewUsers = maps:put(Username,Port,Users),
            io:fwrite("Registered private notification socket. ~p ~p\n", [Username, Port]),
            response_manager:sendResponse(Socket,true,"Registered private notification socket."),
            loop(maps:update(District,{DistSocket,NewUsers},Districts));
        
        {location, District, Username, Location} ->
            {Socket,_} = maps:get(District,Districts),
            response_manager:sendUserLocation(Socket,Username,Location),
            io:fwrite("Sent new location to district server. ~p ~p\n", [Username, District]),
            loop(Districts);

        {sick_user, District, Username} ->
            {Socket,_} = maps:get(District,Districts),
            response_manager:sendSickPing(Socket,Username),
            loop(Districts);

        {{nr_people, District, Location}, From} ->
            {Socket,_} = maps:get(District,Districts),
            response_manager:sendLocationToCountPeople(Socket,Location),
            loop(Districts);

        {remove_user, District, Username} ->
            {DistSocket,Users} = maps:get(District,Districts),
            NewUsers = maps:remove(Username,Users),
            loop(maps:update(District,{DistSocket,NewUsers},Districts))
    end.