package agents.qlearning;

public class Estado implements java.io.Serializable {
    public int[][] scene;
    public boolean[] action;
	Estado(int[][] scene, boolean[] action){
        this.scene = scene;
        this.action = action;
    }
}