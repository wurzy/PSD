-module(district_manager).

-export([start/0, verifyDistrict/1, countPeopleInLocation/3]).

start() -> 
    Districts = #{
        aveiro => {element(2,gen_tcp:listen(8100, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        beja => {element(2,gen_tcp:listen(8101, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        braga => {element(2,gen_tcp:listen(8102, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        bragança => {element(2,gen_tcp:listen(8103, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        castelo_branco => {element(2,gen_tcp:listen(8104, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        coimbra => {element(2,gen_tcp:listen(8105, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        evora => {element(2,gen_tcp:listen(8106, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        faro => {element(2,gen_tcp:listen(8107, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        guarda => {element(2,gen_tcp:listen(8108, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        leiria => {element(2,gen_tcp:listen(8109, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        lisboa => {element(2,gen_tcp:listen(8110, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        portalegre => {element(2,gen_tcp:listen(8111, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        porto => {element(2,gen_tcp:listen(8112, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        santarem => {element(2,gen_tcp:listen(8113, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        setubal => {element(2,gen_tcp:listen(8114, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        viana_do_castelo => {element(2,gen_tcp:listen(8115, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        vila_real => {element(2,gen_tcp:listen(8116, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}},
        viseu => {element(2,gen_tcp:listen(8117, [binary, {active, once}, {packet, 0}, {reuseaddr, true}])), #{}}
    },
    io:fwrite("Districts list: ~p\n", [Districts]),
    register(?MODULE, spawn(fun() -> loop(Districts) end)).

rpc(Request) ->
    ?MODULE ! {Request,self()},
    receive
        {?MODULE, Result} -> Result
    end.

verifyDistrict(District) ->
    rpc({verify_district,District}).

countPeopleInLocation(Socket,District,Location) ->
    rpc({nr_people,Socket,District,Location}).

% Districts -> #{district_name -> {district_socket, #{username -> notif_socket}}}
loop(Districts) ->
    receive
        {{verify_district, District}, From} ->
            case maps:is_key(District,Districts) of
                true -> From ! {?MODULE, ok};
                false -> From ! {?MODULE, invalid}
            end,
            loop(Districts);

        {register_user, CliSocket, District, Username, Port} ->
            {DistSocket,Users} = maps:get(District,Districts),
            NewUsers = maps:put(Username,Port,Users),
            io:fwrite("Registered private notification socket. ~p ~p\n", [Username, Port]),
            response_manager:sendResponse(CliSocket,true,"Registered private notification socket."),
            loop(maps:update(District,{DistSocket,NewUsers},Districts));
        
        {location, District, Username, Location} ->
            io:fwrite("D ~p U ~p L ~p\n", [District,Username,Location]),
            {Socket,_} = maps:get(District,Districts),
            response_manager:sendUserLocation(Socket,Username,Location),
            io:fwrite("Sent new location to district server. ~p ~p\n", [Username, District]),
            loop(Districts);

        {sick_user, District, Username} ->
            {Socket,_} = maps:get(District,Districts),
            response_manager:sendSickPing(Socket,Username),
            loop(Districts);

        {{nr_people, Socket, District, Location}, From} ->
            {Socket,_} = maps:get(District,Districts),
            response_manager:sendLocationToCountPeople(Socket,Location),
            receive
                {tcp, Socket, Bin} ->
                    inet:setopts(Socket, [{active, once}]),
                    Msg = messages:decode_msg(Bin,'Message'),
                    Username = maps:get(username,Msg),
                    Total = maps:get(total,Msg),
                    io:fwrite("Message nr people reply: ~p ~p\n", [Username, Total]),
                    From ! {?MODULE, {ok, Total}}
            end,
            loop(Districts);

        {remove_user, District, Username} ->
            {DistSocket,Users} = maps:get(District,Districts),
            NewUsers = maps:remove(Username,Users),
            loop(maps:update(District,{DistSocket,NewUsers},Districts))
    end.