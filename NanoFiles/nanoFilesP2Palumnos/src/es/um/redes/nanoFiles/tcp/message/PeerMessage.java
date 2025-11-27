package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes binarios del protocolo P2P.
 * Define la estructura y serialización de mensajes intercambiados
 * entre peers para solicitar y transferir ficheros mediante TCP.
 * 
 * @author Redes
 */
public class PeerMessage {

	public static final byte HASH_LENGTH = 40;


	private byte opcode;

	/*
	 * TODO: (Boletín MensajesBinarios) Añadir atributos u otros constructores
	 * específicos para crear mensajes con otros campos, según sea necesario
	 * 
	 */

	 private int fileSize;
	 private String fileHash;
	 private String fileName;
	 private byte longFileName;



	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	public PeerMessage(byte op, int fileSize, String fileHash) {
		opcode = op;
		this.fileSize = fileSize;
		this.fileHash = fileHash;
		
	}
	public PeerMessage(byte op, byte longFileName,String fileName) {
		opcode = op;
		this.fileName = fileName;
		this.longFileName = longFileName;
	}

	/*
	 * TODO: (Boletín MensajesBinarios) Crear métodos getter y setter para obtener
	 * los valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public byte getOpcode() {
		return opcode;
	}

	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		
		this.fileSize = fileSize;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		
		this.fileHash = fileHash;
	}

	public  byte getLongFilename () {
		return longFileName;
	}

	public void setLongFilename (byte longFileName) {
		
		this.longFileName = longFileName;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		
		this.fileName = fileName;
	}

	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: (Boletín MensajesBinarios) En función del tipo de mensaje, leer del
		 * socket a través del "dis" el resto de campos para ir extrayendo con los
		 * valores y establecer los atributos del un objeto DirMessage que contendrá
		 * toda la información del mensaje, y que será devuelto como resultado. NOTA:
		 * Usar dis.readFully para leer un array de bytes, dis.readInt para leer un
		 * entero, etc.
		 */
		byte opcode = dis.readByte();
		PeerMessage message = new PeerMessage(opcode); // Crear mensaje con el opcode correcto
		
		switch (opcode) {
			
			case PeerMessageOps.OPCODE_DOWNLOAD_FILE: {
				byte longFileName = dis.readByte();
				byte[] fileName = new byte[longFileName];
				dis.readFully(fileName);
				message.longFileName = longFileName;
				message.fileName = new String(fileName);
				break;
			}
			case PeerMessageOps.OPCODE_DOWNLOAD_FILE_ACCEPTED: {
				int fileSize = dis.readInt();
				byte[] fileHash = new byte[HASH_LENGTH];
				dis.readFully(fileHash);
				message.fileSize = fileSize;
				message.fileHash = new String(fileHash);
				break;
			}
			default:
				System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
				System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO (Boletín MensajesBinarios): Escribir los bytes en los que se codifica el
		 * mensaje en el socket a través del "dos", teniendo en cuenta opcode del
		 * mensaje del que se trata y los campos relevantes en cada caso. NOTA: Usar
		 * dos.write para leer un array de bytes, dos.writeInt para escribir un entero,
		 * etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {
			
			
			
			case PeerMessageOps.OPCODE_DOWNLOAD_FILE: {
				dos.writeByte(longFileName);
				dos.write(fileName.getBytes());
				break;
			}
			case PeerMessageOps.OPCODE_DOWNLOAD_FILE_ACCEPTED: {
				dos.writeInt(fileSize);
				dos.write(fileHash.getBytes());
				break;
			}




		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}




}
