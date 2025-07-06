package faang_leetcode;

public class MergeTwoSortedList21 {
    public static ListNode mergeTwoList(ListNode list1,ListNode list2){
        ListNode dummy=new ListNode(1);
        ListNode merge=dummy;

        while(list1!=null && list2!=null){
            if(list1.val<=list2.val){
                merge.next=list1;
                list1=list1.next;
            }else{
                merge.next=list2;
                list2=list2.next;
            }
            merge=merge.next;
        }
        if(merge.next==list1){
            merge.next=list2;
        }else{
            merge.next=list1;
        }
        return dummy.next;
    }
    public static void main(String[] args) {
        ListNode list1=new ListNode(1);
        ListNode node12=new ListNode(2);
        ListNode node13=new ListNode(4);
        list1.next=node12;
        node12.next=node13;
        System.out.println("list1 :"+node13.next);
        ListNode list2=new ListNode(1);
        ListNode node22=new ListNode(3);
        ListNode node23=new ListNode(4);
        list2.next=node22;
        node22.next=node23;
        System.out.println("list2 :"+node23.next);
        ListNode mergeList=mergeTwoList(list1,list2);
        ListNode curr=mergeList;
        while(curr!=null){
            System.out.println("Final merge list : "+curr.val);
            curr=curr.next;
        }

    }
}
