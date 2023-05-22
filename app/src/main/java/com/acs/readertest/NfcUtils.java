package com.acs.readertest;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

import java.util.ArrayList;

public class NfcUtils {

    //Récupérer l'ATR pour avoir le type de carte
    //Si classik 1k : vérifier si l'authentification en écriture à été faite sur tout les blocs mémoire


    //Effacer tout le contenu de la carte
    public void formate(){

    }





    public static String writeTag(String hexStartPage, String hexNumberBytes, String hexText){

        System.out.println("test");
        return "FF D6 00" + hexStartPage + hexNumberBytes + hexText;
    }



    // Function to split the String into substring of lenght K
    static ArrayList<String> divideString(String str, int K, char ch) {
        int N = str.length();
        int j = 0;
        ArrayList<String> result = new ArrayList<>();
        String res = "";
        while (j < N) {

            res += str.charAt(j);
            if (res.length() == K) {
                result.add(res);
                res = "";
            }
            j++;
        }

        if (res != "") {
            while (res.length() < K) {
                res += ch;
            }
            result.add(res);
        }
        return result;
    }


    //Formatage en NDefMessage
    public static String parseText(String text){

        //Création d'un NDefMessage à partir d'un texte
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            NdefRecord textRecord = NdefRecord.createTextRecord("en",text);
            NdefMessage textMessage = new NdefMessage(textRecord);
            String hexStringTextMessage = NfcUtils.toHexString(textMessage.toByteArray());

            //Ajouter TLV
            if(text.length()>0){
                String StringTLVstart = "03 ";
                String StringTLVTerminator = "FE";
                int TLVLenght = textMessage.getByteArrayLength();
                String StringTLVLenght = NfcUtils.toHexString(TLVLenght);
                return StringTLVstart + StringTLVLenght + " " + hexStringTextMessage + StringTLVTerminator;
            }
        }
        return null;
    }







    //authentification sur un bloc mémoire
    public void logInDataBlock(){

    }


    public NdefMessage[] getNdefMessages(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
            return messages;
        } else {
            return null;
        }
    }



    public static String toHexString(int i) {

        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        return hexString.toUpperCase();
    }




    public static String toHexString(byte[] buffer) {

        StringBuilder bufferString = new StringBuilder();

        for (byte b : buffer) {
            String hexChar = Integer.toHexString(b & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }
            bufferString.append(hexChar.toUpperCase()).append(" ");
        }
        return bufferString.toString();
    }






    public static byte[] toByteArray(String hexString) {

        int hexStringLength = hexString.length();
        byte[] byteArray;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {
            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {
                if (first) {
                    byteArray[len] = (byte) (value << 4);
                } else {
                    byteArray[len] |= value;
                    len++;
                }
                first = !first;
            }
        }
        return byteArray;
    }
}
