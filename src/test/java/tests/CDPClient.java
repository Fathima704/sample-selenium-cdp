package tests;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CDPClient {
    private String wsUrl;
    private WebSocket ws = null;
    private WebSocketFactory factory;
    private BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>(100000);
    public CDPClient(String wsURL){
        factory = new WebSocketFactory();
        factory.setVerifyHostname(false);
        this.wsUrl = wsURL;
    }

    private void connect() throws IOException, WebSocketException {
        if(Objects.isNull(ws)){
            System.out.println("Making the new WS connection to: " + wsUrl);
            ws = factory
                .createSocket(wsUrl)
                .addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket ws, String message) {
                        System.out.println("Received this ws message: "+message);
                        blockingQueue.add(message);
                    }
                })
                .connect();
        }
    }

    public void sendMessage(String message) throws IOException, WebSocketException {
        if(Objects.isNull(ws))
            this.connect();
        System.out.println("Sending this ws message: " + message);
        ws.sendText(message);
    }

    public static int getDynamicID() {
        int min = 100000;
        int max = 999999;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public void disconnect(){
        ws.disconnect();
    }
}
