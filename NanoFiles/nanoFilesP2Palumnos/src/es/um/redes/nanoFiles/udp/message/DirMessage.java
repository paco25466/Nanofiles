package es.um.redes.nanoFiles.udp.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes del protocolo de comunicación con el directorio.
 * Estos mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * Soporta operaciones como ping, registro de servidor, consulta de ficheros
 * y búsqueda de servidores.
 * 
 * @author Redes
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	private static final char FILE_ITEM_DELIMITER = ';'; // Define el delimitador para elementos de archivo



	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	/*
	 * TODO: (Boletín MensajesASCII) Definir de manera simbólica los nombres de
	 * todos los campos que pueden aparecer en los mensajes de este protocolo
	 * (formato campo:valor)
	 */ 
	
	private static final String FIELDNAME_PROTOCOL = "protocol";

	private static final String FIELDNAME_FILE = "filelist";

	private static final String FIELDNAME_PORT = "port";

	private static final String FIELDNAME_SERVERS = "servers";

	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/**
	 * Identificador de protocolo usado, para comprobar compatibilidad del directorio.
	 */
	private String protocolId;
	/*
	 * TODO: (Boletín MensajesASCII) Crear un atributo correspondiente a cada uno de
	 * los campos de los diferentes mensajes de este protocolo.
	 */

	private FileInfo[] fileList;

	private int port;
	private LinkedList<FileInfo> files;

	private List<String> servers = new ArrayList<>();
	
	private String fileName;



	public DirMessage(String op) {
		operation = op;
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear diferentes constructores adecuados para
	 * construir mensajes de diferentes tipos con sus correspondientes argumentos
	 * (campos del mensaje)
	 */
	public DirMessage(String op, String fileName) {
		operation = op;
		if (op.equals(DirMessageOps.OPERATION_PING)) {
			protocolId = fileName; 
		} else if (op.equals(DirMessageOps.OPERATION_DOWNLOAD)) {
			this.fileName = fileName;
		}
	}

	



	public String getOperation() {
		return operation;
	}


	// Constructor para FILELIST_RESPONSE
	public DirMessage(String op, FileInfo[] files) {
	    this(op); 
	   
	    if (files == null) {
	        // Si la lista de archivos es nula, inicializarla como un array vacío
	        this.fileList = new FileInfo[0];
	    } else {
	        this.fileList = files;
	    }
	}


	// Constructor para serve
	public DirMessage(String op, int port, LinkedList<FileInfo> files) {
		this.operation = op;
		this.port = port;
		this.files = files;

	}






	/*
	 * TODO: (Boletín MensajesASCII) Crear métodos getter y setter para obtener los
	 * valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public void setProtocolID(String protocolIdent) {
		if (!operation.equals(DirMessageOps.OPERATION_PING)) {
			throw new RuntimeException(
					"DirMessage: setProtocolId called for message of unexpected type (" + operation + ")");
		}
		protocolId = protocolIdent;
	}

	public String getProtocolId() {



		return protocolId;
	}

	public FileInfo[] getFileList() {
		if (!operation.equals(DirMessageOps.OPERATION_FILELIST_RESPONSE)) {
			// Retornar un array vacío si la operación no es FILELIST_RESPONSE
			
			return new FileInfo[0];
		}
		
		return fileList;
	}

	public void setFileList(FileInfo[] files) {
		if (files == null) {
			this.fileList = new FileInfo[0];
		} else {
			this.fileList = files;
		}
	}

	public int getPort() {
		if (!operation.equals(DirMessageOps.OPERATION_SERVE)) {
			throw new RuntimeException(
					"DirMessage: getPort called for message of unexpected type (" + operation + ")");
		}
		return port;
	}
	public void setPort(int port) {
		if (!operation.equals(DirMessageOps.OPERATION_SERVE)) {
			throw new RuntimeException(
					"DirMessage: setPort called for message of unexpected type (" + operation + ")");
		}
		this.port = port;
	}

	public void setServers(List<String> serverList) {
		if (!operation.equals(DirMessageOps.OPERATION_DOWNLOAD_OK)) {
			throw new RuntimeException(
				"DirMessage: setServers called for message of unexpected type (" + operation + ")");
		}
		this.servers = new ArrayList<>(serverList);
	}


	public LinkedList<FileInfo> getServe() {
		if (!operation.equals(DirMessageOps.OPERATION_SERVE)) {
			throw new RuntimeException(
					"DirMessage: getServe called for message of unexpected type (" + operation + ")");
		}
		return files;
	}

	public void insertFile(FileInfo file) {
		if (!operation.equals(DirMessageOps.OPERATION_SERVE)) {
			throw new RuntimeException(
					"DirMessage: insertFile called for message of unexpected type (" + operation + ")");
		}
		if (files == null) {
			files = new LinkedList<>();
		}
		files.add(file);
	}


	public String getFileName() {
		if (operation.equals(DirMessageOps.OPERATION_DOWNLOAD)) {
			return fileName; 
		}
		return null;
	}

	

	public List<String> getServerEntries() {
		if (operation.equals(DirMessageOps.OPERATION_DOWNLOAD_OK)) {
			return servers;
		}
		return new ArrayList<>();
	}



 












	

	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: (Boletín MensajesASCII) Usar un bucle para parsear el mensaje línea a
		 * línea, extrayendo para cada línea el nombre del campo y el valor, usando el
		 * delimitador DELIMITER, y guardarlo en variables locales.
		 */

		
		String[] lines = message.split(END_LINE + "");
		// Variables locales para almacenar el mensaje y la lista de archivos
		DirMessage m = null;
		LinkedList<FileInfo> list = new LinkedList<>();
		
		for (String line : lines) {
			// Ignorar líneas vacías
			if (line.trim().isEmpty()) {
				continue;
			}
			
			try {
				int idx = line.indexOf(DELIMITER); // Posición del delimitador
				String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
				String value = line.substring(idx + 1).trim();
	
				switch (fieldName) {
				case FIELDNAME_OPERATION: {
					assert (m == null);
					m = new DirMessage(value);
					break;
				}
				case FIELDNAME_PROTOCOL: {
					m.setProtocolID(value);
					break;
				}
	
				case FIELDNAME_FILE: {
					
	
					// Dividimos el 'value' usando el delimitador interno ';' (FILE_ITEM_DELIMITER)
					// El límite '4' asegura que si la ruta contiene ';', no se divida más allá de la 4ª parte.
					String[] parts = value.split(FILE_ITEM_DELIMITER + "", 4);
	
					// Verificamos si obtuvimos exactamente 4 partes, como se espera.
					if (parts.length == 4) {
						try {
							// Extraemos las partes según el orden esperado en 'value':
							String name = parts[0]; // Primera parte es el nombre
							long size = Long.parseLong(parts[1]); // Segunda parte es el tamaño (convertido a long)
							String hash = parts[2]; // Tercera parte es el hash
							String path = parts[3]; // Cuarta parte es la ruta
	
							// Creamos el objeto FileInfo 
							
							list.add(new FileInfo(hash, name, size, path));
	
						} catch (NumberFormatException e) {
							// Error si la segunda parte (tamaño) no es un número válido.
							System.err.println("DirMessage.fromString: Invalid file size format in line: " + line);
						} catch (Exception e) {
							// Captura cualquier otro error durante la creación de FileInfo.
							System.err.println("DirMessage.fromString: Error processing file line: " + line + " - " + e.getMessage());
						}
					} else {
						// Error si la línea no contenía exactamente 4 partes separadas por ';'.
						System.err.println("DirMessage.fromString: Invalid file format in line (expected 4 parts separated by '" + DELIMITER + "'): " + line);
					}
					break; 
				}
				case FIELDNAME_PORT: {
					m.setPort(Integer.parseInt(value));
					break;
				}
	
				case FIELDNAME_SERVERS: {
					if (m!=null) {
						m.servers = Arrays.asList(value.split(";"));
					}
					break;
				}
				case "filename": {
					if (m != null) {
						m.fileName = value;
					}
					break;
				}
	
				default:
					System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
					System.err.println("Message was:\n" + message);
					System.exit(-1);
				}
			} catch (StringIndexOutOfBoundsException e) {
				System.err.println("Warning: Line without proper delimiter found: " + line);
				continue; // Saltar esta línea y continuar con la siguiente
			}
		}
	
		if (m != null) {
			if (m.getOperation().equals(DirMessageOps.OPERATION_FILELIST_RESPONSE)) {
				FileInfo[] fileArray = list.toArray(new FileInfo[0]);
				m.setFileList(fileArray);
				System.out.println("Processed " + fileArray.length + " files in FILELIST_RESPONSE");
			} else if (m.getOperation().equals(DirMessageOps.OPERATION_SERVE)) {
				for (FileInfo file : list) {
					m.insertFile(file);
				}
				System.out.println("Processed " + list.size() + " files in SERVE message");
			}
		}
	
		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
		/*
		 * TODO: (Boletín MensajesASCII) En función de la operación del mensaje, crear
		 * una cadena la operación y concatenar el resto de campos necesarios usando los
		 * valores de los atributos del objeto.
		 */
		switch (operation) {
			case DirMessageOps.OPERATION_PING: {
				sb.append(FIELDNAME_PROTOCOL + DELIMITER + protocolId + END_LINE); // Añadimos el campo	
				break;
			}

			case DirMessageOps.OPERATION_FILELIST_RESPONSE: {
				// Incluso si la lista está vacía, añadir una indicación en el mensaje
				if (fileList == null ) {
					break;
				} else {
					for (FileInfo file : fileList) {
						if (file != null) {
							sb.append(FIELDNAME_FILE + DELIMITER
								+ file.getName() + FILE_ITEM_DELIMITER 
								+ file.getSize() + FILE_ITEM_DELIMITER 
								+ file.getHash() + FILE_ITEM_DELIMITER 
								+ file.getPath() + END_LINE);
						}
					}
				}
				break;
			}

			case DirMessageOps.OPERATION_SERVE: {
				sb.append(FIELDNAME_PORT + DELIMITER + port + END_LINE); // Añadimos el campo
				if (files != null) {
					for (FileInfo file : files) {
						if (file != null) {
							// Añadimos el campo de archivo
							sb.append(FIELDNAME_FILE + DELIMITER // Campo principal usa ':'
									+ file.getName() + FILE_ITEM_DELIMITER // Separador interno usa ';'
									+ file.getSize() + FILE_ITEM_DELIMITER // Separador interno usa ';'
									+ file.getHash() + FILE_ITEM_DELIMITER // Separador interno usa ';'
									+ file.getPath() + END_LINE);          // Se añade path y fin de línea
						}
					}
				}
				break;
			}
			 

			case DirMessageOps.OPERATION_DOWNLOAD: {
				if (fileName != null && !fileName.isEmpty()) {
					sb.append("filename" + DELIMITER + fileName + END_LINE);
				}
				break;
			}

			case DirMessageOps.OPERATION_DOWNLOAD_OK: {
				// Añadir información de los archivos si están disponibles
				if (fileList != null) {
					for (FileInfo file : fileList) {
						if (file != null) {
							sb.append(FIELDNAME_FILE + DELIMITER
								+ file.getName() + FILE_ITEM_DELIMITER 
								+ file.getSize() + FILE_ITEM_DELIMITER 
								+ file.getHash() + FILE_ITEM_DELIMITER 
								+ file.getPath() + END_LINE);
						}
					}
				}
				
				// Añadir la lista de servidores que comparten el archivo
				if (servers != null && !servers.isEmpty()) {
					sb.append(FIELDNAME_SERVERS + DELIMITER);
					sb.append(String.join(";", servers));
					sb.append(END_LINE);
				}
				
				break;
			}
			
			



		}
		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}

}
