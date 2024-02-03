package com.bc.utilities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * This class defines methods for performing Exclusive Or operation against two hexadecimal values
 * that are supplied in string format.
 */
@Getter
@Setter
@Slf4j
public class Xor {
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]+$")
    private String leftOperand;
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]+$")
    private String rightOperand;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private StringBuilder result = new StringBuilder();
    /**
     * Method that perform XOR function on two hexadecimal strings passed.
     */
    public String doXor() {
        // Check if object state is valid or not
        if(isValidObject()) {
            // Convert left and right operands to bytearray and try Xor operation on each byte
            try {
                byte [] leftOperandBytes = Hex.decodeHex(leftOperand);
                byte [] rightOperandBytes = Hex.decodeHex(rightOperand);
                for (int i = 0; i < leftOperandBytes.length; i++) {
                    result.append(Hex.encodeHexString(new byte[]{(byte) (leftOperandBytes[i] ^ rightOperandBytes[i])}));
                }
            } catch (DecoderException decoderException) {
                throw new RuntimeException(this.getClass() +  " - Data decoding to byte array failed - "
                        + decoderException.getCause() + " - " + decoderException.getMessage());
            } finally {
                debugLog();
            }
            return result.toString();
        }
        return null;
    }
    /**
     * Method for invoking the self validator bean validator class' isAValidObject method for validating
     * the input attributes and ensure all the input attributes are set as required.
     */
    private boolean isValidObject(){

        SelfValidator<Xor> xorSelfValidator = new SelfValidator<>();

        // Pad operands to the left with "0" to match the length of the longest operand.
        if (leftOperand.length() > rightOperand.length()){
            rightOperand = Padding.padString(rightOperand, "0", leftOperand.length(), true);
        } else if(leftOperand.length() < rightOperand.length()){
            leftOperand = Padding.padString(leftOperand, "0", rightOperand.length(), true);
        }
        return xorSelfValidator.isAValidObject(this);

    }
    /**
     * Override method for the object's default toString method.
     * @return String representing object's attribute values.
     */
    @Override
    public String toString() {
        return "{" +
                "leftOperand='" + leftOperand + '\'' +
                ", rightOperand='" + rightOperand + '\'' +
                ", result=" + result +
                '}';
    }
    /**
     * Method for logging the input data and output data for the Xor function, when the debug log level is enabled.
     */
    private void debugLog(){
        if (log.isDebugEnabled()) {
            log.debug(" Debug log : {}", this);
        }
    }

}