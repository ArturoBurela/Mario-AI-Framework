package agents.qlearning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

public class Agent implements MarioAgent {
	// Useful for exploration generating random choices
	private Random rnd;
	private ArrayList<boolean[]> choices;
	private agents.robinBaumgarten.Agent a;

	private boolean[] actions;

	// Q Table
	private double[][][] qtable;
	// epsilon
	private float epsilon = 0.95f;
	// alpha
	private float alpha = 0.15f;
	// Gamma
	private final float gamma = 0.8f;
	// Scene matrix
	private int[][] scene;
	// Mac qtable score
	private float maxValue = 0f;

	// second qtable
	public HashMap<Estado, Double> qtable2;

	@Override
	public void initialize(final MarioForwardModel model, final MarioTimer timer) {
		rnd = new Random();
		initPossibleChoices();
		a.initialize(model, timer);
		// initTable();
		initTable2();
		// saveTable();
		actions = new boolean[MarioActions.numberOfActions()];
	}

	private void initPossibleChoices() {
		choices = new ArrayList<>();
		a = new agents.robinBaumgarten.Agent();
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
		try {
			loadTable();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveTable2() {
		try {
			final LocalDateTime date = LocalDateTime.now();
			FileOutputStream fileOut = new FileOutputStream(
					"/home/burela/Documentos/Mario-AI-Framework/hashtable" + date + ".txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(qtable2);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in /tmp/employee.ser");
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	private void initTable2() {
		qtable2 = new HashMap<Estado, Double>();
		// Init for all Estados
		/*
		 * for (int i = 0; i < 16; i++)// for each row { for (int j = 0; j < 16; j++)//
		 * for each column { for (int k = 0; k < 8; k++) { // For each action
		 * qtable2.put(new Estado(i, j, choices.get(k)), 0d); } } }
		 */
	}

	@Override
	public boolean[] getActions(final MarioForwardModel model, final MarioTimer timer) {
		boolean[] action = null;
		scene = model.getMarioCompleteObservation();
		// printScene();
		// printTable();
		// Exploration vs exploitation
		if (rnd.nextFloat() < epsilon) {
			// do a random choice
			action = a.getActions(model, timer); // choices.get(rnd.nextInt(choices.size()));
		} else {
			// Look table and get action
			action = getBestAction2(scene);
		}
		// Update table values
		final MarioForwardModel s1 = model.clone();
		float reward = s1.getCompletionPercentage();
		// Take action and get reward
		s1.advance(action);
		float reward2 = s1.getCompletionPercentage();
		reward = reward2 - reward;
		// Set new table values
		setTableValues2(scene, reward, action, s1);
		// Decay epsilon
		// epsilon *= 0.99f;
		return action;
	}

	private boolean[] getBestAction2(int[][] scene) {
		// Look for scenes of every action
		Estado e;
		boolean[] bestAction = choices.get(0);
		double max = 0d;
		for (boolean[] action : choices) {
			e = new Estado(scene, action);
			if (qtable2.containsKey(e)) {
				if (qtable2.get(e) > max) {
					bestAction = action;
					max = qtable2.get(e);
				}
			} else {
				qtable2.put(e, 0d);
				// bestAction = action;
			}
		}
		return bestAction;
	}

	private double getMax2(int[][] scene) {
		// Look for scenes of every action
		Estado e;
		boolean[] bestAction = choices.get(0);
		double max = 0d;
		for (boolean[] action : choices) {
			e = new Estado(scene, action);
			if (qtable2.containsKey(e)) {
				if (qtable2.get(e) > max) {
					bestAction = action;
					max = qtable2.get(e);
				}
			} else {
				qtable2.put(e, 0d);
				bestAction = action;
			}
		}
		return max;
	}

	private void setTableValues2(int[][] scene, float reward, boolean[] action, MarioForwardModel nextState) {
		Estado e = new Estado(scene, action);
		if (!qtable2.containsKey(e)) {
			qtable2.put(e, 0d);
		}
		qtable2.put(e, qtable2.get(e) + alpha * ((double) reward
				+ (double) gamma * getMax2(nextState.getMarioCompleteObservation()) - qtable2.get(e)));
		// Q[state, action] = Q[state, action] + lr * (reward + gamma *
		// np.max(Q[new_state, :]) — Q[state, action])
	}

	@Override
	public String getAgentName() {
		return "QLearningAgent";
	}

	private void loadTable() throws IOException {
		final String file = "/home/burela/Documentos/Mario-AI-Framework/win.txt";
		try {
			String data = "";
			data = new String(Files.readAllBytes(Paths.get(file)));
			System.out.println(data);
			String[] datos = data.split(",");
			int n = 0;
			for (int i = 0; i < qtable.length; i++)// for each row
			{
				for (int j = 0; j < qtable[0].length; j++)// for each column
				{
					for (int k = 0; k < qtable[0][0].length; k++) { // For each action
						qtable[i][j][k] = Double.parseDouble(datos[n]);
						n++;
						// builder.append(qtable[i][j][k] + "");
						// builder.append(",");
					}
				}
			}
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setTableValues(final int actionIndex, final float reward, final MarioForwardModel newState) {
		for (int x = 0; x < qtable.length; x++)// for each row
		{
			for (int y = 0; y < qtable[0].length; y++) { // For each column
				// qtable[x][y][actionIndex] += alpha * (reward + gamma * (maxValue -
				// qtable[x][y][actionIndex]));
				// float mQ = maxQ(x, y);
				// qtable[x][y][actionIndex] = (1 - alpha) * qtable[x][y][actionIndex] + alpha *
				// (reward + gamma * mQ);
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
		final StringBuilder builder = new StringBuilder();
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
		final LocalDateTime date = LocalDateTime.now();
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");

		final String formattedDateTime = date.format(formatter);
		try {
			BufferedWriter writer;
			writer = new BufferedWriter(
					new FileWriter("/home/burela/Documentos/Mario-AI-Framework/table" + date + ".txt"));
			writer.write(builder.toString());// save the string representation of the board
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void train() {

	}

	public void setEpsilon(float e) {
		epsilon = e;
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
