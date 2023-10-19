// 
// Decompiled by Procyon v0.5.36
// 

package ch.qos.logback.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class SecureRandomFactoryBean
{
    private String algorithm;
    private String provider;
    
    public SecureRandom createSecureRandom() throws NoSuchProviderException, NoSuchAlgorithmException {
        try {
            return (this.getProvider() != null) ? SecureRandom.getInstance(this.getAlgorithm(), this.getProvider()) : SecureRandom.getInstance(this.getAlgorithm());
        }
        catch (NoSuchProviderException ex) {
            throw new NoSuchProviderException("no such secure random provider: " + this.getProvider());
        }
        catch (NoSuchAlgorithmException ex2) {
            throw new NoSuchAlgorithmException("no such secure random algorithm: " + this.getAlgorithm());
        }
    }
    
    public String getAlgorithm() {
        if (this.algorithm == null) {
            return "SHA1PRNG";
        }
        return this.algorithm;
    }
    
    public void setAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }
    
    public String getProvider() {
        return this.provider;
    }
    
    public void setProvider(final String provider) {
        this.provider = provider;
    }
}
