package tests;


import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CDPClient {
    private String wsUrl;
    private WebSocket ws = null;
    private WebSocketFactory factory;
    private BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>(100000);

    public CDPClient(String wsURL) {
        factory = new WebSocketFactory();
        turnOffSslChecking(factory);
        factory.setVerifyHostname(false);
        this.wsUrl = wsURL;
    }


    private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] {
        new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
    };

    public static void turnOffSslChecking(WebSocketFactory factory) {
        try {
            // Install the all-trusting trust manager
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            factory.setSSLContext(sc);
        } catch (Exception e) {
            System.out.println("Error in SSL Utils");
        }
    }

    private void connect() throws IOException, WebSocketException {
        if (Objects.isNull(ws)) {
            System.out.println("Making the new WS connection to: " + wsUrl);
            ws = factory
                .createSocket(wsUrl)
                .addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket ws, String message) {
                        System.out.println("Received this ws message: " + message);
                        blockingQueue.add(message);
                    }
                })
                .connect();
        }
    }

    public void sendMessage(String message) throws IOException, WebSocketException {
        if (Objects.isNull(ws)) {
            this.connect();
        }
        System.out.println("Sending this ws message: " + message);
        ws.sendText(message);
    }
}
