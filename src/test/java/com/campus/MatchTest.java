package com.campus;

import com.campus.entity.LostFound;
import com.campus.util.MatchUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 失物招领智能匹配算法自测。
 * 构造一条「失物」与若干「拾物」样例，打印各自的相似度得分与匹配理由，
 * 用于直观验证评分算法是否符合预期（高相似的排在前面）。
 * 直接运行 main 方法即可，无需数据库与第三方测试框架。
 */
public class MatchTest {

    public static void main(String[] args) {
        // 源信息：丢失的黑色钱包
        LostFound lost = make(1, "黑色钱包", 5, "三号教学楼",
                "黑色真皮钱包，内有校园卡和少量现金", daysAgo(1));

        // 候选拾物
        LostFound foundWallet = make(2, "钱包", 5, "三教一楼",
                "捡到一个黑色钱包，里面有校园卡", daysAgo(0));        // 期望：高相似
        LostFound foundLeather = make(2, "黑色皮夹", 5, "教学楼",
                "黑色皮夹一个，内含银行卡", daysAgo(3));               // 期望：中高
        LostFound foundCup = make(2, "蓝色保温杯", 8, "图书馆",
                "不锈钢保温杯一个", daysAgo(2));                       // 期望：低（应被过滤）

        System.out.println("=== 失物招领智能匹配算法自测 ===");
        print(lost, foundWallet);
        print(lost, foundLeather);
        print(lost, foundCup);
    }

    private static void print(LostFound a, LostFound b) {
        System.out.printf("[%s] ←→ [%s]  相似度 %3d%%   理由：%s%n",
                a.getName(), b.getName(), MatchUtil.score(a, b), MatchUtil.reason(a, b));
    }

    private static LostFound make(int type, String name, int categoryId,
                                  String place, String feature, Date happenTime) {
        LostFound lf = new LostFound();
        lf.setType(type);
        lf.setName(name);
        lf.setCategoryId(categoryId);
        lf.setPlace(place);
        lf.setFeature(feature);
        lf.setHappenTime(happenTime);
        return lf;
    }

    private static Date daysAgo(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }
}
