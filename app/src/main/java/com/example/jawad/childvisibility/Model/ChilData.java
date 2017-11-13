package com.example.jawad.childvisibility.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jawad on 3/31/2016.
 */
public class ChilData {
    public String Email,Name;
    LatLng Address;
    public int ID;
    public ChilData(String name, String email,int id,LatLng _Address){
        Email=email;
        Name=name;
        ID= id;
        Address = _Address;
    }
    public  ChilData(String email){
        Email =email;
    }
    public ChilData(){

}

    public LatLng getAddress() {
        return Address;
    }

    public void setAddress(LatLng address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
