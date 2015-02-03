package fantasyfootball;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fantasyfootball.scrapers.EspnCostScraper;
import fantasyfootball.scrapers.FourForFourScraper;
import fantasyfootball.scrapers.Scraper;


public class FindBest {
	private static final int defaultMaxIndex = 100;
	public static void main(String[] args) throws Exception {
		int cap = 500;
		boolean gameOfWeek = false;
		long gameOfWeekT1 = "PIT".hashCode();
		long gameOfWeekT2 = "CIN".hashCode();
		String baseDir = "data/playoff2";
		boolean cached = false;
		boolean kicker3x = false;
		long time = System.currentTimeMillis();
		int[] bestLineup = new int[9];
		int bestScore = 0;
		int[] lineup = new int[9];
		lineup[1] = 1;
		lineup[3] = 1;
		lineup[5] = 1;
		LeagueData leagueData = getLegueData(baseDir, cached, kicker3x);
		System.out.println("Got Data (remember change GOW)");
		System.out.println("time = " + ((double)(System.currentTimeMillis()-time))/1000/60);
		time = System.currentTimeMillis();
		Map<Position, List<Player>> data = leagueData.getData();
		boolean moreLineups = true;
		List<Object[]> playersList = new ArrayList<Object[]>();
		int[] maxIndexes = new int[lineup.length];
		for (int i = 0 ; i < lineup.length; i++) {
			Position p = LeagueData.getPosition(i);
			List<Player> players = data.get(p);
			maxIndexes[i]=Math.min(defaultMaxIndex, players.size()-1);
			playersList.add(players.toArray());
		}
		int[] maxScoreRemaining = new int[lineup.length-1];
		for (int i = lineup.length-2 ; i >= 0 ; i--) {
			maxScoreRemaining[i] = leagueData.getMaxScore(i+1);
			if (i != lineup.length-2) {
				maxScoreRemaining[i] = maxScoreRemaining[i+1]+maxScoreRemaining[i];
			}
		}
		Object[] playersArr = playersList.toArray();
		while (moreLineups) {
			int score = 0;
			int cost = 0;
			int changeSpot = lineup.length-1;
			boolean hasGameOfWeekPlayer = false;
			for (int i = 0 ; i < lineup.length; i++) {
				int playerIndex = lineup[i];
				Player player = (Player) ((Object[]) playersArr[i])[playerIndex];
				int teamHashCode = player.getTeamHashCode();
				if (teamHashCode == gameOfWeekT1 || teamHashCode == gameOfWeekT2) {
					hasGameOfWeekPlayer = true;
				}
				score += player.getScore();
				cost += player.getCost();
				if (cost > cap || (!player.getTeam().equals("SEA")&&!player.getTeam().equals("NWE"))) {
					changeSpot = i;
					break;
				}
				if (i != lineup.length-1 && score+maxScoreRemaining[i] < bestScore) {
					changeSpot = i;
					break;
				}
			}
			if (cost <= cap && score >= bestScore && (!gameOfWeek || hasGameOfWeekPlayer) && changeSpot == lineup.length-1) {
				for (int i = 0 ; i < lineup.length; i++) {
					bestLineup[i] = lineup[i];
				}
				bestScore = score;
				
				System.out.println("new best "+bestScore + " "+cost+" "+leagueData.getPlayersInLineUp(bestLineup));
			}
			moreLineups = nextLineup(lineup, changeSpot, maxIndexes);
		}
		System.out.println("bestScore " + bestScore);
		if (bestScore != 0.0) {
			System.out.println("lineup " + leagueData.getPlayersInLineUp(bestLineup));
		}
		System.out.println("time = " + ((double)(System.currentTimeMillis()-time))/1000/60);
	}
	private static LeagueData getLegueData(String baseDir, boolean cached, boolean kicker3x) throws Exception {
		File cacheDir = new File(baseDir);
		if (cached) {
			LeagueData leagueData = new LeagueData();
			leagueData.readData(baseDir);
			//leagueData.dump("data");
			return leagueData;
		} else {
			LeagueData data = new LeagueData();
			EspnCostScraper costScrapper = new EspnCostScraper();
			costScrapper.scrapeData(data, cacheDir);
			Scraper scoreScrapper = new FourForFourScraper(kicker3x);
			scoreScrapper.scrapeData(data, cacheDir);
			data.dump(baseDir);
			return data;
		}
	}
	private static void dumpLineup(int[] lineup) {
		List<Integer> values = new ArrayList<Integer>();
		for (Integer i : lineup) {
			values.add(i);
		}
		System.out.println(values);
	}

	private static boolean nextLineup(int[] lineup, int changeSpot, int[] maxIndexes) {
		zeroOut(lineup, changeSpot, maxIndexes);
		for (int i = changeSpot ; i >= 0; i--) {
			int playerIndex = lineup[i];
			if ((i==0 || i==2 || i==4) && playerIndex == maxIndexes[i]-1) {
				lineup[i] = 0;
			} else if (playerIndex != maxIndexes[i]) {
				lineup[i]++;
				return true;
			} else {
				zeroOutDigit(lineup, i, maxIndexes);
			}
		}
		return false;
	}

	private static void zeroOutDigit(int[] lineup, int i, int[] maxIndexes) {
		if (i==1 || i==3 || i==5) {
			if (lineup[i-1] != maxIndexes[i]-1) {
				lineup[i] = lineup[i-1]+2;
			} else {
				lineup[i] = 1;
			}
		} else {
			lineup[i] = 0;
		}
	}

	private static void zeroOut(int[] lineup, int changeSpot, int[] maxIndexes) {
		for (int i = changeSpot+1 ; i < lineup.length; i++) {
			zeroOutDigit(lineup, i, maxIndexes);
		}
	}

	public static void main2(String[] args) {
		boolean cont = true;
		int[] arr = new int[8];
		arr[1] = 1;
		arr[3] = 1;
		arr[5] = 1;
		int count = 0;
		while (cont && count++ < 1000000) {
			dumpLineup(arr);
			cont = nextLineup(arr, arr.length, null);
		}
		dumpLineup(arr);
	}
}
