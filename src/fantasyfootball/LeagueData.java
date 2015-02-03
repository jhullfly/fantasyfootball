package fantasyfootball;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class LeagueData {
	private final Map<Position, List<Player>> data = new HashMap<Position, List<Player>>();
	public static Position getPosition(int i) {
		if (i < 2) {
			return Position.QB;
		} else if (i < 4) {
			return Position.RB;
		} else if (i < 6) {
			return Position.WR;
		} else if (i < 7) {
			return Position.TE;
		} else if (i < 8) {
			return Position.K;
		} else if (i < 9) {
			return Position.D;
		} else {
			throw new IllegalArgumentException("unknow index "+i);
		}
	}
	public LeagueData() {
		for (Position pos : Position.values()) {
			data.put(pos, new ArrayList<Player>());
		}
	}
	public List<String> getPlayersInLineUp(int[] lineup) {
		List<String> names = new ArrayList<String>();
		for (int i = 0 ; i < lineup.length; i++) {
			int playerIndex = lineup[i];
			Position p = LeagueData.getPosition(i);
			List<Player> players = data.get(p);
			Player player = players.get(playerIndex);
			names.add(player.getName() + " ("+player.getCost()+", "+player.getScore()+", "+player.getTeam()+")");
		}
		return names;
	}
	public void addCost(String team, Position p, String name, int cost) {
		Player player = initialPlayer(team, p, name);
		player.setCost(cost);
	}
	public void addScoreFirstInitial(String team, Position p, String name, int score) {
		List<Player> players = data.get(p);
		Player player = findPlayerByNameFirstInitial(players, name);
		if (player == null) {
			//System.out.println("WARNING: can't find cost for "+p+": "+name);
			return;
		}
		player.setScore(score);
	} 
	public void addScore(String team, Position p, String name, int score) {
		Player player = initialPlayer(team, p, name);
		player.setScore(score);
	}
	public Map<Position, List<Player>> getData() {
		return data;
	}
	private Player initialPlayer(String team, Position p, String name) {
		List<Player> players = data.get(p);
		Player player = findPlayerByName(players, name);
		if (player == null) {
			player = new Player(team, p, name);
			players.add(player);
		}
		return player;
	}
	private Player findPlayerByName(List<Player> players, String name) {
		for (Player player : players) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	private Player findPlayerByNameFirstInitial(List<Player> players, String name) {
		String[] flname = name.split(" ");
		if (flname.length<2) {
			return findPlayerByName(players, name);
		}
		for (Player player : players) {
			String[] pflname = player.getName().split(" ");
			if (pflname.length < 2) {
				continue;
			}
			if (flname[0].charAt(0) == pflname[0].charAt(0) && 
				flname[1].equals(pflname[1])) {
				return player;
			}
		}
		//System.out.println("no match: "+name);
		return null;
	}
	public int getMaxScore(int i) {
		Position pos = getPosition(i);
		List<Player> players = data.get(pos);
		int score = 0;
		for (Player player : players) {
			if (player.getScore() > score) {
				score = player.getScore();
			}
		}
		return score;
	}
	public void dump(String baseDir) throws IOException {
		FileWriter f = new FileWriter(new File(baseDir, "allData.csv"));
		CSVWriter writer = new CSVWriter(f);
		for (Position p : Position.values()) {
			for (Player player : data.get(p)) {
				String[] line = new String[5];
				line[0] = player.getName();
				line[1] = player.getTeam();
				line[2] = player.getPosition().toString();
				line[3] = Integer.toString(player.getCost());
				line[4] = Integer.toString(player.getScore());
				writer.writeNext(line);
			}
		}
		writer.close();
		f.close();
	}
	public void readData(String baseDir) throws IOException {
		FileReader f = new FileReader(new File(baseDir, "allData.csv"));
		CSVReader reader = new CSVReader(f);
		String [] line = reader.readNext();
		while (line != null) {
			Player player = new Player(line[1], Position.valueOf(line[2]), line[0]);
			player.setCost(Integer.parseInt(line[3]));
			player.setScore(Integer.parseInt(line[4]));
			data.get(Position.valueOf(line[2])).add(player);
			line = reader.readNext();
		}
		reader.close();
		f.close();
	}
}
