package customView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marioviti on 25/08/15.
 */
public class SupportContentValues {
    public static int currentEntry=-1;
    public static int numOfEntry=0;
    public static ArrayList<float[]> valf;
    public static ArrayList<String> vals;

    public SupportContentValues(int numOfEntry) {
        this.numOfEntry = numOfEntry;
        valf = new ArrayList<float[]>(numOfEntry);
        createEntries();
    }

    private void createEntries(){
        valf.add(0,new float[] {(float)0,(float)10,(float)20,(float)30,(float)40,});
        valf.add(1,new float[] {(float)40, (float)30, (float)20,(float)10,(float)0,});
        valf.add(2,new float[] {(float)40, (float)30, (float)0,(float)10,(float)20,});
        valf.add(3,new float[] {(float)50, (float)0,(float)0,(float)30,(float)20,});
        valf.add(4,new float[] {(float)40, (float)30,(float)20,(float)10,(float)0,});
        valf.add(5,new float[] {(float)40, (float)30,(float)0,(float)10,(float)20,});
        valf.add(6, new float[]{(float) 50, (float) 0, (float) 0, (float) 30, (float)20,});
    }

    public static void incEntry(){
        currentEntry=(currentEntry+1)%numOfEntry;
    }

    public static float[] giveValues(){
        return valf.get(currentEntry);
    }
}
