package com.ridesharing.common.pojo;
//乘车类型
public enum RideType {
    STANDARD,
    PREMIUM,
    SHARED;

    public Double getPricePerKm() {
        switch (this) {
            case PREMIUM:return 0.4;
            case SHARED:return 0.1;
            case STANDARD:
            default: return 0.2;
        }
    }
}

