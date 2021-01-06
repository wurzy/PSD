-module(country_manager).

-export([start/0, registerUserInDistrict/2]).

start() -> 
    register(?MODULE, spawn(fun() -> loop([]) end)).

rpc(Request) ->
    ?MODULE ! {Request,self()},
    receive
        {?MODULE, Result} -> Result
    end.

registerUserInDistrict(District,Username) ->
    rpc({user_in_district,District,Username}).

%countPeopleInLocation(District,Location) ->
%    rpc({nr_people,District,Location}).

% Districts -> List(DistrictName)
loop(Districts) ->
    io:fwrite("Districts list: ~p\n", [Districts]),
    receive
        {{user_in_district, District, Username}, From} ->
            case lists:member(District,Districts) of
                false ->
                    district_manager:start(District,Username),
                    From ! {?MODULE, ok},
                    loop(Districts ++ [District]);
                true ->
                    District ! {{new_user,Username},self()},
                    receive
                        ok -> From ! {?MODULE, ok};
                        {error, username_taken} -> From ! {?MODULE, {error, username_taken}} % nunca deve acontecer
                    end,
                    loop(Districts)
            end
        %{{nr_people, District, Location}, From} ->
        %    Name = string:lowercase(District),
        %    case lists:member(Name,Districts) of
        %        true ->
        %            Name ! {{nr_people,Location},self()},
        %            receive
        %                {ok, Nr_People} ->
        %                    From ! {?MODULE, Nr_People},
        %                    loop(Districts)
        %            end;
        %        false -> % nunca deve acontecer
        %            From ! {?MODULE, {error, unregistered_district}},
        %            loop(Districts)
        %    end
    end.