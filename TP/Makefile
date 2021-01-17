

build: build-boker build-districts build-client build-frontend

build-broker:
	javac -cp dependencies/jar/broker.jar districts/src/main/java/District/Broker.java -d exec/

build-districts:
	protoc --java_out=districts/src/main/java protos/messages.proto
	javac -cp dependencies/jar/districtServer.jar districts/src/main/java/District/DistrictServer.java -d exec/

build-client:
	protoc --java_out=client/src/main/java protos/messages.proto
	javac -cp dependencies/jar/client.jar client/src/main/java/Client/Client.java -d exec/

build-frontend:
	dependencies/gpb/bin/protoc-erl -I. -maps -o frontend/src protos/messages.proto
	erlc -I dependencies/gpb/include -o frontend/ebin frontend/src/messages.erl
	erlc -I dependencies/chumak/include -o frontend/ebin dependencies/chumak/src/chumak_pattern.erl
	erlc -I dependencies/chumak/include -o frontend/ebin -pa frontend/ebin dependencies/chumak/src/*.erl 
	erlc -o frontend/ebin frontend/src/*.erl


run: run-broker run-districts

run-broker:
	java -cp .:dependencies/jar/broker.jar District/Broker 8000 8001 &

run-districts:
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8100 aveiro &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8101 beja &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8102 braga &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8103 braganca &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8104 castelo_branco &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8105 coimbra &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8106 evora &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8107 faro &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8108 guarda &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8109 leiria &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8110 lisboa &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8111 portalegre &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8112 porto &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8113 santarem &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8114 setubal &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8115 viana_do_castelo &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8116 vila_real &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8117 viseu &

run-client:
	java -cp .:dependencies/jar/client.jar Client/Client 12345 $(PORT) 8000


kill-broker:
	kill -9 $$(lsof -t -i:8000)

kill-districts:
	for p in {8100..8117}; do kill -9 $(lsof -t -i:$p); done


clean:
	-@rm -rf exec/*
	-@rm client/src/main/java/Protos/Messages.java
	-@rm districts/src/main/java/Protos/Messages.java
	-@rm -rf frontend/ebin/*.beam
	-@rm frontend/messages.erl