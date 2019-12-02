import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import agents.andySloane.Agent;
import engine.core.MarioGame;
import engine.core.MarioResult;

public class Training {
	public static void printResults(MarioResult result) {
		System.out.println("****************************************************************");
		System.out.println("Game Status: " + result.getGameStatus().toString() + " Percentage Completion: "
				+ result.getCompletionPercentage());
		System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins()
				+ " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
		System.out.println("Mario State: " + result.getMarioMode() + " (Mushrooms: " + result.getNumCollectedMushrooms()
				+ " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
		System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp()
				+ " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() + " Falls: "
				+ result.getKillsByFall() + ")");
		System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps()
				+ " Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
		System.out.println("****************************************************************");
	}

	public static String getLevel(String filepath) {
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
		}
		return content;
	}

	public static void main(String[] args) {
		MarioGame game = new MarioGame();
		// Create new agent
		agents.qlearning.QLearningAgent agent = new agents.qlearning.QLearningAgent();
		// Show screen
		boolean show = false;
		for (int i = 0; i < 10000; i++) {

			if (i % 1000 == -1) {
				show = true;
			} else {
				show = false;
			}

			// MarioResult r = game.runGame(agent, getLevel("levels/original/lvl-1.txt"),
			// 100, 0, false);
			MarioResult r = game.runGame(agent,
					"----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n"
							+ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n"
							+ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n"
							+ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n"
							+ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n"
							+ "----------------------------------------------------------------------------------g-----------------------------------------------------------------------------------------------------------------------\n"
							+ "----------------------!---------------------------------------------------------SSSSSSSS---SSS!--------------@-----------SSS----S!!S--------------------------------------------------------##------------\n"
							+ "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------###------------\n"
							+ "-------------------------------------------------------------------------------g----------------------------------------------------------------------------------------------------------####------------\n"
							+ "----------------------------------------------------------------1------------------------------------------------------------------------------------------------------------------------#####------------\n"
							+ "----------------!---S@S!S---------------------tt---------tt------------------S@S--------------C-----SU----!--!--!-----S----------SS------#--#----------##--#------------SS!S------------######------------\n"
							+ "--------------------------------------tt------tt---------tt-----------------------------------------------------------------------------##--##--------###--##--------------------------#######------------\n"
							+ "----------------------------tt--------tt------tt---------tt----------------------------------------------------------------------------###--###------####--###-----tt--------------tt-########--------F---\n"
							+ "---M-----------------g------tt--------tt-g----tt-----g-g-tt------------------------------------g-g--------k-----------------gg-g-g----####--####----#####--####----tt---------gg---tt#########--------#---\n"
							+ "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--XXXXXXXXXXXXXXX---XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n"
							+ "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--XXXXXXXXXXXXXXX---XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
					100, 0, show);

			if (i % 1000 == 0) {
				agent.saveTable();
				printResults(r);
			}
		}
		printResults(game.runGame(agent, getLevel("levels/original/lvl-1.txt"), 100, 0, true));
	}
}
