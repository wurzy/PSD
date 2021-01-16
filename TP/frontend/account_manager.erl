-module(account_manager).
-export([start/0, signup/3, login/2, logout/1, sick/1]).

start() -> 
    register(?MODULE, spawn(fun() -> loop(#{}) end)). 

rpc(Request) -> 
    ?MODULE ! {Request,self()},
    receive
        {?MODULE, Result} -> Result
    end.

signup(Username,Password,District) ->
    rpc({signup,Username,Password,District}).

login(Username,Password) ->
    rpc({login,Username,Password}).

logout(Username) ->
    rpc({logout,Username}).

sick(Username) ->
    rpc({sick,Username}).

% Accounts = Map{Username => {Password, District, logged_flag, sick_flag}}
loop(Accounts) ->
    receive
        {{signup,Username,Password,District},From} ->
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
                    From ! {?MODULE, {error, quarantined, "Não pode entrar enquanto estiver em quarentena"}},
                    loop(Accounts);
                {ok, {Password,_,true,_}} -> 
                    From ! {?MODULE, {error, login_status, "Já está alguém nesta conta"}},
                    loop(Accounts);
                {ok, _} -> 
                    From ! {?MODULE, {error, wrong_password, "Password errada"}},
                    loop(Accounts);
                _ -> 
                    From ! {?MODULE, {error, wrong_username, "Username não existe"}},
                    loop(Accounts)
            end;
        {{logout,Username},From} ->
            case maps:find(Username,Accounts) of 
                {ok, {Password,District,true,SickFlag}} -> 
                    From ! {?MODULE, ok},
                    loop(maps:update(Username,{Password,District,false,SickFlag},Accounts));
                _ -> % à partida nunca acontece
                    From ! {?MODULE, {error, "Erro ao dar logout"}},
                    loop(Accounts)
            end;
        {{sick,Username},From} ->
            case maps:find(Username,Accounts) of 
                {ok, {Password,District,true,false}} -> 
                    From ! {?MODULE, ok},
                    loop(maps:update(Username,{Password,District,false,true},Accounts));
                _ -> % à partida nunca acontece
                    From ! {?MODULE, {error, "Não foi possível sinalizar o utilizador"}},
                    loop(Accounts)
            end
    end.