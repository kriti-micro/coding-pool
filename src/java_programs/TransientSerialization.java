package java_programs;

import java.io.*;

public class TransientSerialization {
    public static void main(String[] args) {

        //------------Serialization
        Country c = new Country("India", 100000);
        FileOutputStream fileOut;
        ObjectOutputStream outStream;
        try {
            fileOut = new FileOutputStream("country.ser");
            outStream = new ObjectOutputStream(fileOut);
            outStream.writeObject(c);
            outStream.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Serialized");

        //------Deserialization-------
        FileInputStream filein;
        ObjectInputStream inStream;
        Country india;

        try {
            filein=new FileInputStream("country.ser");
            inStream=new ObjectInputStream(filein);
            india= (Country) inStream.readObject();
            inStream.close();
            filein.close();
            System.out.println("Deserialization Country object : "+india.name+" "+india.population);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}



class Country implements Serializable {
    String name;
    transient long population;

    public Country(String name, int p) {
        this.name = name;
        this.population = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }
}
