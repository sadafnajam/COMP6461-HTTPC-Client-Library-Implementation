import argparse
import sys
sys.path.extend(["../"])
sys.path.extend(["."])
from client.http import http


def create_http_client():
    h = http(args.URL, args.port)
    h.setType(args.which)
    if args.verbose:
        h.setVerbosity(True)
    if args.headers:
        header = ""
        if len(args.headers) > 0:
            for head in args.headers:
                split_head = head.split(":")
                if len(split_head) == 2:
                    h.addHeader(split_head[0], split_head[1])
    body = ""
    if args.which == "post":
       if args.data:
            body = args.data
            print(body)
            h.setData(body)
            if "Content-Type" not in h.header.keys():
                h.addHeader("Content-Type", "application/json")
            h.addHeader("Content-Length",str(len(body)))
       if args.file:
            with open(args.file, 'r') as f:
                body = f.read()
            h.setFile(body)
            if "Content-Type" not in h.header.keys():
                h.addHeader("Content-Type", "application/json")
            h.addHeader("Content-Length",str(len(body)))
    h.buildRequestInfo()
    return h


parser = argparse.ArgumentParser(
    description="httpc is a curl-like application but supports HTTP protocol only.")
subparsers = parser.add_subparsers(help='commands')

get_parser = subparsers.add_parser('get', help='executes a HTTP GET request and prints the response.')
get_parser.add_argument("-v", action='store_true', dest="verbose", default=False,
                        help="Prints the detail of the response such as protocol, status, and headers.")

get_parser.add_argument("-head", action="append", dest="headers", default=[],
                        help="Associates headers to HTTP Request with the format 'key:value'.")

get_parser.add_argument("-o", action="store", dest="output" , default = "", help = "Output the body to a file", required = False)
get_parser.add_argument("-p", action="store", dest="port", help="Set server port", type=int, default=80)
get_parser.set_defaults(which='get')

post_parser = subparsers.add_parser('post', help='executes a HTTP POST request and prints the response.')
post_parser.add_argument("-v", action='store_true', dest="verbose", default=False,
                         help="Prints the detail of the response such as protocol, status, and headers.")

post_parser.add_argument("-head", action="append", dest="headers", default=[],
                         help="Associates headers to HTTP Request with the format 'key:value'.")

group = post_parser.add_mutually_exclusive_group(required=False)
group.add_argument("-d", action="store", dest="data", help="Associates an inline data to the body HTTP POST request.")

group.add_argument("-f", action="store", dest="file", default="",
                   help="Associates the content of a file to the body HTTP POST request.")

post_parser.add_argument("-o", action="store", dest="output" , default = "",
                         help = "Output the body to a file", required = False)

post_parser.add_argument("-p", action="store", dest="port", help="Set server port", type=int, default=80)
post_parser.set_defaults(which='post')


parser.add_argument("URL", help="HTTP URL address")


args = parser.parse_args()

h = create_http_client()
while True:
    reply = h.send()
    if reply:
        break

if args.output:
    print(reply.headMap)
    if 'text' in  reply.headMap['Content-Type']:
        o = open(args.output, 'w')
        o.write(reply.body.decode("utf-8"))
        o.close()
    else:
        with open(args.output, 'w') as f:
            f.write(reply.body)
if h.getVerbosity():
    print("\nOutput:\n" + reply.reply)
else:
    print("\nOutput:\n" + reply.body)
