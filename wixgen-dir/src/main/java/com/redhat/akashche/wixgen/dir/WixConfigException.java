package com.redhat.akashche.wixgen.dir;

/**
 * User: alexkasko
 * Date: 5/13/16
 */
public class WixConfigException extends RuntimeException {
    public WixConfigException(String message) {
        super(message);
    }

    public WixConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
