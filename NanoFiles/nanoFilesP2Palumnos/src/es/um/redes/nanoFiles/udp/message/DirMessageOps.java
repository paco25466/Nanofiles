package es.um.redes.nanoFiles.udp.message;

/**
 * Definición de operaciones del protocolo de mensajes del directorio.
 * Contiene las constantes que identifican los diferentes tipos
 * de mensajes intercambiados entre clientes y el servidor de
 * directorio mediante UDP.
 * 
 * @author Redes
 */
public class DirMessageOps {

	/*
	 * TODO: (Boletín MensajesASCII) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con el
	 * directorio (valores posibles del campo "operation").
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_PING = "ping";

	public static final String OPERATION_PING_OK = "pingOK";
	public static final String OPERATION_PING_BAD = "pingBad";

	public static final String OPERATION_FILELIST = "filelist";
	public static final String OPERATION_FILELIST_RESPONSE = "getfilelist";

	public static final String OPERATION_SERVE = "serve";
	public static final String OPERATION_SERVE_OK = "serveOK";

	public static final String OPERATION_DOWNLOAD = "download";
	public static final String OPERATION_DOWNLOAD_OK = "downloadOK";

}
