package com.example.lcong.androidbluetooth;


import android.bluetooth.BluetoothAdapter;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lcong on 13/10/2017.
 */

public class MainActivityUnitTest {
    private static final int DISABLE_TIMEOUT = 1000;
    private static final int ENABLE_TIMEOUT = 1000;
    private static final int POLL_TIME = 100;

    @Test
    public void on() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter.getState() == BluetoothAdapter.STATE_ON) {
            assertTrue(adapter.isEnabled());
            return;
        }
        assertEquals(BluetoothAdapter.STATE_OFF, adapter.getState());
        assertFalse(adapter.isEnabled());
        adapter.enable();
    }

    @Test
    public void find() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.enable();
        String name = adapter.getName();
        String address = adapter.getAddress();
        assertNotNull(name);
        assertNotNull(address);
    }

    @Test
    public void off() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.getState() == BluetoothAdapter.STATE_OFF) {
            assertFalse(adapter.isEnabled());
            return;
        }
        assertEquals(BluetoothAdapter.STATE_ON, adapter.getState());
        assertTrue(adapter.isEnabled());
        adapter.disable();
    }


    private static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {}
    }

}
