import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by stcn501a0032 on 15-7-22.
 */
public class Statics {
    static  File alog = new File("jp.a.log");
    static  File elog = new File("jp.e.log");
    Scanner input = null;

    static Map<Integer,Set<String>> caidToSpid = new HashMap<Integer,Set<String>>();//广告活动id与排期id的一对多映射关系
    static Map<String,Integer> spidPageView = new HashMap<String,Integer>();//排期id对应的PV；
    static Map<String,Set<String>> spidOldUser= new HashMap<String,Set<String>>(); //排期id对应的老用户
    static Map<String,Set<String>> spidNewUser= new HashMap<String,Set<String>>();//排期id对应的新用户
    static Map<String,Integer> spidImpression = new HashMap<String,Integer>();//spid对应的曝光次数；
    static Map<String,Integer> spidClick = new HashMap<String,Integer>();//spid对应的点击次数；

    public void computing(File file) throws FileNotFoundException{
        input = new Scanner(file);
        while(input.hasNext()){
           String current = input.next();
            String ip = input.next();
            String[] info = current.split("\\^|%26");
            if(info[0].charAt(1)!='r' && info[0].charAt(1)!='x')
                continue;
//            System.out.println(info[0]);
            Integer caid = null;
            String spid = null;
            if(info[0].charAt(7)=='k') {
                caid = Integer.valueOf(info[0].substring(9));
            }
            if(info[1].charAt(0)=='p') {
                spid = info[1].substring(2);
            }
            String userid = "";
            int av = 0;
            int ag = 0;
            for(int i=2;i<info.length;i++){
                if(info[i].length()<4)
                    continue;
                if(info[i].charAt(0)=='a' && info[i].charAt(1)=='=')
                    userid = info[i].substring(2);
                else if(info[i].charAt(0)=='a' && info[i].charAt(1)=='g'&&info[i].charAt(2)=='=')
                    ag = Integer.valueOf(info[i].substring(3));
                else if(info[i].charAt(0)=='a' && info[i].charAt(1)=='v'&&info[i].charAt(2)=='=')
                    av = Integer.valueOf(info[i].substring(3));
                else
                    continue;
            }
//            System.out.println(caid+"  "+spid+" "+userid+" "+av+" "+ag);
            if(!caidToSpid.containsKey(caid)){
                Set<String> spidSet = new HashSet<String>();
                spidSet.add(spid);
                caidToSpid.put(caid,spidSet);
            }else
                caidToSpid.get(caid).add(spid);

            if(spidPageView.containsKey(spid)){
                spidPageView.put(spid,spidPageView.get(spid)+1);
            }else{
                spidPageView.put(spid,1);
            }

            if(av==0){
                if(!spidNewUser.containsKey(spid)){
                    Set<String> userSet = new HashSet<String>();
                    userSet.add(userid);
                    spidNewUser.put(spid,userSet);
                }else{
                    spidNewUser.get(spid).add(userid);
                }
            }else{
                if(!spidOldUser.containsKey(spid)){
                    Set<String> userSet = new HashSet<String>();
                    userSet.add(userid);
                    spidOldUser.put(spid,userSet);
                }else {
                    spidOldUser.get(spid).add(userid);
                }
            }

            if(file == alog){
                if(!spidImpression.containsKey(spid)){
                    spidImpression.put(spid,1);
                }else {
                    spidImpression.put(spid, spidImpression.get(spid)+1);
                }
            }else if(file == elog){
                if(!spidClick.containsKey(spid)){
                    spidClick.put(spid,1);
                }else {
                    spidClick.put(spid, spidClick.get(spid)+1);
                }
            }

        }
    }

    public static void main(String[] args) throws FileNotFoundException{
        Statics sta = new Statics();
        sta.computing(alog);
        sta.computing(elog);
        Set<Integer> caidSet = caidToSpid.keySet();
        for(Integer caid:caidSet){
            if(caid==null)
                continue;
            Set<String> spidSet = caidToSpid.get(caid);
            int sumPV = 0;
            int oldUV = 0;
            int newUV = 0;
            int impression = 0;
            int click = 0;
            System.out.println("广告活动"+caid+"下的各个spid的PV和UV为：");
            for(String spid:spidSet){
                if(spid==null)
                    continue;
                System.out.println("spid:"+spid+"  PV:"+spidPageView.get(spid));
                sumPV += spidPageView.get(spid);
                int olduv = 0;
                int newuv = 0;
                if(spidOldUser.get(spid)!=null) {
                    olduv = spidOldUser.get(spid).size();
                }
                if(spidNewUser.get(spid)!=null) {
                     newuv = spidNewUser.get(spid).size();
                }

                System.out.println("old UV:"+olduv+"    New UV:"+newuv+"    total UV:"+(olduv+newuv));
                oldUV += olduv;
                newUV += newuv;
                if(spidImpression.get(spid)!=null) {
                    System.out.println("曝光量：" + spidImpression.get(spid));
                    impression += spidImpression.get(spid);
                }
                if(spidClick.get(spid)!=null){
                    System.out.println("点击量："+ spidClick.get(spid));
                    click += spidClick.get(spid);
                }

            }
            int totalUV = oldUV+newUV;
            System.out.println(caid+"总的PV为："+sumPV+"    总的UV为"+totalUV);
            System.out.println("总的曝光量为："+impression+"   总的点击量为："+click+"    转化率为： "+click*1.0/impression);
        }

    }
}
