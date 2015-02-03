package fantasyfootball.scrapers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import fantasyfootball.LeagueData;
import fantasyfootball.Position;

public class EspnScoreScraper extends Scraper {

	@Override
	protected String buildUrl(Position pos) {
		String baseUrl = "http://games.espn.go.com/ffl/tools/projections?&slotCategoryId=";
		if (pos.equals(Position.QB)) {
			return baseUrl+"0";
		} else if (pos.equals(Position.RB)) {
			return baseUrl+"2";
		} else if (pos.equals(Position.WR)) {
			return baseUrl+"4";
		} else if (pos.equals(Position.TE)) {
			return baseUrl+"6";
		} else if (pos.equals(Position.K)) {
			return baseUrl+"17";
		} else if (pos.equals(Position.D)) {
			return baseUrl+"16";
		} else {
			throw new IllegalArgumentException("Unknown position "+pos);
		}
		
	}

	@Override
	protected String buildTeam(Document d, Position pos, int row)
			throws XPathExpressionException {
		if (pos.equals(Position.D)) {
			String team = super.buildName(d, pos, row);
			if (team.equals("")) {
				return team;
			}
			String letter3 = TeamNameMapper.nameTo3Letter.get(team);
			return letter3;
		} else {
			String team = super.buildTeam(d, pos, row);
			if (team.equals("")) {
				return team;
			}
			String parts[] = team.split(Pattern.quote(" "));
			if (parts.length < 2) {
				return "unknown";
			} else {
				// remove stuff after no break space.
				String[] parts2 = parts[1].split(Pattern.quote("\u00A0"));
				String letter2 = parts2[0].toUpperCase();
				return TeamNameMapper.map2Letterto3Letter(letter2);
			}
		}
	}
	
	@Override
	protected String buildTeamXPath(Position pos, int row) {
		return "//div[3]/div/table/tbody/tr[td]["+(row+1)+"]/td[1]/text()";
	}	

	@Override
	protected String buildNameXPath(Position pos, int row) {
		String xpath = "//div[3]/div/table/tbody/tr[td]["+(row+1)+"]/td[1]/a[1]";
		if (pos.equals(Position.D)) {
			return "substring("+xpath+", 1, string-length("+xpath+")-4)";
		}
		return xpath;
	}

	@Override
	protected String buildValueXPath(Position pos, int row) {
		return "//div[3]/div/table/tbody/tr[td]["+(row+1)+"]/td[14]";
	}

	@Override
	protected void addData(LeagueData data, String team, Position pos, String name, String value) {
		if (value.equals("")) {
			value = "0";
		}
		data.addScore(team, pos, name, (int)(10*Double.parseDouble(value)));
	}

	@Override
	public String getType() {
		return "score";
	}

	public static void main(String[] args) throws Exception {
		File cacheDir = new File("data/test");
		cacheDir.mkdirs();
		LeagueData data = new LeagueData();
		Scraper scoreScrapper = new EspnScoreScraper();
		scoreScrapper.scrapeData(data, cacheDir);
	}	

}
