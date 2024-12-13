package com.servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LanzaServidor implements Runnable {

      final ServidorChat interfaz;

    LanzaServidor(ServidorChat interfaz) {
        this.interfaz = interfaz;
    }

    @Override
    public void run() {

        ServerSocket serverSocket;
        final int PUERTO = 9876;
        List<HiloConexion> conexiones = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(PUERTO);

            while (true) {
                Socket conexion;
                System.out.println("Servidor iniciado");
                conexion = serverSocket.accept();
                HiloConexion hc = new HiloConexion(this, conexion);
                conexiones.add(hc);
                new Thread(hc).start();
            }

        } catch (Exception e) {
            System.out.println("Se ha producido algún error y no se ha iniciado el servidor.");
            return;
        }

    }
}