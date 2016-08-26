package io.iwa.certificateviewer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final PackageManager packageManager = this.getPackageManager();
    final List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
    File root = android.os.Environment.getExternalStorageDirectory();
    File dir = new File (root.getAbsolutePath() + "/certificates");
    dir.mkdirs();
    File file = new File(dir, "certificate.txt");
    FileOutputStream f = null;
    try {
      f = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    PrintWriter pw = new PrintWriter(f);
    for (PackageInfo p : packageList) {
      String strName = p.applicationInfo.loadLabel(packageManager).toString();
      String strVendor = p.packageName;
      StringBuilder sb = new StringBuilder();

      final Signature[] arrSignatures = p.signatures;
      for (final Signature sig : arrSignatures) {
        /*
        * Get the X.509 certificate.
        */
        final byte[] rawCert = sig.toByteArray();
        InputStream certStream = new ByteArrayInputStream(rawCert);

        CertificateFactory certFactory;
        X509Certificate x509Cert;
        try {
          certFactory = CertificateFactory.getInstance("X509");
          x509Cert = (X509Certificate) certFactory.generateCertificate(certStream);

          sb.append("Certificate subject: " + x509Cert.getSubjectDN() + "\n");
          sb.append("Certificate issuer: " + x509Cert.getIssuerDN() + "\n");
          sb.append("Certificate serial number: " + x509Cert.getSerialNumber() + "\n");
          sb.append("\n");
        }
        catch (CertificateException e) {
          // e.printStackTrace();
        }
        Log.wtf(MainActivity.class.getName(),sb.toString());

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        pw.println(sb.toString());

      }
    }
    pw.flush();
    pw.close();
    try {
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
