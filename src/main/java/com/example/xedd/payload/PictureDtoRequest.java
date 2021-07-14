package com.example.xedd.payload;

import org.springframework.web.multipart.MultipartFile;

public class PictureDtoRequest {
    private long itemId;
    private MultipartFile picture;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }
}
