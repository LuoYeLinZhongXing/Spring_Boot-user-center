package com.luoye.usercenter.utils;


import java.util.List;

// 算法工具类
public class AlgorithmUtils {
    // 最小编辑距离(计算最相似字符串)
     public static int minDistance(List<String> tageList1, List<String> tageList2) {
         int n = tageList1.size();
         int m = tageList2.size();

         if (n * m == 0)
             return n + m;

         int[][] d = new int[n + 1][m + 1];
         for (int i = 0; i < n + 1; i++) {
             d[i][0] = i;
         }

         for (int j = 0; j < m + 1; j++) {
             d[0][j] = j;
         }

         for (int i = 1; i < n + 1; i++) {
             for (int j = 1; j < m + 1; j++) {
                 int left = d[i - 1][j] + 1;
                 int down = d[i][j - 1] + 1;
                 int left_down = d[i - 1][j - 1];
                 if (!tageList1.get(i - 1).equals(tageList2.get(j - 1)))
                     left_down += 1;
                 d[i][j] = Math.min(left, Math.min(down, left_down));
             }
         }
         return d[n][m];
     }
}
