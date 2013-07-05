package com.mfp.rooms.utils;

public class RomanNumbersUtils {

	private static final String[] RCODE = {"M", "CM", "D", "CD", "C", "XC", "L",
        "XL", "X", "IX", "V", "IV", "I"};
	private static final int[]    BVAL  = {1000, 900, 500, 400,  100,   90,  50,
        40,   10,    9,   5,   4,    1};
	
    public static String binaryToRoman(int binary) {
    	
        if (binary <= 0 || binary >= 4000) {
            throw new NumberFormatException("Value outside roman numeral range.");
        }
        String roman = "";
        
        for (int i = 0; i < RCODE.length; i++) {
            while (binary >= BVAL[i]) {
                binary -= BVAL[i];
                roman  += RCODE[i];
            }
        }
        return roman;
    }
}
