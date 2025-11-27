package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Servidor de directorio UDP para NanoFiles.
 * Mantiene el registro de peers servidores y los ficheros
 * que comparten. Responde a peticiones de ping, registro
 * de servidores, consulta de ficheros y búsqueda de peers.
 * 
 * @author Redes
 */
public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */

	// Mapa que asocia cada servidor (dirección IP y puerto) con la lista de archivos que ha publicado.
	// Clave: InetSocketAddress (IP y puerto del servidor).
	// Valor: LinkedList<FileInfo> (Lista de archivos compartidos por el servidor).
	private HashMap<InetSocketAddress, LinkedList<FileInfo>> filesByServer;

	// Mapa que asocia el identificador del servidor con su dirección IP y puerto.
	// Clave: String (Nombre o ID único del servidor).
	// Valor: InetSocketAddress (IP y puerto del servidor).
	private HashMap<String, InetSocketAddress> registeredServers;


	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		  TODO: (Boletín SocketsUDP) Inicializar el atributo socket: Crear un socket
		 * UDP ligado al puerto especificado por el argumento directoryPort en la
		 * máquina local,
		 */
		socket = new DatagramSocket(DIRECTORY_PORT);



		/*
		  TODO: (Boletín SocketsUDP) Inicializar atributos que mantienen el estado del
		 * servidor de directorio: ficheros, etc.)
		 */
		filesByServer = new HashMap<>();
  		registeredServers = new HashMap<>();




		if (NanoFiles.testModeUDP) {
			if (socket == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public DatagramPacket receiveDatagram() throws IOException {
		DatagramPacket datagramReceivedFromClient = null;
		boolean datagramReceived = false;
		while (!datagramReceived) {
			/*
			  TODO: (Boletín SocketsUDP) Crear un búfer para recibir datagramas y un
			 * datagrama asociado al búfer (datagramReceivedFromClient)
			 */

			 byte buffer[] = new byte[DirMessage.PACKET_MAX_SIZE];
			 datagramReceivedFromClient = new DatagramPacket(buffer, buffer.length);

			/*
			  TODO: (Boletín SocketsUDP) Recibimos a través del socket un datagrama
			 */
			socket.receive(datagramReceivedFromClient);
			

			if (datagramReceivedFromClient == null) {
				System.err.println("[testMode] NFDirectoryServer.receiveDatagram: code not yet fully functional.\n"
						+ "Check that all TODOs have been correctly addressed!");
				System.exit(-1);
			} else {
				// Vemos si el mensaje debe ser ignorado (simulación de un canal no confiable)
				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println(
							"Directory ignored datagram from " + datagramReceivedFromClient.getSocketAddress());
				} else {
					datagramReceived = true;
					System.out
							.println("Directory received datagram from " + datagramReceivedFromClient.getSocketAddress()
									+ " of size " + datagramReceivedFromClient.getLength() + " bytes.");
				}
			}

		}

		return datagramReceivedFromClient;
	}

	public void runTest() throws IOException {

		System.out.println("[testMode] Directory starting...");

		System.out.println("[testMode] Attempting to receive 'ping' message...");
		DatagramPacket rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);

		System.out.println("[testMode] Attempting to receive 'ping&PROTOCOL_ID' message...");
		rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);
	}

	private void sendResponseTestMode(DatagramPacket pkt) throws IOException {
		/*
		  TODO: (Boletín SocketsUDP) Construir un String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración.
		 */

		String receivedMessage = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("Data received: " + receivedMessage);

		/*
		  TODO: (Boletín SocketsUDP) Después, usar la cadena para comprobar que su
		 * valor es "ping"; en ese caso, enviar como respuesta un datagrama con la
		 * cadena "pingok". Si el mensaje recibido no es "ping", se informa del error y
		 * se envía "invalid" como respuesta.
		 */
		byte[] data;
   		if (receivedMessage.equals("ping")) {
       		data = "pingok".getBytes();
    	}else {
			data = "invalid".getBytes();
			}
		

		/*
		 * TODO: (Boletín Estructura-NanoFiles) Ampliar el código para que, en el caso
		 * de que la cadena recibida no sea exactamente "ping", comprobar si comienza
		 * por "ping&" (es del tipo "ping&PROTOCOL_ID", donde PROTOCOL_ID será el
		 * identificador del protocolo diseñado por el grupo de prácticas (ver
		 * NanoFiles.PROTOCOL_ID). Se debe extraer el "protocol_id" de la cadena
		 * recibida y comprobar que su valor coincide con el de NanoFiles.PROTOCOL_ID,
		 * en cuyo caso se responderá con "welcome" (en otro caso, "denied").
		 */
		
		if (receivedMessage.startsWith("ping&")  ) {
			if (receivedMessage.substring(5).equals(NanoFiles.PROTOCOL_ID)) {
				data = "welcome".getBytes();
			}
			else {
				data = "denied".getBytes();
			}
		
		}
		
		 DatagramPacket pktToSend = new DatagramPacket(data,data.length, pkt.getAddress(), pkt.getPort());
		 socket.send(pktToSend);


	}

	public void run() throws IOException {

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio
			DatagramPacket rcvDatagram = receiveDatagram();

			sendResponse(rcvDatagram);

		}
	}

	private void sendResponse(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín MensajesASCII) Construir String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración. Después, usar la cadena para construir un objeto
		 * DirMessage que contenga en sus atributos los valores del mensaje. A partir de
		 * este objeto, se podrá obtener los valores de los campos del mensaje mediante
		 * métodos "getter" para procesar el mensaje y consultar/modificar el estado del
		 * servidor.
		 */
		String receivedMessage = new String(pkt.getData(), 0, pkt.getLength());
		//System.out.println("Data received: " + receivedMessage);
		DirMessage msgReceived = DirMessage.fromString(receivedMessage);
		InetSocketAddress clientAddr = (InetSocketAddress) pkt.getSocketAddress();


		/*
		 * TODO: Una vez construido un objeto DirMessage con el contenido del datagrama
		 * recibido, obtener el tipo de operación solicitada por el mensaje y actuar en
		 * consecuencia, enviando uno u otro tipo de mensaje en respuesta.
		 */
		String operation = DirMessageOps.OPERATION_INVALID; // TODO: Cambiar!
		if(msgReceived != null) {
			operation = msgReceived.getOperation();
		}

		/*
		 * TODO: (Boletín MensajesASCII) Construir un objeto DirMessage (msgToSend) con
		 * la respuesta a enviar al cliente, en función del tipo de mensaje recibido,
		 * leyendo/modificando según sea necesario el "estado" guardado en el servidor
		 * de directorio (atributos files, etc.). Los atributos del objeto DirMessage
		 * contendrán los valores adecuados para los diferentes campos del mensaje a
		 * enviar como respuesta (operation, etc.)
		 */
		
		 DirMessage msgToSend = null;

		switch (operation) {
		case DirMessageOps.OPERATION_PING: {


			/*
			 * TODO: (Boletín MensajesASCII) Comprobamos si el protocolId del mensaje del
			 * cliente coincide con el nuestro.
			 */
			String protocolId = msgReceived.getProtocolId();

			/*
			 * TODO: (Boletín MensajesASCII) Construimos un mensaje de respuesta que indique
			 * el éxito/fracaso del ping (compatible, incompatible), y lo devolvemos como
			 * resultado del método.
			 */
			if(protocolId.equals(NanoFiles.PROTOCOL_ID )){
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_OK);
				
			}else {
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_BAD);
			}
			/*
			 * TODO: (Boletín MensajesASCII) Imprimimos por pantalla el resultado de
			 * procesar la petición recibida (éxito o fracaso) con los datos relevantes, a
			 * modo de depuración en el servidor
			 */

			System.out.println("Ping ... received protocolId: " + protocolId);
			break;
		}
		case DirMessageOps.OPERATION_FILELIST: {
            System.out.println("Processing FILELIST request from " + clientAddr);

            // Recopilar todos los ficheros de todos los servidores registrados
            LinkedList<FileInfo> allFiles = new LinkedList<>();
            // filesByServer es HashMap<InetSocketAddress, LinkedList<FileInfo>>
            for (LinkedList<FileInfo> serverFileList : filesByServer.values()) {
                if (serverFileList != null) {
                    allFiles.addAll(serverFileList);
                }
            }

            // Convertir la lista a un array
            FileInfo[] fileListArray = allFiles.toArray(new FileInfo[0]);

            // Crear el mensaje de respuesta 
            msgToSend = new DirMessage(DirMessageOps.OPERATION_FILELIST_RESPONSE, fileListArray);

            System.out.println("-> Responding with FILELIST_RESPONSE containing " + fileListArray.length + " files.");
            break;
        }
		case DirMessageOps.OPERATION_SERVE: {
			System.out.println("Processing serve request from " + clientAddr);
			
			// Extraer el puerto del cliente y la lista de archivos a servir
			int clientPort = msgReceived.getPort();
			LinkedList<FileInfo> serveFiles = msgReceived.getServe();
			
			//Crear la dirección del servidor a partir de la dirección IP del cliente y el puerto
			InetSocketAddress serverAddr = new InetSocketAddress(clientAddr.getAddress(), clientPort);
			
			// Guardar la lista de archivos en el mapa filesByServer
			filesByServer.put(serverAddr, serveFiles);
			
			//Generar un ID  para el servidor
			String serverId = serverAddr.getAddress().getHostAddress() + ":" + serverAddr.getPort();
			
			// Registrar el servidor en el mapa registeredServers
			registeredServers.put(serverId, serverAddr);
			
			System.out.println("-> Server " + serverId + " registered with " + 
							  (serveFiles != null ? serveFiles.size() : 0) + " files");
			
			// Crear el mensaje de respuesta
			msgToSend = new DirMessage(DirMessageOps.OPERATION_SERVE_OK);
			
			break;
		}
		case DirMessageOps.OPERATION_DOWNLOAD: {
			System.out.println("Processing DOWNLOAD request from " + clientAddr);
			
			// Obtener el nombre del archivo solicitado
			String requestedFileName = msgReceived.getFileName();
			
			if (requestedFileName == null || requestedFileName.isEmpty()) {
				System.err.println("-> Error: No filename provided in DOWNLOAD request");
				break;
			}
			
			System.out.println("-> Looking for servers sharing file: " + requestedFileName);
			
			// Lista para almacenar los servidores que comparten el archivo
			LinkedList<String> serverList = new LinkedList<>();
			
			// Buscar en todos los servidores registrados
			for (InetSocketAddress serverAddr : filesByServer.keySet()) {
				LinkedList<FileInfo> serverFiles = filesByServer.get(serverAddr);
				
				if (serverFiles != null) {
					for (FileInfo file : serverFiles) {
						if (file.getName().contains(requestedFileName)) {
							// Añadir el servidor a la lista
							serverList.add(serverAddr.getAddress().getHostAddress() + ":" + serverAddr.getPort());
							break; // Solo añadir el servidor una vez
						}
					}
				}
			}
			
			// Crear el mensaje de respuesta
			msgToSend = new DirMessage(DirMessageOps.OPERATION_DOWNLOAD_OK);

			// Usar el método setter para asignar la lista de servidores
			msgToSend.setServers(serverList);
			
			System.out.println("-> Responding with DOWNLOAD_OK containing " + serverList.size() + " servers");
			break;
		}



		default:
			System.err.println("Unexpected message operation: \"" + operation + "\"");
			System.exit(-1);
		}

		/*
		 * TODO: (Boletín MensajesASCII) Convertir a String el objeto DirMessage
		 * (msgToSend) con el mensaje de respuesta a enviar, extraer los bytes en que se
		 * codifica el string y finalmente enviarlos en un datagrama
		 */

		 if(msgToSend != null) {
			String msgToSendString  = msgToSend.toString();
			System.out.println("Sending response ... " + msgToSendString);
			byte[] data = msgToSendString.getBytes();
			DatagramPacket pktToSend = new DatagramPacket(data, data.length, clientAddr);
			socket.send(pktToSend);


	     }
	}
}