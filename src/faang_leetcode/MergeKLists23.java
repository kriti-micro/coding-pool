package faang_leetcode;

import java.util.PriorityQueue;

public class MergeKLists23 {
    public static ListNode mergeKLists(ListNode[] lists){

        PriorityQueue<Integer> minHeap=new PriorityQueue<>();
        for (ListNode list:lists){
            System.out.println(" List :"+list);
            while(list!=null){
                System.out.println("value : "+list.val);
                minHeap.add(list.val);
                list=list.next;
            }
        }

        ListNode dummy=new ListNode(0);
        ListNode merge=dummy;

        while(!minHeap.isEmpty()){
            merge.next=new ListNode(minHeap.remove());
            merge=merge.next;
        }
        return dummy.next;
    }

    public static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Example: merging 3 lists
        ListNode[] lists = new ListNode[3];

        lists[0] = new ListNode(1);
        lists[0].next = new ListNode(4);
        lists[0].next.next = new ListNode(5);

        lists[1] = new ListNode(1);
        lists[1].next = new ListNode(3);
        lists[1].next.next = new ListNode(4);

        lists[2] = new ListNode(2);
        lists[2].next = new ListNode(6);

        ListNode merged = mergeKLists(lists);
        printList(merged);
        // Output: 1 1 2 3 4 4 5 6
    }
}
