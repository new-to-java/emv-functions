package com.bc.utilities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * This class defines methods for performing Exclusive Or operation against two hexadecimal values
 * that are supplied in string format.
 */
@Slf4j
public class Xor
        extends AbstractSelfValidator<Xor>
        implements LoggerUtility {
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]+$")
    private String leftOperand;
    @NotEmpty
    @Pattern(regexp = "^[\\da-fA-F]+$")
    private String rightOperand;
    private StringBuilder result;
    /**
     * All args constructor
     */
    public Xor(String leftOperand, String rightOperand){
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.result = new StringBuilder();
        this.selfValidate();
        logInfo(log,
                "Self validated successful for object {}.",
                this
        );
    }
    /**
     * Method that perform XOR function on two hexadecimal strings passed.
     */
    public String doXor() {
        // Convert left and right operands to bytearray and try Xor operation on each byte
        try {
            byte [] leftOperandBytes = Hex.decodeHex(leftOperand);
            byte [] rightOperandBytes = Hex.decodeHex(rightOperand);
            for (int i = 0; i < leftOperandBytes.length; i++) {
                result.append(Hex.encodeHexString(new byte[]{(byte) (leftOperandBytes[i] ^ rightOperandBytes[i])}));
            }
        } catch (DecoderException decoderException) {
            throwExceptionAndTerminate(
                    decoderException
            );
        } finally {
            logDebug(log,
                    "Xor request object: {}.",
                    this
            );
        }
        return result.toString();
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
     * Method to raise an exception and terminate processing.
     *
     * @param exception Generic exception object.
     */
    private void throwExceptionAndTerminate(Exception exception) {
        throw new RuntimeException(this.getClass() +
                " --> " +
                "Data decoding to byte array failed" +
                " Cause: " +
                exception.getCause() +
                " Message: " +
                exception.getMessage()
        );
    }
}