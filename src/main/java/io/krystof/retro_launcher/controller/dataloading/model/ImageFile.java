package io.krystof.retro_launcher.controller.dataloading.model;

public class ImageFile {
    private String path;
    private String type;
    private Integer diskNumber;

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getDiskNumber() { return diskNumber; }
    public void setDiskNumber(Integer diskNumber) { this.diskNumber = diskNumber; }
}
