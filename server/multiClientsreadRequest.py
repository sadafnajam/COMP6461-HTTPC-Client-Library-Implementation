import sys
sys.path.extend(["../"])
sys.path.extend(["."])
from threading import Thread
from client.http import http
#   Concurrent Requests: Multi clients are reading the same file(foo):


def test_get_file(file, self):
    _reqInfo = http("http://localhost/"+file, 8080)
    _reqInfo.setType('get')
    _reqInfo.buildRequestInfo()
    reply = _reqInfo.send()


for i in range(0, 5):
    thread = Thread(target=test_get_file, args=("foo", i)).start()