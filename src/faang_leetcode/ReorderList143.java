package faang_leetcode;

public class ReorderList143 {
    public static void main(String[] args) {
        ListNode head=new ListNode(1);
        ListNode curr=head;
        for (int i = 2; i <=5; i++) {
            curr.next=new ListNode(i);
            curr=curr.next;
        }
        System.out.print("----Before: "); printList(head);

        reorderList(head);

        System.out.print("-----After:  "); printList(head);
        // Output:
        // Before: 1→2→3→4→5
        // After:  1→5→2→4→3
    }

    private static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + (head.next!=null ? "→" : ""));
            head = head.next;
        }
        System.out.println();
    }

    private static void reorderList(ListNode head) {
        if(head==null){
            return;
        }

        //1.Middle of the list
        ListNode fast=head,slow=head;
        while(fast!=null && fast.next!=null){
            slow=slow.next;
            fast=fast.next.next;
        }
        System.out.println("Middle node (start of second half before reverse): " + slow.val);
        System.out.print("The list with head : ");
        printList(head);
        //2.Reverse the list
        ListNode prev=null,curr=slow,temp;
        while(curr!=null){
            temp=curr.next;
            curr.next=prev;
            prev=curr;
            curr=temp;
        }

        System.out.print("First half: ");
        printList(head); // from head to slow (exclusive) 1->2->3 coz after reverse the 3 node is having null

        System.out.print("Reversed second half: ");
        printList(prev); // from prev to null (reversed second half)

        //3.Merge 2 halves
        ListNode first=head,second=prev;
        while(second.next!=null){
            System.out.println(" Before First "+first.val);
            temp=first.next;
            first.next=second;
            first=temp;
            System.out.println(" After First "+first.val);

            System.out.println(" Before Second "+second.val);
            temp=second.next;
            second.next=first;
            second=temp;
            System.out.println(" Before Second "+second.val);
        }
    }
}
