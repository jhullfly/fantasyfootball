package fantasyfootball;

public class Player {
	public Position getPosition() {
		return position;
	}
	public int getScore() {
		return score;
	}
	public String getName() {
		return name;
	}
	public Player(String team, Position position, String name) {
		super();
		this.team = team;
		this.teamHashCode = team.hashCode();
		this.position = position;
		this.name = name;
	}
	public int getCost() {
		return cost;
	}
	public String getTeam() {
		return team;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getTeamHashCode() {
		return teamHashCode;
	}
	public void setScore(int score) {
		this.score = score;
	}
	private final String team;
	private final Position position;
	private final String name;
	private final int teamHashCode;
	private int cost;
	private int score;
}
