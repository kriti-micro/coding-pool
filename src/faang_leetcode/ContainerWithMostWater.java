package faang_leetcode;

import java.util.Arrays;

public class ContainerWithMostWater {
    public static int maxArea(int[] height) {
        System.out.println("height = " + Arrays.toString(height));
        int left=0;
        int right=height.length-1;
        int max=0;
        while(left<right){
            int width=right-left;
            // Note : we are using min height for area
            int area=Math.min(height[left],height[right])*width;
            max=Math.max(max,area);
            System.out.println("max = " + max+" area = "+area+" width = "+width+" left height = "+height[left]+" right height = "+height[right]);
            if(height[left]<height[right]){
                left++;
            }else{
                right--;
            }

        }
        return max;
    }

    public static void main(String[] args) {
        int maxArea=maxArea(new int[]{1,8,6,2,5,4,8,3,7});
        System.out.println("Max Area = " + maxArea);
    }
}
