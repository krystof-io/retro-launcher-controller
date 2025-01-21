package io.krystof.retro_launcher.controller.dataloading.model;

import java.util.Objects;

public class CSDbRelease {
    private String title;
    private String url;
    private double rating;
    private int votes;

    private String groupName;
    private int releaseId;

    private CSDbRelease(Builder builder) {
        this.title = builder.title;
        this.url = builder.url;
        this.rating = builder.rating;
        this.votes = builder.votes;

        this.groupName = builder.groupName;
        this.releaseId = builder.releaseId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String url;
        private double rating;
        private int votes;

        private String groupName;
        private int releaseId;


        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder rating(double rating) {
            this.rating = rating;
            return this;
        }
        public Builder releaseId(int releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        public Builder votes(int votes) {
            this.votes = votes;
            return this;
        }


        public Builder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public CSDbRelease build() {
            return new CSDbRelease(this);
        }
    }

    @Override
    public String toString() {
        return "CSDbRelease{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", rating=" + rating +
                ", votes=" + votes +

                ", groupName='" + groupName + '\'' +
                ", releaseId=" + releaseId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CSDbRelease that = (CSDbRelease) o;
        return Double.compare(rating, that.rating) == 0 && votes == that.votes && releaseId == that.releaseId && Objects.equals(title, that.title) && Objects.equals(url, that.url) && Objects.equals(groupName, that.groupName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(url);
        result = 31 * result + Double.hashCode(rating);
        result = 31 * result + votes;

        result = 31 * result + Objects.hashCode(groupName);
        result = 31 * result + releaseId;
        return result;
    }

    public int getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(int releaseId) {
        this.releaseId = releaseId;
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


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}
