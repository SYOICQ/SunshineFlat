package pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Constans {
    public static List<String> getVrList(){
        List<String> tem = new ArrayList<>();
        tem.add("http://img3.imgtn.bdimg.com/it/u=9385118,3851987353&fm=26&gp=0.jpg");
        tem.add("http://img1.imgtn.bdimg.com/it/u=2399992492,2498359699&fm=26&gp=0.jpg");
        tem.add("http://img4.imgtn.bdimg.com/it/u=3715244691,1316307005&fm=26&gp=0.jpg");
        tem.add("http://img0.imgtn.bdimg.com/it/u=729774959,187632613&fm=26&gp=0.jpg");
        tem.add("http://img4.imgtn.bdimg.com/it/u=4098565275,2265249587&fm=26&gp=0.jpg");
        tem.add("http://img0.imgtn.bdimg.com/it/u=216973429,269526539&fm=26&gp=0.jpg");
        tem.add(" http://img2.imgtn.bdimg.com/it/u=3522480664,3449933708&fm=26&gp=0.jpg");
        tem.add("http://img5.imgtn.bdimg.com/it/u=3692161836,1328131329&fm=26&gp=0.jpg");
        tem.add("https://img.zcool.cn/community/010071595e5c1ba8012193a39418e2.jpg");
        tem.add("http://img0.imgtn.bdimg.com/it/u=3440183004,234158802&fm=26&gp=0.jpg");
        tem.add("http://img3.imgtn.bdimg.com/it/u=10041280,418987289&fm=26&gp=0.jpg");
        tem.add("http://img0.imgtn.bdimg.com/it/u=3062240931,645403563&fm=26&gp=0.jpg");
        tem.add("http://img0.imgtn.bdimg.com/it/u=3622615107,2834644285&fm=26&gp=0.jpg");
        tem.add("http://img2.imgtn.bdimg.com/it/u=3889509719,574041427&fm=26&gp=0.jpg");
        tem.add("http://img3.imgtn.bdimg.com/it/u=2147297065,3674749446&fm=26&gp=0.jpg");
        tem.add("http://img0.imgtn.bdimg.com/it/u=3188673745,529185587&fm=26&gp=0.jpg");
        tem.add("http://img2.imgtn.bdimg.com/it/u=2759874929,1907893777&fm=26&gp=0.jpg");
        tem.add("http://img1.imgtn.bdimg.com/it/u=2228450099,1315447298&fm=26&gp=0.jpg");
        tem.add("http://img2.imgtn.bdimg.com/it/u=1542115323,1685067779&fm=26&gp=0.jpg");
        tem.add("http://img2.imgtn.bdimg.com/it/u=3734533853,890989927&fm=26&gp=0.jpg");
        tem.add("http://img5.imgtn.bdimg.com/it/u=3455804869,2009605778&fm=26&gp=0.jpg");
        tem.add("http://img0.imgtn.bdimg.com/it/u=2367584592,92726064&fm=11&gp=0.jpg");
        tem.add("http://img4.imgtn.bdimg.com/it/u=2354852450,1902692912&fm=11&gp=0.jpg");
        tem.add("http://img4.imgtn.bdimg.com/it/u=3052503324,3776720372&fm=11&gp=0.jpg");
        tem.add("http://img5.imgtn.bdimg.com/it/u=1477059259,3708312476&fm=26&gp=0.jpg");
        tem.add("http://img2.imgtn.bdimg.com/it/u=2729170110,802485188&fm=26&gp=0.jpg");
        HashSet<Integer> hs = new HashSet<Integer>();
        while (true) {
            int a = (int) (Math.random() * tem.size());
            if (a >= 0 && a < tem.size()) {
                hs.add(a);
            }
            if (hs.size() == 5) {
                break;
            }
        }

        List<String> result  = new ArrayList<>();
        for (Integer s:hs) {
            result.add(tem.get(s));
        }
        return result;
    }
}
