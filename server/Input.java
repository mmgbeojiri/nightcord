import java.util.Scanner;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

class Input{

    public static Scanner sc = new Scanner(System.in);
    public static int readInt(){
        int data = sc.nextInt();
        sc.nextLine();//Clear the trailing carriage return
        return data;
    }
    public static double readDouble(){
        double data = sc.nextDouble();
        sc.nextLine();//Clear the trailing carriage return
        return data;
    }
    public static String readString(){
        return sc.nextLine();
    }
    public static char readChar(){
        return sc.nextLine().charAt(0);
    }
    public static boolean readBoolean(){
        return sc.nextBoolean();
    }
    public static String readFile(String fn){
        String file = "";
        try{
            file = new String(Files.readAllBytes(Paths.get(fn)), StandardCharsets.UTF_8);
        }catch(Exception e){

        }
        return  file;
    }
    public static void writeFile(String file, String data) {
        try {
            Files.write(Paths.get(file), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    public static ArrayList<Row> readCSV(String fn){
        ArrayList<Row> data =  new ArrayList<>();

        String file = readFile(fn);
        String[] line = file.split("\n");
        String header = line[0];
        for(int pos = 1; pos < line.length; pos++){
            data.add(new Row(header,line[pos]));
        }
        return data;
    }*/

}