% SYSTEM MESSAGES DEFINITION

% REGISTER

% pedido de registo
encode_msg(#{type => "REGISTER", user => #{ username => "zesilva63", password => "pass"}, request => #{}, response => #{}}

% resposta a registo com sucesso
#{type => "RESPONSE", user => #{}, request => #{}, response => #{ result => "OK", description => "You are now registered!"}}

% resposta a registo com erro user exists
#{type => "RESPONSE", user => #{}, request => #{}, response => #{result => "ERROR", description => "User already exists in the system."}}


% LOGIN

% pedido de login
#{type => "LOGIN", user => #{ username => "zesilva63", password => "pass" }, request => #{}, response => #{}}

% resposta a login com sucesso
#{type => "RESPONSE", user => #{}, request => #{}, response => #{result => "OK", description => "You are now logged in."}}

% resposta a login com erro 
#{type => "RESPONSE", user => #{}, request => #{}, response => #{result => "ERROR", description => "Wrong username or password."}}



% SELL 

% venda google 150 unidades a 2.5 euros cada
#'Message'{type => "SELL", user => #{ username => "marcelo140", password => "pass" }, request => #{company => "Google", quantity => 150, price => 2.50}, response => #{}}

% resposta de venda efetuada 150 unidades google a 2.75 euros cada
#'Message'{type => "BUY", user => #{ username => "marcelo140", password => "pass" }, request => #{}, response => #{result => "OK", description => "150 units of Google stocks sold for 2.75 each."}}


% BUY

% compra google 200 unidades a 3 euros cada
#'Message'{type => "BUY", user => #{ username => "zesilva63", password => "pass" }, request => #{company => "Google", quantity => 200, price => 3.00}, response => #{}}

% resposta de compra efetuada 150 unidades google a 2.75 euros cada
#'Message'{type => "RESPONSE", user => #{ username => "zesilva63", password => "pass" }, request => #{}, response => #'Response'{result => "OK", description => "150 units of Google stocks bought for 2.75 each."}}