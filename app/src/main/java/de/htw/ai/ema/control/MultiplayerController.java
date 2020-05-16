package de.htw.ai.ema.control;


import de.htw.ai.ema.network.service.handler.ConnectionHandler;

public class MultiplayerController implements Control {

    ConnectionHandler handler;

    public MultiplayerController(ConnectionHandler handler){
        this.handler = handler;
    }
}
