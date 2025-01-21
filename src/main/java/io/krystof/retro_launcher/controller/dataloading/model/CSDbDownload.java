package io.krystof.retro_launcher.controller.dataloading.model;

public class CSDbDownload {
    public String href;
    public String linkText;

    public CSDbDownload() {
    }

    public CSDbDownload(String href, String linkText) {
        this.href = href;
        this.linkText = linkText;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    @Override
    public String toString() {
        return "CSDbDownload{" +
                "href='" + href + '\'' +
                ", linkText='" + linkText + '\'' +
                '}';
    }
}
