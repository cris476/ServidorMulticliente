package com.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServidorMultihilo {

    public static final int PUERTO = 4444;
    private final Map<String, Hilo> clientes = new HashMap<>();

    public static void main(String[] args) {
        ServidorMultihilo servidor = new ServidorMultihilo();
        servidor.iniciarServidor();
    }

    public void iniciarServidor() {
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);

            while (true) {
                try {
                    Socket socketCliente = socketServidor.accept();
                    System.out.println("Cliente conectado desde " + socketCliente.getInetAddress() + ":"
                            + socketCliente.getPort());
                    Hilo clienteHilo = new Hilo(socketCliente, this);
                    new Thread(clienteHilo).start();

                } catch (IOException e) {
                    System.out.println("Error al aceptar la conexión: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("No se puede escuchar en el puerto: " + PUERTO);
            System.exit(-1);
        }
    }

    public synchronized void eliminarClienteMapValor(Hilo hilo) {
        clientes.entrySet().removeIf(entry -> entry.getValue().equals(hilo));
    }

    public synchronized void addCliente(String nickname, Hilo hilo) {
        clientes.put(nickname, hilo);
    }

    public synchronized void mensajesPrivado(String nickname, String mensaje, Hilo hilo) {
        String userDirection = getUserMe(hilo);
        for (String clave : clientes.keySet()) {
            if (clave.trim().equals(nickname.trim())) {
                Hilo cliente = clientes.get(clave);
                cliente.enviarMensaje("PRV " + userDirection + "," + mensaje);
            }
        }
    }

    public synchronized String getUserMe(Hilo hilo) {
        for (String clave : clientes.keySet()) {
            if (clientes.get(clave) == hilo) {
                return clave;
            }
        }
        return "";
    }

    public synchronized String allUsers() {
        return "LST " + String.join(",", clientes.keySet());
    }

    public synchronized Map<String, Hilo> getListUser() {
        return clientes;
    }

    public synchronized void mensajesTodos(String mensaje) {
        for (Hilo cliente : clientes.values()) {
            cliente.enviarMensaje(mensaje);
        }
    }

    public synchronized void mensajesTodosExit(String mensaje, Hilo hilo) {
        String mensajeExit = "";
        for (String clave : clientes.keySet()) {
            Hilo cliente = clientes.get(clave);
            if (cliente == hilo) {
                mensajeExit = mensaje + clave;
                break;
            }
        }
        mensajesTodos(mensajeExit);
    }

    public synchronized boolean userExist(String user) {
        for (String clave : clientes.keySet()) {
            if (clave.trim().equalsIgnoreCase(user.trim())) {
                return true;
            }
        }
        return false;
    }
}
