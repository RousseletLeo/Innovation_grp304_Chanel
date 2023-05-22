package com.acs.readertest.nfc;

import static com.acs.readertest.MainActivity.mOpenButton;
import static com.acs.readertest.MainActivity.mReader;

import android.os.AsyncTask;

import com.acs.readertest.MainActivity;

public class ReaderTools {


    //Open, close, power, setPrococol, transmitAPDU (read, write, authentificate...)


    //Fermer l'accès au lecteur
    public static class  CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mReader.close();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {mOpenButton.setEnabled(true);}
    }


    public void readTag(){

    }

    public void writeTag(){

    }

    public void authentificate(){

    }

}
