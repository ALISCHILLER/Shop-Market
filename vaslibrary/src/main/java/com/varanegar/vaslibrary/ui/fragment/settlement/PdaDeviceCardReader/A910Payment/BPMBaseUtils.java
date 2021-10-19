package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910Payment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
/**
 * Created by saeede on 7/14/2021.
 */
public class BPMBaseUtils {

    public BPMBaseUtils() {
    }

    public static String bcd2Str(byte[] bytes) {
        if (bytes == null){
            return "";
        }
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            temp.append((byte)((bytes[i] & 240) >>> 4));
            temp.append((byte)(bytes[i] & 15));
        }

        return temp.toString();
    }

    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if(mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte[] abt = new byte[len];
        if(len >= 2) {
            len /= 2;
        }

        byte[] bbt = new byte[len];
        abt = asc.getBytes();

        for(int p = 0; p < asc.length() / 2; ++p) {
            int j;
            if(abt[2 * p] >= 48 && abt[2 * p] <= 57) {
                j = abt[2 * p] - 48;
            } else if(abt[2 * p] >= 97 && abt[2 * p] <= 122) {
                j = abt[2 * p] - 97 + 10;
            } else {
                j = abt[2 * p] - 65 + 10;
            }

            int k;
            if(abt[2 * p + 1] >= 48 && abt[2 * p + 1] <= 57) {
                k = abt[2 * p + 1] - 48;
            } else if(abt[2 * p + 1] >= 97 && abt[2 * p + 1] <= 122) {
                k = abt[2 * p + 1] - 97 + 10;
            } else {
                k = abt[2 * p + 1] - 65 + 10;
            }

            int a = (j << 4) + k;
            byte b = (byte)a;
            bbt[p] = b;
        }

        return bbt;
    }

    public static String charArr2String(byte[] dataResp, String charset) {
        String str = null;
        try {
            str = new String(dataResp, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean  ipCheck(String text) {
        if (text != null && !"".equals(text)) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static String byteArr2HexStr(byte[] data) {
        StringBuffer sb = new StringBuffer();
        byte[] var5 = data;
        int var4 = data.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            String value = Integer.toHexString(b & 255);
            if(value.length() < 2) {
                sb.append("0");
            }

            sb.append(value);
        }

        return sb.toString().toUpperCase();
    }

    public static byte[] hexStr2ByteArr(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] convertBCD2ByteArr(String bcdStr) {
        byte[] datas = new byte[bcdStr.length() / 2];

        for(int i = 0; i < bcdStr.length(); i += 2) {
            datas[i / 2] = (byte)(Short.valueOf(bcdStr.substring(i, i + 2).toString(), 16).shortValue() & 255);
        }

        return datas;
    }

    public static String stringPattern(String date, String oldPattern, String newPattern) {
        String datetime = null;

        try {
            if(date == null || oldPattern == null || newPattern == null) {
                return "";
            }

            SimpleDateFormat e = new SimpleDateFormat(oldPattern);
            SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern);
            Date d = null;

            try {
                d = e.parse(date);
                datetime = sdf2.format(d);
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return datetime;
    }

    public static byte[] generateRandByteArray(int arraySize) {
        byte[] result = new byte[arraySize];
        (new Random()).nextBytes(result);
        return result;
    }

    public static byte[] bytesXor(byte[] b1, byte[] b2) {
        byte[] xorRet = null;
        if(b1 != null && b2 != null) {
            int b1Len = b1.length;
            int b2Len = b2.length;
            if(b1Len == b2Len) {
                xorRet = new byte[b1Len];

                for(int i = 0; i < b1Len; ++i) {
                    xorRet[i] = (byte)(b1[i] & 255 ^ b2[i] & 255);
                }
            }
        }

        return xorRet;
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static byte[] int2ByteArr(int i) {
        byte[] arr = new byte[]{(byte)(i >>> 24 & 255), (byte)(i >>> 16 & 255), (byte)(i >>> 8 & 255), (byte)(i & 255)};
        return arr;
    }

    public static int byteArrtoInt(byte[] arr) {
        int ret = 0;
        if(arr != null && arr.length == 4) {
            ret = arr[0] << 24 & -1 | arr[1] << 16 & -1 | arr[2] << 8 & -1 | arr[3] & -1;
        }

        return ret;
    }

    public static void reverseArray(byte[] byteArray) {
        int i = 0;

        for(int n = byteArray.length - 1; n > 2 * i; ++i) {
            byte x = byteArray[i];
            byteArray[i] = byteArray[n - i];
            byteArray[n - i] = x;
        }

    }

    public static byte[] arrayPadding(byte[] data, byte b) {
        Object ret = null;
        int len = data.length;
        int residue = len % 8;
        byte[] var9;
        if(residue != 0) {
            int fillLen = 8 - residue;
            int newLen = len + fillLen;
            var9 = new byte[newLen];
            byte[] padding = new byte[fillLen];

            for(int i = 0; i < fillLen; ++i) {
                padding[i] = b;
            }

            System.arraycopy(data, 0, var9, 0, len);
            System.arraycopy(padding, 0, var9, len, fillLen);
        } else {
            var9 = data;
        }

        return var9;
    }

    public static boolean isNetworkConnected(Context context) {
        if(context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    public static void vibrate(Context context, long milliseconds) {
        Vibrator vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public static String formatTime(String src, String word) {
        int len = src.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i += 2) {
            sb.append(src.substring(i, i + 2));
            sb.append(word);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String formatDate(String src) {
        int len = src.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i += 2) {
            sb.append(src.substring(i, i + 2));
            sb.append("/");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String convertToIp(String ip){
        StringBuffer sb = new StringBuffer();
        String prefix= "";
        if (ip.length() < 12){
            return "";
        }
        for (int i = 0; i < 4; i++){
            String ipPart = ip.substring(i*3,(i+1)*3);
            sb.append(prefix);
            sb.append(String.valueOf(Integer.valueOf(ipPart)));
            prefix = ".";
        }
        return sb.toString();
    }
}
