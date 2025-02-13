package com.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Hilo implements Runnable {
    private Socket socketCliente;
    private DataOutputStream salida;
    private ServidorMultihilo servidor;
    private Boolean connection = true;

    public Hilo(Socket socketCliente, ServidorMultihilo servidor) {
        this.socketCliente = socketCliente;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try (DataInputStream entrada = new DataInputStream(socketCliente.getInputStream())) {
            salida = new DataOutputStream(socketCliente.getOutputStream());
            String mensaje;
            while (connection) {
                mensaje = entrada.readUTF();
                System.out.println("Cliente (" + socketCliente.getInetAddress()
                        + "): " + mensaje);

                String comando = (mensaje != null && mensaje.length() >= 3) ? mensaje.substring(0, 3) : "";
                String input;

                switch (comando.toLowerCase()) {
                    case "con":
                        input = (mensaje != null && mensaje.length() >= 5) ? mensaje.substring(4) : "";

                        if (servidor.userExist(input)) {
                            enviarMensaje("NOK");
                            connection = false;
                        } else {
                            servidor.addCliente(input, this);
                            enviarMensaje("OK");
                            try {

                                Thread.sleep(200);
                                servidor.mensajesTodos(servidor.allUsers());
                                Thread.sleep(200);
                                servidor.mensajesTodos("CHT " + input
                                        + ",se ha conectado " + input);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                    case "msg":
                        input = (mensaje != null && mensaje.length() >= 5) ? mensaje.substring(4) : "";

                        for (String clave : servidor.getListUser().keySet()) {
                            Hilo cliente = servidor.getListUser().get(clave);
                            if (this == cliente) {
                                servidor.mensajesTodos("CHT " + clave + "," + input);
                            }
                        }
                        break;
                    case "lus":
                        servidor.mensajesTodos(servidor.allUsers());
                        break;
                    case "exi":
                        servidor.mensajesTodosExit("EXI ", this);
                        connection = false;
                        break;
                    case "prv":
                        String[] mensajePrivado = mensaje.split(",");
                        input = (mensaje != null && mensaje.length() >= 5) ? mensajePrivado[0].substring(4) : "";

                        if (servidor.userExist(input)) {
                            servidor.mensajesPrivado(input, mensajePrivado[1], this);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al manejar la conexión del cliente: " + e.getMessage());
        } finally {
            try {
                socketCliente.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
            System.out.println("Cliente desconectado.");
            servidor.eliminarClienteMapValor(this);
        }
    }

    public void enviarMensaje(String mensaje) {
        try {
            System.out.println("Enviando: " + mensaje);
            salida.writeUTF(mensaje);
        } catch (IOException e) {
            System.out.println("Error enviando mensaje: " + e.getMessage());
        }
    }
}