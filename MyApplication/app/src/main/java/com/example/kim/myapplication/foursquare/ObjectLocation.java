package com.example.kim.myapplication.foursquare;


public class ObjectLocation {
    public String nameLocation;
    public ObjectLatLng mObjLatLng;
    public String addVenues;
    public String linkIcon;
    public String distance;

    public ObjectLocation() {
        super();
    }

    public ObjectLocation(String nameLocation, ObjectLatLng mObjLatLng, String addVenues, String linkIcon, String distance) {
        super();
        this.setNameLocation(nameLocation);
        this.setmObjLatLng(mObjLatLng);
        this.setAddVenues(addVenues);
        this.setLinkIcon(linkIcon);
        this.setDistance(distance);
    }

    //@@@
    public String getNameLocation() {
        return nameLocation;
    }
    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }

    //@@@
    public ObjectLatLng getmObjLatLng() {
        return mObjLatLng;
    }
    public void setmObjLatLng(ObjectLatLng mObjLatLng) {
        this.mObjLatLng = mObjLatLng;
    }

    //@@@
    public String getAddVenues() {
        return addVenues;
    }
    public void setAddVenues(String addVenues) {
        this.addVenues = addVenues;
    }

    //@@@
    public String getLinkIcon() {
        return linkIcon;
    }
    public void setLinkIcon(String linkIcon) {
        this.linkIcon = linkIcon;
    }

    //@@@
    public String getDistance(){ return distance;}
    public void setDistance(String distance){ this.distance = distance;}
}
