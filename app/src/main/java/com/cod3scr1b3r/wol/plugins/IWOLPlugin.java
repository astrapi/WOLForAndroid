package com.cod3scr1b3r.wol.plugins;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Eyal on 08-12-14.
 * Generic plugins interface, allows a setting option in te menu and an intent to be used
 * to start a config page by the menu.
 * Also init method with params so we can pass on special params to the plugin on startup.
 * each plugin should be autonomous and do it's own handling of intents and broadcasts
 * when need be it can send the broadcast to wake the PC.
 * First step i allow only one broadcast configured manually.
 */
public interface IWOLPlugin {

    public int getSettingsResourceID();
    public Intent getSettingsIntent();
    public void init(Bundle params);
}
