/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexey
 */
public class ChatServer {

    private static ArrayList<String> chat = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8000);

        while (true) {
            Socket socket = server.accept();
            Thread thread = new Thread(new ServerRespond(socket));
            thread.start();
        }
    }

    private static class ServerRespond implements Runnable {

        Socket socket;

        public ServerRespond(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                parseRequest(socket);
            } catch (IOException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void parseRequest(Socket socket) throws IOException {
            HttpParser parser = new HttpParser(socket.getInputStream());
            parser.parseRequest();
            if (parser.getMethod() == "POST") {
                //the server doesnt handle post request
                return;
            }
            String data = parser.getParam("text");
            //add data text to chat
            if(data != null && data != "")
                chat.add(data);
            boolean isURLEmpty = parser.getRequestURL().substring(1).isEmpty();
            writeResponce(isURLEmpty);
        }

        public void writeResponce(boolean isURLEmpty) throws IOException {
            OutputStream out = socket.getOutputStream();
            if (!isURLEmpty) {
                out.write(responce404().getBytes());
            } else {
                out.write(responce().getBytes());
            }
        }

        public String responce() {
            String text = "";
            for(String str : chat){
                text += str + "</br>" ;
            }
            String s = "<head><title>Beta Chat</title>\n"
                    + "</head><body>\n"
                    + "<h1></DIC> Chat Box</h1>\n"
                    + "<div style=\"border:1px solid #ccc; width: 220px; height:400px;\">"
                    + text
                    + "</div>"
                    + "<div id=\"chatBox\"></div>\n"
                    + "<div id=\"usersOnLine\"></div>\n"
                    + "<form id=\"messageForm\" method=\"get\">\n"
                    + "  <input type=\"text\" name=\"text\" />\n"
                    + "  <input type=\"submit\" value=\"Submit\">\n"
                    + "</form>"
                    + "<div id=\"serverRes\"></div>\n"
                    + "</form>\n"
                    + "</body>\n"
                    + "</html>";
            String response = "HTTP/1.1 200 OK\r\n"
                    + "Server: Alexggg99/2014-12\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + s.length() + "\r\n"
                    + "Connection: close\r\n\r\n" + s;
            return response;
        }

        public String responce404() {
            String s = "<html><head><meta charset='utf-8'/><title>404 - this page does not exist</title>"
                    + "<style>"
                    + "#content{margin:0 auto;\n"
                    + "width: 800px ;\n}"
                    + "</style></head><body>"
                    + "<center> <h1 style='centered' >Page or resource not found.</h1> </center>"
                    + "</body></html>";
            String response = "HTTP/1.1 404 OK\r\n"
                    + "Server: Alexggg99/2014-12\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + s.length() + "\r\n"
                    + "Connection: close\r\n\r\n" + s;
            return response;
        }

    }

}
