package io.krystof.retro_launcher.controller.dataloading.model;

public class CSDbGroup {
    public String id;
    public String name;
    public String websiteUrl;  // Optional, for the [web] links

    public CSDbGroup() {
    }
    public CSDbGroup(String id, String name, String websiteUrl) {
        this.id = id;
        this.name = name;
        this.websiteUrl = websiteUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Override
    public String toString() {
        return "CSDbGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                '}';
    }
}
