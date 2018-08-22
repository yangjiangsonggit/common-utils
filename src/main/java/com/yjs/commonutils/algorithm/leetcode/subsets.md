leetcode 78. Subsets-数组子集|回溯算法
【思路1-Java】回溯算法|递归实现

本解法采用回溯算法实现，回溯算法的基本形式是“递归+循环”，正因为循环中嵌套着递归，递归中包含循环，这才使得回溯比一般的递归和单纯的循环更难理解，其实我们熟悉了它的基本形式，就会觉得这样的算法难度也不是很大。原数组中的每个元素有两种状态：存在和不存在。

① 外层循环逐一往中间集合 temp 中加入元素 nums[i]，使这个元素处于存在状态

② 开始递归，递归中携带加入新元素的 temp，并且下一次循环的起始是 i 元素的下一个，因而递归中更新 i 值为 i + 1

③ 将这个从中间集合 temp 中移除，使该元素处于不存在状态

public class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        List<Integer> temp = new ArrayList<Integer>();
        dfs(res, temp, nums, 0);
        return res;
    }
    private void dfs(List<List<Integer>> res, List<Integer> temp, int[] nums, int j) {
        res.add(new ArrayList<Integer>(temp));
        for(int i = j; i < nums.length; i++) {
            temp.add(nums[i]);  //① 加入 nums[i]
            dfs(res, temp, nums, i + 1);  //② 递归
            temp.remove(temp.size() - 1);  //③ 移除 nums[i]
        }
    }
}
10 / 10 test cases passed. Runtime: 2 ms  Your runtime beats 61.73% of javasubmissions.

【思路2-Java、Python】组合|非递归实现

这种方法是一种组合的方式

① 最外层循环逐一从 nums 数组中取出每个元素 num

② 内层循环从原来的结果集中取出每个中间结果集，并向每个中间结果集中添加该 num 元素

③往每个中间结果集中加入 num

④将新的中间结果集加入结果集中

public class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        res.add(new ArrayList<Integer>());
        for (int num : nums) {  // ①从数组中取出每个元素
            int size = res.size();
            for (int i = 0; i < size; i++) {
                List<Integer> temp = new ArrayList<>(res.get(i));  // ②逐一取出中间结果集
                temp.add(num);  // ③将 num 放入中间结果集
                res.add(temp);  // ④加入到结果集中
            }
        }
        return res;
    }
}
10 / 10 test cases passed. Runtime: 2 ms  Your runtime beats 61.73% of javasubmissions.

class Solution(object):
    def subsets(self, nums):
        """
        :type nums: List[int]
        :rtype: List[List[int]]
        """
        res = [[]]
        for num in nums :
            for temp in res[:] :
                x = temp[:]
                x.append(num)
                res.append(x)
        return res
10 / 10 test cases passed. Runtime: 52 ms  Your runtime beats 98.24% of pythonsubmissions.
