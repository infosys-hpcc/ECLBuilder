package org.hpccsystems.eclBuilder.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

public class PasswordUtil {

	static SecretKey key;
	static Cipher ecipher;
	static Cipher dcipher;
	static {
		try {
			key = KeyGenerator.getInstance("DES").generateKey();
			ecipher = Cipher.getInstance("DES");
			dcipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException e) {
			// logger
		} catch (NoSuchPaddingException e) {
			// logger
		} catch (InvalidKeyException e) {
			// logger
		}
	}

	public static String getEncryptedPassword(String password) {

		String encryptedPasswd = null;
		try {
			byte[] utf8 = password.getBytes("UTF8");
			byte[] enc = ecipher.doFinal(utf8);
			enc = BASE64EncoderStream.encode(enc);
			encryptedPasswd = new String(enc);
		} catch (Exception e) {
			// write logger
		}

		return encryptedPasswd;

	}

	public static String decrypt(String str) {
		String decryptedPasswd = null;
		try {
			byte[] dec = BASE64DecoderStream.decode(str.getBytes());
			byte[] utf8 = dcipher.doFinal(dec);
			decryptedPasswd = new String(utf8, "UTF8");
		} catch (Exception e) {
			// write logger
		}
		return decryptedPasswd;
	}
}