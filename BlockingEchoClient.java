package ca.concordia.echo;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static java.util.Arrays.asList;

public class BlockingEchoClient {

    private static void readEchoAndRepeat(SocketChannel socket) throws IOException,Exception {
        Charset utf8 = StandardCharsets.UTF_8;
        System.out.println("Use format \"httpc (get|post) [-v] (-h \"k:v\")* [-d inline-data] [-f file] URL\"");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            run(line);
        }
    }

    private static void runClient(SocketAddress endpoint) throws IOException,Exception {
    	SocketChannel socket = null;
    	readEchoAndRepeat(socket);
    }

    public static void main(String[] args) throws IOException,Exception {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(asList("host", "h"), "EchoServer hostname")
                .withOptionalArg()
                .defaultsTo("localhost");

        parser.acceptsAll(asList("port", "p"), "EchoServer listening port")
                .withOptionalArg()
                .defaultsTo("8007");

        OptionSet opts = parser.parse(args);

        String host = (String) opts.valueOf("host");
        int port = Integer.parseInt((String) opts.valueOf("port"));

        SocketAddress endpoint = new InetSocketAddress(host, port);
        runClient(endpoint);
    }
    public static void run(String _opt) throws Exception {
    	String [] _sttArray = _opt.split(" ");
    	if(_sttArray.length < 2) {
    		System.out.println("Insufficient arguments\n");
    	}else if(_sttArray[0].toUpperCase().equals("HTTPC")) {
    		if(_sttArray[1].toUpperCase().equals("HELP")) {
    			helpOutput(_sttArray.length, _opt);
    		}else if(_sttArray[1].toUpperCase().equals("GET") || _sttArray[1].toUpperCase().equals("POST") || _sttArray[_sttArray.length -2].toUpperCase().equals("-O")
    				|| _sttArray[1].toUpperCase().equals("-L")){
    			requestCreator(_opt,_sttArray.length);
    		}
    	}else {
    		System.out.println("Command line arguments format is wrong, try again.\n");
    	}
    }
    
    public static void requestCreator(String paramargs , int nmbrOfPrmtrs) throws Exception{
        int verboseSetter = 1;
        int headerAllocationBool = 0;
        int isNewFile = 0;
        String headerLines = null;
        String _urlParam = null;
		String url = null;
		String fileData = null;
		String fileName = null;
        int DAndFCheck = 0;
        Boolean isOK = true;
        int isRedirection = 0;
        String [] parameters = paramargs.split(" ");
        if(parameters[1].toUpperCase().contains("GET")){
            DAndFCheck = 1;
        }
        for( int i = 1; i < nmbrOfPrmtrs; i++){
            if(parameters[i].toUpperCase().equals("-V")){   // check for the verbose attribute
                verboseSetter = 0;
            }else if(parameters[i].toUpperCase().equals("-H")){ // check for headerlines
                i++;
                if(headerAllocationBool == 0){
                    headerAllocationBool = 1;
                    headerLines = parameters[i];
                }else if(headerAllocationBool == 1){
                }
            }else if(parameters[i].toUpperCase().equals("--D")){
                if(DAndFCheck == 0){
                    DAndFCheck = 1;
                    i++;
                    _urlParam = parameters[i].replaceAll("\'","");
                }else if(DAndFCheck == 1){
                    System.out.print("POST request should only have -f or -d but not both at the same time. GET should not have any of the two...\n");
                    isOK = false;
                }
            }else if(parameters[i].toUpperCase().equals("-F")){
                if(DAndFCheck == 0){
                    DAndFCheck = 1;
                    i++;
                    fileData = getFileData(parameters[i]).replaceAll("\'","");
                }else if(DAndFCheck == 1){
                	 System.out.print("Error: POST request should only have -f or -d but not both at the same time. GET should not have any of the two...\n");
                	 isOK = false;
                }
            }else if(parameters[i].toUpperCase().equals("-O")){
            		isNewFile = 1;
                    i++;
                    fileName = parameters[i].replaceAll("\'","");
                
            }else if(parameters[i].toUpperCase().equals("-L")) {
            	isRedirection = 1;
            }
            	else  if(i == nmbrOfPrmtrs - 1 && isNewFile == 0){
            	url = parameters[i].toLowerCase().replaceAll("\'","");
            } 
            if(isNewFile == 1) {
            	url = parameters[nmbrOfPrmtrs-3].toLowerCase().replaceAll("\'","");
            }
        }
        String _url = isNewFile == 0 ? parameters[1].toUpperCase() : null;
        if(isRedirection == 1) {
        	_url = null;
        }
        if(isOK) {
        	HttpClientCall( _url ,verboseSetter , headerLines , _urlParam , url , fileData , isNewFile , fileName);
        }
    }
    /**
     * @param callType
     * @param verboseSetter
     * @param headerLines
     * @param _urlParam
     * @param url
     * @param fileData
     * @throws Exception
     */
    public static void HttpClientCall(String callType ,int verboseSetter , String headerLines , String _urlParam , String url, String fileData, int isNewFile ,String  fileName) throws Exception {
    	try {
    	 	URL _url = new URL(url);
        	String hostname = _url.getHost();
            int port1 = 80;
            Socket socket = new Socket();
    		socket.connect(new InetSocketAddress(hostname, port1));
    		DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            PrintWriter writer = new PrintWriter(output, true);
            callType = (callType == null) ? "GET" : callType;
            if(_url.getQuery() == null) {
            	writer.println(callType+" " + _url.getPath() + " HTTP/1.0\r");
            }else {
            	writer.println(callType+" " + _url.getPath() + "?"+_url.getQuery() + " HTTP/1.0\r");
            }
            writer.println("Host: " + hostname+"\r");
            writer.println("User-Agent: Concordia-HTTP/1.0\r");
    		if(headerLines != null) {
    			writer.println(headerLines+"\r");
    		}
            writer.println("\r");
            if(_urlParam != null || fileData!= null) {
            	HttpPostCall(callType ,verboseSetter , headerLines , _urlParam , _url, fileData);
		 	}else {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            Boolean flag = false;
            if(isNewFile == 1) {
            	createNewFile(fileName , reader);
            }else {
	            while ((line = reader.readLine()) != null) {
	           if(line.contains("300 Multiple Choices") || line.contains("301 Moved Permanently") || line.contains("302 Found") 
	            			|| line.contains("303 See Other") || line.contains("304 Not Modified") || line.contains("305 Use Proxy") || line.contains("306 Switch Proxy") || line.contains("307 Temporary Redirect") || line.contains("308 Permanent Redirect")) {
	            		System.out.println("This page has been moved and status is " + line);
	            		System.out.println("redirecting to http://httpbin.org");
	            		run("httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'");
	            		break;
	            	}
	            	if(verboseSetter == 0) {
	            		System.out.println(line);
	            	}
	            	if (line.isEmpty() && verboseSetter != 0){
						flag = true;
					}
	            	if(flag){
						System.out.println(line);
					}else if(!false && line.isEmpty()){
						System.out.println(line);
					}
	            }
            }
            output.close();
            reader.close();
            socket.close();
		 }
		}catch(Exception e) {
			System.out.println(e);
		}
    }
    public static void HttpPostCall(String callType ,int verboseSetter , String headerLines , String _urlParam , URL _url, String fileData) throws Exception {
		try {
		HttpURLConnection con = (HttpURLConnection) _url.openConnection();
		con.setRequestMethod(callType);
		if(headerLines != null) {
			String [] _headers = headerLines.split(":");
			con.setRequestProperty(_headers[0], _headers[1]);
		}
		if(_urlParam != null || fileData!= null) {
			con.setDoOutput(true);
			String _paramData = _urlParam == null ? fileData : _urlParam;
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(_paramData);
			wr.flush();
			wr.close();
		}
		if(con.getResponseCode() == 200) {
		if(verboseSetter == 0) {
		 Map<String, java.util.List<String>> hdrs = con.getHeaderFields();
		    Set<String> hdrKeys = hdrs.keySet();
		    for (String k : hdrKeys) {
		    if(k == null) {
		    	System.out.println(hdrs.get(k));
		    }else {
		      System.out.println(k + ":" + hdrs.get(k));
		    }
		 }
		}
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
		 String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
			in.close();
	    }else {
			System.out.println("Something went wrong");
		}
		}catch(Exception e) {
			System.out.println(e);
		}
    }
    public static String getFileData(String fname) throws IOException {
		   String st = null; 
		    try {
		   	 File file = new File(fname);
		BufferedReader br = new BufferedReader(new FileReader(file));
		  while ((st = br.readLine()) != null)  
			  return st;
		} catch (IOException e) {
			System.out.println("File not found");
		    }
			return st;
   }
    public static void createNewFile(String fileName , BufferedReader reader) {
            File file = new File(fileName);
            FileWriter fr = null;
            BufferedWriter br = null;
            String line = null;
            try{
                fr = new FileWriter(file);
                br = new BufferedWriter(fr);
                while ((line = reader.readLine()) != null) {
                	br.write(line);
		            	if (line.isEmpty()){
		            		break;
						}
		            }
                System.out.println("File created with name "+ fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    br.close();
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
    public static void helpOutput(int nmbrOfRgmnts, String othrRgmnt){
        if(nmbrOfRgmnts < 3)
        {
        	System.out.println("httpc is a curl-like application but supports HTTP protocol only.\nUsage:\n\thttpc command [arguments]\nThe commands are:\n\tget executes a HTTP GET request and prints the response.\n\tpost executes a HTTP POST request and prints the response.\n\thelp prints this screen.\nUse \"httpc help [command]\" for more information about a command.\n");
        }else if(nmbrOfRgmnts == 3){
            othrRgmnt = othrRgmnt.toUpperCase();
            if(othrRgmnt.toUpperCase().contains("POST"))
            {
                System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\nPost executes a HTTP POST request for a given URL with inline data or from file.\n    -v\t\t\tPrints the detail of the response such as protocol,status, and headers.\n    -h key:value\tAssociates headers to HTTP Request with the format 'key:value'.\n    -d string\t\tAssociates an inline data to the body HTTP POST request.\n    -f file\t\tAssociates the content of a file to the body HTTP POST request.\nEither [-d] or [-f] can be used but not both.\n");
            }
            else if(othrRgmnt.toUpperCase().contains("GET"))
            {
            	System.out.println("usage: httpc get [-v] [-h key:value] URL\nGet executes a HTTP GET request for a given URL.\n    -v\t\t\tPrints the detail of the response such as protocol, status, and headers.\n    -h key:value\tAssociates headers to HTTP Request with the format 'key:value'.\n");
            }else{
            	System.out.println("Not a recognizable command, try:\n httpc HELP GET \n httpc HELP POST\n");
            }
        }
    }
}