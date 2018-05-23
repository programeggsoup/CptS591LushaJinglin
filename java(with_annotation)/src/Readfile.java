import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Readfile {
    public static void main(String[] args) throws IOException
    {
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader("data/118busQ.csv"));
        String line;
        List<String[]> edges = new ArrayList<>();
        //Set<String> nodes = new HashSet<>();
        while ((line = bufferedReader.readLine()) != null){
            String[] tokens = line.split(",");
            edges.add(tokens);
            //nodes.add(tokens[0]);
            //nodes.add(tokens[1]);
        }
        bufferedReader.close();

        BufferedWriter bufferedWriter;
        bufferedWriter = new BufferedWriter(new FileWriter("data/bus1180.txt"));
        bufferedWriter.write("graph");
        bufferedWriter.newLine();
        bufferedWriter.write("[");
        bufferedWriter.newLine();
        for (String[] str: edges)
        {
            bufferedWriter.write("node");
            bufferedWriter.newLine();
            bufferedWriter.write("[");
            bufferedWriter.newLine();
            int temp = Integer.parseInt(str[0])-1;
            bufferedWriter.write("id "+ temp);
            bufferedWriter.newLine();
            bufferedWriter.write("label "+temp);
            bufferedWriter.newLine();
            bufferedWriter.write("Qsupply "+str[1]);
            bufferedWriter.newLine();
            bufferedWriter.write("Qdemand "+str[2]);
            bufferedWriter.newLine();
            bufferedWriter.write("]");
            bufferedWriter.newLine();
        }
        /*for (String[] str: edges)
        {
            bufferedWriter.write("edge");
            bufferedWriter.newLine();
            bufferedWriter.write("[");
            bufferedWriter.newLine();
            bufferedWriter.write("source "+str[0]);
            bufferedWriter.newLine();
            bufferedWriter.write("target "+str[1]);
            bufferedWriter.newLine();
            bufferedWriter.write("value 1");
            bufferedWriter.newLine();
            bufferedWriter.write("]");
            bufferedWriter.newLine();
        }*/
        bufferedWriter.write("]");
        bufferedWriter.newLine();
        bufferedWriter.close();
    }
}
