-module(loginmanager).
-export([start/0, create_account/3, close_account/2, login/2, logout/2, online/0]).

start() -> 
    register(?MODULE, spawn(fun()->loop(#{}) end)). 

rpc(Request) -> 
    ?MODULE ! {Request,self()},
    receive
        {Result,?MODULE} -> Result
    end.

create_account(User,Password,District) ->
    rpc({create_account,User,Password,District}).

close_account(User,Password) ->
    rpc({close_account,User,Password}).

login(User,Password) ->
    rpc({login,User,Password}).

logout(User,Password) ->
    rpc({logout,User,Password}).

online() ->
    rpc(online).

loop(Accounts) ->
    receive
        {{create_account,User,Password,District},From} -> 
            case maps:find(User,Accounts) of 
                error -> 
                    From ! {ok, ?MODULE},
                    loop(maps:put(User,{Password,false,District}, Accounts));
                _ -> 
                    From ! {invalid, ?MODULE},
                    loop(Accounts)
            end;
        {{close_account,User,Password},From} ->
            case maps:find(User,Accounts) of 
                {ok, {Password,_,_}} -> 
                    From ! {ok, ?MODULE},
                    loop(maps:remove(User,Accounts));
                _ -> 
                    From ! {invalid, ?MODULE},
                    loop(Accounts)
            end;
        {{login,User,Password},From} -> 
            case maps:find(User,Accounts) of 
                {ok, {Password,false,District}} -> 
                    From ! {ok, ?MODULE},
                    loop(maps:update(User,{Password,true,District},Accounts));
                _ -> 
                    From ! {invalid, ?MODULE},
                    loop(Accounts)
            end;
        {{logout,User,Password},From} ->
            case maps:find(User,Accounts) of 
                {ok, {Password,true,District}} -> 
                    From ! {ok, ?MODULE},
                    loop(maps:update(User,{Password,false,District},Accounts));
                _ -> 
                    From ! {invalid, ?MODULE},
                    loop(Accounts)
            end;
        {online,From} -> 
            From ! {maps:keys(Accounts), ?MODULE},
            loop(Accounts)
    end.