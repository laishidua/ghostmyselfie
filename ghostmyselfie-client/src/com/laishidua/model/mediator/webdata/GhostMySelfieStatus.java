package com.laishidua.model.mediator.webdata;

/**
 * This "Plain Ol' Java Object" (POJO) class represents meta-data of
 * interest downloaded in Json from the GhostMySelfie Service via the
 * GhostMySelfieServiceProxy.
 */
public class GhostMySelfieStatus {
    /**
     * Various fields corresponding to data downloaded in Json from
     * the GhostMySelfie WebService.
     */
    public enum GhostMySelfieState {
        READY, 
        PROCESSING
    }

    /**
     * State of the GhostMySelfie.
     */
    private GhostMySelfieState state;

    /**
     * Constructor that initializes all the fields of interest.
     */
    public GhostMySelfieStatus(GhostMySelfieState state) {
        super();
        this.state = state;
    }
    
    /*
     * Getters and setters to access GhostMySelfieStatus.
     */
    
    public GhostMySelfieState getState() {
        return state;
    }

    public void setState(GhostMySelfieState state) {
        this.state = state;
    }
}
