package fantasyfootball.scrapers;

import java.io.File;

import fantasyfootball.LeagueData;
import fantasyfootball.Position;

public class EspnCostScraper extends Scraper {
	public static void main(String[] args) throws Exception {
		File cacheDir = new File("data/test");
		cacheDir.mkdirs();
		LeagueData data = new LeagueData();
		EspnCostScraper costScrapper = new EspnCostScraper();
		costScrapper.scrapeData(data, cacheDir);
	}
	@Override
	public String getType() {
		return "cost";
	}
//	private static String baseUrl = "http://games.espn.go.com/nfl-gridiron-challenge/2014/en/format/ajax/getPlayersTable?entryID=69326&spid=52&ssid=1&slotID=";
	private static String baseUrl = "http://games.espn.go.com/nfl-gridiron-playoff-challenge/2014/en/format/ajax/getPlayersTable?entryID=337600&ssid=3&spid=54&slotID=";
	@Override
	protected String buildUrl(Position pos) {
		if (pos.equals(Position.QB)) {
			return baseUrl+"1";
		} else if (pos.equals(Position.RB)) {
			return baseUrl+"3";
		} else if (pos.equals(Position.WR)) {
			return baseUrl+"5";
		} else if (pos.equals(Position.TE)) {
			return baseUrl+"7";
		} else if (pos.equals(Position.K)) {
			return baseUrl+"8";
		} else if (pos.equals(Position.D)) {
			return baseUrl+"9";
		} else {
			throw new IllegalArgumentException("Unknown position "+pos);
		}
	}
	@Override
	protected String buildTeamXPath(Position pos, int row) {
		if (pos.equals(Position.D)) {
			return "//tbody/tr[td]["+row+"]/td[2]/a/@ta";
		}
		return "//tbody/tr[td]["+row+"]/td[2]/span[1]";
	}
	@Override
	protected String buildValueXPath(Position pos, int row) {
		return "//tbody/tr[td]["+row+"]/td[12]";
	}
	@Override
	protected String buildNameXPath(Position pos, int row) {
		if (pos.equals(Position.D)) {
			return "//tbody/tr[td]["+row+"]/td[2]//span[2]";
		}
		return "concat(//tbody/tr[td]["+row+"]/td[2]//span[1], ' ', //tbody/tr[td]["+row+"]/td[2]//span[2])";
	}
	@Override
	protected void addData(LeagueData data, String team, Position pos, String name, String value) {
		data.addCost(team, pos, name, (int)(10*Double.parseDouble(value)));
	}
	
}