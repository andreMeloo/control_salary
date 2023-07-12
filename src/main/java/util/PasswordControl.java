package util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordControl {
    public static String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create();
        try {
            return argon2.hash(4, 65536, 1, password.toCharArray());
        } finally {
            argon2.wipeArray(password.toCharArray());
        }
    }

    public static boolean verifyPassword(String password, String hash) {
        Argon2 argon2 = Argon2Factory.create();
        try {
            return argon2.verify(hash, password.toCharArray());
        } finally {
            argon2.wipeArray(password.toCharArray());
        }
    }
}
