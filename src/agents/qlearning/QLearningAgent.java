package agents.qlearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;

public class QLearningAgent implements MarioAgent {

    private Random rnd;
    private HashMap<String, Double> qtable;
    private ArrayList<boolean[]> actions;

    private final double EPSILON = 0.3d;
    private final double APLHA = 0.15d;
    private final double GAMMA = 0.8d;

    public QLearningAgent(){
        qtable = new HashMap<>();
        initPossibleActions();
        System.out.println("Init Agent");
    }

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        rnd = new Random();
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        // Serialize observation
        String scene = serializeScene(model.getMarioCompleteObservation());
        boolean[] action;
        int actionIndex;
        // Exploration vs exploitation
        if (rnd.nextFloat() < EPSILON) {
            // do a random choice
            actionIndex = rnd.nextInt(actions.size());
            action = actions.get(actionIndex);
        } else {
            // Look table and get action
            actionIndex = getBestAction(scene);
            action = actions.get(actionIndex);
        }
        // Update table values
        MarioForwardModel s1 = model.clone();
        Double actualScore = (double) s1.getCompletionPercentage();
        // Take action
        s1.advance(action);
        Double nextScore = (double) s1.getCompletionPercentage();
        String nextScene = serializeScene(s1.getMarioCompleteObservation());
        Double reward = nextScore - actualScore;
        // Update table values
        String state = scene + actionIndex;
        Double value = (1 - APLHA) * qtable.get(state) + APLHA * (reward + GAMMA + getMax(nextScene));
        qtable.put(state, value);
        // Return action
        return action;
    }

    @Override
    public String getAgentName() {
        return "QLearningAgent";
    }

    private void initPossibleActions() {
        actions = new ArrayList<>();
        // right run
        actions.add(new boolean[] { false, true, false, true, false });
        // right jump and run
        actions.add(new boolean[] { false, true, false, true, true });
        // right
        actions.add(new boolean[] { false, true, false, false, false });
        // right jump
        actions.add(new boolean[] { false, true, false, false, true });
        // left
        actions.add(new boolean[] { true, false, false, false, false });
        // left run
        actions.add(new boolean[] { true, false, false, true, false });
        // left jump
        actions.add(new boolean[] { true, false, false, false, true });
        // left jump and run
        actions.add(new boolean[] { true, false, false, true, true });
    }

    private String serializeScene(int[][] scene) {
        String state = "";
        for (int i = 0; i < scene.length; i++) {
            for (int j = 0; j < scene[i].length; j++) {
                state += scene[i][j];
            }
        }
        return state;
    }

    private Double getMax(String scene) {
        String state = "";
        Double max = 0d;
        int maxIndex = 0;
        for (int i = 0; i < actions.size(); i++) {
            state = scene + i;
            if (qtable.containsKey(state)) {
                if (qtable.get(state) > max) {
                    maxIndex = i;
                    max = qtable.get(state);
                }
            } else {
                qtable.put(state, 0d);
            }
        }
        return max;
    }

    private int getBestAction(String scene) {
        String state = "";
        Double max = 0d;
        int maxIndex = 0;
        for (int i = 0; i < actions.size(); i++) {
            state = scene + i;
            if (qtable.containsKey(state)) {
                if (qtable.get(state) > max) {
                    maxIndex = i;
                    max = qtable.get(state);
                }
            } else {
                qtable.put(state, 0d);
            }
        }
        return maxIndex;
    }

}