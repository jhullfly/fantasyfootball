package fantasyfootball.scrapers;

import java.io.File;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import fantasyfootball.LeagueData;
import fantasyfootball.Position;

public class FourForFourScraper extends Scraper {

	private final boolean kicker3x;

	public FourForFourScraper(boolean kicker3x) {
		this.kicker3x = kicker3x;
	}

	@Override
	protected String buildUrl(Position pos) {
		String posString = pos.toString().toLowerCase();
		if (pos.equals(Position.D)) {
			posString = "def";
		}
		return "http://www.4for4.com/members/fi/"+posString+"cheatsheet.php?fi_table=ff_nflstats&leagueId=Scars";
	}

	@Override
	protected String getCookies() {
		return "SESSc40475ee089b0d4e8158fa797261c63c=-4SzVZok84uGlc5x0DZZHogZtH0Ysba1-z-jIZODpAM; Drupal.visitor.drupal_login=a%3A5%3A%7Bs%3A16%3A%22SESSION_USERNAME%22%3Bs%3A8%3A%22jhullfly%22%3Bs%3A12%3A%22SESSION_TYPE%22%3Bi%3A1%3Bs%3A16%3A%22SESSION_LOGGEDIN%22%3Bi%3A1%3Bs%3A19%3A%22SESSION_LOGINFAILED%22%3Bi%3A0%3Bs%3A11%3A%22FULL_IMPACT%22%3Bi%3A1%3B%7D; _gat=1; has_js=1; _ga=GA1.2.125820440.1415465401";
	}
	
	@Override
	protected String buildNameXPath(Position pos, int row) {
		return "//div[2]/table/tbody/tr[td]["+(row+1)+"]/td[2]/a";
	}

	@Override
	protected String buildValueXPath(Position pos, int row) {
		return "//div[2]/table/tbody/tr[td]["+(row+1)+"]/td[last()]";
	}
	
	@Override
	protected String buildTeam(Document d, Position pos, int row) throws XPathExpressionException {
		if (pos.equals(Position.D)) {
			String team = super.buildName(d, pos, row);
			if (team.equals("")) {
				return team;
			}
			String letter3 = TeamNameMapper.nameTo3Letter.get(team);
			return letter3;
		}
		String letter2 = super.buildTeam(d, pos, row);
		if (letter2.equals("")) {
			return "";
		}
		return TeamNameMapper.map2Letterto3Letter(letter2);
	}

	@Override
	protected String buildTeamXPath(Position pos, int row) {
		return "//div[2]/table/tbody/tr[td]["+(row+1)+"]/td[3]";
	}

	@Override
	protected void addData(LeagueData data, String team, Position pos, String name, String value) {
		if (pos.equals(Position.D)) {
			data.addScore(team, pos, name, (int)(10*Double.parseDouble(value)));
			return;
		}
		int multiplier = 1;
		if (pos.equals(Position.K) && kicker3x) {
			multiplier = 3;
		}
		data.addScoreFirstInitial(team, pos, name, (int)(10*multiplier*Double.parseDouble(value)));
	}

	@Override
	public String getType() {
		return "fourscore";
	}

	public static void main(String[] args) throws Exception {
		File cacheDir = new File("data/test");
		cacheDir.mkdirs();
		LeagueData data = new LeagueData();
		EspnCostScraper costScrapper = new EspnCostScraper();
		costScrapper.scrapeData(data, cacheDir);
		Scraper scoreScrapper = new FourForFourScraper(false);
		scoreScrapper.scrapeData(data, cacheDir);
	}	

}
