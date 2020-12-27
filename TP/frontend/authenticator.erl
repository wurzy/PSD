-module(authenticator).

%% API
-export([authenticate/1, logout/1]).

%% Import
-include("messages.hrl").

%% Implementation
authenticate(Socket) ->
  receive
    {tcp, _, Bin} ->
      {'LoginRequest', Username, Password, Role} = messages:decode_msg(Bin, 'LoginRequest'),
      io:fwrite("Login request. ~p ~p ~p\n", [Username, Password, Role]),
      login(Socket, Username, Password, Role);
    _ ->
      io:fwrite("bye~n"),
      bye
  end.

login(Socket, Username, Password, Role) ->
  case loginManager:login(Username, Password, Role) of
    ok ->
      io:fwrite("Login ok. ~p ~p ~p\n", [Username, Password, Role]),
      Reply = messages:encode_msg(#'Reply'{result = true, message = "Login ok."}),
      gen_tcp:send(Socket, Reply),
      handle(Socket, Username, Role);
    _ ->
      io:fwrite("Wrong credentials. ~p ~p ~p\n", [Username, Password, Role]),
      Reply = messages:encode_msg(#'Reply'{result = false, message = "Wrong credentials."}),
      gen_tcp:send(Socket, Reply),
      authenticate(Socket)
  end.

handle(Socket, Username, "Investor") -> investor:handle(Socket, Username);
handle(Socket, Username, "Company") -> company:handle(Socket, Username).

logout(Username) ->
  loginManager:logout(Username).