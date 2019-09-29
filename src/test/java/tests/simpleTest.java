package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.model.ConnectionType;
import org.openqa.selenium.devtools.performance.*;
import org.openqa.selenium.devtools.performance.model.TimeDomain;
import org.openqa.selenium.devtools.security.Security;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;


import java.util.*;
import java.util.logging.Level;

import static org.openqa.selenium.devtools.network.Network.*;
import static org.openqa.selenium.devtools.security.Security.setIgnoreCertificateErrors;


public class simpleTest {

    ChromeDriver driver;
    DevTools devTools;

    @Before
    public void setUp() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 1);
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.CLIENT, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        logs.enable(LogType.PERFORMANCE, Level.ALL);
        logs.enable(LogType.PROFILER, Level.ALL);
        logs.enable(LogType.SERVER, Level.ALL);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("prefs", prefs);
        options.setCapability(CapabilityType.LOGGING_PREFS, logs);
        driver = new ChromeDriver(options);
        devTools = driver.getDevTools();
    }


    @Test
    public void simulateBandwidthWithNetwork() {

        driver.get("http://www.facebook.com");
        LogEntries x = driver.manage().logs().get(LogType.DRIVER);
        for (LogEntry e : x.getAll()) {
            if (e.getMessage().contains("DevTools request:")) {
                String url = e.getMessage().replaceFirst("DevTools request:", "").trim();
            }

            if (e.getMessage().contains("DevTools response:")) {
                String json = e.getMessage().replaceFirst("DevTools response:", "");
                try {
                    System.out.println(json);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
            System.out.println(e.getMessage());
        }
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
        MessageBuilder message = enableNetwork();
        driver.get("http://www.facebook.com");
        driver.executeCdpCommand(message.method, message.params);
        MessageBuilder simulateNetwork = setNetworkBandWidth();
        driver.executeCdpCommand(simulateNetwork.method, simulateNetwork.params);
        driver.get("http://www.google.com");

    }

    @Test
    public void setLocation() {
        driver.get("https://the-internet.herokuapp.com/geolocation");

        MessageBuilder simulateLocation = overrideLocation();
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
