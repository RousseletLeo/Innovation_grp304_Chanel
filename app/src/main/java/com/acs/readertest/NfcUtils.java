package com.acs.readertest;

public class NfcUtils {

    //Récupérer l'ATR pour avoir le type de carte
    //Si classik 1k : vérifier si l'authentification en écriture à été faite sur tout les blocs mémoire


    //Effacer tout le contenu de la carte
    public void formate(){

    }



    public String readTag(String hexStartPage, String hexNumberBytes ){
        return "FF B0 00" + hexStartPage + hexNumberBytes;
    }




    //Pour l'instant gère que les Mifare Ultralight
    public String writeTag(String hexStartPage, String hexNumberBytes, String hexText){
        return "FF D6 00" + hexStartPage + hexNumberBytes + hexText;
    }




    //Vérifier/Créer la structure des messages hexa écrit en mémoire.
    //Nous servira à différencier différentes infos dans une même carte
    public String parseMemory(){
        return "";
    }




    //Définis les clefs d'authentification (et leurs autorisations) d'une Mifare Classic 1k
    //Clef A : lecture    clef B : écriture
    public void loadKeys(){

    }





    //authentification sur un bloc mémoire
    public void logInDataBlock(){

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
