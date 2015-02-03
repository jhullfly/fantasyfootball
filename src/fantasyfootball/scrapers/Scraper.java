package fantasyfootball.scrapers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import fantasyfootball.LeagueData;
import fantasyfootball.Position;
import fantasyfootball.UrlToXml;

public abstract class Scraper {

	public Scraper() {
		super();
	}

	protected abstract String buildUrl(Position pos);
	protected abstract String buildNameXPath(Position pos, int row);
	protected abstract String buildValueXPath(Position pos, int row);
	protected abstract String buildTeamXPath(Position pos, int row);
	protected abstract void addData(LeagueData data, String team, Position pos, String name, String value);

	public abstract String getType();

	public void scrapeData(LeagueData data, File cacheDir) throws Exception {
		File cacheDir2 = new File(cacheDir, getType());
		cacheDir2.mkdirs();
		for (Position pos : Position.values()) {
			if (!shouldSkip(pos)) {
				scrapeData(data, pos, cacheDir);	
			}
		}
	}

	protected boolean shouldSkip(Position pos) {
		return false;
	}

	public void scrapeData(LeagueData data, Position pos, File cacheDir)
			throws Exception {	
				File fileName = new File(new File(cacheDir, getType()), pos.toString()+".csv");
				UrlToXml urlToXml = new UrlToXml();
				String cookies = getCookies();
				if (cookies != null) {
					urlToXml.setCookies(cookies);
				}
				String url = buildUrl(pos);
				Document d = urlToXml.toXml(url);
				System.out.println("Fetching url = "+url);
				parseData(data, pos, d, fileName);
			}

	protected String getCookies() {
		return null;
	}

	protected String evalXPath(Document d, String xpathS)
			throws XPathExpressionException {
				javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
				javax.xml.xpath.XPath xpath = factory.newXPath();
				String result = xpath.compile(xpathS).evaluate(d);	
				return result.trim();
			}


	protected void parseData(LeagueData data, Position pos, Document d,
			File cacheFile) throws IOException, XPathExpressionException {
				FileWriter out = new FileWriter(cacheFile);
				PrintWriter sout = new PrintWriter(out);
				try {
					int row = 1;
					while(true) {
						String team = buildTeam(d, pos, row).trim();
						String name = buildName(d, pos, row).trim();
						String score = evalXPath(d, buildValueXPath(pos, row)).trim();
						if (name.equals("") && score.equals("")) {
							System.out.println("Processed "+(row-1)+" items");
							break;
						}
						row++;
						sout.println(team+","+name +","+score);
						//sout.println("		nameTo3Letter.put(\""+name+"\", \""+team+"\");");
						addData(data, team, pos, name, score);
					}
				} finally {
					// TODO Auto-generated catch block
					sout.close();
					out.close();
				}
			}

	protected String buildName(Document d, Position pos, int row)
			throws XPathExpressionException {
		return evalXPath(d, buildNameXPath(pos, row));
	}

	protected String buildTeam(Document d, Position pos, int row)
			throws XPathExpressionException {
		return evalXPath(d, buildTeamXPath(pos, row));
	}


}