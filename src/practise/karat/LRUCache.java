package practise.karat;

import java.util.*;

class Main {
    public static void main(String args[]){
        // Create an LRUCache with a capacity of 2
        LRUCache<Integer,String> lru=new LRUCache<>(2);
        lru.put(1,"A");
        lru.put(2,"B");
        lru.display();
        System.out.println("After Cache values : ");
        lru.get(1);
        lru.put(3,"C");
        lru.display();
    }

}
//complexity O(1) for get and put operations and space complexity O(n) for storing the cache, n number of elements in the cache
public class LRUCache<K,V> {
    private final int capacity;
    private final LinkedHashMap<K,V> cache;

    public LRUCache(int capacity){
        if(capacity<=0){
            throw new IllegalArgumentException("capacity must be positive");
        }

        this.capacity=capacity;

        this.cache=new LinkedHashMap<>(capacity,0.75f,true){
            @Override
            public boolean removeEldestEntry(Map.Entry<K,V> eldest){
                return size()>LRUCache.this.capacity;
            }
        };

    }

    public synchronized V get(K k){
        return this.cache.get(k);
    }

    public synchronized  void put(K k,V v){
        this.cache.put(k,v);
    }

    public synchronized  int size(){
        return this.cache.size();
    }

    public void display(){
        //this.cache.toString();
        this.cache.entrySet().stream().forEach(System.out::println);
    }
}