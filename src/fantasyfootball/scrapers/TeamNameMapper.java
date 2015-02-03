package fantasyfootball.scrapers;

import java.util.HashMap;
import java.util.Map;

public class TeamNameMapper {
	public static final Map<String, String> nameTo3Letter = new HashMap<String, String>();
	public static String map2Letterto3Letter(String letter2) {
		if (letter2.length()==3) {
			return letter2;
		} else {
			return TeamNameMapper.letter2Toletter3Map.get(letter2);
		}
	}
	private static final Map<String, String> letter2Toletter3Map = new HashMap<String, String>();;
	static {
		letter2Toletter3Map.put("GB", "GNB");
		letter2Toletter3Map.put("NO", "NOR");
		letter2Toletter3Map.put("SF", "SFO");
		letter2Toletter3Map.put("TB", "TAM");
		letter2Toletter3Map.put("KC", "KAN");
		letter2Toletter3Map.put("NE", "NWE");
		letter2Toletter3Map.put("SD", "SDG");
		letter2Toletter3Map.put("FA", "FA");
		
		nameTo3Letter.put("Patriots", "NWE");
		nameTo3Letter.put("Cardinals", "ARI");
		nameTo3Letter.put("Bills", "BUF");
		nameTo3Letter.put("Lions", "DET");
		nameTo3Letter.put("Broncos", "DEN");
		nameTo3Letter.put("Chiefs", "KAN");
		nameTo3Letter.put("Steelers", "PIT");
		nameTo3Letter.put("Ravens", "BAL");
		nameTo3Letter.put("Browns", "CLE");
		nameTo3Letter.put("Seahawks", "SEA");
		nameTo3Letter.put("49ers", "SFO");
		nameTo3Letter.put("Dolphins", "MIA");
		nameTo3Letter.put("Bengals", "CIN");
		nameTo3Letter.put("Texans", "HOU");
		nameTo3Letter.put("Colts", "IND");
		nameTo3Letter.put("Eagles", "PHI");
		nameTo3Letter.put("Vikings", "MIN");
		nameTo3Letter.put("Rams", "STL");
		nameTo3Letter.put("Saints", "NOR");
		nameTo3Letter.put("Cowboys", "DAL");
		nameTo3Letter.put("Panthers", "CAR");
		nameTo3Letter.put("Packers", "GNB");
		nameTo3Letter.put("Giants", "NYG");
		nameTo3Letter.put("Bears", "CHI");
		nameTo3Letter.put("Jaguars", "JAC");
		nameTo3Letter.put("Titans", "TEN");
		nameTo3Letter.put("Redskins", "WAS");
		nameTo3Letter.put("Buccaneers", "TAM");
		nameTo3Letter.put("Chargers", "SDG");
		nameTo3Letter.put("Falcons", "ATL");
		nameTo3Letter.put("Raiders", "OAK");
		nameTo3Letter.put("Jets", "NYJ");
	}

}
