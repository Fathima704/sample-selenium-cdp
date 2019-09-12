package tests;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.model.ConnectionType;
import org.openqa.selenium.devtools.performance.*;
import org.openqa.selenium.devtools.performance.model.TimeDomain;
import org.openqa.selenium.devtools.security.Security;


import java.util.*;

import static org.openqa.selenium.devtools.network.Network.*;
import static org.openqa.selenium.devtools.security.Security.setIgnoreCertificateErrors;


public class simpleTest {

    ChromeDriver driver = new ChromeDriver();
    DevTools devTools = driver.getDevTools();


    @Test
    public void simulateBandwidth1() {

        driver.get("http://www.facebook.com");


        devTools.createSession();
        devTools.send(enable(Optional.of(100000000), Optional.empty(), Optional.empty()));

        devTools.send(
                emulateNetworkConditions(false, 100, 1000, 2000, Optional.of(ConnectionType.cellular3g)));
        driver.get("http://www.google.com");

    }

    @Test
    public void loadInsecureWebsite() {

        devTools.send(Security.enable());

        devTools.send(setIgnoreCertificateErrors(false));

//Todo: Test this
        driver.get("https://google.com/");
        devTools.send(Security.disable());

    }

    @Test
    public void performance() {

        devTools.createSession();
        devTools.send(Performance.setTimeDomain(TimeDomain.timeTicks));
        devTools.send(Performance.enable());
        System.out.println(devTools.send(Performance.getMetrics()));

        driver.get("http://www.facebook.com");
        devTools.close();
    }

    @Test
    public void simulateBandwidth() {
        Message message = enableNetwork();
        driver.get("http://www.facebook.com");
        driver.executeCdpCommand(message.method, message.params);
        Message simulateNetwork = setNetworkBandWidth();
        driver.executeCdpCommand(simulateNetwork.method, simulateNetwork.params);
        driver.get("http://www.google.com");

    }

    @Test
    public void setLocation()  {
        Message message = enableEmulation();
        driver.executeCdpCommand(message.method, message.params);
        Message simulateLocation = setGeoLocation1();
        driver.executeCdpCommand(simulateLocation.method, simulateLocation.params);
        driver.get("https://www.google.com/maps");

    }


    // Following tests needs to be implemented

    @Test
    public void trackNetwork() {

//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-debugging-port=9222");
//        WebDriver driver = new ChromeDriver(options);
    }

    @Test
    public void mockWebResponse() {
    }

    private Message setNetworkBandWidth() {
        Message msg = new Message("Network.emulateNetworkConditions");
        msg.addParam("offline", false);
        msg.addParam("latency", 100);
        msg.addParam("downloadThroughput", 10000);
        msg.addParam("uploadThroughput", 2000);
        return msg;
    }


    private Message enableNetwork() {
        Message msg = new Message("Network.enable");
        msg.addParam("maxTotalBufferSize", 10000000);
        return msg;
    }

    private Message enableEmulation() {
        Message msg = new Message("Emulation.setFocusEmulationEnabled");
        msg.addParam("enabled", true);
        return msg;
    }
    private Message setGeoLocation1() {
        Message msg = new Message("Emulation.setGeolocationOverride");
        msg.addParam("latitude", 19.075984);
        msg.addParam("longitude", 72.877656);
        return msg;
    }


    public class Message {
        private String method;
        private Map<String,Object> params;

        Message(String method) {
            this.method = method;
        }

        void addParam(String key, Object value){
            if(Objects.isNull(params))
                params = new HashMap<>();
            params.put(key,value);
        }
    }

    @After
    public void teardown(){
        driver.close();
        driver.quit();
    }

}
