package com.ridesharing.passengermanagement.util;

import com.ridesharing.passengermanagement.pojo.Passenger;

public class GlobalUser {
    private static GlobalUser instance;
    private Passenger currentUser;

    private GlobalUser() {}

    public static GlobalUser getInstance() {
        if (instance == null) {
            instance = new GlobalUser();
            instance.setUser(new Passenger(1, "wu","123456"));

        }
        return instance;
    }

    public void setUser(Passenger user) {
        this.currentUser = user;
    }

    public Passenger getUser() {
        return currentUser;
    }
}

