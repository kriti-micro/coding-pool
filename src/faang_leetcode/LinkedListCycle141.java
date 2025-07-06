package faang_leetcode;

import java.util.Objects;

//Time n space-O(n), O(1)
public class LinkedListCycle141 {
    public static boolean hasCycle(CycleNode head){
        if(head==null){
            return false;
        }
        CycleNode slow=head;
        CycleNode fast=head.next;

        while(slow!=null || fast!=null){
            System.out.println(" slow node details : "+slow+" "+ Objects.requireNonNull(slow).val+" "+slow.next);
            System.out.println(" fast node details : "+fast+" "+ Objects.requireNonNull(fast).val+" "+fast.next);
            System.out.println("---------------");
            if(fast==null || fast.next==null){
                return false;
            }
            if(slow==fast){
                return true;
            }
            slow=slow.next;
            fast=fast.next.next;
        }
        return false;
    }

    public static void main(String[] args) {
        CycleNode node1=new CycleNode(1);
        CycleNode node2=new CycleNode(2);
        CycleNode node3=new CycleNode(3);
        CycleNode node4=new CycleNode(4);
        System.out.println(" all nodes :"+node1+" "+node2+" "+node3+" "+node4);
        System.out.println("---------------");
        node1.next=node2;
        node2.next=node3;
        node3.next=node4;
        node4.next=node2;
        boolean cycleFlag=hasCycle(node1);
        System.out.println("The cycle is present in Linked List ="+cycleFlag);
    }
}

class CycleNode {
    int val;
    CycleNode next;
    CycleNode(int x) {
        val = x;
        next = null;
    }
}
