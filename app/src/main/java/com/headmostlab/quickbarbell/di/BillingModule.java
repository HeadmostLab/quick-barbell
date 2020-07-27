package com.headmostlab.quickbarbell.di;

import com.headmostlab.quickbarbell.licensing.Base64;
import com.headmostlab.quickbarbell.licensing.Base64DecoderException;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class BillingModule {

    @ApplicationScope
    @Provides
    @Named("PublicKey")
    static String provideEncodedPublicKey() {
        try {
            return new String(Base64.decode("TUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFsT0hBaWZtM0" +
                    "l1cFhhTndsbDJ4TEcxazZkbzVnQldpWXhORTc3dzF2YTRpV3puNVA3end0aWtJdDhaVGI3b01wN3QveF" +
                    "E1VG1CQUNETmQwZmtKM0o1VzRkTmE1YVllUXB2emFJS1FCZ3NqOTAzL0lKMzM1MGJneVlzeFpRNXh6SE" +
                    "hVejRubzRGZ2wzMkVIWE5jUFNCMlF4VktxTmtpWGdtM3l0NkpvZ1Q1eGFEYVppSTFSaDh6bmtRL1Yzdmx" +
                    "xT0RNTENpRlhHcHYvT1VVTnhQaTFGeEpoSnYwYUdrSWY2MW5hVnFPV3pwcFkwMjJXaTZlb3pGVXpXMk" +
                    "Y3ZGNnM2ROKy9LNG0vSmZ6Sjg0S21xUHBWSkR4UUo2WjRHUldjc25Za0NGK3RDUW5UYkVRbkhpa2hVW" +
                    "TRLNHY0S05wZFE5RzhIcU1MNTdRYkFXdy9IR1pBYklCQXdJREFRQUI="));
        } catch (Base64DecoderException e) {
            return "";
        }
    }

}
