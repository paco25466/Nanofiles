package es.um.redes.nanoFiles.tcp.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import javax.xml.crypto.Data;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;


/**
 * Servidor TCP de ficheros para NanoFiles.
 * Escucha conexiones de otros peers y sirve los ficheros
 * compartidos localmente. Soporta múltiples clientes
 * simultáneos mediante hilos concurrentes.
 * 
 * @author Redes
 */
public class NFServer implements Runnable {

	public static final int PORT = 10000;



	private ServerSocket serverSocket = null;

	public NFServer() throws IOException {
		/*
		 * TODO: (Boletín SocketsTCP) Crear una direción de socket a partir del puerto
		 * especificado (PORT)
		 */


		 InetSocketAddress socketAddress = new InetSocketAddress(0);
		/*
		 * TODO: (Boletín SocketsTCP) Crear un socket servidor y ligarlo a la dirección
		 * de socket anterior
		 */
		 serverSocket = new ServerSocket();
		 serverSocket.bind(socketAddress);


	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación.
	 * 
	 */
	public void test() {
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"[fileServerTestMode] Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("[fileServerTestMode] NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}

		while (true) {
			/*
			 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
			 * otros peers que soliciten descargar ficheros.
			 */
			Socket socket = null;

			try {
				socket = serverSocket.accept();
				serveFilesToClient(socket);
			} catch (IOException e) {
				System.err.println("Connection error  " + e.getMessage());
			}
			/*
			 * TODO: (Boletín SocketsTCP) Tras aceptar la conexión con un peer cliente, la
			 * comunicación con dicho cliente para servir los ficheros solicitados se debe
			 * implementar en el método serveFilesToClient, al cual hay que pasarle el
			 * socket devuelto por accept.
			 */
			 
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

				int intReceived = dis.readInt();
				System.out.println("Received int: " + intReceived);
				int intToSend = intReceived ;
				System.out.println("Sending int: " + intToSend);
				dos.writeInt(intToSend);
				
				socket.close();
			} catch (Exception e) {
				System.out.println("Error in testttttttttt " + e.getMessage());
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}



		}
	}

	/**
	 * Método que ejecuta el hilo principal del servidor en segundo plano, esperando
	 * conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
		 * otros peers que soliciten descargar ficheros
		 */
		while (true) {
			boolean connection = false;
			Socket socket = null;
			
		
		/*
		 * TODO: (Boletín SocketsTCP) Al establecerse la conexión con un peer, la
		 * comunicación con dicho cliente se hace en el método
		 * serveFilesToClient(socket), al cual hay que pasarle el socket devuelto por
		 * accept
		 */
		try {
			socket = serverSocket.accept();
			connection = true;
		}catch (IOException e) {
			System.err.println("conection failed: " + e.getMessage());
		}
		/*
		 * TODO: (Boletín TCPConcurrente) Crear un hilo nuevo de la clase
		 * NFServerThread, que llevará a cabo la comunicación con el cliente que se
		 * acaba de conectar, mientras este hilo vuelve a quedar a la escucha de
		 * conexiones de nuevos clientes (para soportar múltiples clientes). Si este
		 * hilo es el que se encarga de atender al cliente conectado, no podremos tener
		 * más de un cliente conectado a este servidor.
		 */
		if (connection) {
			System.out.println("Connection accepted " + socket.getInetAddress());
			NFServerThread connectionThread = new NFServerThread(socket); 
			connectionThread.start(); 
		}

		}




	}
	/*
	 * TODO: (Boletín SocketsTCP) Añadir métodos a esta clase para: 1) Arrancar el
	 * servidor en un hilo nuevo que se ejecutará en segundo plano 2) Detener el
	 * servidor (stopserver) 3) Obtener el puerto de escucha del servidor etc.
	 */
	public void startServer(){
		new Thread(this).start();
	}


	public int getPortServer() {
		if (serverSocket != null ) {
			return serverSocket.getLocalPort();
		} else {
			
			return 0;
		}
	}



	/**
	 * Método de clase que implementa el extremo del servidor del protocolo de
	 * transferencia de ficheros entre pares.
	 * 
	 * @param socket El socket para la comunicación con un cliente que desea
	 *               descargar ficheros.
	 */
	public static void serveFilesToClient(Socket socket) throws IOException {
		/*
		 * TODO: (Boletín SocketsTCP) Crear dis/dos a partir del socket
		 */
		
		
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

			
			
		/*
		 * TODO: (Boletín SocketsTCP) Mientras el cliente esté conectado, leer mensajes
		 * de socket, convertirlo a un objeto PeerMessage y luego actuar en función del
		 * tipo de mensaje recibido, enviando los correspondientes mensajes de
		 * respuesta.
		 */
	

    	while (true) {
			try {
				PeerMessage message = PeerMessage.readMessageFromInputStream(dis);
				byte opcode = message.getOpcode();
				switch (opcode) {
				
        	
    	
		/*
		 * TODO: (Boletín SocketsTCP) Para servir un fichero, hay que localizarlo a
		 * partir de su hash (o subcadena) en nuestra base de datos de ficheros
		 * compartidos. Los ficheros compartidos se pueden obtener con
		 * NanoFiles.db.getFiles(). Los métodos lookupHashSubstring y
		 * lookupFilenameSubstring de la clase FileInfo son útiles para buscar ficheros
		 * coincidentes con una subcadena dada del hash o del nombre del fichero. El
		 * método lookupFilePath() de FileDatabase devuelve la ruta al fichero a partir
		 * de su hash completo.
		 * 
		 *
		 */


					case PeerMessageOps.OPCODE_DOWNLOAD_FILE:
						String filename = message.getFileName();
						System.out.println("Filename: " + filename);
						FileInfo[] matchingFiles = FileInfo.lookupFilenameSubstring(NanoFiles.db.getFiles(), filename);

						FileInfo fileToSend = matchingFiles[0];
						System.out.println("Sending file: " + fileToSend.getName() + " (Hash: " + fileToSend.getHash() + ")");
						
						// Send file metadata
						PeerMessage fileMetadataMessage = new PeerMessage(
							PeerMessageOps.OPCODE_DOWNLOAD_FILE_ACCEPTED, 
							(int)fileToSend.getSize(), 
							fileToSend.getHash()  
						);
						fileMetadataMessage.writeMessageToOutputStream(dos);



						try {
								File file = new File(fileToSend.getPath());
								FileInputStream fis = new FileInputStream(file);
								
								// Buffer para leer el archivo
								byte[] buffer = new byte[4096];
								int bytesRead;
								
								// Leer el archivo y enviarlo al cliente
								while ((bytesRead = fis.read(buffer)) != -1) {
									dos.write(buffer, 0, bytesRead);
								}
								
								// Cerrar el stream del archivo
								fis.close();
								
								System.out.println("File sent successfully: " + fileToSend.getName());
								
							} catch (IOException e) {
								
							}

						break;
				
					default:
						break;
				}

			}
			 catch (IOException e) {
				socket.close();
			}

	}


	}
}