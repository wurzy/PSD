build: build-broker build-districts build-client build-frontend build-directory

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

build-directory:
	javac -cp dependencies/jar/directory.jar directory/src/main/java/Directory/DirectoryApp.java -d exec/


run-broker:
	java -cp .:dependencies/jar/broker.jar District/Broker 8000 8001 &

run-districts:
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8100 aveiro 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8101 beja 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8102 braga 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8103 braganca 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8104 castelo_branco 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8105 coimbra 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8106 evora 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8107 faro 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8108 guarda 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8109 leiria 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8110 lisboa 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8111 portalegre 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8112 porto 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8113 santarem 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8114 setubal 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8115 viana_do_castelo 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8116 vila_real 8080 &
	java -cp .:dependencies/jar/districtServer.jar District/DistrictServer 12346 8001 8117 viseu 8080 &

run-client:
	java -cp .:dependencies/jar/client.jar Client/Client 12345 $(PORT) 8000

run-directory:
	java -cp .:dependencies/jar/directory.jar Directory/DirectoryApp server directory/config.yml &


kill-broker:
	kill -9 $$(lsof -t -i:8000)

kill-districts:
	for p in {8100..8117}; do kill -9 $(lsof -t -i:$p); done


clean:
	-@rm -rf exec/*
	-@rm client/src/main/java/Protos/Messages.java
	-@rm districts/src/main/java/Protos/Messages.java
	-@rm -rf frontend/ebin/*.beam
	-@rm frontend/src/messages.erl