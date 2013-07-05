package nz.edu.unitec.sosgame1;

public class Player {

    private String name;
    private int score;
    
    public static final int COLOUR_BLUE = 0;
    public static final int COLOUR_RED = 1;
    
    public String getName() {
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }
    
    public int getScore() {
            return score;
    }
    
    public void setScore(int score) {
            this.score = score;
    }
    
}
