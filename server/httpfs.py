import socket
import threading
import argparse
import os
import json
import pathlib
import sys
sys.path.extend(["./"])
from lockfile import LockFile
from server.http import http
import magic

#   Run HTTP Server


def runserver(host, port, directory):
    listen_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        listen_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        listen_socket.bind((host, port))
        listen_socket.listen(10)
        if args.debugging:
            print('Serving HTTP on port', port)
        while True:
            client_connection, client_address  = listen_socket .accept()
            threading.Thread(target=handle_client_request, args=(client_connection, client_address, directory)).start()
    finally:
        listen_socket.close()

#   handle the client requests from clients
# -----GET / &  GET /foo & POST /bar


def handle_client_request(conn, address, _directory):
    if args.debugging:
        print('Handle New client from', address)
    try:
        while True:
            _data = conn.recv(2048)
            _data = _data.decode("utf-8")
            if not _data:
                break
            #   parse_request function will return the formatted data
            (_method, _path, _query, _body, _headers) = parse_request(_data)
            if args.debugging:
                print(_method, _path, _body, _headers)
            if ".." in _path:
                if args.debugging:
                    print("Access Denied", _path)
                info = http(400, "Access Denied".encode("ascii"))
            else:
                if not _directory.endswith("/"):
                    _directory = _directory + "/"
                path = (_directory + _path).replace("//", "/")
            #   handle the GET request from client
                if _method == "GET":
                    try:
                        #   GET / returns a list of the current files in the data directory.
                        if path.endswith("/"):
                            if args.debugging:
                                print("GET Directory", path)
                            _filesInfo = os.listdir(path)
                            info = http(200, json.dumps(_filesInfo).encode("ascii"))
                            info.addHeader("Content-Type", "application/json")
                        else:
                            #   GET /foo returns the content of the file named foo in the data directory
                            if os.path.exists(path):
                                #    If the content does not exist
                                if args.debugging:
                                    print("FIND File", path)
                                info = http(200, "")
                                file_format = magic.from_file(path, mime=True)
                                info.addHeader("Content-Type", file_format)
                                if "text" in file_format:
                                    with open(path, 'r') as f:
                                        content = f.read()
                                        info.setContent(content.encode("ascii"))
                                else:
                                    with open(path, 'rb') as f:
                                        content = f.read()
                                        info.setContent(content)

                                if "Content-disposition" in _headers:
                                    info.addHeader("Content-disposition", _headers["Content-disposition"])
                                elif "inline" in _query:
                                    info.addHeader("Content-disposition", "inline")
                                else:
                                    info.addHeader("Content-disposition", "attachment")
                            else:
                                info = http(404, "".encode("ascii"))
                    except OSError as e:
                        if args.debugging:
                            print(e)
                            info = http(400, e.strerror)
                #   handle the POST request from client
                elif _method == "POST":
                    #   POST /bar should create or overwrite the file named bar in the data directory with
                    # the content of the body of the request
                    try:
                        if args.debugging:
                            print("POST File", path)
                        pathlib.Path(os.path.dirname(path)).mkdir(parents=True, exist_ok=True)
                        lock = LockFile(path)
                        lock.acquire()
                        print(os.path.basename(path), " Content", _body)
                        with open(path, 'a+') as f:
                            f.write(_body + "\n")
                        lock.release()
                        info = http(200, "".encode("ascii"))
                    except OSError as e:
                        if args.debugging:
                            print(e)
                        info = http(400, e.strerror)
                else:
                    info = http(400, "")
            if args.debugging:
                print(info.headToString())
            conn.sendall(info.headToString().encode("ascii"))
            conn.sendall(info.getBody())
            break

    finally:
        conn.close()


def parse_request(resp):
    (_head, _body) = resp.split("\r\n\r\n")
    _headArray = _head.split("\r\n")
    _command = _headArray.pop(0).split()
    method = _command[0]
    path = _command[1]
    query = ""
    if "?" in path:
        path, query = path.split("?")
    elif "&" in path:
        path, query = path.split("&")
    _headMap = {}
    for key in _headArray:
        _keyAndValue = key.split(":")
        _headMap[_keyAndValue[0]] = _keyAndValue[1].strip()
    return method, path, query, _body, _headMap

#   parse command line arguments using argparse
#    httpfs [-v] [-p PORT] [-d PATH-TO-DIR]
#   https://docs.python.org/dev/library/argparse.html#sub-commands


parser = argparse.ArgumentParser(description='Socket based HTTP file server')
parser .add_argument("-p", action="store", dest="port",
                    help="Specifies the port number that the server will listen and serve at.Default is 8080.",
                    type=int, default=8080)
parser .add_argument("-v", action="store_true", dest="debugging", help="Prints debugging messages", default=False)
parser .add_argument("-d", action="store", dest="directory",
                    help="Specifies the directory that the server will use to read/write requested files.",
                    default='./')

args = parser .parse_args()
runserver('', args.port, args.directory)
