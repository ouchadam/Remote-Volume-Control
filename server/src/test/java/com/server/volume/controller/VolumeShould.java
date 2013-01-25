package com.server.volume.controller;

import com.server.util.OSHelper;
import com.server.volume.VolumeController;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VolumeShould {

    final OSHelper osHelper = mock(OSHelper.class);

    @Test
    public void return_a_windows_instance_when_os_is_windows() {
        when(osHelper.isWindows7()).thenReturn(true);
        VolumeController winControl = VolumeControllerFactory.getVolumeController(osHelper);

        assertTrue(winControl instanceof WinVolume);
    }

    @Test
    public void return_a_osx_instance_when_os_is_osx() {
        when(osHelper.isOSX()).thenReturn(true);
        VolumeController winControl = VolumeControllerFactory.getVolumeController(osHelper);

        assertTrue(winControl instanceof MacVolume);
    }

    @Test
    public void return_a_linux_instance_when_os_is_linux() {
        when(osHelper.isLinux()).thenReturn(true);
        VolumeController winControl = VolumeControllerFactory.getVolumeController(osHelper);

        assertTrue(winControl instanceof LinuxVolume);
    }


}
