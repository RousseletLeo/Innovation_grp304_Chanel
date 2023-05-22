package com.acs.readertest.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

public class NfcUtils {

    //Formatage en NDefMessage
    public static String textToNDef(String text){

        //Création d'un NDefMessage à partir d'un texte
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            NdefRecord textRecord = NdefRecord.createTextRecord("en",text);
            NdefMessage textMessage = new NdefMessage(textRecord);
            String hexStringTextMessage = NfcUtils.toHexString(textMessage.toByteArray());

            //Ajouter TLV
            if(text.length()>0){
                String TLVstart = "03 ";
                String TLVTerminator = "FE";
                String TLVLenght = NfcUtils.toHexString(textMessage.getByteArrayLength());

                return TLVstart + TLVLenght + " " + hexStringTextMessage + TLVTerminator;
            }
        }
        return null;
    }



    //String hexa avec espace en ascii : "48 65 6C 6C 6F " == "Hello"
    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i += 3) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
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


    //Return the length of any NDefMessage in a given String
    public static String readTLVdata(String dataContent){

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < dataContent.length(); i += 3) {
            String octet = dataContent.substring(i, i + 2);
            if(octet.equals("03")){
                return dataContent.substring(i+3,i+5); //return Message length
            }
        }
        return "00";
    }


}
