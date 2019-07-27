package org.jeecg.modules.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Author: Roylion
 * @Description: AES算法封装
 * @Date: Created in 9:46 2018/8/9
 * AES加密模式：ECB
       填充：pkcs5padding
       数据块：128位
       输出：base64
       字符集：utf8
 */
public class AES128Util {
	/**
	 * 加密算法
	 */
	private static final String ENCRY_ALGORITHM = "AES";

	/**
	 * 加密算法/加密模式/填充类型 本例采用AES加密，ECB加密模式，PKCS5Padding填充
	 */
	private static final String CIPHER_MODE = "AES/ECB/PKCS5Padding";

	/**
	 * 设置iv偏移量 本例采用ECB加密模式，不需要设置iv偏移量
	 */
	private static final String IV_ = null;

	/**
	 * 设置加密字符集 本例采用 UTF-8 字符集
	 */
	private static final String CHARACTER = "UTF-8";

	/**
	 * 设置加密密码处理长度。 不足此长度补0；
	 */
	private static final int PWD_SIZE = 16;

	/**
	 * 密码处理方法 如果加解密出问题， 请先查看本方法，排除密码长度不足补"0",导致密码不一致
	 * @param password 待处理的密码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] pwdHandler(String password) throws UnsupportedEncodingException {
		byte[] data = null;
		if (password == null) {
			password = "";
		}
		StringBuffer sb = new StringBuffer(PWD_SIZE);
		sb.append(password);
		while (sb.length() < PWD_SIZE) {
			sb.append("0");
		}
		if (sb.length() > PWD_SIZE) {
			sb.setLength(PWD_SIZE);
		}
		data = sb.toString().getBytes("UTF-8");
		return data;
	}

	// ======================>原始加密<======================

	/**
	 * 原始加密
	 * 
	 * @param clearTextBytes 明文字节数组，待加密的字节数组
	 * @param pwdBytes       加密密码字节数组
	 * @return 返回加密后的密文字节数组，加密错误返回null
	 */
	public static byte[] encrypt(byte[] clearTextBytes, byte[] pwdBytes) {
		try {
			// 1 获取加密密钥
			SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);

			// 2 获取Cipher实例
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);

			// 查看数据块位数 默认为16（byte） * 8 =128 bit
//	            System.out.println("数据块位数(byte)：" + cipher.getBlockSize());

			// 3 初始化Cipher实例。设置执行模式以及加密密钥
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			// 4 执行
			byte[] cipherTextBytes = cipher.doFinal(clearTextBytes);

			// 5 返回密文字符集
			return cipherTextBytes;

		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 原始解密
	 * 
	 * @param cipherTextBytes 密文字节数组，待解密的字节数组
	 * @param pwdBytes        解密密码字节数组
	 * @return 返回解密后的明文字节数组，解密错误返回null
	 */
	public static byte[] decrypt(byte[] cipherTextBytes, byte[] pwdBytes) {

		try {
			// 1 获取解密密钥
			SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);

			// 2 获取Cipher实例
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);

			// 查看数据块位数 默认为16（byte） * 8 =128 bit
//	            System.out.println("数据块位数(byte)：" + cipher.getBlockSize());

			// 3 初始化Cipher实例。设置执行模式以及加密密钥
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

			// 4 执行
			byte[] clearTextBytes = cipher.doFinal(cipherTextBytes);

			// 5 返回明文字符集
			return clearTextBytes;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 解密错误 返回null
		return null;
	}

	// ======================>BASE64<======================

	/**
	 * BASE64加密
	 * @param clearText 明文，待加密的内容
	 * @param password  密码，加密的密码
	 * @return 返回密文，加密后得到的内容。加密错误返回null
	 */
	public static String encryptBase64(String clearText, String password) {
		try {
			// 1 获取加密密文字节数组
			byte[] cipherTextBytes = encrypt(clearText.getBytes(CHARACTER), pwdHandler(password));

			// 2 对密文字节数组进行BASE64 encoder 得到 BASE6输出的密文
			/*
			 * BASE64Encoder base64Encoder = new BASE64Encoder();
			String cipherText = base64Encoder.encode(cipherTextBytes);
			*/
			String cipherText =Base64.getEncoder().encodeToString(cipherTextBytes);
			// 3 返回BASE64输出的密文
			return cipherText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 加密错误 返回null
		return null;
	}

	/**
	 * BASE64解密
	 * 
	 * @param cipherText 密文，带解密的内容
	 * @param password   密码，解密的密码
	 * @return 返回明文，解密后得到的内容。解密错误返回null
	 */
	public static String decryptBase64(String cipherText, String password) {
		try {
			// 1 对 BASE64输出的密文进行BASE64 decodebuffer 得到密文字节数组
			/*BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] cipherTextBytes = base64Decoder.decodeBuffer(cipherText);*/
			byte[] cipherTextBytes =Base64.getDecoder().decode(cipherText);
			// 2 对密文字节数组进行解密 得到明文字节数组
			byte[] clearTextBytes = decrypt(cipherTextBytes, pwdHandler(password));
			// 3 根据 CHARACTER 转码，返回明文字符串
			return new String(clearTextBytes, CHARACTER);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		// 解密错误返回null
		return null;
	}

	// ======================>HEX<======================

	/**
	 * HEX加密
	 * 
	 * @param clearText 明文，待加密的内容
	 * @param password  密码，加密的密码
	 * @return 返回密文，加密后得到的内容。加密错误返回null
	 */
	public static String encryptHex(String clearText, String password) {
		try {
			// 1 获取加密密文字节数组
			byte[] cipherTextBytes = encrypt(clearText.getBytes(CHARACTER), pwdHandler(password));
			// 2 对密文字节数组进行 转换为 HEX输出密文
			String cipherText = byte2hex(cipherTextBytes);
			// 3 返回 HEX输出密文
			return cipherText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 加密错误返回null
		return null;
	}

	/**
	 * HEX解密
	 * 
	 * @param cipherText 密文，带解密的内容
	 * @param password   密码，解密的密码
	 * @return 返回明文，解密后得到的内容。解密错误返回null
	 */
	public static String decryptHex(String cipherText, String password) {
		try {
			// 1 将HEX输出密文 转为密文字节数组
			byte[] cipherTextBytes = hex2byte(cipherText);

			// 2 将密文字节数组进行解密 得到明文字节数组
			byte[] clearTextBytes = decrypt(cipherTextBytes, pwdHandler(password));

			// 3 根据 CHARACTER 转码，返回明文字符串
			return new String(clearTextBytes, CHARACTER);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 解密错误返回null
		return null;
	}

	/* 字节数组转成16进制字符串 */
	public static String byte2hex(byte[] bytes) { // 一个字节的数，
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		String tmp = "";
		for (int n = 0; n < bytes.length; n++) {
			// 整数转成十六进制表示
			tmp = (Integer.toHexString(bytes[n] & 0XFF));
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString().toUpperCase(); // 转成大写
	}

	/* 将hex字符串转换成字节数组 */
	private static byte[] hex2byte(String str) {
		if (str == null || str.length() < 2) {
			return new byte[0];
		}
		str = str.toLowerCase();
		int l = str.length() / 2;
		byte[] result = new byte[l];
		for (int i = 0; i < l; ++i) {
			String tmp = str.substring(2 * i, 2 * i + 2);
			result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
		}
		return result;
	}

	public static void main(String[] args) {
		/*String key="1234567800000000";
		String text="hell worlod!hell worlod!hell worlod!hell worlod!hell worlod!hell worlod!hell worlod!hell worlod!hell worlod!";
		
		//hex
		StopWatch sw2=new StopWatch();
		sw2.start();
		String result2 = encryptHex(text, key);
		System.err.println(result2);
		System.err.println(decryptHex(result2, key));
		System.err.println("执行时间："+sw2.getTime());
		
		StopWatch sw=new StopWatch();
    	sw.start();
		//base64
		String result=encryptBase64(text, key);
		System.err.println(result);
		System.err.println(decryptBase64(result, key));
		System.err.println("执行时间："+sw.getTime());*/
		
		
		/*String key="57c30eeb";
		String id="1410065445";
		
		String aeskey="mtizndu2zmfkc2zh";
		
		Long time=System.currentTimeMillis();
		
		Wmessage msg=new Wmessage();
		msg.setType(1);
		msg.setDataobj(new JSONObject());
		String resstr=JSON.toJSONString(msg);
		String data=encryptBase64(resstr, aeskey);
		
		String sign=DigestUtils.md5Hex(data+time+key);
		
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("data", data);
		map.put("sign", sign);
		map.put("time", time);
		map.put("id", id);
		System.err.println(map);*/
		
		
		
		
		
		
		
		
		
		
		
		
	}
}