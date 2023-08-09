package com.trackspot.Exception;

public class CustomService {

    public void errorMessage() {
        throw new ResourceNotFoundException("file not found");
    }

}