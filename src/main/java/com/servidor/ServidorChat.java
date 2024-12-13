package com.servidor;

public class ServidorChat {

    public static void main(String[] args) {
        ServidorChat servidorChat = new ServidorChat();
        new Thread(new LanzaServidor(servidorChat)).start();
    }

    public void escribirTexto(String s) {
        System.out.println(s + "\n");
    }

}
