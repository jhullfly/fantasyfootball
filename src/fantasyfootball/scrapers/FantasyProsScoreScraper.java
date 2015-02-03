package fantasyfootball.scrapers;

import java.io.File;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import fantasyfootball.LeagueData;
import fantasyfootball.Position;

public class FantasyProsScoreScraper extends Scraper {

	@Override
	public String getType() {
		return "score";
	}
	private static String baseUrl = "http://www.fantasypros.com/nfl/projections/";
	@Override
	protected String buildUrl(Position pos) {
		return baseUrl+pos.toString().toLowerCase()+".php";
	}
	@Override
	protected boolean shouldSkip(Position pos) {
		return pos.equals(Position.D);
	}
	@Override
	protected void addData(LeagueData data, String team, Position pos, String name, String value) {
		data.addScore(team, pos, name, (int) (10*Double.parseDouble(value)));
	}
	@Override
	protected String buildTeam(Document d, Position pos, int row)
			throws XPathExpressionException {
		String team = super.buildTeam(d, pos, row);
		if (team.length()>2) {
			return team.substring(1, team.length()-1);
		} else {
			return team;
		}
	}
	@Override
	protected String buildTeamXPath(Position pos, int row) {
		return "//div[1]/table/tbody/tr[td]["+row+"]/td[1]/small";
	}	
	@Override
	protected String buildNameXPath(Position pos, int row) {
		return "//div[1]/table/tbody/tr[td]["+row+"]/td[1]/a";
	}
	@Override
	protected String buildValueXPath(Position pos, int row) {
		return "//div[1]/table/tbody/tr[td]["+row+"]/td["+getColumn(pos)+"]";
	}
	int getColumn(Position pos) {
		if (pos.equals(Position.QB)) {
			return 11;
		} else if (pos.equals(Position.RB)) {
			return 9;
		} else if (pos.equals(Position.WR)) {
			return 9;
		} else if (pos.equals(Position.TE)) {
			return 6;
		} else if (pos.equals(Position.K)) {
			return 5;
		} else {
			throw new IllegalArgumentException("Unknown position "+pos);
		}
	}
	
	public static void main(String[] args) throws Exception {
		File cacheDir = new File("data/test");
		cacheDir.mkdirs();
		LeagueData data = new LeagueData();
		Scraper scoreScrapper = new FantasyProsScoreScraper();
		scoreScrapper.scrapeData(data, cacheDir);
	}	
}