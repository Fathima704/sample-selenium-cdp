package tests;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.network.model.ConnectionType;
import org.openqa.selenium.devtools.network.model.RequestId;
import org.openqa.selenium.devtools.performance.*;
import org.openqa.selenium.devtools.performance.model.Metric;
import org.openqa.selenium.devtools.performance.model.TimeDomain;
import org.openqa.selenium.devtools.profiler.Profiler;
//import org.openqa.selenium.devtools;
import org.openqa.selenium.devtools.*;
import org.openqa.selenium.devtools.security.Security;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.devtools.network.Network.*;
import static org.openqa.selenium.devtools.network.Network.getCertificate;
import static org.openqa.selenium.devtools.performance.Performance.*;
import static org.openqa.selenium.devtools.security.Security.setIgnoreCertificateErrors;

import static org.junit.Assert.assertTrue;


public class simpleTest {

    ChromeDriver driver = new ChromeDriver();
    DevTools devTools = driver.getDevTools();


    @Test
    public void simulateBandwidth() {

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

    // Following needs to be implemented

    @Test
    public void trackNetwork() {

//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-debugging-port=9222");
//        WebDriver driver = new ChromeDriver(options);
    }

    @Test
    public void setGeoLocation() {

    }

    @Test
    public void mockWebResponse() {
    }

    @After
    public void teardown(){
        driver.close();
        driver.quit();
    }

}
