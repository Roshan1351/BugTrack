package com.bugtrack.bugtrack.util;

import org.springframework.stereotype.Component;

@Component
public class StringSimilarityUtil {
    public int levenshteinDistance(String s1, String s2){ //using dynamic programming...
        s1= s1.toLowerCase().trim();
        s2= s2.toLowerCase().trim();

        int len1= s1.length();
        int len2= s2.length();

        int[][] dp= new int[len1+1][len2+1];
        for(int i= 0; i<=len1; i++){
            dp[i][0]= i;
        }
        for(int i= 0;i<=len2; i++){
            dp[0][i]= i;
        }

        for(int i= 1; i<=len1; i++){
            for(int j= 1; j<=len2; j++){
                if(s1.charAt(i-1)==s2.charAt(j-1)){ //for same character
                    dp[i][j]= dp[i-1][j-1];
                }else{
                    dp[i][j]= 1+Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]));
                }
            }
        }
        return dp[len1][len2];
    }

    public double similarpercentage(String s1, String s2){
        if(s1== null || s2==null) return 0.0; //0 percentage not equal
        if(s1.equals(s2)){ //full equal 100 percentage
            return 100.0;
        }
        int maxlen= Math.max(s1.length(), s2.length());
        if(maxlen==0) return 100.0;
        int distance= levenshteinDistance(s1, s2);
        return (1.0-(double) distance/maxlen)*100;
    }

    public boolean issimilar(String s1, String s2, double threshold){//threshold for criteria or condition like 70%, 60% etc
        return similarpercentage(s1, s2)>=threshold; //return true or false value.
    }
}
