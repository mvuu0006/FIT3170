package monash.edu.git;
/**
 * This class is unused
 */
public class ProjectID {
    private int nextId;

    public ProjectID() {
        nextId = 0;
    }

    public int getNext() {
        nextId++;
        return nextId;
    }
}