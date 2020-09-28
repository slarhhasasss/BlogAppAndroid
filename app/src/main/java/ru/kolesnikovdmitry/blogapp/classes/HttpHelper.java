package ru.kolesnikovdmitry.blogapp.classes;

import java.util.HashMap;
import java.util.Set;

public class HttpHelper {

    //сюда надо вписывать ip-адрес компьютера, на котором запущен локальный сервер
    private static final String BASE_URL = "http://192.168.1.67:8081";

    public static String getBASE_URL() {
        return BASE_URL;
    }

    public static final String RESP_OK       = "200";
    public static final String RESP_NOT_FOUND = "404";
    public static final String RESP_WRONG_PASS = "201";
    public static final String RESP_WRONG_LOGIN = "202";
    public static final String RESP_LOGIN_EXISTS = "203";
    public static final String RESP_SERVER_ERROR  = "500";
    public static final String RESP_UNUSUAL_ERROR  = "505";
    public static final String RESP_INTERNAL_ERROR  = "101";



    //Эта функция превращает словарь в http запрос и возвращает переделанную для запроса строку,
    // чтобы его прибавить к url и отпрвить на сервер. Эту строку нужно просто добавить к url.
    public static String getStringForQuery(HashMap<String, String[]> hashMap){
        StringBuilder sb = new StringBuilder("");
        Set<String> keySet = hashMap.keySet();
        for (String key : keySet) {
            int j = 0;
            while (j < hashMap.get(key).length) {
                String curVal = hashMap.get(key)[j];
                int i = 0;
                while (i < curVal.length()) {
                    if (curVal.charAt(i) == ' ') {
                        curVal = curVal.replace(' ', '+');
                    }
                    i++;
                }
                sb.append(key).append("=").append(curVal).append("&");
                j++;
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
