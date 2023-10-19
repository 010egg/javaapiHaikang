// 
// Decompiled by Procyon v0.5.36
// 

package org.postgresql.ssl;

import java.security.cert.CertificateException;
import java.io.IOException;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.Certificate;
import java.util.UUID;
import java.security.cert.CertificateFactory;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.GeneralSecurityException;
import org.postgresql.util.GT;

public class SingleCertValidatingFactory extends WrappedFactory
{
    private static final String FILE_PREFIX = "file:";
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String ENV_PREFIX = "env:";
    private static final String SYS_PROP_PREFIX = "sys:";
    
    public SingleCertValidatingFactory(final String sslFactoryArg) throws GeneralSecurityException {
        if (sslFactoryArg == null || sslFactoryArg.equals("")) {
            throw new GeneralSecurityException(GT.tr("The sslfactoryarg property may not be empty."));
        }
        InputStream in = null;
        try {
            if (sslFactoryArg.startsWith("file:")) {
                final String path = sslFactoryArg.substring("file:".length());
                in = new BufferedInputStream(new FileInputStream(path));
            }
            else if (sslFactoryArg.startsWith("classpath:")) {
                final String path = sslFactoryArg.substring("classpath:".length());
                in = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
            }
            else if (sslFactoryArg.startsWith("env:")) {
                final String name = sslFactoryArg.substring("env:".length());
                final String cert = System.getenv(name);
                if (cert == null || "".equals(cert)) {
                    throw new GeneralSecurityException(GT.tr("The environment variable containing the server's SSL certificate must not be empty."));
                }
                in = new ByteArrayInputStream(cert.getBytes("UTF-8"));
            }
            else if (sslFactoryArg.startsWith("sys:")) {
                final String name = sslFactoryArg.substring("sys:".length());
                final String cert = System.getProperty(name);
                if (cert == null || "".equals(cert)) {
                    throw new GeneralSecurityException(GT.tr("The system property containing the server's SSL certificate must not be empty."));
                }
                in = new ByteArrayInputStream(cert.getBytes("UTF-8"));
            }
            else {
                if (!sslFactoryArg.startsWith("-----BEGIN CERTIFICATE-----")) {
                    throw new GeneralSecurityException(GT.tr("The sslfactoryarg property must start with the prefix file:, classpath:, env:, sys:, or -----BEGIN CERTIFICATE-----."));
                }
                in = new ByteArrayInputStream(sslFactoryArg.getBytes("UTF-8"));
            }
            final SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[] { new SingleCertTrustManager(in) }, null);
            this._factory = ctx.getSocketFactory();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            if (e2 instanceof GeneralSecurityException) {
                throw (GeneralSecurityException)e2;
            }
            throw new GeneralSecurityException(GT.tr("An error occurred reading the certificate"), e2);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public class SingleCertTrustManager implements X509TrustManager
    {
        X509Certificate cert;
        X509TrustManager trustManager;
        
        public SingleCertTrustManager(final InputStream in) throws IOException, GeneralSecurityException {
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            try {
                ks.load(null);
            }
            catch (Exception ex) {}
            final CertificateFactory cf = CertificateFactory.getInstance("X509");
            this.cert = (X509Certificate)cf.generateCertificate(in);
            ks.setCertificateEntry(UUID.randomUUID().toString(), this.cert);
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            for (final TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    this.trustManager = (X509TrustManager)tm;
                    break;
                }
            }
            if (this.trustManager == null) {
                throw new GeneralSecurityException(GT.tr("No X509TrustManager found"));
            }
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            this.trustManager.checkServerTrusted(chain, authType);
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] { this.cert };
        }
    }
}
