package com.acs.readertest;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class NfcUtils {


    //Split une String en plusieurs sous String de longueur K, char de complétion 'ch'
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

        //Complétion si lenght(str)//k n'est pas nul
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


                return StringTLVstart + StringTLVLenght + " " +
                        hexStringTextMessage + StringTLVTerminator;
            }
        }
        return null;
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


    public static byte[] concateneByteArrays(byte[] arrayA, byte[] arrayB){

        //Transformer arrayA et arrayB en ArrayList pour pouvoir utiliser la class ArrayList
        List<Byte> listA = new ArrayList<>();
        for (byte b : arrayA) {
            listA.add(b);
        }
        List<Byte> listB = new ArrayList<>();
        for (byte b : arrayB) {
            listB.add(b);
        }
        listA.addAll(listB);

        byte[] byteArray = new byte[listA.size()];
        for (int i = 0; i < listA.size(); i++) {
            byteArray[i] = listA.get(i);
        }
        return byteArray;
    }


    public static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}
