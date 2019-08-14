import Vue from 'vue'
import CryptoJS from 'crypto-js'

//加密方法
export function Encrypt(word, keyStr){
  let srcs = CryptoJS.enc.Utf8.parse(word);
  let key = CryptoJS.enc.Utf8.parse(keyStr);
  let encrypted = CryptoJS.AES.encrypt(srcs, key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 });
  return encrypted.toString().toUpperCase();
}
//解密方法
export function Decrypt(word){
  let encryptedHexStr = CryptoJS.enc.Hex.parse(word);
  let srcs = CryptoJS.enc.Base64.stringify(encryptedHexStr);
  let decrypt = CryptoJS.AES.decrypt(srcs, key, { iv: iv, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 });
  let decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
  return decryptedStr.toString();
}

//MD5
export function MD5(word) {
  let yxcsigns = CryptoJS.MD5(word).toString();
  return yxcsigns;
}