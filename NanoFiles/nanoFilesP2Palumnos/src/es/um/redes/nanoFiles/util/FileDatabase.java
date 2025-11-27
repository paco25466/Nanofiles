package es.um.redes.nanoFiles.util;

import java.io.File;
import java.util.Map;

/**
 * Base de datos de ficheros locales compartidos por este peer.
 * Escanea la carpeta compartida y mantiene un registro de todos
 * los ficheros disponibles con su metadata (nombre, tamaño, hash).
 * 
 * @author Redes
 */

public class FileDatabase {

	private Map<String, FileInfo> files;

	public FileDatabase(String sharedFolder) {
		File theDir = new File(sharedFolder);
		if (!theDir.exists()) {
			theDir.mkdirs();
		}
		this.files = FileInfo.loadFileMapFromFolder(new File(sharedFolder));
		if (files.size() == 0) {
			System.err.println("*WARNING: No files found in folder " + sharedFolder);
		}
	}

	public FileInfo[] getFiles() {
		FileInfo[] fileinfoarray = new FileInfo[files.size()];
		int numFiles = 0;
		for (FileInfo f : files.values()) {
			fileinfoarray[numFiles++] = f;
		}
		return fileinfoarray;
	}

	public String lookupFilePath(String fileHash) {
		FileInfo f = files.get(fileHash);
		if (f != null) {
			return f.filePath;
		}
		return null;
	}
}
