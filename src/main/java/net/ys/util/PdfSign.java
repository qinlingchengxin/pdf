package net.ys.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * pdf签章，包含证书信息
 * User: NMY
 * Date: 19-7-18
 */
public class PdfSign {

    public static String password = "123456";//证书密码
    public static String certPath;//证书路径
    public static String srcPath;//源pdf路径
    public static String imgPath;//图片路径

    static {
        certPath = PdfSign.class.getClassLoader().getResource("cert.pfx").getPath();
        srcPath = PdfSign.class.getClassLoader().getResource("test.pdf").getPath();
        imgPath = PdfSign.class.getClassLoader().getResource("test.png").getPath();
    }

    public static void main(String[] args) throws Exception {
        //签章
        String desPath = "E:/sign_" + System.currentTimeMillis() + ".pdf";
        String fieldName = "signature-" + System.currentTimeMillis();
        List<Integer> pages = new ArrayList<Integer>();
        for (int i = 1; i < 150; i++) {//可实现多页签章每一千页可能会多一秒，后面需要循环调用加入签名任务
            pages.add(i);
        }

        long now = System.currentTimeMillis();
        sign(srcPath, desPath, imgPath, pages, fieldName);
        System.out.println("use time:" + (System.currentTimeMillis() - now) + " -->" + fieldName);

      /*  //撤销签章
        now = System.currentTimeMillis();
        String unSignPath = "E:/unSign_" + System.currentTimeMillis() + ".pdf";
        unSign(desPath, unSignPath, fieldName);
        System.out.println("use time:" + (System.currentTimeMillis() - now) + " -->" + fieldName);*/
    }

    /**
     * 签章
     *
     * @param srcPath
     * @param desPath
     * @param imgPath
     * @param pages     页码，从1开始
     * @param fieldName
     * @throws Exception
     */
    public static void sign(String srcPath, String desPath, String imgPath, List<Integer> pages, String fieldName) throws Exception {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance("pkcs12");
        ks.load(new FileInputStream(certPath), password.toCharArray());
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        PdfReader reader = new PdfReader(srcPath);
        FileOutputStream os = new FileOutputStream(desPath);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
        //PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
        //在一份PDF文档中嵌入多个数字证书请参考上面一行注释的代码
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(bfChinese, 8, Font.NORMAL);
        Rectangle rectangle = new Rectangle(100, 100, 150, 144);//x,y,w,h
        Image image = Image.getInstance(imgPath);
        appearance.setImage(image);
        appearance.setLayer2Font(font);
        appearance.setLayer2Text("\n\n\n   浙 江 X X X X 科 技 有 限 公 司");
        appearance.setReason("签名防伪");
        appearance.setLocation("浙江杭州");
        appearance.setContact("service@yunhetong.net");
        ExternalDigest digest = new BouncyCastleDigest();
        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider.getName());
        appearance.setVisibleSignature(rectangle, pages.get(0), fieldName);
        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS, pages);
    }

    /**
     * 撤销签名
     *
     * @param srcPath
     * @param desPath
     * @param fieldName
     * @throws IOException
     * @throws DocumentException
     */
    public static void unSign(String srcPath, String desPath, String fieldName) throws IOException, DocumentException {
        UndoPdfSignature.undoSign(srcPath, desPath, fieldName);
    }
}
