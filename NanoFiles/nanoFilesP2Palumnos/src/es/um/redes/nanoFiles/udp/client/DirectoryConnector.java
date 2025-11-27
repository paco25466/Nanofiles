package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio.
 * Proporciona la funcionalidad para comunicarse con el servidor de directorio
 * mediante UDP, incluyendo ping, registro de servidor, consulta de ficheros
 * y búsqueda de peers que comparten ficheros.
 * 
 * @author Redes
 */
public class DirectoryConnector {

	
	private static  boolean fileNotFound = false;
    
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;
	/**
	 * Nombre/IP del host donde se ejecuta el directorio
	 */
	private String directoryHostname;

	public static  boolean isFileNotFound() {
		return fileNotFound;
	}





	public DirectoryConnector(String hostname) throws IOException {
		// Guardamos el string con el nombre/IP del host
		directoryHostname = hostname;
		/*
		  TODO: (Boletín SocketsUDP) Convertir el string 'hostname' a InetAddress y
		 * guardar la dirección de socket (address:DIRECTORY_PORT) del directorio en el
		 * atributo directoryAddress, para poder enviar datagramas a dicho destino.
		 */
		InetAddress inetAddress = InetAddress.getByName(hostname);
   		directoryAddress = new InetSocketAddress(inetAddress, DIRECTORY_PORT);
		/*
		  TODO: (Boletín SocketsUDP) Crea el socket UDP en cualquier puerto para enviar
		 * datagramas al directorio
		 */
		socket = new DatagramSocket();	
	}



	

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 */
	private byte[] sendAndReceiveDatagrams(byte[] requestData) {
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response[] = null;
		if (directoryAddress == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP server destination address is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"directoryAddress\"");
			System.exit(-1);

		}
		if (socket == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP socket is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"socket\"");
			System.exit(-1);
		}
		/*
		  TODO: (Boletín SocketsUDP) Enviar datos en un datagrama al directorio y
		 * recibir una respuesta. El array devuelto debe contener únicamente los datos
		 * recibidos, *NO* el búfer de recepción al completo.
		 */
		
		try{
			DatagramPacket datagramPacketToServer = new DatagramPacket(requestData, requestData.length, directoryAddress);
			DatagramPacket datagramPacketFromServer = new DatagramPacket(responseData, responseData.length);

		/*
		  TODO: (Boletín SocketsUDP) Una vez el envío y recepción asumiendo un canal
		 * confiable (sin pérdidas) esté terminado y probado, debe implementarse un
		 * mecanismo de retransmisión usando temporizador, en caso de que no se reciba
		 * respuesta en el plazo de TIMEOUT. En caso de salte el timeout, se debe volver
		 * a enviar el datagrama y tratar de recibir respuestas, reintentando como
		 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones.
		 */
			
			 int contador = MAX_NUMBER_OF_ATTEMPTS;
			 while (contador > 0) {
				 try {
					 socket.send(datagramPacketToServer);
					 socket.setSoTimeout(TIMEOUT);
					 socket.receive(datagramPacketFromServer);
					 break;
					 } catch (SocketTimeoutException e) {
					 contador--;
				 }
			 }
			 if (contador > 0) {
				 System.out.println("Received packet after " + (MAX_NUMBER_OF_ATTEMPTS - contador) + " failed attempts");
			 } else {
				 System.out.println("TIMEOUT");
			 }

		 
		/*
		 * TODO: (Boletín SocketsUDP) Las excepciones que puedan lanzarse al
		 * leer/escribir en el socket deben ser capturadas y tratadas en este método. Si
		 * se produce una excepción de entrada/salida (error del que no es posible
		 * recuperarse), se debe informar y terminar el programa.
		 */
		/*
		 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
		 * SocketTimeoutException es más concreta que IOException.
		 */
		
			response = new byte[datagramPacketFromServer.getLength()];
       		System.arraycopy(responseData, 0, response, 0, datagramPacketFromServer.getLength());
			
			
		 
		 }catch (SocketTimeoutException e) {
            System.err.println("SocketTimeoutException: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }


		if (response != null && response.length == responseData.length) {
			System.err.println("Your response is as large as the datagram reception buffer!!\n"
					+ "You must extract from the buffer only the bytes that belong to the datagram!");
		}
		return response;

	}

	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 */
	public boolean testSendAndReceive() throws IOException {
		/*
		 * TODO: (Boletín SocketsUDP) Probar el correcto funcionamiento de
		 * sendAndReceiveDatagrams. Se debe enviar un datagrama con la cadena "ping" y
		 * comprobar que la respuesta recibida empieza por "pingok". En tal caso,
		 * devuelve verdadero, falso si la respuesta no contiene los datos esperados.
		 */
		boolean success = false;
	
		// Crear el mensaje "ping"
		byte[] requestData = "ping".getBytes();
	
		// Enviar el datagrama y recibir la respuesta
		byte[] responseData = sendAndReceiveDatagrams(requestData);
	
		// Convertir la respuesta a una cadena
		String responseString = new String(responseData, 0, responseData.length);
		System.out.println("Data received: " + responseString);
		// Comprobar si la respuesta empieza por "pingok"
		if (responseString.startsWith("pingok")) {
			success = true;
		}

		
		
		return success;
	}
	

	public String getDirectoryHostname() {
		return directoryHostname;
	}

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que
	 * usa un protocolo compatible. Este método no usa mensajes bien formados.
	 * 
	 * @return Verdadero si
	 */
	public boolean pingDirectoryRaw() {
		boolean success = false;
		/*
		 * TODO: (Boletín EstructuraNanoFiles) Basándose en el código de
		 * "testSendAndReceive", contactar con el directorio, enviándole nuestro
		 * PROTOCOL_ID (ver clase NanoFiles). Se deben usar mensajes "en crudo" (sin un
		 * formato bien definido) para la comunicación.
		 * 
		 * PASOS: 1.Crear el mensaje a enviar (String "ping&protocolId"). 2.Crear un
		 * datagrama con los bytes en que se codifica la cadena : 4.Enviar datagrama y
		 * recibir una respuesta (sendAndReceiveDatagrams). : 5. Comprobar si la cadena
		 * recibida en el datagrama de respuesta es "welcome", imprimir si éxito o
		 * fracaso. 6.Devolver éxito/fracaso de la operación.
		 */
		

		 String message = "ping&" + NanoFiles.PROTOCOL_ID;
		 byte[] requestData = message.getBytes();
		 byte[] responseData = sendAndReceiveDatagrams(requestData);
		 String responseString = new String(responseData, 0, responseData.length);
		 System.out.println("Data received: " + responseString);

		 if (responseString.equals("welcome")) {
			System.out.println("Ping success " );
			 success = true;
		 }else{
			System.out.println("Ping failed: " );
		 }
		return success;
	}

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que es
	 * compatible.
	 * 
	 * @return Verdadero si el directorio está operativo y es compatible
	 */
	public boolean pingDirectory() {
		boolean success = false;
		/*
		  TODO: (Boletín MensajesASCII) Hacer ping al directorio 1.Crear el mensaje a
		 * enviar (objeto DirMessage) con atributos adecuados (operation, etc.) NOTA:
		 * Usar como operaciones las constantes definidas en la clase DirMessageOps :
		 * 2.Convertir el objeto DirMessage a enviar a un string (método toString)
		 * 3.Crear un datagrama con los bytes en que se codifica la cadena : 4.Enviar
		 * datagrama y recibir una respuesta (sendAndReceiveDatagrams). : 5.Convertir
		 * respuesta recibida en un objeto DirMessage (método DirMessage.fromString)
		 * 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */

		DirMessage pingMessage = new DirMessage(DirMessageOps.OPERATION_PING, NanoFiles.PROTOCOL_ID);
		String pingMessageString = pingMessage.toString();
		byte[] requestData = pingMessageString.getBytes();
		byte[] responseData = sendAndReceiveDatagrams(requestData);
		if(responseData != null){
			String responseString = new String (responseData, 0, responseData.length);
			System.out.println("Receiving ...  " + responseString);
			DirMessage responseMessage = DirMessage.fromString(responseString);
			if(responseMessage != null && responseMessage.getOperation().equals(DirMessageOps.OPERATION_PING_OK)){
				success = true;
			}
		}			

		return success;
	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado y
	 * publicar los ficheros que este peer servidor está sirviendo.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @param files      La lista de ficheros que este peer está sirviendo.
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 */
	public boolean registerFileServer(int serverPort, FileInfo[] files) {
		boolean success = false;

		// TODO: Ver TODOs en pingDirectory y seguir esquema similar

		// Convertir el array de FileInfo a una LinkedList
    	LinkedList<FileInfo> fileList = new LinkedList<>(Arrays.asList(files));

    	// Crear un mensaje DirMessage para la operación serve
    	DirMessage serveDirMessage = new DirMessage(DirMessageOps.OPERATION_SERVE, serverPort, fileList);

    	// Convertir el mensaje a bytes y enviarlo al directorio
   	 	byte[] serveResponse = sendAndReceiveDatagrams(serveDirMessage.toString().getBytes());

    	// Verificar si se recibió una respuesta
    	if (serveResponse == null) {
        	System.out.println("No response received from the directory.");
        	return false;
    	}
		// Convertir la respuesta en un objeto DirMessage
		DirMessage response = DirMessage.fromString(new String(serveResponse));
		
		// Verificar si la operación en la respuesta es la esperada
		if (!response.getOperation().equals(DirMessageOps.OPERATION_SERVE_OK)) {
			System.out.println("Unexpected response operation: " + response.getOperation());
			return false;
		}
	
		// Si todo es correcto, marcar la operación como exitosa
		success = true;

		return success;
	}

	/**
	 * Método para obtener la lista de ficheros que los peers servidores han
	 * publicado al directorio. Para cada fichero se debe obtener un objeto FileInfo
	 * con nombre, tamaño y hash. Opcionalmente, puede incluirse para cada fichero,
	 * su lista de peers servidores que lo están compartiendo.
	 * 
	 * @return Los ficheros publicados al directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 */
	public FileInfo[] getFileList() {
		FileInfo[] filelist = new FileInfo[0];
		// TODO: Ver TODOs en pingDirectory y seguir esquema similar

		DirMessage requestMessage = new DirMessage(DirMessageOps.OPERATION_FILELIST);

		// Convertir a String 
		String requestString = requestMessage.toString();
		System.out.println("Sending getFileList request:\n" + requestString);

		//  Convertir a bytes 
		byte[] requestData = requestString.getBytes();

		
		byte[] responseData = sendAndReceiveDatagrams(requestData);

		
		if (responseData != null) {
			//  Convertir bytes de respuesta a String 
			
			String responseString = new String(responseData, 0, responseData.length);
			System.out.println("Receiving response for getFileList:\n" + responseString);

			// Convertir String a objeto DirMessage 
			DirMessage responseMessage = DirMessage.fromString(responseString);

			//  Validar el mensaje de respuesta 
			
			if (responseMessage != null && responseMessage.getOperation().equals(DirMessageOps.OPERATION_FILELIST_RESPONSE)) {
				// Extraer el resultado 
				
				FileInfo[] extractedList = responseMessage.getFileList();
				if (extractedList != null) {
					// Asignamos la lista extraída si no es null
					filelist = extractedList;
					System.out.println("Successfully parsed file list response. Found " + filelist.length + " files.");
				} else {
					System.err.println("getFileList: Response operation OK, but failed to extract file list data.");
					// Mantenemos filelist como array vacío
				}
			} else {
				// El mensaje no se pudo parsear o la operación no era la esperada
				System.err.println("getFileList: Received invalid or unexpected response message.");
				if (responseMessage != null) {
					System.err.println("  Expected operation " + DirMessageOps.OPERATION_FILELIST_RESPONSE + " but got " + responseMessage.getOperation());
				}
				// Mantenemos filelist como array vacío
			}
		} else {
			// No se recibió respuesta del servidor
			System.err.println("getFileList: No response received from directory.");
			// Mantenemos filelist como array vacío
		}

		//  Devolver el resultado (el array de ficheros o un array vacío)
		return filelist;


	}

	/**
	 * Método para obtener la lista de servidores que tienen un fichero cuyo nombre
	 * contenga la subcadena dada.
	 * 
	 * @filenameSubstring Subcadena del nombre del fichero a buscar
	 * 
	 * @return La lista de direcciones de los servidores que han publicado al
	 *         directorio el fichero indicado. Si no hay ningún servidor, devuelve
	 *         una lista vacía.
	 */
	public InetSocketAddress[] getServersSharingThisFile(String filenameSubstring) {
		// TODO: Ver TODOs en pingDirectory y seguir esquema similar
		fileNotFound = false;
        
		InetSocketAddress[] serversList = new InetSocketAddress[0];
	
		//  Obtener la lista completa de archivos del directorio
		FileInfo[] allFiles = getFileList();
		
		if (allFiles == null || allFiles.length == 0) {
			System.err.println("Error: No se encontraron archivos en el directorio.");
			fileNotFound = true;
			return serversList; // Retornar lista vacía
		}
		
		//  Usar FileInfo.lookupFilenameSubstring para filtrar los archivos que coinciden
		FileInfo[] matchingFiles = FileInfo.lookupFilenameSubstring(allFiles, filenameSubstring);
		
		if (matchingFiles == null || matchingFiles.length == 0) {
			System.err.println("Error: No se encontró ningún fichero que contenga: " + filenameSubstring);
			fileNotFound = true;
			return serversList; // Retornar lista vacía
		}
		
		// Verificar si hay ambigüedad 
		if (matchingFiles.length > 1) {
			// Verificar si todos los archivos tienen el mismo hash
			String firstHash = matchingFiles[0].fileHash;
			boolean sameHash = true;
			
			for (int i = 1; i < matchingFiles.length; i++) {
				if (!matchingFiles[i].fileHash.equals(firstHash)) {
					sameHash = false;
					break;
				}
			}
			
			if (!sameHash) {
				System.err.println("Error: La subcadena es ambigua. Se encontraron varios ficheros:\n");
				for (FileInfo file : matchingFiles) {
					System.out.println("  - " + file.fileName + " (Hash: " + file.fileHash + ")");
				}
				fileNotFound = true;
				return serversList;
			}
		}
		
		// Si llegamos aquí, o hay un solo archivo o todos tienen el mismo hash
		String uniqueMatch = matchingFiles[0].fileName;
		System.out.println("Using file: " + uniqueMatch);
		
		//  Crear un mensaje para solicitar los servidores que comparten este archivo
		DirMessage requestMessage = new DirMessage(DirMessageOps.OPERATION_DOWNLOAD, uniqueMatch);
		
		//  Convertir a String
		String requestString = requestMessage.toString();
		System.out.println("Sending getServersSharing request for '" + uniqueMatch + "'");
		
		//  Convertir a bytes
		byte[] requestData = requestString.getBytes();
		
		//  Enviar y recibir
		byte[] responseData = sendAndReceiveDatagrams(requestData);
		
		//  Procesar la respuesta si se recibió
		if (responseData != null) {
			String responseString = new String(responseData, 0, responseData.length);
			System.out.println("Receiving response for getServersSharing:\n" + responseString);
			
			//  Convertir String a objeto DirMessage
			DirMessage responseMessage = DirMessage.fromString(responseString);
			
			//  Validar el mensaje de respuesta
			if (responseMessage != null && responseMessage.getOperation().equals(DirMessageOps.OPERATION_DOWNLOAD_OK)) {
				//  Extraer las direcciones de los servidores
				List<String> serverEntries = responseMessage.getServerEntries();
				List<InetSocketAddress> servers = new ArrayList<>();
				
				if (serverEntries != null && !serverEntries.isEmpty()) {
					for (String serverEntry : serverEntries) {
						String[] addrParts = serverEntry.split(":");
						
						if (addrParts.length == 2) {
							try {
								String host = addrParts[0];
								int port = Integer.parseInt(addrParts[1]);
								servers.add(new InetSocketAddress(host, port));
							} catch (NumberFormatException e) {
								System.err.println("Error parsing port number in: " + serverEntry);
							}
						}
					}
					
					serversList = servers.toArray(new InetSocketAddress[0]);
					System.out.println("Encontrados " + serversList.length + " servidores para el archivo: " + uniqueMatch);
				} else {
					System.out.println("No hay servidores disponibles para el archivo: " + uniqueMatch);
				}
			} else {
				System.err.println("getServersSharing: Received invalid or unexpected response message.");
				if (responseMessage != null) {
					System.err.println("  Expected operation " + DirMessageOps.OPERATION_DOWNLOAD_OK + 
									  " but got " + responseMessage.getOperation());
				}
			}
		} else {
			System.err.println("getServersSharing: No response received from directory.");
		}
		
		return serversList;
	}

	/**
	 * Método para darse de baja como servidor de ficheros.
	 * 
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y ha dado de baja sus ficheros.
	 */
	public boolean unregisterFileServer() {
		boolean success = false;




		return success;
	}




}
