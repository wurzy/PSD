-module(district_manager).

-export([start/0, verifyDistrict/1, countPeopleInLocation/2]).

start() -> 
    Districts = #{
        aveiro => {8100, #{}},
        beja => {8101, #{}},
        braga => {8102, #{}},
        bragança => {8103, #{}},
        castelo_branco => {8104, #{}},
        coimbra => {8105, #{}},
        evora => {8006, #{}},
        faro => {8007, #{}},
        guarda => {8008, #{}},
        leiria => {8009, #{}},
        lisboa => {8010, #{}},
        portalegre => {8011, #{}},
        porto => {80012, #{}},
        santarem => {80013, #{}},
        setubal => {8014, #{}},
        viana_do_castelo => {8015, #{}},
        vila_real => {8016, #{}},
        viseu => {8017, #{}}
    },
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