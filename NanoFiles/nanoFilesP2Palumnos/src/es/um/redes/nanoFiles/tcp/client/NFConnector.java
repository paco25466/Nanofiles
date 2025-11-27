package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

/**
 * Conector TCP cliente para comunicación con peers servidores.
 * Establece conexiones TCP con servidores de ficheros remotos
 * y proporciona streams para el intercambio de mensajes y
 * la descarga de ficheros.
 * 
 * @author Redes
 */
public class NFConnector {
	public Socket socket;
	private InetSocketAddress serverAddr;
 
	
	public DataInputStream dis = null;
	public DataOutputStream dos = null;


	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;


		/*
		 * TODO: (Boletín SocketsTCP) Se crea el socket a partir de la dirección del
		 * servidor (IP, puerto). La creación exitosa del socket significa que la
		 * conexión TCP ha sido establecida.
		 */
		socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
		/*
		 * TODO: (Boletín SocketsTCP) Se crean los DataInputStream/DataOutputStream a
		 * partir de los streams de entrada/salida del socket creado. Se usarán para
		 * enviar (dos) y recibir (dis) datos del servidor.
		 */

		 dis = new DataInputStream(socket.getInputStream());
		 dos = new DataOutputStream(socket.getOutputStream());

	}

	public void test() {
		/*
		 * TODO: (Boletín SocketsTCP) Enviar entero cualquiera a través del socket y
		 * después recibir otro entero, comprobando que se trata del mismo valor.
		 */

		 try {
			int intReceived ;
			int intToSend = 10;
			System.out.println("Int to send : " + intToSend);
			dos.writeInt(intToSend);
			intReceived = dis.readInt();
			System.out.println("Int received : " + intReceived);

			socket.close();
		 } catch (Exception e) {
			System.out.println("Error in teskkkkkk " + e.getMessage());
			
		 }
	}





	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
