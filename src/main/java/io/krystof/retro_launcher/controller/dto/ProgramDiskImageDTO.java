package io.krystof.retro_launcher.controller.dto;

import java.util.Objects;

public class ProgramDiskImageDTO {
    private Long id;
    private Integer diskNumber;
    private String filePath;
    private String fileHash;
    private Long fileSize;
    private String displayName;
    private String description;
    private String storagePath;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getDiskNumber() { return diskNumber; }
    public void setDiskNumber(Integer diskNumber) { this.diskNumber = diskNumber; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    @Override
    public String toString() {
        return "ProgramDiskImageDTO{" +
                "id=" + id +
                ", diskNumber=" + diskNumber +
                ", filePath='" + filePath + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", fileSize=" + fileSize +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", storagePath='" + storagePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramDiskImageDTO that = (ProgramDiskImageDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(diskNumber, that.diskNumber) && Objects.equals(filePath, that.filePath) && Objects.equals(fileHash, that.fileHash) && Objects.equals(fileSize, that.fileSize) && Objects.equals(displayName, that.displayName) && Objects.equals(description, that.description) && Objects.equals(storagePath, that.storagePath);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(diskNumber);
        result = 31 * result + Objects.hashCode(filePath);
        result = 31 * result + Objects.hashCode(fileHash);
        result = 31 * result + Objects.hashCode(fileSize);
        result = 31 * result + Objects.hashCode(displayName);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(storagePath);
        return result;
    }
}