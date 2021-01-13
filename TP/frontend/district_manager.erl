-module(district_manager).

-export([start/0, verifyDistrict/1, countPeopleInLocation/3]).

start() -> 
    Districts = #{
        %aveiro => {element(2,gen_tcp:connect("localhost", 8100, [binary, {packet, 0}])), #{}},
        %beja => {element(2,gen_tcp:connect("localhost", 8101, [binary, {packet, 0}])), #{}},
        braga => {element(2,gen_tcp:connect("localhost", 8102, [binary, {packet, 0}])), #{}}
        %bragança => {element(2,gen_tcp:connect("localhost", 8103, [binary, {packet, 0}])), #{}},
        %castelo_branco => {element(2,gen_tcp:connect("localhost", 8104, [binary, {packet, 0}])), #{}},
        %coimbra => {element(2,gen_tcp:connect("localhost", 8105, [binary, {packet, 0}])), #{}},
        %evora => {element(2,gen_tcp:connect("localhost", 8106, [binary, {packet, 0}])), #{}},
        %faro => {element(2,gen_tcp:connect("localhost", 8107, [binary, {packet, 0}])), #{}},
        %guarda => {element(2,gen_tcp:connect("localhost", 8108, [binary, {packet, 0}])), #{}},
        %leiria => {element(2,gen_tcp:connect("localhost", 8109, [binary, {packet, 0}])), #{}},
        %lisboa => {element(2,gen_tcp:connect("localhost", 8110, [binary, {packet, 0}])), #{}},
        %portalegre => {element(2,gen_tcp:connect("localhost", 8111, [binary, {packet, 0}])), #{}},
        %porto => {element(2,gen_tcp:connect("localhost", 8112, [binary, {packet, 0}])), #{}},
        %santarem => {element(2,gen_tcp:connect("localhost", 8113, [binary, {packet, 0}])), #{}},
        %setubal => {element(2,gen_tcp:connect("localhost", 8114, [binary, {packet, 0}])), #{}},
        %viana_do_castelo => {element(2,gen_tcp:connect("localhost", 8115, [binary, {packet, 0}])), #{}},
        %vila_real => {element(2,gen_tcp:connect("localhost", 8116, [binary, {packet, 0}])), #{}},
        %viseu => {element(2,gen_tcp:connect("localhost", 8117, [binary, {packet, 0}])), #{}}
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

        {{nr_people, CliSocket, District, Location}, From} ->
            {DistSocket,_} = maps:get(District,Districts),
            response_manager:sendLocationToCountPeople(DistSocket,Location),
            receive
                {tcp, CliSocket, Bin} ->
                    inet:setopts(CliSocket, [{active, once}]),
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