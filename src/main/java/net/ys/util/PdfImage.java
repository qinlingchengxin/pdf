package net.ys.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.ExternalBlankSignatureContainer;
import com.itextpdf.text.pdf.security.ExternalSignatureContainer;
import com.itextpdf.text.pdf.security.MakeSignature;

import java.io.*;
import java.util.*;

/**
 * pdf 插入图片
 * User: LiWenC
 * Date: 18-5-21
 */
public class PdfImage {

    public static void main(String[] args) throws Exception {
        //sign();
        //removeImage("e:", "des.pdf", System.currentTimeMillis() + ".pdf", "Signature-161ba2786a214b2682c007df2d72d9b7");
        //testHasField();
        //getFieldPosition("e:/da05b9b8-5caf-4ea8-b389-680c3e88fb18.pdf", "e:/target.pdf", "Signe85f99014ee1bc60");
        queryAllFields();
    }

    /**
     * 获取指定域名的坐标位置
     *
     * @param src
     * @param des
     * @param fieldName
     * @throws IOException
     * @throws DocumentException
     */
    public static void getFieldPosition(String src, String des, String fieldName) throws IOException, DocumentException {
        InputStream input = new FileInputStream(new File(src));
        PdfReader reader = new PdfReader(input);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(des));
        AcroFields form = stamper.getAcroFields();
        form.addSubstitutionFont(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
        Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
        float x = signRect.getLeft();
        float y = signRect.getBottom();
        System.out.println(x);
        System.out.println(y);
        stamper.close();
        reader.close();
    }

    public static void sign() throws Exception {
        int estimatedSize = 50000;
        float lx = 200;
        float by = 200;
        float rx = 110;
        float ty = 110;
        List<Integer> pages = new ArrayList<Integer>();
        for (int i = 1; i < 5; i++) {
            pages.add(i);
        }
        String imagePath = "e:/test.png";
        String srcPath = "e:";
        String srcFileName = "des.pdf";
        String desPath = "e:";
        String desFileName = "des_new.pdf";
        String fieldName = System.currentTimeMillis() + "";
        boolean flag = addImage(srcPath, desPath, srcFileName, desFileName, imagePath, pages, lx, by, rx, ty, fieldName, estimatedSize);
        System.out.println(flag + "--" + fieldName);
    }

    public static void testHasField() throws Exception {
        String srcPath = "e:/test/test.pdf";
        boolean flag = hasField(srcPath, Arrays.asList("1527742856849", "1527742937385"));
        System.out.println(flag);
    }

    public static void queryAllFields() throws Exception {
        String srcPath = "e:/test/temp__0945add6-d697-4759-80ec-69bf225fe1a6.pdf";
        List<String> fieldNames = queryAllFields(srcPath);
        System.out.println(fieldNames);
    }

    public static boolean hasField(String filePath, List<String> fieldNames) throws IOException, DocumentException {
        File osTmp = File.createTempFile("signTemp", ".pdf");
        PdfReader reader = new PdfReader(filePath);
        FileOutputStream os = new FileOutputStream(osTmp);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\000');
        AcroFields acroFields = stamper.getAcroFields();

        boolean flag = true;
        for (String fieldName : fieldNames) {
            if (acroFields.getField(fieldName) == null) {
                flag = false;
                break;
            }
        }
        os.close();
        osTmp.delete();
        return flag;
    }

    public static List<String> queryAllFields(String filePath) throws IOException, DocumentException {
        File osTmp = File.createTempFile("signTemp", ".pdf");
        PdfReader reader = new PdfReader(filePath);
        FileOutputStream os = new FileOutputStream(osTmp);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\000');
        AcroFields acroFields = stamper.getAcroFields();
        List<String> fieldNames = new ArrayList<String>();
        Map<String, AcroFields.Item> fields = acroFields.getFields();
        for (Map.Entry<String, AcroFields.Item> entry : fields.entrySet()) {
            fieldNames.add(entry.getKey());
        }
        os.close();
        osTmp.delete();
        return fieldNames;
    }

    /**
     * 添加图片
     *
     * @param srcPath
     * @param desPath
     * @param imagePath
     * @param pages
     * @param lx
     * @param by
     * @param rx
     * @param ty
     * @param fieldName
     * @param estimatedSize
     * @throws Exception
     */
    public static boolean addImage(String srcPath, String desPath, String srcFileName, String desFileName, String imagePath, List<Integer> pages, float lx, float by, float rx, float ty, String fieldName, int estimatedSize) throws Exception {
        PdfReader reader = null;
        PdfStamper stamper = null;
        FileOutputStream os = null;
        try {
            if (estimatedSize <= 8192) {
                estimatedSize = 8192;
            }
            File tmp = File.createTempFile("signTemp", ".pdf");
            reader = new PdfReader(srcPath + "/" + srcFileName);
            os = new FileOutputStream(desPath + "/" + desFileName);
            stamper = PdfStamper.createSignature(reader, os, '\000', tmp, true);
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            Rectangle rectangle = new Rectangle(lx, by, rx, ty);
            appearance.setVisibleSignature(rectangle, pages.get(0), fieldName);
            Image image = Image.getInstance(imagePath);
            appearance.setImage(image);
            appearance.setLayer2Text("");
            appearance.setLayer4Text("");
            appearance.setReason("新月 SM2签章");
            appearance.setSignDate(Calendar.getInstance());
            appearance.setSignatureEvent(new PdfSignatureAppearance.SignatureEvent() {
                @Override
                public void getSignatureDictionary(PdfDictionary pdfDictionary) {
                    pdfDictionary.put(new PdfName("LK_SIGN_ALG"), new PdfString("3"));
                }
            });
            ExternalSignatureContainer blank = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
            MakeSignature.signExternalContainer(appearance, blank, estimatedSize, pages);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stamper != null) {
                    stamper.close();
                }
                if (reader != null) {
                    reader.close();
                }

                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 删除图片
     *
     * @param filePath    文件路径
     * @param fileName    文件名称
     * @param desFileName 临时文件名称
     * @param fieldName   域名称
     * @return
     */
    public static boolean removeImage(String filePath, String fileName, String desFileName, String fieldName) throws IOException, DocumentException {
        String signedFile = filePath + "/" + fileName;
        String desFile = filePath + "/" + desFileName;
        return UndoPdfSignature.undoSign(signedFile, desFile, fieldName);
    }
}
