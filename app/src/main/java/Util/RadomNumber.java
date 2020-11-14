package Util;

import com.zhenzi.sms.ZhenziSmsClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;

public class RadomNumber {

    public  static String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    public static Observable<String> sendCode(String phoneNumber,String code){
        ZhenziSmsClient client = new ZhenziSmsClient("https://sms_developer.zhenzikj.com","104558",  "888712ab-f476-4613-a649-0179e4d04ba7");
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if(!subscriber.isUnsubscribed()){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("message", "验证码为: "+code);
                    params.put("number", phoneNumber);
                    try {
                        String result = client.send(params);
                        subscriber.onNext(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    subscriber.onCompleted();
                }
            }
        });
    }
}
