package faang_leetcode;


public class ReverseSinglyLinkList206 {

    public static void main(String[] args) {
        ListNode node1=new ListNode(1);
        ListNode node2=new ListNode(2);
        ListNode node3=new ListNode(3);
        ListNode node4=new ListNode(4);
        node1.next=node2;
        node2.next=node3;
        node3.next=node4;
        node4.next=null;

        ListNode result=reverseList(node1);
        System.out.println("After Reverse : ");
        ListNode curr=result;
        while(curr!=null){
           System.out.println(curr.val);
           curr=curr.next;
        }
    }

    private static ListNode reverseList(ListNode head) {
        System.out.println("head = " + head);
        System.out.println("Before Reverse : ");
        ListNode curr1=head;
        while(curr1!=null){
            System.out.println(curr1.val);
            curr1=curr1.next;
        }
        ListNode prev=null;
        ListNode curr=head;

        while(curr!=null){
            ListNode temp=curr.next;
            curr.next=prev;
            prev=curr;
            curr=temp;
        }
        return prev;
    }
}

class ListNode {
     int val;
      ListNode next;
     ListNode() {}
     ListNode(int val) { this.val = val; }
     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
