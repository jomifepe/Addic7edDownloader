package com.jomifepe.addic7eddownloader.model;

public class Content {
    protected Integer contentId;
    protected String title;
    protected String pageURL;

    public Content(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getContentId() {
        return contentId;
    }
}
