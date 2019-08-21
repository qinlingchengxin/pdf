
package net.ys.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.io.IOException;

public class DecryptPdf {


    public static void main(String[] args) throws IOException, DocumentException {
        String src = "E:/test_enc.pdf";
        String des = "E:/test_dec.pdf";
        String password = "hello";
        decrypt(src, des, password);
    }

    public static boolean decrypt(String src, String des, String password) throws IOException, DocumentException {
        try {
            PdfReader reader = new PdfReader(src, password.getBytes());
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(des));
            stamper.close();
            reader.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}