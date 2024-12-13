package com.servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class HiloConexion implements Runnable {
    private Socket conexion;
    private LanzaServidor servidor;

    private BufferedReader flujoEntrada;
    private PrintWriter flujoSalida;

    public HiloConexion(LanzaServidor servidor, Socket conexion) {
        this.conexion = conexion;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            flujoEntrada = new BufferedReader(new InputStreamReader(this.conexion.getInputStream()));
            flujoSalida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.conexion.getOutputStream())),
                    true);
            System.out.println("hilo iniciado");
            while (true) {
                String lectura = flujoEntrada.readLine();
                String comando = lectura.substring(0, 3);
                if (comando.equals("MSG")) {

                }
                if (comando.equals("CON")) {

                }
                if (comando.equals("EXI")) {
                    break;
                }
                System.out.println(lectura);
                flujoSalida.println(lectura);
            }
        } catch (IOException e) {

            e.printStackTrace();
            System.out.println("No se pudo crear algún flujo");
            return;
        } finally {
            try {
                flujoEntrada.close();
                flujoSalida.close();
                conexion.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
