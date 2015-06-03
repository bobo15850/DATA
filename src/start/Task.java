package start;

import html.MatchHtml;
import html.MatchMapHtml;
import html.PlayerHtml;
import html.PlayerMapHtml;
import html.TeamMapHtml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import database.DB;
import beans.GamePlayer;
import beans.GameTeam;
import beans.GeneralMatch;
import beans.GeneralPlayer;
import beans.GeneralTeam;
import beans.SeasonPlayer;
import beans.SeasonTeam;

public class Task {
	private DB db = DB.getInstance();

	public void getMatchs() {
		String before = "http://www.basketball-reference.com/leagues/NBA_";
		String urlString = null;
		String after = "_games.html";
		for (int m = 2002; m >= 2002; m--) {
			urlString = before + String.valueOf(m) + after;
			MatchMapHtml schedule = new MatchMapHtml(urlString);
			ArrayList<GeneralMatch> generalMatchList = schedule.getGeneralMatchList();
			ArrayList<String> detailMatchUrlList = schedule.getDetailMatchUrlList();
			for (int i = 0; i < detailMatchUrlList.size(); i++) {
				db.update(generalMatchList.get(i).getInsertTableStr());
				System.out.println("-------------------------" + detailMatchUrlList.get(i) + "-------------------------------------------");
				System.out.println("-------------------------" + generalMatchList.get(i) + "-----------------------------------------------");
				MatchHtml match = new MatchHtml(detailMatchUrlList.get(i), generalMatchList.get(i));
				HashMap<String, GamePlayer> gamePlayerMap = match.getGamePlayerMap();
				for (Entry<String, GamePlayer> temp : gamePlayerMap.entrySet()) {
					db.update(temp.getValue().getInsertTableStr());
				}
				HashMap<String, GameTeam> gameTeamMap = match.getGameTeamMap();
				for (Entry<String, GameTeam> temp : gameTeamMap.entrySet()) {
					db.update(temp.getValue().getInsertTableStr());
				}
			}
		}
	}// 得到比赛简略信息和详细信息

	public void getPlayer() {
		String UrlString = "http://www.basketball-reference.com/players/a";
		for (int k = 0; k < 26; k++) {
			PlayerMapHtml playerMap = new PlayerMapHtml(UrlString);
			ArrayList<GeneralPlayer> generalPlayerList = playerMap.getGeneralPlayerList();
			for (int i = 0; i < generalPlayerList.size(); i++) {
				db.update(generalPlayerList.get(i).getInsertTableStr());
				System.out.println(generalPlayerList.get(i).toString());
			}
			ArrayList<String> detailPlayerUrlList = playerMap.getDetailPlayerUrlList();
			for (int i = 0; i < detailPlayerUrlList.size(); i++) {
				System.out.println(detailPlayerUrlList.get(i));
				System.out.println(generalPlayerList.get(i).getPlayerName());
				PlayerHtml playerHtml = new PlayerHtml(detailPlayerUrlList.get(i), generalPlayerList.get(i).getPlayerName());
				HashMap<String, SeasonPlayer> playerSeasonMap = playerHtml.getSeasonPlayerList();
				for (Entry<String, SeasonPlayer> temp : playerSeasonMap.entrySet()) {
					db.update(temp.getValue().getInsertTableStr());
				}
			}
			String before = UrlString;
			UrlString = before.substring(0, UrlString.length() - 1) + String.valueOf(((char) (before.charAt(before.length() - 1) + 1)));
		}
	}// 得到球员基本信息和每个赛季比赛信息

	public void getTeam() {
		String url = "http://www.basketball-reference.com/teams/";
		TeamMapHtml teamMapHtml = new TeamMapHtml(url);
		ArrayList<String> teamSeasonMapUrlList = teamMapHtml.getTeamSeasonMapUrlList();
		for (int i = 0; i < teamSeasonMapUrlList.size(); i++) {
			System.out.println(teamSeasonMapUrlList.get(i));
		}
		ArrayList<String> teamNameList = teamMapHtml.getTeamNameList();
		for (int i = 0; i < teamNameList.size(); i++) {
			System.out.println(teamNameList.get(i));
		}
	}

	public void createDB() {
		DB db = DB.getInstance();
		db.update(new GamePlayer().getCreateTableStr());
		db.update(new GameTeam().getCreateTableStr());
		db.update(new GeneralMatch().getCreateTableStr());
		db.update(new GeneralPlayer().getCreateTableStr());
		db.update(new GeneralTeam().getCreateTableStr());
		db.update(new SeasonPlayer().getCreateTableStr());
		db.update(new SeasonTeam().getCreateTableStr());
	}// 建立数据库表格
}
