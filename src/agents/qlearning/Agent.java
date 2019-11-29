package agents.qlearning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
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
	private double[][][] qtable;
	// epsilon
	private float epsilon = 0.3f;
	// alpha
	private float alpha = 0.15f;
	// Gamma
	private float gamma = 0.8f;
	// Scene matrix
	private int[][] scene;
	// Mac qtable score
	private float maxValue = 0f;

	@Override
	public void initialize(MarioForwardModel model, MarioTimer timer) {
		rnd = new Random();
		initPossibleChoices();
		initTable();
		// saveTable();
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

	private void initTable() {
		qtable = new double[16][16][8];
	}

	@Override
	public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
		boolean[] action = null;
		scene = model.getMarioCompleteObservation();
		// printScene();
		// printTable();
		int actionIndex = 0;
		// Exploration vs exploitation
		if (rnd.nextFloat() < epsilon) {
			// do a random choice
			actionIndex = rnd.nextInt(choices.size());
			action = choices.get(actionIndex);
		} else {
			// Look table and get action
			actionIndex = getBestAction();
			action = choices.get(actionIndex);
		}
		// Update table values
		MarioForwardModel s1 = model.clone();
		float reward = s1.getCompletionPercentage();
		// Take action and get reward
		s1.advance(action);
		getBestAction();
		float reward2 = s1.getCompletionPercentage();
		reward = reward2 - reward;
		// Set new table values
		setTableValues(actionIndex, reward, s1);
		return action;
	}

	@Override
	public String getAgentName() {
		return "QLearningAgent";
	}

	private void loadTable() {

	}

	private void setTableValues(int actionIndex, float reward, MarioForwardModel newState) {
		for (int x = 0; x < qtable.length; x++)// for each row
		{
			for (int y = 0; y < qtable[0].length; y++) { // For each column
				qtable[x][y][actionIndex] += alpha * (reward + gamma * (maxValue - qtable[x][y][actionIndex]));
			}
		}
	}

	private int getBestAction() {
		float maxNum = 0f;
		float counter = 0f;
		int actionIndex = 0;
		for (int a = 0; a < qtable[0][0].length; a++)// for action
		{
			counter = 0f;
			for (int x = 0; x < qtable.length; x++)// for each row
			{
				for (int y = 0; y < qtable[0].length; y++) { // For each column
					counter += qtable[x][y][a];
				}
			}
			if (counter > maxNum) {
				maxNum = counter;
				maxValue = maxNum / (qtable.length * qtable[0].length);
				actionIndex = a;
			}
		}
		return actionIndex;
	}

	public void saveTable() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < qtable.length; i++)// for each row
		{
			for (int j = 0; j < qtable[0].length; j++)// for each column
			{
				for (int k = 0; k < qtable[0][0].length; k++) { // For each action
					builder.append(qtable[i][j][k] + "");
					builder.append(",");
				}
			}
		}
		LocalDateTime date = LocalDateTime.now();
		try {
			BufferedWriter writer;
			writer = new BufferedWriter(
					new FileWriter("/Users/arturoburelat/Documents/Mario-AI-Framework/table" + date + ".txt"));
			writer.write(builder.toString());// save the string representation of the board
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void train() {

	}

	private void printScene() {
		for (int i = 0; i < scene.length; i++) {
			for (int j = 0; j < scene[i].length; j++) {
				System.out.print(scene[i][j] + ",");
			}
			System.out.println();
		}
		System.out.println("--------------------");
	}

	private void printTable() {
		for (int i = 0; i < qtable.length; i++)// for each row
		{
			for (int j = 0; j < qtable[0].length; j++)// for each column
			{
				for (int k = 0; k < qtable[0][0].length; k++) { // For each action
					System.out.print(qtable[i][j][k] + ",");
				}
				System.out.println();
			}
		}
	}

}
