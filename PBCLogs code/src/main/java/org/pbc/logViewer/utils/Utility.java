package org.pbc.logViewer.utils;

import java.security.MessageDigest;

public final class Utility {

    public static String calculateHash(final byte fileBytes[], final String algoName) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(algoName);
            final byte[] bytesHash = digest.digest(fileBytes);
            return convertByteArrayToHexString(bytesHash);
        } catch (final Exception e) {
            return StringConstants.EMPTY_STRING;
        }
    }

    private static String convertByteArrayToHexString(final byte[] arrayBytes) {
        final StringBuilder stringBuffer = new StringBuilder();
        for (final byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }
}
