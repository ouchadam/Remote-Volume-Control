package com.server.server;

import com.server.util.OSHelper;
import com.server.util.Shutdown;
import com.server.volume.VolumeController;
import com.server.volume.controller.VolumeControllerFactory;

public class Protocol {

    private static final String UPDATE_VOLUME = "uvol";
    private static final String CONNECTED = "conn";
    private static final String SHUTDOWN = "sdown";
    private static final String EXIT = "exit";
    private static final String VOLUME = "vol";

    private final VolumeController volumeController;

	private String vol;

	public Protocol() {
		this.vol = "00";
        volumeController = VolumeControllerFactory.getVolumeController(new OSHelper());
	}

    public String processInput(String input) {
        return handleMessage(input);
    }

    private String handleMessage(String input) {
        if (input.equals(UPDATE_VOLUME)) {
            vol = String.format("%02x", volumeController.getVolume());
        }
        if (input.equals(SHUTDOWN)) {
            System.out.println("Client chose shutdown");
            new Shutdown().shutDown();
        }
        if (input.equals(EXIT)) {
            System.out.println("Client chose to disconnect");
            return "1000";
        }
        if (input.substring(0, 3).equals(VOLUME)) {
            int volume = getVolumeFromMessage(input);
            volumeController.setVolume(volume);
        }
        return "00" + vol;
    }

    private int getVolumeFromMessage(String input) {
        return Integer.parseInt(input.substring(3));
    }

}
	