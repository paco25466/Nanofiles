package es.um.redes.nanoFiles.logic;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import es.um.redes.nanoFiles.tcp.client.NFConnector;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.application.NanoFiles;



import es.um.redes.nanoFiles.tcp.server.NFServer;
import es.um.redes.nanoFiles.udp.client.DirectoryConnector;
import es.um.redes.nanoFiles.util.FileDigest;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Controlador de lógica para la comunicación P2P entre peers.
 * Gestiona el servidor de ficheros local, las conexiones con
 * otros peers para descargas y subidas de ficheros, y la
 * transferencia de datos mediante el protocolo TCP.
 * 
 * @author Redes
 */
public class NFControllerLogicP2P {
	/*
	 * TODO: Se necesita un atributo NFServer que actuará como servidor de ficheros
	 * de este peer
	 */
	private NFServer fileServer = null;




	protected NFControllerLogicP2P() {
	}

	/**
	 * Método para ejecutar un servidor de ficheros en segundo plano. Debe arrancar
	 * el servidor en un nuevo hilo creado a tal efecto.
	 * 
	 * @return Verdadero si se ha arrancado en un nuevo hilo con el servidor de
	 *         ficheros, y está a la escucha en un puerto, falso en caso contrario.
	 * 
	 */
	protected boolean startFileServer() {
		boolean serverRunning = false;
		/*
		 * Comprobar que no existe ya un objeto NFServer previamente creado, en cuyo
		 * caso el servidor ya está en marcha.
		 */
		if (fileServer != null) {
			System.err.println("File server is already running");
		} else {

			/*
			 * TODO: (Boletín Servidor TCP concurrente) Arrancar servidor en segundo plano
			 * creando un nuevo hilo, comprobar que el servidor está escuchando en un puerto
			 * válido (>0), imprimir mensaje informando sobre el puerto de escucha, y
			 * devolver verdadero. Las excepciones que puedan lanzarse deben ser capturadas
			 * y tratadas en este método. Si se produce una excepción de entrada/salida
			 * (error del que no es posible recuperarse), se debe informar sin abortar el
			 * programa
			 * 
			 */

			try {
				fileServer = new NFServer();
				fileServer.startServer();
			} catch (IOException e) {
				fileServer = null;
				System.err.println("Cannot start the file server");
				return false;
			}
			int listeningPort = fileServer.getPortServer();
			if (listeningPort >0) {
				serverRunning = true;
				System.out.println("File server is running on port " + listeningPort);
			} else {
				System.err.println("Error: File server is not running");
			}

		}
		return serverRunning;

	}

	protected void testTCPServer() {
		assert (NanoFiles.testModeTCP);
		/*
		 * Comprobar que no existe ya un objeto NFServer previamente creado, en cuyo
		 * caso el servidor ya está en marcha.
		 */
		assert (fileServer == null);
		try {

			fileServer = new NFServer();
			/*
			 * (Boletín SocketsTCP) Inicialmente, se creará un NFServer y se ejecutará su
			 * método "test" (servidor minimalista en primer plano, que sólo puede atender a
			 * un cliente conectado). Posteriormente, se desactivará "testModeTCP" para
			 * implementar un servidor en segundo plano, que se ejecute en un hilo
			 * secundario para permitir que este hilo (principal) siga procesando comandos
			 * introducidos mediante el shell.
			 */
			fileServer.test();
			// Este código es inalcanzable: el método 'test' nunca retorna...
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("Cannot start the file server");
			fileServer = null;
		}
	}

	public void testTCPClient() {

		assert (NanoFiles.testModeTCP);
		/*
		 * (Boletín SocketsTCP) Inicialmente, se creará un NFConnector (cliente TCP)
		 * para conectarse a un servidor que esté escuchando en la misma máquina y un
		 * puerto fijo. Después, se ejecutará el método "test" para comprobar la
		 * comunicación mediante el socket TCP. Posteriormente, se desactivará
		 * "testModeTCP" para implementar la descarga de un fichero desde múltiples
		 * servidores.
		 */

		try {
			NFConnector nfConnector = new NFConnector(new InetSocketAddress(NFServer.PORT));
			nfConnector.test();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param serverAddressList       La lista de direcciones de los servidores a
	 *                                los que se conectará
	 * @param targetFileNameSubstring Subcadena del nombre del fichero a descargar
	 * @param localFileName           Nombre con el que se guardará el fichero
	 *                                descargado
	 */
	






			protected boolean downloadFileFromServers(InetSocketAddress[] serverAddressList, String targetFileNameSubstring,
        String localFileName) throws IOException {
    boolean downloaded = false;

        /*
			*
			*TODO: Crear un objeto NFConnector distinto para establecer una conexión TCP
			*con cada servidor de ficheros proporcionado, y usar dicho objeto para
			*descargar trozos (chunks) del fichero. Se debe comprobar previamente si ya
			*existe un fichero con el mismo nombre (localFileName) en esta máquina, en
			*cuyo caso se informa y no se realiza la descarga. Se debe asegurar que el
			*fichero cuyos datos se solicitan es el mismo para todos los servidores
			*involucrados (el fichero está identificado por su hash). Una vez descargado,
			*se debe comprobar la integridad del mismo calculando el hash mediante
			*FileDigest.computeFileChecksumString. Si todo va bien, imprimir resumen de la
			*descarga informando de los trozos obtenidos de cada servidor involucrado. Las
			*excepciones que puedan lanzarse deben ser capturadas y tratadas en este
			*	método. Si se produce una excepción de entrada/salida (error del que no es
			*	posible recuperarse), se debe informar sin abortar el programa*/



    // Verificar si la lista está vacía
    if (serverAddressList.length == 0) {
        if (!DirectoryConnector.isFileNotFound()) {
            System.err.println("* Cannot start download - No list of server addresses provided");
        }
        return false;
    }

    // Verificar si el archivo ya existe localmente
    File localFile = new File(NanoFiles.sharedDirname + "/" + localFileName);
    if (localFile.exists()) {
        System.err.println("* Cannot start download - File already exists locally: " + localFileName);
        return false;
    }

    // Variables para almacenar metadatos del archivo
    String fileHash = null;
    int fileSize = 0;
    int totalBytesDownloaded = 0;
    int[] bytesFromServer = new int[serverAddressList.length];

    try {
        //  Obtener metadatos del archivo del primer servidor disponible
        for (int i = 0; i < serverAddressList.length && fileHash == null; i++) {
            NFConnector connector = null;
            try {
                connector = new NFConnector(serverAddressList[i]);
                
                // Enviar solicitud de descarga
                PeerMessage downloadRequest = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FILE,
                        (byte) targetFileNameSubstring.length(), targetFileNameSubstring);
                downloadRequest.writeMessageToOutputStream(connector.dos);
                
                // Recibir respuesta con metadatos
                PeerMessage response = PeerMessage.readMessageFromInputStream(connector.dis);
                if (response.getOpcode() == PeerMessageOps.OPCODE_DOWNLOAD_FILE_ACCEPTED) {
                    fileHash = response.getFileHash();
                    fileSize = response.getFileSize();
                    System.out.println("* File found: " + targetFileNameSubstring);
                    System.out.println("* File hash: " + fileHash);
                    System.out.println("* File size: " + fileSize + " bytes");
                }
                
                connector.socket.close();
            } catch (IOException e) {
                System.err.println("* Error connecting to server " + serverAddressList[i] + ": " + e.getMessage());
                if (connector != null && !connector.socket.isClosed()) {
                    connector.socket.close();
                }
            }
        }
        
        if (fileHash == null || fileSize <= 0) {
            System.err.println("* Could not get file metadata from any server");
            return false;
        }
        
        //  Crear array para almacenar todo el archivo en memoria
        byte[] completeFileData = new byte[fileSize];
        
        //  Calcular la distribución de chunks entre servidores
        final int CHUNK_SIZE = 4096;
        int totalChunks = (int) Math.ceil((double) fileSize / CHUNK_SIZE);
        int chunksPerServer = (int) Math.ceil((double) totalChunks / serverAddressList.length);
        boolean[] chunksDownloaded = new boolean[totalChunks];
        
        // Descargar chunks de cada servidor
        for (int serverIndex = 0; serverIndex < serverAddressList.length; serverIndex++) {
            // Determinar qué chunks debe descargar este servidor
            int startChunk = serverIndex * chunksPerServer;
            int endChunk = Math.min(startChunk + chunksPerServer, totalChunks);
            
            if (startChunk >= totalChunks) continue;
            
            NFConnector connector = null;
            try {
                // Crear nueva conexión para cada servidor
                connector = new NFConnector(serverAddressList[serverIndex]);
                
                // Enviar solicitud de descarga
                PeerMessage downloadRequest = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FILE,
                        (byte) targetFileNameSubstring.length(), targetFileNameSubstring);
                downloadRequest.writeMessageToOutputStream(connector.dos);
                
                // Recibir respuesta con metadatos
                PeerMessage response = PeerMessage.readMessageFromInputStream(connector.dis);
                if (response.getOpcode() == PeerMessageOps.OPCODE_DOWNLOAD_FILE_ACCEPTED) {
                    // Verificar que sea el mismo archivo
                    if (!response.getFileHash().equals(fileHash)) {
                        System.err.println("* Server " + serverIndex + " has different file - skipping");
                        connector.socket.close();
                        continue;
                    }
                    
                    // Calcular el rango de bytes para este servidor
                    int startByte = startChunk * CHUNK_SIZE;
                    int endByte = Math.min(endChunk * CHUNK_SIZE, fileSize);
                    int bytesToDownload = endByte - startByte;
                    
                    
                    
                    //  Saltar los bytes iniciales que no necesitamos
                    long bytesToSkip = startByte;
                    while (bytesToSkip > 0) {
                        long skipped = connector.dis.skip(bytesToSkip);
                        if (skipped <= 0) break;
                        bytesToSkip -= skipped;
                    }
                    
                    // Leer solo los bytes que necesitamos
                    int totalRead = 0;
                    int chunkIndex = startChunk;
                    
                    while (totalRead < bytesToDownload && chunkIndex < endChunk) {
                        int currentChunkSize = Math.min(CHUNK_SIZE, bytesToDownload - totalRead);
                        byte[] buffer = new byte[currentChunkSize];
                        
                        int bytesRead = 0;
                        int bufferOffset = 0;
                        
                        // Leer el chunk completo
                        while (bufferOffset < currentChunkSize) {
                            bytesRead = connector.dis.read(buffer, bufferOffset, currentChunkSize - bufferOffset);
                            if (bytesRead <= 0) break;
                            bufferOffset += bytesRead;
                        }
                        
                        if (bufferOffset == currentChunkSize) {
                            // Copiar este chunk al array completo en la posición correcta
                            int destPos = startByte + totalRead;
                            System.arraycopy(buffer, 0, completeFileData, destPos, currentChunkSize);
                            chunksDownloaded[chunkIndex] = true;
                            totalRead += currentChunkSize;
                            
                            System.out.println("* Downloaded chunk " + chunkIndex + " (" + currentChunkSize + 
                                             " bytes) from server " + (serverIndex + 1));
                        } else {
                            System.err.println("* Error downloading chunk " + chunkIndex + 
                                             " (read " + bufferOffset + "/" + currentChunkSize + " bytes)");
                            break;
                        }
                        
                        chunkIndex++;
                    }
                    
                    bytesFromServer[serverIndex] = totalRead;
                    totalBytesDownloaded += totalRead;
                    
                    System.out.println("* Downloaded " + totalRead + "/" + bytesToDownload + 
                                     " bytes from server " + (serverIndex + 1));
                }
                
                // Cerrar esta conexión
                connector.socket.close();
                
            } catch (IOException e) {
                System.err.println("* Error with server " + serverIndex + ": " + e.getMessage());
                if (connector != null && !connector.socket.isClosed()) {
                    connector.socket.close();
                }
            }
        }
        
        //  Verificar que todos los chunks se hayan descargado
        boolean allChunksDownloaded = true;
        for (int i = 0; i < chunksDownloaded.length; i++) {
            if (!chunksDownloaded[i]) {
                allChunksDownloaded = false;
                System.err.println("* Missing chunk " + i);
            }
        }
        
        if (!allChunksDownloaded) {
            System.err.println("* Download incomplete: missing chunks");
            return false;
        }
        
        //  Escribir el archivo completo de una sola vez
        FileOutputStream fos = new FileOutputStream(NanoFiles.sharedDirname + "/" + localFileName);
        fos.write(completeFileData);
        fos.close();
        
        //  Verificar la integridad del archivo
        String downloadedFileHash = FileDigest.computeFileChecksumString(NanoFiles.sharedDirname + "/" + localFileName);
        if (downloadedFileHash.equals(fileHash)) {
            downloaded = true;
            System.out.println("* Download complete: " + NanoFiles.sharedDirname + "/" + localFileName);
            System.out.println("* File integrity verified (hash: " + downloadedFileHash + ")");
            
            // Resumen
            System.out.println("* Download summary:");
            for (int i = 0; i < serverAddressList.length; i++) {
                if (bytesFromServer[i] > 0) {
                    System.out.println("  - Server " + (i+1) + ": " +
                            bytesFromServer[i] + " bytes (" +
                            String.format("%.1f", (bytesFromServer[i] * 100.0 / fileSize)) + "%)");
                }
            }
        } else {
            System.err.println("* File integrity check failed!");
            System.err.println("* Expected hash: " + fileHash);
            System.err.println("* Actual hash: " + downloadedFileHash);
            localFile.delete();
        }
        
    } catch (IOException e) {
        System.err.println("* Download error: " + e.getMessage());
        if (localFile.exists()) {
            localFile.delete();
        }
        throw e;
    }
    
    return downloaded;
}
	/**
	 * Método para obtener el puerto de escucha de nuestro servidor de ficheros
	 * 
	 * @return El puerto en el que escucha el servidor, o 0 en caso de error.
	 */
	protected int getServerPort() {
		int port = 0;
		/*
		 * TODO: Devolver el puerto de escucha de nuestro servidor de ficheros
		 */
		
		port = fileServer.getPortServer();

		return port;
	}

	/**
	 * Método para detener nuestro servidor de ficheros en segundo plano
	 * 
	 */
	protected void stopFileServer() {
		/*
		 * TODO: Enviar señal para detener nuestro servidor de ficheros en segundo plano
		 */



	}

	protected boolean serving() {
		boolean result = false;



		return result;

	}

	protected boolean uploadFileToServer(FileInfo matchingFile, String uploadToServer) {
		boolean result = false;



		return result;
	}

}
