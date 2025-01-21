package io.krystof.retro_launcher.controller.dataloading.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSDbReleaseDetail {
    private String title;
    private String url;
    private int releaseId;
    private double rating;
    private int votes;
    private List<CSDbGroup> groups = new ArrayList<>();
    private List<CSDbDownload> downloads = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private List<ImageFile> images = new ArrayList<>();

    public List<ImageFile> getImages() {
        return images;
    }

    public void setImages(List<ImageFile> images) {
        this.images = images;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(int releaseId) {
        this.releaseId = releaseId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public List<CSDbGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CSDbGroup> groups) {
        this.groups = groups;
    }

    public List<CSDbDownload> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<CSDbDownload> downloads) {
        this.downloads = downloads;
    }
}
