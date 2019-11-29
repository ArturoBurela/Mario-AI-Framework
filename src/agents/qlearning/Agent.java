package agents.qlearning;

import java.util.ArrayList;
import java.util.Random;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

public class Agent implements MarioAgent {
	// Useful for exploration generating random choices
	private Random rnd;
	private ArrayList<boolean[]> choices;

	private boolean[] actions;

	// Q Table
	private float[][][] qtable;
	// epsilon
	private float epsilon = 1;
	// Gamma
	private float gamma = 0.85f;

	@Override
	public void initialize(MarioForwardModel model, MarioTimer timer) {
		rnd = new Random();
		initPossibleChoices();
		actions = new boolean[MarioActions.numberOfActions()];
	}

	private void initPossibleChoices() {
		choices = new ArrayList<>();
		// right run
		choices.add(new boolean[] { false, true, false, true, false });
		// right jump and run
		choices.add(new boolean[] { false, true, false, true, true });
		// right
		choices.add(new boolean[] { false, true, false, false, false });
		// right jump
		choices.add(new boolean[] { false, true, false, false, true });
		// left
		choices.add(new boolean[] { true, false, false, false, false });
		// left run
		choices.add(new boolean[] { true, false, false, true, false });
		// left jump
		choices.add(new boolean[] { true, false, false, false, true });
		// left jump and run
		choices.add(new boolean[] { true, false, false, true, true });
	}

	@Override
	public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
		int[][] scene = model.getMarioCompleteObservation();
		System.out.println(scene);
		System.out.println(scene.length);
		System.out.println(scene[0].length);
		System.out.println(scene[1].length);
		return choices.get(rnd.nextInt(choices.size()));
	}

	@Override
	public String getAgentName() {
		return "QLearningAgent";
	}

	private void loadTable() {

	}

	private void saveTable() {

	}

}
