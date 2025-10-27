package faang_leetcode;

import java.util.*;

public class LRUCache {
    private static class Node{
        Node prev;
        int key;
        int value;
        Node next;

        public Node(int key,int value){
            this.key=key;
            this.value=value;
        }
    }

    private final int capacity;
    private final Map<Integer,Node> map;
    private final Node head;
    private final Node tail;

    LRUCache(int capacity){
        this.capacity=capacity;
        map=new HashMap<>();
        head=new Node(0,0);
        tail=new Node(0,0);
        head.next=tail;
        tail.prev=head;
    }

    public int get(int key){
        if(map.containsKey(key)){
            Node node=map.get(key);
            remove(node);
            insertToHead(node);
            System.out.println();
            printDoublyLinkedList();
            System.out.println();
            return node.value;
        }
        return -1;
    }

    public void put(int key,int value){


            if(map.containsKey(key)){
                Node node=map.get(key);
                node.value=value;
                remove(node);
                insertToHead(node);
            }else {
                if(map.size()==capacity) {
                    map.remove(tail.prev.key);
                    remove(tail.prev);

                    printDoublyLinkedList();
                    System.out.println();
                }
                Node newNode=new Node(key,value);
                map.put(key,newNode);
                insertToHead(newNode);
            }


    }

    public void remove(Node node){
        node.prev.next=node.next;
        node.next.prev=node.prev;

    }

    public void insertToHead(Node node){
            node.next=head.next;
            node.prev=head;
            head.next.prev=node;
            head.next=node;
    }

    public void printDoublyLinkedList(){
        Node temp=head;
        while(temp!=null){
            if(temp.next==null){
                System.out.print("[" + temp.key + ":" + temp.value + "]");
            }else {
                System.out.print("[" + temp.key + ":" + temp.value + "] -> ");
            }
            temp=temp.next;
        }
    }

    public static void main(String[] args) {
        LRUCache lRUCache=new LRUCache(2);
        lRUCache.printDoublyLinkedList();
        System.out.println("");
        lRUCache.put(1, 1); // cache is {1=1}
        lRUCache.printDoublyLinkedList();
        System.out.println();
        lRUCache.put(2, 2); // cache is {1=1, 2=2}
        lRUCache.printDoublyLinkedList();
        System.out.println("get(1) : "+lRUCache.get(1));    // return 1
        lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
        lRUCache.printDoublyLinkedList();
        System.out.println("\n get(2) : "+lRUCache.get(2));    // returns -1 (not found)
        lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
        lRUCache.printDoublyLinkedList();
        System.out.println("\n get(1) : "+lRUCache.get(1));    // return -1 (not found)
        System.out.println(" get(3) : "+lRUCache.get(3));    // return 3
        System.out.println(" get(4) : "+lRUCache.get(4));    // return 4
        lRUCache.printDoublyLinkedList();
    }


}
