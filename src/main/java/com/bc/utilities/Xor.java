package com.bc.utilities;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;

/**
 * This class defines methods for performing Exclusive Or operation against two hexadecimal values
 * that are supplied in string format.
 */
@Getter
@Setter
public class Xor {

    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]+$")
    private String leftOperand;
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]+$")
    private String rightOperand;

    /**
     * Method that perform XOR function on two hexadecimal strings passed.
     */
    public String doXor() {
        StringBuilder xOredValue = new StringBuilder();
        // Check if object state is valid or not
        if(isValidObject()) {
            // Try Xor operation
            try {
                byte [] leftOperandBytes = Hex.decodeHex(leftOperand);
                byte [] rightOperandBytes = Hex.decodeHex(rightOperand);
                System.out.println("Length of left operand bytes: " + leftOperandBytes.length);
                for (int i = 0; i < leftOperandBytes.length; i++) {
                    xOredValue.append(Hex.encodeHexString(new byte[]{(byte) (leftOperandBytes[i] ^ rightOperandBytes[i])}));
                }
            } catch (DecoderException decoderException) {
                throw new RuntimeException("Hexadecimal decoding to byte array failed!");
            } finally {
                System.out.println("Left operand value: " + leftOperand);
                System.out.println("Right operand value: " + rightOperand);
                System.out.println("XOR Length: " + xOredValue.length());
                System.out.println("XOR Value: " + xOredValue);
            }
            return xOredValue.toString();
        }
        return null;
    }
    /**
     * Method for invoking the self validator bean validator class' isAValidObject method for validating
     * the input attributes and ensure all the input attributes are set as required.
     */
    private boolean isValidObject(){
        SelfValidator<Xor> xorSelfValidator = new SelfValidator<>();
        if (leftOperand.length() > rightOperand.length()){
            rightOperand = Padding.padString(rightOperand, "0", leftOperand.length(), true);
        } else if(leftOperand.length() < rightOperand.length()){
            leftOperand = Padding.padString(leftOperand, "0", rightOperand.length(), true);
        }
        return xorSelfValidator.isAValidObject(this);
    }

}
