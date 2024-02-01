package com.bc.utilities;

/**
 * Class defining methods used to pad a string.
 */
public class Padding {

    /**
     * Method that pads an input string with padding character to make it as long as the length supplied. You can
     * also specify padding orientation.
     * @param inputData Input data to be padded.
     * @param paddingCharacter Character to pad input data with.
     * @param paddedStringLength desired end length of the padded string.
     * @param padToLeft Orientation control for padding:
     *                  When true the padding character will be padded to the left.
     *                  When false the padding character will be padded to the right.
     * @return Updated inputData padded with the padding character.
     */
    public static String padString(String inputData, String paddingCharacter, int paddedStringLength, boolean padToLeft){
        int inputDataLength = inputData.length();
        int timesToPad = paddedStringLength - inputDataLength;
        if (timesToPad > 0){
            if (padToLeft){
                return paddingCharacter.repeat(timesToPad) + inputData;
            } else {
                return inputData + paddingCharacter.repeat(timesToPad);
            }
        }
        return inputData;
    }

}