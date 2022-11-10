import java.util.ArrayList;

public class Person {
    public String name;
    public ArrayList<Integer> talkTime = new ArrayList<Integer>();
    public int wordsSaid = 0;

    public Person(String name) {
        this.name = name;
    }

    public void addTime(int time) {
        talkTime.add(time);
    }

    public void addWords(String str) {
        String[] arr = str.split(" ");
        wordsSaid+= arr.length;
    }

    public double calcSpeakTime() {
        double speakTime = 0;
        for (int i = 0; i < talkTime.size(); i++) {
            speakTime+= talkTime.get(i);
        }
        return speakTime;
    }

    public double calcAverageTime() {
        return calcSpeakTime()/talkTime.size();
    }

    public double calcWordsPerMinute() {
        double time = (calcSpeakTime()/60);
        return wordsSaid/time;
    }
}
