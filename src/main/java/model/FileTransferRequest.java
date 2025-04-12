package model;

import java.io.File;
import java.io.Serializable;

public class FileTransferRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fileName;
    private long fileSize;

    public FileTransferRequest(File file) {
        this.setFileName(file.getName());
        this.setFileSize(file.length());
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}