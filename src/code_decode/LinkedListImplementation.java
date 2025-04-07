package code_decode;

public class LinkedListImplementation {
    Node head;//Require head to start

    static class Node{
        Integer data;
        Node next;

        public Node(Integer data) {
            this.data = data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Integer getData() {
            return data;
        }

        public void setData(Integer data) {
            this.data = data;
        }
    }
    public void printLL(Node head){
        Node temp=head;
        //require temp node to iterate
        while(temp.next!=null){
            System.out.print(temp.data+"-->");
            temp=temp.next;
        }
        System.out.print(temp.data);
    }

    public static void main(String ...args){
        Node first=new Node(7);
        Node second =new Node(9);
        Node third =new Node(0);
        first.next=second;
        second.next=third;
        third.next=null; //End with null as tail
        LinkedListImplementation ll=new LinkedListImplementation();
        ll.head=first;
        ll.printLL(ll.head);
    }
}
