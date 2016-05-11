package com.mygaadi.imageselectorlibrary.modal;

/**
 * Created by Manmohan on 9/19/2015.
 */
public class HomeItemModal {
    private String text;

    /**
     * Creating the constructor for the class
      * @param text String
     */
    public HomeItemModal(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
