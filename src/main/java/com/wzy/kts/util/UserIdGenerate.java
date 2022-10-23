package com.wzy.kts.util;

import java.util.*;

/**
 * @author yu.wu
 * @description userId和GroupId生成，规则：id_14位随机数 ，id_group_14位随机数
 * @date 2022/10/23 16:56
 */
public class UserIdGenerate {

    private final static String DATA_SOURCE =  "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V," +
            "W,S,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,s,y,z";

    private final static List<String> SOURCE_LIST = Arrays.asList(DATA_SOURCE.split(","));

    private final static Integer LEN = 14;

    private final static Random RANDOM = new Random();

    /**
     * @description 生成userId
     * @return
     */
    public String generateUserId(){
        // TODO: 2022/10/23 shuffle方法不知道什么作用
        Collections.shuffle(SOURCE_LIST);
        StringBuilder userId = new StringBuilder("id_");
        for (int i = 0; i < LEN; i++) {
            userId.append(SOURCE_LIST.get(RANDOM.nextInt(62)));
        }
        return userId.toString();
    }

    public String generateGroupId(){
        Collections.shuffle(SOURCE_LIST);
        StringBuilder groupId = new StringBuilder("id_group_");
        for (int i = 0 ; i < LEN ; i++){
            groupId.append(SOURCE_LIST.get(RANDOM.nextInt(62)));
        }
        return groupId.toString();
    }
}
