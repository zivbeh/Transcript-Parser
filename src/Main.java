import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    static String previousSpeaker = "";
    static int numberOfSwitches = 0;
    static ArrayList<Person> listOfSpeakers = new ArrayList<Person>();
    static ArrayList<String> condensedFile = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        String DirectoryPath = "Files";
        File dir = new File(DirectoryPath);
        File[] directoryListing = dir.listFiles();
        int numberOfFilesCounter = 0;
        for (File child : directoryListing) {
            RunTranscript(child,numberOfFilesCounter);
            numberOfFilesCounter++;
        }
    }

    private static void RunTranscript(File child, int numberOfFilesCounter) throws IOException{
        String fileContents = readFromFile("Files/"+child.getName());
        String[] words = fileContents.split("\n");		// split into array of lines
        for (int i = 3; i < words.length; i++) {
            // do stuff for each 3 lines here
            String line2 = words[i];
            String line3 = words[i+1];
            findDataBy3Lines(line2, line3);
            // skip 3 lines
            i += 3;
        }
        // print information
        printFinished(words[words.length-2], numberOfFilesCounter);
    }

    private static void printFinished(String lastLineOfTime, int FileNumber) {
        String str = "";
        str+="Overall Statistics\n";
        str += "Total NUmber Of Speakers: " + listOfSpeakers.size() +"\n";
        double sessionLength = findTotalTime(lastLineOfTime);
        str += "Total Length Of session: " + sessionLength +" mins\n";
        str += "Total NUmber of Speaker Changes: " + (numberOfSwitches-1)+"\n\n";

        for (int i = 0; i < listOfSpeakers.size(); i++) {
            Person p = listOfSpeakers.get(i);
            double timeSpokenByPerson = p.calcSpeakTime();
            str+=p.name + " spoke " + timeSpokenByPerson + " Sec\n";
            double AvgTimeSpokenByPerson = p.calcAverageTime();
            str+="Average Speaking time for " + p.name + " is " + AvgTimeSpokenByPerson+"\n";
            str+="WPM for " + p.name + " is " + p.calcWordsPerMinute()+"\n";
        }
        writeDataToFile("Summary"+FileNumber+".txt", str);
        printCondsned(FileNumber);
        numberOfSwitches = 0;
        previousSpeaker = "";
        listOfSpeakers = new ArrayList<Person>();
        condensedFile = new ArrayList<String>();
    }

    private static void findDataBy3Lines(String line2, String line3) {
        String name = findName(line3);
        if (!previousSpeaker.equals(name)) {
            numberOfSwitches++;
            previousSpeaker = name;
        }
        // check time
        int time = getSpeakingTime(line2);
        // find speakerObj
        Person speaker = null;
        for (int i = 0; i < listOfSpeakers.size(); i++) {
            if (listOfSpeakers.get(i).name.equals(previousSpeaker)) {
                speaker = listOfSpeakers.get(i);
            }
        }
        if (speaker == null) {
            speaker = new Person(previousSpeaker);
            listOfSpeakers.add(speaker);
        }
        condensedFile.add(speaker.name + ": "+ time);
        speaker.addTime(time);
        if (!line3.contains(":")){
            speaker.addWords(line3.substring(0));
            return;
        }
        speaker.addWords(line3.substring(line3.indexOf(":")+2));
    }

    private static String findName(String line3) {
        int indexOfTheColon = line3.indexOf(":");
        if (indexOfTheColon == -1) {
            return previousSpeaker;
        }
        return line3.substring(0, indexOfTheColon);
    }

    public static String readFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static int getSpeakingTime(String str){
        int startTime = 0;
        int endTime = 0;
        //00:52:04.200 --> 00:52:16.320
        String[] s = str.split(" ");
        startTime += Integer.parseInt(s[0].substring(0,2)) * 3600;
        startTime += Integer.parseInt(s[0].substring(3,5)) * 60;
        startTime += Integer.parseInt(s[0].substring(6,8));

        endTime += Integer.parseInt(s[2].substring(0,2)) * 3600;
        endTime += Integer.parseInt(s[2].substring(3,5)) * 60;
        endTime += Integer.parseInt(s[2].substring(6,8));
        return endTime - startTime;
    }

    public static double findTotalTime(String str){
        int endTime = 0;
        String[] s = str.split(" ");
        endTime += Integer.parseInt(s[2].substring(0,2)) * 3600;
        endTime += Integer.parseInt(s[2].substring(3,5)) * 60;
        endTime += Integer.parseInt(s[2].substring(6,8));

        // covert into min
        return endTime/60.0;
    }
    public static void printCondsned(int numFile) {
        String string = "";
        for (String str : condensedFile) {
            string += str + "\n";
        }
        writeDataToFile("CondensedFile"+numFile,string);
    }
    public static void writeDataToFile(String filePath, String data) {
        try (FileWriter f = new FileWriter(filePath);
             BufferedWriter b = new BufferedWriter(f);
             PrintWriter writer = new PrintWriter(b);) {
            writer.println(data);
        } catch (IOException error) {
            System.err.println("There was a problem writing to the file: " + filePath);
            error.printStackTrace();
        }
    }

}
