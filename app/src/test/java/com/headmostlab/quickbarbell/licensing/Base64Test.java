package com.headmostlab.quickbarbell.licensing;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64Test {

    @Test
    public void test() throws UnsupportedEncodingException, Base64DecoderException {
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlOHAifm3IupXaNwll2xLG1k6do5gBWiYx" +
                "NE77w1va4iWzn5P7zwtikIt8ZTb7oMp7t/xQ5TmBACDNd0fkJ3J5W4dNa5aYeQpvzaIKQBgsj903/IJ" +
                "3350bgyYsxZQ5xzHHUz4no4Fgl32EHXNcPSB2QxVKqNkiXgm3yt6JogT5xaDaZiI1Rh8znkQ/V3vlqO" +
                "DMLCiFXGpv/OUUNxPi1FxJhJv0aGkIf61naVqOWzppY022Wi6eozFUzW2F7dcg3dN+/K4m/JfzJ84Km" +
                "qPpVJDxQJ6Z4GRWcsnYkCF+tCQnTbEQnHikhUY4K4v4KNpdQ9G8HqML57QbAWw/HGZAbIBAwIDAQAB";

        String encodedKey = "TUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFsT0hBaWZtM0" +
                "l1cFhhTndsbDJ4TEcxazZkbzVnQldpWXhORTc3dzF2YTRpV3puNVA3end0aWtJdDhaVGI3b01wN3QveF" +
                "E1VG1CQUNETmQwZmtKM0o1VzRkTmE1YVllUXB2emFJS1FCZ3NqOTAzL0lKMzM1MGJneVlzeFpRNXh6SE" +
                "hVejRubzRGZ2wzMkVIWE5jUFNCMlF4VktxTmtpWGdtM3l0NkpvZ1Q1eGFEYVppSTFSaDh6bmtRL1Yzdmx" +
                "xT0RNTENpRlhHcHYvT1VVTnhQaTFGeEpoSnYwYUdrSWY2MW5hVnFPV3pwcFkwMjJXaTZlb3pGVXpXMk" +
                "Y3ZGNnM2ROKy9LNG0vSmZ6Sjg0S21xUHBWSkR4UUo2WjRHUldjc25Za0NGK3RDUW5UYkVRbkhpa2hVW" +
                "TRLNHY0S05wZFE5RzhIcU1MNTdRYkFXdy9IR1pBYklCQXdJREFRQUI=";

        assertThat(Base64.encode(publicKey.getBytes())).isEqualTo(encodedKey);

        assertThat(new String(Base64.decode(encodedKey))).isEqualTo(publicKey);

    }
}