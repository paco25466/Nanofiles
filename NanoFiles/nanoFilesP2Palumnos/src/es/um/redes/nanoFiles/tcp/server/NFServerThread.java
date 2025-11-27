package es.um.redes.nanoFiles.tcp.server;

import java.net.Socket;

/**
 * Hilo de servidor para atender conexiones TCP de clientes.
 * Cada instancia maneja la comunicación con un cliente conectado,
 * permitiendo que el servidor principal continúe aceptando
 * nuevas conexiones de forma concurrente.
 * 
 * @author Redes
 */
public class NFServerThread extends Thread  {
	/*
	 * TODO: Esta clase modela los hilos que son creados desde NFServer y cada uno
	 * de los cuales simplemente se encarga de invocar a
	 * NFServer.serveFilesToClient con el socket retornado por el método accept
	 * (un socket distinto para "conversar" con un cliente)
	 */

	private	Socket socket;

	public NFServerThread(Socket newSocket) {
		socket = newSocket;
	}

	public void run() {
		try {
			NFServer.serveFilesToClient(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
