package io.krystof.retro_launcher.controller.dto;

import java.util.Objects;

public class ProgramDiskImageDTO {
    private Long id;
    private Integer diskNumber;
    private String imageName;
    private String fileHash;
    private Long fileSize;
    private String storagePath;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getDiskNumber() { return diskNumber; }
    public void setDiskNumber(Integer diskNumber) { this.diskNumber = diskNumber; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }


    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    @Override
    public String toString() {
        return "ProgramDiskImageDTO{" +
                "id=" + id +
                ", diskNumber=" + diskNumber +
                ", imageName='" + imageName + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", fileSize=" + fileSize +
                ", storagePath='" + storagePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramDiskImageDTO that = (ProgramDiskImageDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(diskNumber, that.diskNumber) && Objects.equals(imageName, that.imageName) && Objects.equals(fileHash, that.fileHash) && Objects.equals(fileSize, that.fileSize)  && Objects.equals(storagePath, that.storagePath);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(diskNumber);
        result = 31 * result + Objects.hashCode(imageName);
        result = 31 * result + Objects.hashCode(fileHash);
        result = 31 * result + Objects.hashCode(fileSize);
        result = 31 * result + Objects.hashCode(storagePath);
        return result;
    }
}