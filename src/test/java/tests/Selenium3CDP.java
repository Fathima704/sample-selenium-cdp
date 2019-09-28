package tests;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Selenium3CDP {

    RemoteWebDriver driver;
    ChromeDriverService service;
    DevTools devTools;

    @Before
    public void setUp() throws IOException {
        Map<String, Object> prefs = new HashMap<String, Object>();
        //1-Allow, 2-Block, 0-default
        prefs.put("profile.default_content_setting_values.notifications", 1);
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments(Arrays.asList("--start-maximized"));
        options.setExperimentalOption("prefs", prefs);

        DesiredCapabilities crcapabilities = new DesiredCapabilities();
        crcapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        crcapabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
        crcapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        crcapabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, System.getProperty("user.dir") + "/target/chromedriver.log");
        //System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, System.getProperty("user.dir") + "/driver/chromedriver");
        service = new ChromeDriverService.Builder()
            .usingAnyFreePort()
            .withVerbose(true)
            .build();
        service.start();

        try {
            driver = new RemoteWebDriver(service.getUrl(), crcapabilities);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testApp() throws IOException {
        System.out.println("====");
        String ws = null;
        Stream<String> lines = Files.lines(Paths.get(System.getProperty("user.dir") + "/target/chromedriver.log"));
        Optional<String> hasDevTools = lines.filter(s -> s.contains("DevTools listening on")).findFirst();
        ws = hasDevTools.get().split("on")[1].trim();
        System.out.println(ws);
        lines.close();
    }

}
