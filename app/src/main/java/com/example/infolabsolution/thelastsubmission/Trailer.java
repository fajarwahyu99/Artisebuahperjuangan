package com.example.infolabsolution.thelastsubmission;

public class Trailer {

    private String mKeyOfTrailer;

    public Trailer(String key) {
        mKeyOfTrailer = key;
    }

    public String getKeyString() {
        return mKeyOfTrailer;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "mKeyOfTrailer='" + mKeyOfTrailer + '\'' +
                '}';
    }
}
