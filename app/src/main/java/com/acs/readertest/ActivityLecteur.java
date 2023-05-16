package com.acs.readertest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ActivityLecteur extends Activity {

    private BroadcastReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur);

        // Vérifier si le lecteur NFC est branché en USB
        NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
        if (nfcAdapter == null) {
            // Le lecteur NFC n'est pas disponible
            Toast.makeText(this, "Lecteur NFC non disponible", Toast.LENGTH_SHORT).show();
        } else {
            // Le lecteur NFC est disponible
            Toast.makeText(this, "Lecteur NFC disponible", Toast.LENGTH_SHORT).show();
        }

        // Créer un BroadcastReceiver pour détecter le branchement/débranchement des périphériques USB
        usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    // Un périphérique USB a été branché
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        // Vérifier si le périphérique est un lecteur NFC
                        if (isNfcReader(device)) {
                            // Le lecteur NFC est branché en USB
                            Toast.makeText(ActivityLecteur.this, "Lecteur NFC branché en USB", Toast.LENGTH_SHORT).show();
                            Intent intent986 = new Intent(ActivityLecteur.this, ActivityCarte.class);
                            startActivity(intent986);
                        }
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    // Un périphérique USB a été débranché
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        // Vérifier si le périphérique est un lecteur NFC
                        if (isNfcReader(device)) {
                            // Le lecteur NFC a été débranché
                            Toast.makeText(ActivityLecteur.this, "Lecteur NFC débranché", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        // S'abonner aux actions de branchement/débranchement des périphériques USB
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Désenregistrer le BroadcastReceiver lors de la destruction de l'Activity
        unregisterReceiver(usbReceiver);
    }

    private boolean isNfcReader(UsbDevice device) {
        // Vérifier si le périphérique est un lecteur NFC en utilisant ses informations
        // (par exemple, le Vendor ID et le Product ID)
        // Retourner true si c'est un lecteur NFC, sinon false
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_help:
                //besoin d'aide
                Intent intent = new Intent(this, ActivityHelp.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                //settings();
                Intent intent2 = new Intent(this, ActivitySettings.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}