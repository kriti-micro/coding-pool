package faang_leetcode;

//Time n Space O(L) no of nodes in list ,O(1)
public class RemoveNthNodefromEndOfList19 {

    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next=head;

        ListNode back=dummy;
        ListNode fast=dummy;
        for (int i = 0; i <=n ; i++) {
            fast=fast.next;
        }

        while(fast!=null){
            back=back.next;
            fast=fast.next;
        }

        back.next=back.next.next;

        //Returning head blindly fails when the first node is removed, because head never updates.
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
        // Example: head = [1,2,3,4,5], n = 2 → remove 4
        ListNode head = new ListNode(1,
                new ListNode(2,
                        new ListNode(3,
                                new ListNode(4,
                                        new ListNode(5)))));

        System.out.print("Original list: ");
        printList(head);

        head = removeNthFromEnd(head, 2);

        System.out.print("After removing 2nd from end: ");
        printList(head);
        // Expected output: 1 2 3 5

    }
}