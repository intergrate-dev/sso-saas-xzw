package com.founder.sso.test;

import com.founder.sso.admin.util.Encodes;
import com.founder.sso.admin.utils.Digests;
import sun.security.provider.MD5;

/**
 * Created by yuan-pc on 2018/11/8.
 */
public class EntryptTest {
    public static final String DEFAULT_HASH_ALGORITHM = "SHA-1";

    public static String entryptPassword(String plainPwd, String salt, String ALGORITHM, int iterations) {
        return Encodes.encodeHex(Digests.sha1(plainPwd.getBytes(), Encodes.decodeHex(salt), iterations));
    }

    public static void entryptPassword(String plainPwd, String salt) {
        String ss = entryptPassword(plainPwd, salt, DEFAULT_HASH_ALGORITHM, 2);
        System.out.println("ss: " + ss);
    }

    public static void main(String[] args) {
        String password_MD5 = "6005545589ffb607bc7f9f90ce44e31d";
        String salt = "240976efeb681271";
        entryptPassword(password_MD5, salt);
    }
}
