# COMP6461-HTTPC-Client-Library-Implementation

In this assignment, you will implement a simple HTTP client application and experiment it in real HTTP Servers (web servers). Before starting on this assignment, it is strongly recommend that you read the provided programming samples and review the associated course materials.
Outline
The following is a summary of the main tasks of the Assignment:
1. Setup your development and testing environment.
2. Study HTTP network protocol specifications.
3. Build your own HTTP client library.
4. Program your HTTP client application (curl command).
5. (optional) Implement more HTTP protocol specifications.
6. (optional) Enhance the functionalities of the HTTP client.

  
You should receive the response depicted in the output sample. Similarly, you can explore HTTP protocol operations without the need to program.
HTTP Client Library Implementation
After getting a deeper understanding of the flow of HTTP GET and POST operations, you are requested to implement your HTTP client using TCP Sockets. The programming library implements only a small subset of HTTP specifications. In other words, we expect that your HTTP library supports the following features:
1. GET operation
2. POST operation
3. Query parameters
4. Request headers
5. Body of the request

The implemented client should be named httpc (the name of the produced executable). The following presents the options of your final command line.
httpc (get|post) [-v] (-h "k:v")* [-d inline-data] [-f file] URL
In the following, we describe the purpose of the expected httpc command options:
1. Option -v enables a verbose output from the command-line. Verbosity could be useful for testing and debugging stages where you need more information to do so. You define the format of the output. However, You are expected to print all the status, and its headers, then the contents of the response.
                    Comp 6461 – Fall 2018 - Lab Assignment # 1 Page 5

2. URL determines the targeted HTTP server. It could contain parameters of the HTTP operation. For example, the URL 'https://www.google.ca/?q=hello+world' includes the parameter q with "hello world" value.
3. To pass the headers value to your HTTP operation, you could use -h option. The latter means setting the header of the request in the format "key: value." Notice that; you can have multiple headers by having the -h option before each header parameter.
4. -d gives the user the possibility to associate the body of the HTTP Request with the inline data, meaning a set of characters for standard input.
5. Similarly to -d, -f associate the body of the HTTP Request with the data from a given file.
6. get/post options are used to execute GET/POST requests respectively. post should have either -d or -f but not both. However, get option should not used with the options -d or -f.

Enhance Your HTTP Client library
In the current HTTP library, you already implemented the necessary HTTP specifications, GET and POST. In this optional task, you need to implement one new specification of HTTP protocol that is related to the client side. For example, you could develop one of the Redirection specifications. The latter allow your HTTP client to follow the first request with another one to new URL if the client receives a redirection code (numbers starts with 3xx). This option is useful when the HTTP client deal with temporally or parental moved URLs. Notice, you are free to choose which HTTP specification to implement in your HTTP library. After selecting the specification, you should consult with the Lab Instructor before starting their implementations.
Update The cURL Command line
Accordingly, you could add the newly implemented HTTP specifications in your HTTP library to the httpc command line. To do that, you need to create a new option that allows the user the access the newly implemented specification. In addition, you are requested to add the option –o filename, which allow the HTTP client to write the body of the response to the specified file instead of the console. For example, the following will write the response to hello.txt:
httpc -v 'http://httpbin.org/get?course=networking&assignment=1' -o hello.txt
