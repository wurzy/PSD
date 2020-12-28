-module(client_manager).
-export([start/0, register/3, login/2, logout/2, online/0]).

start() -> 
    register(?MODULE, spawn(fun() -> loop(#{}) end)). 

rpc(Request) -> 
    ?MODULE ! {Request,self()},
    receive
        {Result,?MODULE} -> Result
    end.

register(Username,Password,District) ->
    rpc({register,Username,Password,District}).

login(Username,Password) ->
    rpc({login,Username,Password}).

logout(Username,Password) ->
    rpc({logout,Username,Password}).

online() ->
    rpc(online).

% Accounts = Map{Username => {Password, District, logged_flag, sick_flag}}
loop(Accounts) ->
    receive
        {{register,Username,Password,District},From} -> 
            case maps:find(Username,Accounts) of 
                error -> 
                    From ! ok,
                    loop(maps:put(Username,{Password,District,false,false}, Accounts));
                _ -> 
                    From ! {error, username_taken},
                    loop(Accounts)
            end;
        {{login,Username,Password},From} -> 
            case maps:find(Username,Accounts) of 
                {ok, {Password,District,false,SickFlag}} -> 
                    From ! ok,
                    loop(maps:update(Username,{Password,District,true,SickFlag},Accounts));
                {ok, {Password,_,true,_}} -> 
                    From ! {error, "User already logged in."},
                    loop(Accounts);
                {ok, _} -> 
                    From ! {error, "Wrong password."},
                    loop(Accounts);
                _ -> 
                    From ! {error, "Username doesn't exist."},
                    loop(Accounts)
            end;
        {{logout,Username,Password},From} ->
            case maps:find(Username,Accounts) of 
                {ok, {Password,District,true,SickFlag}} -> 
                    From ! ok,
                    loop(maps:update(Username,{Password,District,false,SickFlag},Accounts));
                {ok, {Password,_,false,_}} -> 
                    From ! {error, "User not logged in."},
                    loop(Accounts);
                {ok, _} -> 
                    From ! {error, "Wrong password."},
                    loop(Accounts);
                _ -> 
                    From ! {error, "Username doesn't exist."},
                    loop(Accounts)
            end;
        {online,From} -> 
            From ! {maps:keys(Accounts), ?MODULE},
            loop(Accounts)
    end.