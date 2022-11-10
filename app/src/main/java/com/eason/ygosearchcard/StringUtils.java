package com.eason.ygosearchcard;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

   static public boolean isNum(String str){
        if (isNull(str)){
            return false;
        }
       String bigStr;
       try {
           bigStr = new BigDecimal(str).toString();
       } catch (Exception e) {
           return false;//异常 说明包含非数字。
       }
       return true;
    }

    static public boolean isEnglisth(String str){
        if (isNull(str)){
            return false;
        }
        if (isNum(str)){
            return false;
        }
        if (str.length()<3){
            return false;
        }

        String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？-]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        str = m.replaceAll(" ").trim();
        str =str.replaceAll(" ","");
        boolean result = str.matches("^[a-z0-9A-Z]+$");
        return result;
    }

    static public boolean isName(String str){
        if (str.contains("ｰ")||str.contains("・")||str.contains("一")){
            return true;
        }

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()&&str.length()>2){
            return true;
        }

        return false;
    }

    static public boolean isJAP(String str){
        if (isNull(str)){
            return false;
        }
        if (isNum(str)){
            return false;
        }
        if (str.length()<2){
            return false;
        }



        String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？ -·]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        str = m.replaceAll(" ").trim();
        str =str.replaceAll(" ","");
        boolean result = false;
        for (int i = 0; i < str.length(); i++) {
            Character.UnicodeScript ub = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ub = Character.UnicodeScript.of(str.charAt(i));
            }
            //日文分平假名和片假名
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if(ub == Character.UnicodeScript.HIRAGANA || ub == Character.UnicodeScript.KATAKANA) {
                    result= true;
                    break;
                }
            }
        }
        return result;
    }

    static public String cardName(String cardName){
        if (cardName.contains("・")){
            String[] names = cardName.split("・");
            cardName = names[0];
        }
        if (cardName.contains("-")){
            String[] names = cardName.split("-");
            cardName = names[1];
        }
       cardName = cardName.replace("刻","剣").replace("则","剣");
       return cardName;
    }

    static  public boolean isNull(String str){
        if (str==null||str==""){
            return true;
        }
        return false;
    }
}
