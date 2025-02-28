package com.example;

import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }
        boolean escucha = true;
        while (escucha) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            String linea = " ";
            outputLine = " ";
            boolean primeraLinea = true;
            while ((inputLine = in.readLine()) != null) {
                if (primeraLinea) {
                    linea = inputLine;
                    primeraLinea = false;
                }
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            if ("/".equals(linea.split(" ")[1])) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>\r\n" + //
                        "<html>\r\n" + //
                        "    <head>\r\n" + //
                        "        <title>Form Example</title>\r\n" + //
                        "        <meta charset=\"UTF-8\">\r\n" + //
                        "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "    </head>\r\n" + //
                        "    <body>\r\n" + //
                        "        <h1>Form with GET</h1>\r\n" + //
                        "        <form action=\"/hello\">\r\n" + //
                        "            <label for=\"name\">Name:</label><br>\r\n" + //
                        "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\r\n" + //
                        "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\r\n" + //
                        "        </form> \r\n" + //
                        "        <div id=\"getrespmsg\"></div>\r\n" + //
                        "\r\n" + //
                        "        <script>\r\n" + //
                        "            function loadGetMsg() {\r\n" + //
                        "                let nameVar = document.getElementById(\"name\").value;\r\n" + //
                        "                const xhttp = new XMLHttpRequest();\r\n" + //
                        "                xhttp.onload = function() {\r\n" + //
                        "                    document.getElementById(\"getrespmsg\").innerHTML =\r\n" + //
                        "                    this.responseText;\r\n" + //
                        "                }\r\n" + //
                        "                xhttp.open(\"GET\", \"/hello?name=\"+nameVar);\r\n" + //
                        "                xhttp.send();\r\n" + //
                        "            }\r\n" + //
                        "        </script>\r\n" + //
                        "\r\n" + //
                        "        <h1>Form with POST</h1>\r\n" + //
                        "        <form action=\"/hellopost\">\r\n" + //
                        "            <label for=\"postname\">Name:</label><br>\r\n" + //
                        "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\r\n" + //
                        "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\r\n" + //
                        "        </form>\r\n" + //
                        "        \r\n" + //
                        "        <div id=\"postrespmsg\"></div>\r\n" + //
                        "        \r\n" + //
                        "        <script>\r\n" + //
                        "            function loadPostMsg(name){\r\n" + //
                        "                let url = \"/hellopost?name=\" + name.value;\r\n" + //
                        "\r\n" + //
                        "                fetch (url, {method: 'POST'})\r\n" + //
                        "                    .then(x => x.text())\r\n" + //
                        "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\r\n" + //
                        "            }\r\n" + //
                        "        </script>\r\n" + //
                        "    </body>\r\n" + //
                        "</html>";
                
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}