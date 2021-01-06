-module(account_manager).
-export([start/0, register/3, login/2, logout/1, sick/1, online/0]).

start() -> 
    register(?MODULE, spawn(fun() -> loop(#{}) end)). 

rpc(Request) -> 
    ?MODULE ! {Request,self()},
    receive
        {?MODULE, Result} -> Result
    end.

register(Username,Password,District) ->
    rpc({register,Username,Password,District}).

login(Username,Password) ->
    rpc({login,Username,Password}).

logout(Username) ->
    rpc({logout,Username}).

sick(Username) ->
    rpc({sick,Username}).

online() ->
    rpc(online).

% Accounts = Map{Username => {Password, District, logged_flag, sick_flag}}
loop(Accounts) ->
    io:fwrite("Client state: ~p.\n", [Accounts]),
    receive
        {{register,Username,Password,District},From} ->
            case maps:find(Username,Accounts) of 
                error -> 
                    From ! {?MODULE, ok},
                    loop(maps:put(Username,{Password,District,false,false}, Accounts));
                _ -> 
                    From ! {?MODULE, {error, username_taken}},
                    loop(Accounts)
            end;
        {{login,Username,Password},From} -> 
            case maps:find(Username,Accounts) of 
                {ok, {Password,District,false,false}} -> 
                    From ! {?MODULE, {ok, District}},
                    loop(maps:update(Username,{Password,District,true,false},Accounts));
                {ok, {Password,_,false,true}} -> 
                    From ! {?MODULE, {error, "Can't login while quarantined."}},
                    loop(Accounts);
                {ok, {Password,_,true,_}} -> 
                    From ! {?MODULE, {error, "User already logged in."}},
                    loop(Accounts);
                {ok, _} -> 
                    From ! {?MODULE, {error, "Wrong password."}},
                    loop(Accounts);
                _ -> 
                    From ! {?MODULE, {error, "Username doesn't exist."}},
                    loop(Accounts)
            end;
        {{logout,Username},From} ->
            case maps:find(Username,Accounts) of 
                {ok, {Password,District,true,SickFlag}} -> 
                    From ! {?MODULE, ok},
                    loop(maps:update(Username,{Password,District,false,SickFlag},Accounts));
                %{ok, _} -> % à partida nunca acontece
                %    From ! {?MODULE, {error, "User not logged in."}},
                %    loop(Accounts);
                _ -> % à partida nunca acontece
                    From ! {?MODULE, {error, "Error logging out."}},
                    loop(Accounts)
            end;
        {{sick,Username},From} ->
            case maps:find(Username,Accounts) of 
                {ok, {Password,District,true,false}} -> 
                    From ! {?MODULE, ok},
                    loop(maps:update(Username,{Password,District,false,true},Accounts));
                %{ok, {_,_,false,_}} -> % à partida nunca acontece
                %    From ! {?MODULE, {error, "User not logged in."}},
                %    loop(Accounts);
                %{ok, {_,_,_,true}} -> % à partida nunca acontece
                %    From ! {?MODULE, {error, "User already sick."}},
                %    loop(Accounts);
                _ -> % à partida nunca acontece
                    From ! {?MODULE, {error, "Error flagging client as sick."}},
                    loop(Accounts)
            end;
        {online,From} -> 
            From ! {maps:keys(Accounts), ?MODULE},
            loop(Accounts)
    end.