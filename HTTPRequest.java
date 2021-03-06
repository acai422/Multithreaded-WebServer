/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * @author aaron
 */
final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;
	
/**
* This is a constructor for the Http request.
*/
    public HttpRequest(Socket socket) throws Exception{
        this.socket = socket;
    }
    
/**
*Implement the run() method of the Runnable interface.
*/
    public void run(){
        try {
            processRequest();
        } catch (Exception e){
            System.out.println(e);
        }
    }
/**
 * This gets a reference to the socket's I/O streams, then sets up input stream filters, 
 * gets and displays the http, then opens the file. 
 */
    private void processRequest() throws Exception{
// Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

// Set up input stream filters.
		
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
			    
// Get the request line of the HTTP request message.
        String requestLine = br.readLine();
        
// Display the request line.
        System.out.println();
        System.out.println(requestLine);
			    
// Get and display the header lines.
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }
        
// Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();  // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();

// Prepend a "." so that file request is within the current directory.
        fileName = "." + fileName;
		 
// Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        }catch (FileNotFoundException e) {
            fileExists = false;
        }
			    
// Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "File Found: ";
            contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
        }
                        
        else {
            statusLine = "File Found: ";
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
        }
// Send the status line.
        os.writeBytes(statusLine);

// Send the content type line.
        os.writeBytes(contentTypeLine);

// Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);
			    
// Send the entity body.
        if (fileExists){
            sendBytes(fis, os);
            fis.close();
        } 
                
        else{
            os.writeBytes(entityBody);
        }
        
// Close streams and socket.
        os.close();
        br.close();
        socket.close();
    }

/**
* This constructs a buffer that will hold the bytes until they reach the socket.
*/
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
// Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

// Copy requested file into the socket's output stream.
        while((bytes = fis.read(buffer)) != -1 ){
            os.write(buffer, 0, bytes);
        }
    }  
			    
/**
* This will make read the file names and return the type of file.
*/
    private static String contentType(String fileName){
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
    
        if(fileName.endsWith(".jpg")) {
            return "text/jpg";
        }
	  	
        if(fileName.endsWith(".gif")) {
            return "text/gif";
        }
        
        return "application/octet-stream";
    }   		
}

