package agents.qlearning;

public class Estado {
    public int[][] scene;
    public boolean[] action;
	Estado(int[][] scene, boolean[] action){
        this.scene = scene;
        this.action = action;
    }
}