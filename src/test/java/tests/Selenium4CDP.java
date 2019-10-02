package tests;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.Message;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.network.model.ConnectionType;
import org.openqa.selenium.devtools.network.model.InterceptionStage;
import org.openqa.selenium.devtools.network.model.RequestPattern;
import org.openqa.selenium.devtools.network.model.ResourceType;
import org.openqa.selenium.devtools.performance.Performance;
import org.openqa.selenium.devtools.performance.model.TimeDomain;
import org.openqa.selenium.devtools.security.Security;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Optional;

import static org.openqa.selenium.devtools.network.Network.*;
import static org.openqa.selenium.devtools.security.Security.setIgnoreCertificateErrors;


public class Selenium4CDP {

    ChromeDriver driver;
    DevTools devTools;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        devTools = driver.getDevTools();
    }

    @Test
    public void simulateBandwidthWithNetwork() {
        driver.get("http://www.facebook.com");
        devTools.createSession();
        devTools.send(enable(Optional.of(100000000), Optional.empty(), Optional.empty()));
        devTools.send(
            emulateNetworkConditions(false,
                100,
                1000,
                2000,
                Optional.of(ConnectionType.cellular3g)));
        driver.get("http://www.google.com");

    }

    @Test
    public void simulateBandwidthWithExecCDP() {
        MessageBuilder message = Messages.enableNetwork();
        driver.get("http://www.facebook.com");
        driver.executeCdpCommand(message.method, message.params);
        MessageBuilder simulateNetwork = Messages.setNetworkBandWidth();
        driver.executeCdpCommand(simulateNetwork.method, simulateNetwork.params);
        driver.get("http://www.google.com");

    }

    @Test
    public void setGeolocation() {
        driver.get("https://the-internet.herokuapp.com/geolocation");

        MessageBuilder simulateLocation = Messages.overrideLocation();
        driver.executeCdpCommand(simulateLocation.method, simulateLocation.params);
        driver.findElementByXPath("//*[@id=\"content\"]/div/button").click();

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
    public void mockWebResponse() throws InterruptedException {
        devTools.createSession();
        devTools.send(enable(Optional.of(100000000), Optional.empty(), Optional.empty()));

        devTools.addListener(requestIntercepted(),
            requestIntercepted -> devTools.send(
                continueInterceptedRequest(requestIntercepted.getInterceptionId(),
                    Optional.empty(),
                    Optional.of("This is Mocked!!!!!"),
                    Optional.empty(), Optional.empty(),
                    Optional.empty(),
                    Optional.empty(), Optional.empty())));
        RequestPattern
            requestPattern =
            new RequestPattern("*", ResourceType.Document, InterceptionStage.HeadersReceived);
        devTools.send(setRequestInterception(ImmutableList.of(requestPattern)));
        driver.navigate().to("http://petstore.swagger.io/v2/swagger.json");
        Thread.sleep(5000);

    }

    private MessageBuilder setNetworkBandWidth() {
        MessageBuilder msg = new MessageBuilder("Network.emulateNetworkConditions");
        msg.addParam("offline", false);
        msg.addParam("latency", 100);
        msg.addParam("downloadThroughput", 10000);
        msg.addParam("uploadThroughput", 2000);
        return msg;
    }


    private MessageBuilder enableNetwork() {
        MessageBuilder msg = new MessageBuilder("Network.enable");
        msg.addParam("maxTotalBufferSize", 10000000);
        return msg;
    }

    private MessageBuilder overrideLocation() {
        MessageBuilder msg = new MessageBuilder("Emulation.setGeolocationOverride");
        msg.addParam("latitude", 19.075984);
        msg.addParam("longitude", 72.877656);
        msg.addParam("accuracy", 1);
        return msg;
    }

    @After
    public void teardown() {
        driver.close();
        driver.quit();
    }

}