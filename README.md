# Networking client-server library

# Commands for http client library
*
*	python client/httpc.py -h
*	python client/httpc.py get -h
*	python client/httpc.py get -v "http://httpbin.org/get?course=networking&assignment=1"
*	python client/httpc.py post -v -head Content-Type:application/json -d '{"Assignment":"1"}' -o "output.txt" "http://httpbin.org/post"
*	python client/httpc.py post -v -head Content-Type:application/json -f "file.json" -o "output.txt" "http://httpbin.org/post"
*	python client/httpc.py get -v "http://httpbin.org/redirect/5"
*
*

# Commands for http server library

*	python ./server/httpfs.py -h
*	python ./server/httpfs.py -v -p 8080 -d PATHTODIR.
	open http://localhost:8080/ into browser
*   python client/httpc.py get -v -p 8080 "http://localhost/"
*   python client/httpc.py get -v -p 8080 "http://localhost/foo"
*   python client/httpc.py post -v -p 8080 -head Content-Type:application/json -d "{\"Assignment\":\"2\"}" "http://localhost/bar"

*	python server/multiClientsreadRequest.py
*	python server/multiClientsreadwriteRequest.py
*	python server/multiClientswriteRequest.py

*   python client/httpc.py get -v -p 8080 -head Content-disposition:inline "http://localhost/foo"
*   python client/httpc.py get -v -p 8080 -head Content-disposition:attachment "http://localhost/foo"

*   http://localhost:8080/foo
*   http://localhost:8080/foo?inline
