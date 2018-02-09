package com.smokinmonkey.vgangapp.utilities;

/**
 * Created by smokinMonkey on 2/1/2018.
 */

public class Blog {

    private String mTitle;
    private String mDescription;
    private String mName;
    private String mPhoto;

    public Blog() { } ;

    public Blog(String title, String description, String name, String photo) {
        this.mTitle = title;
        this.mDescription = description;
        this.mName = name;
        this.mPhoto = photo;
    }

    public Blog(String title, String description, String name) {
        this.mTitle = title;
        this.mDescription = description;
        this.mName = name;
    }

}
