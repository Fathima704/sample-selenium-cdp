package tests;

public class Messages {
    static MessageBuilder setNetworkBandWidth() {
        MessageBuilder msg = new MessageBuilder("Network.emulateNetworkConditions");
        msg.addParam("offline", false);
        msg.addParam("latency", 100);
        msg.addParam("downloadThroughput", 10000);
        msg.addParam("uploadThroughput", 2000);
        return msg;
    }


    static MessageBuilder enableNetwork() {
        MessageBuilder msg = new MessageBuilder("Network.enable");
        msg.addParam("maxTotalBufferSize", 10000000);
        return msg;
    }

    public static MessageBuilder overrideLocation() {
        MessageBuilder msg = new MessageBuilder("Emulation.setGeolocationOverride");
        msg.addParam("latitude", 19.075984);
        msg.addParam("longitude", 72.877656);
        msg.addParam("accuracy", 1);
        return msg;
    }

}


