package net.ys.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptPdf {


    public static void main(String[] args) throws IOException, DocumentException {
        String src = "E:/test.pdf";
        String des = "E:/test_enc.pdf";
        String password = "hello";
        encrypt(src, des, password);
    }

    public static boolean encrypt(String src, String des, String password) throws IOException, DocumentException {
        try {
            PdfReader reader = new PdfReader(src);
            FileOutputStream os = new FileOutputStream(des);
            PdfStamper stamper = new PdfStamper(reader, os);
            stamper.setEncryption(password.getBytes(), password.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
            stamper.close();
            reader.close();
            os.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}