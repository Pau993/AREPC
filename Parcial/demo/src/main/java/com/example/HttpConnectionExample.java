package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpConnectionExample {

    private static final int PORT = 36000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor HTTP en puerto " + PORT + "...");

            while (true) { // Manejo de múltiples conexiones
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> manejarCliente(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manejarCliente(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }

            System.out.println("Solicitud recibida: " + requestLine);

            if (requestLine.startsWith("GET /computar?comando=")) {
                String query = requestLine.split(" ")[1];
                String comando = URLDecoder.decode(query.split("=")[1], "UTF-8");
                Object result = ejecutarComando(comando);

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: application/json");
                out.println();

                if (result instanceof int[]) {
                    out.println("{\"resultado\": " + Arrays.toString((int[]) result) + "}");
                } else {
                    out.println("{\"resultado\": \"" + result + "\"}");
                }
            } else {
                out.println("HTTP/1.1 400 Bad Request");
                out.println("Content-Type: text/plain");
                out.println();
                out.println("Solicitud no reconocida");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object ejecutarComando(String comando) {
        try {
            String[] partes = comando.replaceAll("[()]", "").split(",");
            if (partes.length < 2) {
                return "Comando inválido";
            }

            String metodoNombre = partes[0];

            if (metodoNombre.equals("bubbleSort")) {
                int[] array = new int[partes.length - 1];
                for (int i = 1; i < partes.length; i++) {
                    array[i - 1] = Integer.parseInt(partes[i]);
                }
                return Algoritmos.bubbleSort(array);
            }

            // Lógica para invocar métodos de Math u otros
            Class<?> clazz = Math.class;
            List<Class<?>> paramTypes = new ArrayList<>();
            List<Object> paramValues = new ArrayList<>();
            for (int i = 1; i < partes.length; i += 2) {
                String tipo = partes[i];
                String valor = partes[i + 1];
                switch (tipo) {
                    case "int":
                        paramTypes.add(int.class);
                        paramValues.add(Integer.parseInt(valor));
                        break;
                    case "double":
                        paramTypes.add(double.class);
                        paramValues.add(Double.parseDouble(valor));
                        break;
                    default:
                        return "Tipo de parámetro no soportado: " + tipo;
                }
            }

            Method method = clazz.getMethod(metodoNombre, paramTypes.toArray(new Class[0]));
            return method.invoke(null, paramValues.toArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al ejecutar el comando: " + e.getMessage();
        }
    }

    public static class Algoritmos {
        public static int[] bubbleSort(int[] arr) {
            int n = arr.length;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (arr[j] > arr[j + 1]) {
                        int temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            }
            return arr;
        }
    }
}
