package com.acs.readertest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
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
import com.acs.smartcard.Reader;
import com.acs.smartcard.Reader.OnStateChangeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Read and write Mifare ultralight and Mifare Classik 1k program for ACR122U reader
 *
 * @author Rousselet Léo, Pigot Kris, Chen Coline, ESME
 * @version 0.1 13/01/2023
 */



//TO DO : FF CA 00 00 00, écrire PLUSIEURS messages à la suite. Lire plusieurs message et les lister, les afficher
    // sous format texte, enregistrer les données dans BDD, généraliser le code. Faire ca pour les deux cartes


public class MainActivity extends Activity {

    static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private static final String[] powerActionStrings = { "Power Down", "Cold Reset", "Warm Reset" };

    private static final String[] stateStrings = { "Unknown", "Absent", "Present",
            "Swallowed", "Powered", "Negotiable", "Specific" };

    private UsbManager mManager;
    private static Reader mReader;
    private PendingIntent mPermissionIntent;
    private static final int MAX_LINES = 25;
    private TextView mResponseTextView;
    private Spinner mReaderSpinner;
    private ArrayAdapter<String> mReaderAdapter;
    private Spinner mPowerSpinner;
    private Button mOpenButton;
    private Button mCloseButton;
    private Button mPowerButton;
    private EditText mCommandEditText;
    private Button mTransmitButton;
    private Button mReadTagButton;
    private Button mWriteTagButton;
    private static final Features mFeatures = new Features();
    private String typeOfCard = "";
    private String m_Text;

    //Check la connection USB avec le lecteur, et l'autorisation à utiliser le lecteur.
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //Si appareil en USB detecté, on demande l'autorisation de check.
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
            }
            //Shut down le lecteur si déconnecté du port USB
            else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                synchronized (this) {
                    mReaderAdapter.clear();
                    for (UsbDevice device : mManager.getDeviceList().values()) {
                        if (mReader.isSupported(device)) {
                            mReaderAdapter.add(device.getDeviceName());
                        }
                    }
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (device != null && device.equals(mReader.getDevice())) {
                        setButtons(false);
                        logMsg("Closing reader...");
                        new CloseTask().execute();
                    }
                }
            }
        }
    };






    //Ouvrir l'accès au lecteur
    class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

        @Override
        //Ouvre le lecteur
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
        //Affiche les propriétés du lecteur une fois ouvert
        protected void onPostExecute(Exception result) {

            if (result != null) {
                logMsg(result.toString());

            } else {
                logMsg("Reader name: " + mReader.getReaderName());
                int numSlots = mReader.getNumSlots();
                logMsg("Number of slots: " + numSlots);
                // Remove all control codes
                mFeatures.clear();
                setButtons(true);
            }
        }
    }





    //Fermer l'accès au lecteur
    class CloseTask extends AsyncTask<Void, Void, Void> {

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

    //Alimente la carte NFC
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
                if (result.atr != null) {
                    logMsg("ATR:");
                    String stringAtr = logBuffer(result.atr, result.atr.length);

                    if(stringAtr.equals("3B 8F 80 01 80 4F 0C A0 00 00 03 06 03 00 01 00 00 00 00 6A ")){
                        typeOfCard = "Mifare classic 1k";
                    }else if(stringAtr.equals("3B 8F 80 01 80 4F 0C A0 00 00 03 06 03 00 03 00 00 00 00 68 ")){
                        typeOfCard = "Mifare Ultralight";
                    }
                    else {
                        typeOfCard = "Unknown";
                    }
                    logMsg(typeOfCard);

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


    //Set le protocol de la carte (Pour ultralight ou class1k, forcément T=1)
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
                logMsg("Active Protocol: T=1");
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

    //Transmission d'un APDU et caractéritiques
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
                    command = NfcUtils.toByteArray(params[0].commandString.substring(startIndex));
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
                logBuffer(progress[0].response, progress[0].responseLength);
            }
        }
    }






    @TargetApi(Build.VERSION_CODES.M)
    @Override
    //Initialisation des composants au lancement de l'appli
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.veuillez_brancher_le_lecteur)
                .setTitle(R.string.lecteur_deco)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action à exécuter lorsque l'utilisateur clique sur "OK"

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


        // Get USB manager
        mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // Initialise le lecteur
        assert mManager != null;
        mReader = new Reader(mManager);
        mReader.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            //Vérifie l'état en cours du lecteur (en lecture de carte ou non)
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (prevState < Reader.CARD_UNKNOWN || prevState > Reader.CARD_SPECIFIC) {
                    prevState = Reader.CARD_UNKNOWN;
                }
                if (currState < Reader.CARD_UNKNOWN || currState > Reader.CARD_SPECIFIC) {
                    currState = Reader.CARD_UNKNOWN;
                }
                // Create output string
                final String outputString = "Slot " + slotNum + ": "
                        + stateStrings[prevState] + " -> " + stateStrings[currState];
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
                ACTION_USB_PERMISSION), PendingIntent.FLAG_UPDATE_CURRENT);
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
                            mManager.requestPermission(device, mPermissionIntent);
                            requested = true;
                            break;
                        }
                    }
                }
                if (!requested) {
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
                logMsg("Closing reader...");
                new CloseTask().execute();
            }
        });


        // Initialize power button
        mPowerButton = findViewById(R.id.main_button_power);
        mPowerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get action number
                int actionNum = mPowerSpinner.getSelectedItemPosition();
                // If action is selected
                if (actionNum != Spinner.INVALID_POSITION) {
                    if (actionNum < Reader.CARD_POWER_DOWN || actionNum > Reader.CARD_WARM_RESET) {
                        actionNum = Reader.CARD_WARM_RESET;
                    }

                    // Set parameters
                    PowerParams params = new PowerParams();
                    params.slotNum = 0;
                    params.action = actionNum;

                    // Perform power action
                    logMsg("Slot 0 : " + powerActionStrings[actionNum] + "...");
                    new PowerTask().execute(params);

                    //Automatically set protocol to 1 (due to mifare class1k and ultralight)
                    int preferredProtocols = Reader.PROTOCOL_T1;
                    String preferredProtocolsString = "T=1";


                    SetProtocolParams params2 = new SetProtocolParams();
                    params2.slotNum = 0;
                    params2.preferredProtocols = preferredProtocols;
                    logMsg("Slot 0 :  Setting protocol to " + preferredProtocolsString + "...");
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
                TransmitParams params = new TransmitParams();
                params.slotNum = 0;
                params.controlCode = -1;
                params.commandString = mCommandEditText.getText().toString();
                logMsg("Slot 0 : Transmitting APDU...");
                new TransmitTask().execute(params);
            }
        });

        //Initialize ReadTag button
        mReadTagButton = findViewById(R.id.main_button_ReadTag);
        mReadTagButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logMsg("Processing Read command : please wait ...\n");
                TransmitParams params = new TransmitParams();
                params.controlCode = -1;

                //Number of pages we want to read
                String[] arr;
                logMsg("Informations mémoires : ");

                if (typeOfCard.equals("Mifare Ultralight")){
                    //Read 4 pages by 4 pages (it seems to be impossible to do more in one command)
                    arr= new String[]{"04","08", "0C"};
                    for (String i:arr) {
                        logMsg("Processing reading : " + i);
                        params.commandString = "FF B0 00 " + i + " 10";
                        new TransmitTask().execute(params);
                    }

                }else if (typeOfCard.equals("Mifare classic 1k")){
                    // keyType 60 = key A, read-only | keyType 61 = key B, write only
                    arr = new String[]{"00","04","08","0C","10","14","18","1C","20","24",
                            "28","2C","30","34","38","3C"};
                    for (String i:arr){
                        loadKeys(i,"60","00");
                        logMsg("Processing reading : " + i);
                        params.commandString = "FF B0 00 "+ i + " 10";
                        new TransmitTask().execute(params);
                    }
                }
            }
        });

        mWriteTagButton = findViewById(R.id.main_button_WriteTag);
        mWriteTagButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Ecriture mémoire");
                builder.setMessage("Quel est le message que vous voulez écrire dans la mémoire ? ");

                // Set up the input
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);




                // Set up the buttons
                builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Récupération text, conversion en NDefMessage, supprimer les espaces
                        //et séparer le message en groupe de 4 octets
                        m_Text = input.getText().toString();
                        String textParsed = NfcUtils.parseText(m_Text);
                        textParsed = textParsed.replaceAll("\\s", "");
                        ArrayList textTruncated = NfcUtils.divideString(textParsed, 8, '0');

                        String[] page = new String[]{"04","05", "06","07","08","09","0A","0B","0C",
                                "0D","0E","0F"};

                        //Ecriture 4 octets par 4 pour des ultralight, et 10 par 10 sinon
                        //(restrictions des cartes)
                        try {
                            for(int indexPage=0;indexPage<page.length;indexPage++){
                                TransmitParams params = new TransmitParams();
                                params.controlCode = -1;
                                params.commandString = "FF D6 00 " + page[indexPage] + " 04 " + textTruncated.get(indexPage).toString();
                                new TransmitTask().execute(params);
                            }
                        }catch(Exception e){
                            logMsg("Fin mémoire.");
                        }
                        logMsg("Votre message à bien été enregistré : ");
                        logMsg(m_Text);
                        logMsg(textParsed);
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        setButtons(false);
        // Hide input window
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }





    @Override
    protected void onDestroy() {
        mReader.close();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }






    // Display the message
    public void logMsg(String msg) {

        DateFormat dateFormat = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss]: ");
        Date date = new Date();
        String oldMsg = mResponseTextView.getText().toString();

        mResponseTextView.setText(oldMsg + "\n" + dateFormat.format(date) + msg);

        if (mResponseTextView.getLineCount() > MAX_LINES) {
            mResponseTextView.scrollTo(0, (mResponseTextView.getLineCount() - MAX_LINES)
                            * mResponseTextView.getLineHeight());
        }
    }





//Display the content of the buffer
    private String logBuffer(byte[] buffer, int bufferLength) {
        StringBuilder bufferString = new StringBuilder();
        String completeBufferString = "";
        for (int i = 0; i < bufferLength; i++) {
            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }
            if (i % 16 == 0) {
                if (!bufferString.toString().equals("")) {
                    logMsg(bufferString.toString());
                    completeBufferString = bufferString.toString();
                    bufferString = new StringBuilder();
                }
            }
            bufferString.append(hexChar.toUpperCase()).append(" ");
        }
        if (!bufferString.toString().equals("")) {
            logMsg(bufferString.toString());
        }
        completeBufferString = completeBufferString + bufferString;
        return completeBufferString;
    }



    //Définis les clefs d'authentification (et leurs autorisations) d'une Mifare Classic 1k
    //Clef A : lecture    clef B : écriture
    public void loadKeys(String loadedSector, String keyType, String keyNumber){
        TransmitParams login = new TransmitParams();
        login.slotNum = 0;
        login.controlCode = -1;
        login.commandString = "FF 86 00 00 05 01 00 " + loadedSector + keyType + keyNumber;
        logMsg("Sector " + loadedSector + " loaded.");
        new TransmitTask().execute(login);
    }





    private void setButtons(boolean bool){
        mCloseButton.setEnabled(bool);
        mPowerSpinner.setEnabled(bool);
        mPowerButton.setEnabled(bool);
        mTransmitButton.setEnabled(bool);
        mReadTagButton.setEnabled(bool);
        mWriteTagButton.setEnabled(bool);
    }

}