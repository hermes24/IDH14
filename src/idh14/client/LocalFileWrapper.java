package idh14.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class LocalFileWrapper {

    public static final int EOF = -1;
    private final File file;
    private final MessageDigest messageDigest;
    private String checksum;

    // TODO: historie van checksums in een ArrayList o.i.d.
    public LocalFileWrapper(File file, MessageDigest messageDigest) {
        this.file = file;
        this.messageDigest = messageDigest;
    }    

    public void calculateChecksum() throws IOException {
        FileInputStream f = new FileInputStream(file);
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = f.read(b)) != -1) {
            messageDigest.update(b, 0, n);
        };
        byte[] m = messageDigest.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < m.length; i++) {
            sb.append(Integer.toString((m[i] & 0xff) + 0x100, 16).substring(1));
        }
        checksum = sb.toString();
        f.close();
    }

    public File getFile() {
        return file;
    }

    public String getChecksum() {
        return checksum;
    }


}
