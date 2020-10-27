-module(loginmanager).
-export([start/0, create_account/2, close_account/2, login/2]). % acrescentar ao api o start para abstrair

% Vai dar jeito um Map<User,Passwd&BoolOnline>

start() -> 
    register(?MODULE, spawn(fun()->loop(#{}) end)). % ?MODULE == nome_do_modulo, é um define kinda
% Isto aqui é muito repetitivo... melhorar com método rpc
%create_account(User,Password) -> 
%    ?MODULE ! {create_account,User,Password,self()},
%    receive
%        {Result,?MODULE} -> Result
%    end.
%
%close_account(User,Password) ->
%    ?MODULE ! {close_account,User,Password,self()},
%    receive
%        {Result,?MODULE} -> Result
%    end.
%
%login(User,Password) ->
%    ?MODULE ! {login,User,Password,self()},
%    receive
%        {Result,?MODULE} -> Result
%    end.
%

rpc(Request) -> 
    ?MODULE ! {Request,self()},
    receive
        {Result,?MODULE} -> Result
    end.

create_account(User,Password) ->
    rpc({create_account,User,Password}).

close_account(User,Password) ->
    rpc({close_account,User,Password}).

login(User,Password) ->
    rpc({login,User,Password}).

loop(Accounts) ->
    receive
        {{create_account,User,Password},From} -> 
            case maps:find(User,Accounts) of 
                error -> 
                    From ! {ok, ?MODULE},
                    loop(maps:put(User,{Password,false}, Accounts));
                _ -> % aqui nao e preciso extrair a info do user, basta saber que existe.
                    From ! {user_exists, ?MODULE},
                    loop(Accounts)
            end;
        {{close_account,User,Password},From} ->
            case maps:find(User,Accounts) of 
                {ok, {Password,_}} -> % se existe e se a password está correta
                    From ! {ok, ?MODULE},
                    loop(maps:remove(User,Accounts));
                _ -> % se for qualquer outra coisa nao interessa, erro ou pw errada
                    From ! {invalid, ?MODULE},
                    loop(Accounts)
            end;
        {{login,User,Password},From} -> 
            case maps:find(User,Accounts) of 
                {ok, {Password,false}} -> % se existe e se a password está correta e se estava offline
                    From ! {ok, ?MODULE},
                    loop(maps:update(User,{Password,true},Accounts));
                _ -> % se for qualquer outra coisa nao interessa, erro ou pw errada
                    From ! {invalid, ?MODULE},
                    loop(Accounts)
            end               

    end.