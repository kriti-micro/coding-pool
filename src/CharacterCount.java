import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CharacterCount {
    public static void main(String[] args) {
        System.out.println("Program for character counting :");
        String name="Kriti";
        charCount(name);
        String surname="Singh";
        charCountOld(surname);

    }

    public static void charCount(String s){
        LinkedHashMap<Character,Integer> hmap=new LinkedHashMap<>();
        System.out.println("Char Count SurName :");
        char[] cArr=s.toCharArray();
        //Option 1
        for(char c: cArr){
            hmap.put(c,hmap.getOrDefault(c,0)+1);
        }

        Set<Map.Entry<Character,Integer>> entries = hmap.entrySet();
        for(Map.Entry<Character,Integer> e: entries){
            System.out.println(e.getKey()+" "+e.getValue());
        }
    }

    public static void charCountOld(String s){
        LinkedHashMap<Character,Integer> hmap=new LinkedHashMap<>();
        char[] cArr=s.toCharArray();
        System.out.println("Char Count Old way Name :");
        //option 2
        for(char c: cArr){
            if(hmap.containsKey(c)) {
                hmap.put(c, hmap.get(c) + 1);
            }else{
                hmap.put(c, 1);
            }
        }
        Set<Map.Entry<Character,Integer>> entries = hmap.entrySet();
        for(Map.Entry<Character,Integer> e: entries){
            System.out.println(e.getKey()+" "+e.getValue());
        }
    }
}
