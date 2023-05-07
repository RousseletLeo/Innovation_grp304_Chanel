/*
 * Copyright (C) 2011-2013 Advanced Card Systems Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Advanced
 * Card Systems Ltd. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with ACS.
 */

package com.acs.readertest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.acs.smartcard.Features;
import com.acs.smartcard.PinProperties;
import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;
import com.acs.smartcard.TlvProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Read and write program for ACR122U reader
 *
 * @author Rousselet Léo, Pigot Kris, Chen Coline ESME
 * @version 0.1 13/01/2023
 */

public class MainActivity extends Activity {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private static final String[] powerActionStrings = { "Power Down",
            "Cold Reset", "Warm Reset" };

    private static final String[] stateStrings = { "Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific" };

    private static final String[] featureStrings = { "FEATURE_UNKNOWN",
            "FEATURE_VERIFY_PIN_START", "FEATURE_VERIFY_PIN_FINISH",
            "FEATURE_MODIFY_PIN_START", "FEATURE_MODIFY_PIN_FINISH",
            "FEATURE_GET_KEY_PRESSED", "FEATURE_VERIFY_PIN_DIRECT",
            "FEATURE_MODIFY_PIN_DIRECT", "FEATURE_MCT_READER_DIRECT",
            "FEATURE_MCT_UNIVERSAL", "FEATURE_IFD_PIN_PROPERTIES",
            "FEATURE_ABORT", "FEATURE_SET_SPE_MESSAGE",
            "FEATURE_VERIFY_PIN_DIRECT_APP_ID",
            "FEATURE_MODIFY_PIN_DIRECT_APP_ID", "FEATURE_WRITE_DISPLAY",
            "FEATURE_GET_KEY", "FEATURE_IFD_DISPLAY_PROPERTIES",
            "FEATURE_GET_TLV_PROPERTIES", "FEATURE_CCID_ESC_COMMAND" };

    private static final String[] propertyStrings = { "Unknown", "wLcdLayout",
            "bEntryValidationCondition", "bTimeOut2", "wLcdMaxCharacters",
            "wLcdMaxLines", "bMinPINSize", "bMaxPINSize", "sFirmwareID",
            "bPPDUSupport", "dwMaxAPDUDataSize", "wIdVendor", "wIdProduct" };

    private UsbManager mManager;
    private Reader mReader;
    private PendingIntent mPermissionIntent;
    private static final int MAX_LINES = 25;
    private TextView mResponseTextView;
    private Spinner mReaderSpinner;
    private ArrayAdapter<String> mReaderAdapter;
    private Spinner mSlotSpinner;
    private ArrayAdapter<String> mSlotAdapter;
    private Spinner mPowerSpinner;
    private Button mOpenButton;
    private Button mCloseButton;
    private Button mGetStateButton;
    private Button mPowerButton;
    private EditText mCommandEditText;
    private Button mTransmitButton;
    private EditText mControlEditText;
    private Button mControlButton;
    private Button mGetFeaturesButton;
    private final Features mFeatures = new Features();

    /*Code de Krissou*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_dev_mode:
                //dev_mode(); <-- A FAIRE !!!
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur mode développeur", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                //settings(); <-- Idem ;)
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur les paramètres", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*Code de Krissou*/



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            logMsg("Opening reader: " + device.getDeviceName() + "...");
                            new OpenTask().execute(device);
                        }
                    } else {
                        logMsg("Permission denied for device " + device.getDeviceName());
                        mOpenButton.setEnabled(true);
                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                synchronized (this) {

                    // Update reader list
                    mReaderAdapter.clear();
                    for (UsbDevice device : mManager.getDeviceList().values()) {
                        if (mReader.isSupported(device)) {
                            mReaderAdapter.add(device.getDeviceName());
                        }
                    }
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (device != null && device.equals(mReader.getDevice())) {

                        // Disable buttons
                        setButtons(false);
                        // Clear slot items
                        mSlotAdapter.clear();
                        // Close reader
                        logMsg("Closing reader...");
                        new CloseTask().execute();
                    }
                }
            }
        }
    };



    private class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

        @Override
        protected Exception doInBackground(UsbDevice... params) {
            Exception result = null;
            try {
                mReader.open(params[0]);
            } catch (Exception e) {
                result = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Exception result) {

            if (result != null) {
                logMsg(result.toString());
            } else {

                logMsg("Reader name: " + mReader.getReaderName());
                int numSlots = mReader.getNumSlots();
                logMsg("Number of slots: " + numSlots);

                // Add slot items
                mSlotAdapter.clear();
                for (int i = 0; i < numSlots; i++) {
                    mSlotAdapter.add(Integer.toString(i));
                }

                // Remove all control codes
                mFeatures.clear();
                setButtons(true);
            }
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mReader.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mOpenButton.setEnabled(true);
        }

    }

    private static class PowerParams {

        public int slotNum;
        public int action;
    }

    private static class PowerResult {

        public byte[] atr;
        public Exception e;
    }

    private class PowerTask extends AsyncTask<PowerParams, Void, PowerResult> {

        @Override
        protected PowerResult doInBackground(PowerParams... params) {
            PowerResult result = new PowerResult();
            try {
                result.atr = mReader.power(params[0].slotNum, params[0].action);
            } catch (Exception e) {
                result.e = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(PowerResult result) {

            if (result.e != null) {
                logMsg(result.e.toString());
            } else {
                // Show ATR
                if (result.atr != null) {
                    logMsg("ATR:");
                    logBuffer(result.atr, result.atr.length);
                } else {
                    logMsg("ATR: None");
                }
            }
        }
    }

    private static class SetProtocolParams {
        public int slotNum;
        public int preferredProtocols;
    }

    private static class SetProtocolResult {
        public int activeProtocol;
        public Exception e;
    }

    private class SetProtocolTask extends AsyncTask<SetProtocolParams, Void, SetProtocolResult> {

        @Override
        protected SetProtocolResult doInBackground(SetProtocolParams... params) {
            SetProtocolResult result = new SetProtocolResult();
            params[0].preferredProtocols = Reader.PROTOCOL_T1;
            try {
                result.activeProtocol = mReader.setProtocol(params[0].slotNum,
                        params[0].preferredProtocols);
            } catch (Exception e) {
                result.e = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(SetProtocolResult result) {

            if (result.e != null) {
                logMsg(result.e.toString());

            } else {
                String activeProtocolString = "Active Protocol: ";

                if (result.activeProtocol == Reader.PROTOCOL_T1) {
                    activeProtocolString += "T=1";
                } else {
                    activeProtocolString += "Unknown";
                }
                // Show active protocol
                logMsg(activeProtocolString);
            }
        }
    }

    private static class TransmitParams {
        public int slotNum;
        public int controlCode;
        public String commandString;
    }

    private static class TransmitProgress {
        public int controlCode;
        public byte[] command;
        public int commandLength;
        public byte[] response;
        public int responseLength;
        public Exception e;
    }

    private class TransmitTask extends
            AsyncTask<TransmitParams, TransmitProgress, Void> {

        @Override
        protected Void doInBackground(TransmitParams... params) {

            TransmitProgress progress;
            byte[] command;
            byte[] response;
            int responseLength;
            int foundIndex;
            int startIndex = 0;

            do {
                // Find carriage return
                foundIndex = params[0].commandString.indexOf('\n', startIndex);
                if (foundIndex >= 0) {
                    command = NfcUtils.toByteArray(params[0].commandString.substring(
                            startIndex, foundIndex));
                } else {
                    command = NfcUtils.toByteArray(params[0].commandString
                            .substring(startIndex));
                }

                // Set next start index
                startIndex = foundIndex + 1;
                response = new byte[300];
                progress = new TransmitProgress();
                progress.controlCode = params[0].controlCode;
                try {

                    if (params[0].controlCode < 0) {
                        // Transmit APDU
                        responseLength = mReader.transmit(params[0].slotNum,
                                command, command.length, response,
                                response.length);
                    } else {
                        // Transmit control command
                        responseLength = mReader.control(params[0].slotNum,
                                params[0].controlCode, command, command.length,
                                response, response.length);
                    }

                    progress.command = command;
                    progress.commandLength = command.length;
                    progress.response = response;
                    progress.responseLength = responseLength;
                    progress.e = null;

                } catch (Exception e) {
                    progress.command = null;
                    progress.commandLength = 0;
                    progress.response = null;
                    progress.responseLength = 0;
                    progress.e = e;
                }
                publishProgress(progress);
            } while (foundIndex >= 0);
            return null;
        }

        @Override
        protected void onProgressUpdate(TransmitProgress... progress) {

            if (progress[0].e != null) {
                logMsg(progress[0].e.toString());

            } else {
                logMsg("Command:");
                logBuffer(progress[0].command, progress[0].commandLength);

                logMsg("Response:");
                logBuffer(progress[0].response, progress[0].responseLength);

                if (progress[0].response != null
                        && progress[0].responseLength > 0) {

                    int controlCode;
                    int i;

                    // Show control codes for IOCTL_GET_FEATURE_REQUEST
                    if (progress[0].controlCode == Reader.IOCTL_GET_FEATURE_REQUEST) {

                        mFeatures.fromByteArray(progress[0].response,
                                progress[0].responseLength);

                        logMsg("Features:");
                        for (i = Features.FEATURE_VERIFY_PIN_START; i <= Features.FEATURE_CCID_ESC_COMMAND; i++) {

                            controlCode = mFeatures.getControlCode(i);
                            if (controlCode >= 0) {
                                logMsg("Control Code: " + controlCode + " ("
                                        + featureStrings[i] + ")");
                            }
                        }
                    }

                    controlCode = mFeatures
                            .getControlCode(Features.FEATURE_IFD_PIN_PROPERTIES);
                    if (controlCode >= 0
                            && progress[0].controlCode == controlCode) {

                        PinProperties pinProperties = new PinProperties(
                                progress[0].response,
                                progress[0].responseLength);

                        logMsg("PIN Properties:");
                        logMsg("LCD Layout: "
                                + NfcUtils.toHexString(pinProperties.getLcdLayout()));
                        logMsg("Entry Validation Condition: "
                                + NfcUtils.toHexString(pinProperties
                                        .getEntryValidationCondition()));
                        logMsg("Timeout 2: "
                                + NfcUtils.toHexString(pinProperties.getTimeOut2()));
                    }

                    controlCode = mFeatures
                            .getControlCode(Features.FEATURE_GET_TLV_PROPERTIES);
                    if (controlCode >= 0
                            && progress[0].controlCode == controlCode) {

                        TlvProperties readerProperties = new TlvProperties(
                                progress[0].response,
                                progress[0].responseLength);

                        Object property;
                        logMsg("TLV Properties:");
                        for (i = TlvProperties.PROPERTY_wLcdLayout; i <= TlvProperties.PROPERTY_wIdProduct; i++) {

                            property = readerProperties.getProperty(i);
                            if (property instanceof Integer) {
                                logMsg(propertyStrings[i] + ": "
                                        + NfcUtils.toHexString((Integer) property));
                            } else if (property instanceof String) {
                                logMsg(propertyStrings[i] + ": " + property);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get USB manager
        mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // Initialize reader
        mReader = new Reader(mManager);
        mReader.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (prevState < Reader.CARD_UNKNOWN
                        || prevState > Reader.CARD_SPECIFIC) {
                    prevState = Reader.CARD_UNKNOWN;
                }
                if (currState < Reader.CARD_UNKNOWN
                        || currState > Reader.CARD_SPECIFIC) {
                    currState = Reader.CARD_UNKNOWN;
                }
                // Create output string
                final String outputString = "Slot " + slotNum + ": "
                        + stateStrings[prevState] + " -> "
                        + stateStrings[currState];
                // Show output
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMsg(outputString);
                    }
                });
            }
        });

        // Register receiver for USB permission
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mReceiver, filter);

        // Initialize response text view
        mResponseTextView = findViewById(R.id.main_text_view_response);
        mResponseTextView.setMovementMethod(new ScrollingMovementMethod());
        mResponseTextView.setMaxLines(MAX_LINES);
        mResponseTextView.setText("");

        // Initialize reader spinner
        mReaderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        for (UsbDevice device : mManager.getDeviceList().values()) {
            if (mReader.isSupported(device)) {
                mReaderAdapter.add(device.getDeviceName());
            }
        }
        mReaderSpinner = findViewById(R.id.main_spinner_reader);
        mReaderSpinner.setAdapter(mReaderAdapter);

        // Initialize slot spinner
        mSlotAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        mSlotSpinner = findViewById(R.id.main_spinner_slot);
        mSlotSpinner.setAdapter(mSlotAdapter);

        // Initialize power spinner
        ArrayAdapter<String> powerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, powerActionStrings);
        mPowerSpinner = findViewById(R.id.main_spinner_power);
        mPowerSpinner.setAdapter(powerAdapter);
        mPowerSpinner.setSelection(Reader.CARD_WARM_RESET);

        // Initialize list button
        Button mListButton = findViewById(R.id.main_button_list);
        mListButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mReaderAdapter.clear();
                for (UsbDevice device : mManager.getDeviceList().values()) {
                    if (mReader.isSupported(device)) {
                        mReaderAdapter.add(device.getDeviceName());
                    }
                }
            }
        });

        // Initialize open button
        mOpenButton = findViewById(R.id.main_button_open);
        mOpenButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean requested = false;
                mOpenButton.setEnabled(false);
                String deviceName = (String) mReaderSpinner.getSelectedItem();

                if (deviceName != null) {
                    // For each device
                    for (UsbDevice device : mManager.getDeviceList().values()) {
                        // If device name is found
                        if (deviceName.equals(device.getDeviceName())) {
                            // Request permission
                            mManager.requestPermission(device,
                                    mPermissionIntent);
                            requested = true;
                            break;
                        }
                    }
                }

                if (!requested) {

                    // Enable open button
                    mOpenButton.setEnabled(true);
                }
            }
        });

        // Initialize close button
        mCloseButton = findViewById(R.id.main_button_close);
        mCloseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setButtons(false);
                mSlotAdapter.clear();
                logMsg("Closing reader...");
                new CloseTask().execute();
            }
        });

        // Initialize get state button
        mGetStateButton = findViewById(R.id.main_button_get_state);
        mGetStateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get slot number
                int slotNum = mSlotSpinner.getSelectedItemPosition();
                // If slot is selected
                if (slotNum != Spinner.INVALID_POSITION) {

                    try {
                        // Get state
                        logMsg("Slot " + slotNum + ": Getting state...");
                        int state = mReader.getState(slotNum);

                        if (state < Reader.CARD_UNKNOWN
                                || state > Reader.CARD_SPECIFIC) {
                            state = Reader.CARD_UNKNOWN;
                        }
                        logMsg("State: " + stateStrings[state]);
                    } catch (IllegalArgumentException e) {

                        logMsg(e.toString());
                    }
                }
            }
        });

        // Initialize power button
        mPowerButton = findViewById(R.id.main_button_power);
        mPowerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get slot number
                int slotNum = mSlotSpinner.getSelectedItemPosition();
                // Get action number
                int actionNum = mPowerSpinner.getSelectedItemPosition();
                // If slot and action are selected
                if (slotNum != Spinner.INVALID_POSITION
                        && actionNum != Spinner.INVALID_POSITION) {

                    if (actionNum < Reader.CARD_POWER_DOWN
                            || actionNum > Reader.CARD_WARM_RESET) {
                        actionNum = Reader.CARD_WARM_RESET;
                    }

                    // Set parameters
                    PowerParams params = new PowerParams();
                    params.slotNum = slotNum;
                    params.action = actionNum;

                    // Perform power action
                    logMsg("Slot " + slotNum + ": "
                            + powerActionStrings[actionNum] + "...");
                    new PowerTask().execute(params);

                    int preferredProtocols = Reader.PROTOCOL_T1;
                    String preferredProtocolsString = "T=1";


                    SetProtocolParams params2 = new SetProtocolParams();
                    params2.slotNum = slotNum;
                    params2.preferredProtocols = preferredProtocols;

                    logMsg("Slot " + slotNum + ": Setting protocol to " + preferredProtocolsString + "...");
                    new SetProtocolTask().execute(params2);
                }
            }
        });

        // Initialize command edit text
        mCommandEditText = findViewById(R.id.main_edit_text_command);

        // Initialize transmit button
        mTransmitButton = findViewById(R.id.main_button_transmit);
        mTransmitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get slot number
                int slotNum = mSlotSpinner.getSelectedItemPosition();
                // If slot is selected
                if (slotNum != Spinner.INVALID_POSITION) {
                    // Set parameters
                    TransmitParams params = new TransmitParams();
                    params.slotNum = slotNum;
                    params.controlCode = -1;
                    params.commandString = mCommandEditText.getText().toString();
                    // Transmit APDU
                    logMsg("Slot " + slotNum + ": Transmitting APDU...");
                    new TransmitTask().execute(params);
                }
            }
        });

        // Initialize control edit text
        mControlEditText = findViewById(R.id.main_edit_text_control);
        mControlEditText.setText(Integer.toString(Reader.IOCTL_CCID_ESCAPE));

        // Initialize control button
        mControlButton = findViewById(R.id.main_button_control);
        mControlButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get slot number
                int slotNum = mSlotSpinner.getSelectedItemPosition();
                // If slot is selected
                if (slotNum != Spinner.INVALID_POSITION) {
                    // Get control code
                    int controlCode;
                    try {
                        controlCode = Integer.parseInt(mControlEditText.getText().toString());

                    } catch (NumberFormatException e) {
                        controlCode = Reader.IOCTL_CCID_ESCAPE;
                    }

                    // Set parameters
                    TransmitParams params = new TransmitParams();
                    params.slotNum = slotNum;
                    params.controlCode = controlCode;
                    params.commandString = mCommandEditText.getText().toString();

                    // Transmit control command
                    logMsg("Slot " + slotNum + ": Transmitting control command (Control Code: "
                            + params.controlCode + ")...");
                    new TransmitTask().execute(params);
                }
            }
        });

        // Initialize get features button
        mGetFeaturesButton = findViewById(R.id.main_button_get_features);
        mGetFeaturesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get slot number
                int slotNum = mSlotSpinner.getSelectedItemPosition();
                // If slot is selected
                if (slotNum != Spinner.INVALID_POSITION) {
                    // Set parameters
                    TransmitParams params = new TransmitParams();
                    params.slotNum = slotNum;
                    params.controlCode = Reader.IOCTL_GET_FEATURE_REQUEST;
                    params.commandString = "";

                    // Transmit control command
                    logMsg("Slot " + slotNum + ": Getting features (Control Code: "
                            + params.controlCode + ")...");
                    new TransmitTask().execute(params);
                }
            }
        });

        setButtons(false);
        // Hide input window
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        // Close reader
        mReader.close();
        // Unregister receiver
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }



    /**
     * Logs the message.
     * 
     * @param msg
     *            the message.
     */
    private void logMsg(String msg) {

        DateFormat dateFormat = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss]: ");
        Date date = new Date();
        String oldMsg = mResponseTextView.getText().toString();

        mResponseTextView.setText(oldMsg + "\n" + dateFormat.format(date) + msg);

        if (mResponseTextView.getLineCount() > MAX_LINES) {
            mResponseTextView.scrollTo(0, (mResponseTextView.getLineCount() - MAX_LINES)
                            * mResponseTextView.getLineHeight());
        }
    }

    /**
     * Logs the contents of buffer.
     * 
     * @param buffer
     *            the buffer.
     * @param bufferLength
     *            the buffer length.
     */
    private void logBuffer(byte[] buffer, int bufferLength) {

        StringBuilder bufferString = new StringBuilder();

        for (int i = 0; i < bufferLength; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            if (i % 16 == 0) {

                if (!bufferString.toString().equals("")) {

                    logMsg(bufferString.toString());
                    bufferString = new StringBuilder();
                }
            }
            bufferString.append(hexChar.toUpperCase()).append(" ");
        }

        if (!bufferString.toString().equals("")) {
            logMsg(bufferString.toString());
        }
    }



    private void setButtons(boolean bool){
        mCloseButton.setEnabled(bool);
        mSlotSpinner.setEnabled(bool);
        mGetStateButton.setEnabled(bool);
        mPowerSpinner.setEnabled(bool);
        mPowerButton.setEnabled(bool);
        mTransmitButton.setEnabled(bool);
        mControlButton.setEnabled(bool);
        mGetFeaturesButton.setEnabled(bool);
    }

}