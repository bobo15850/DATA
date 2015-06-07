package start;

import html.MatchHtml;
import html.MatchMapHtml;
import html.PlayOffMapHtml;
import html.PlayerHtml;
import html.PlayerImageHtml;
import html.PlayerMapHtml;
import html.SeasonHtml;
import html.SeasonMapHtml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import beans.GamePlayer;
import beans.GameTeam;
import beans.GeneralMatch;
import beans.GeneralPlayer;
import beans.PlayOffSeries;
import beans.SeasonPlayer;
import beans.SeasonTeam;
import database.DB;

public class Task {
	private DB db = DB.getInstance();

	public void getMatchs() {
		String before = "http://www.basketball-reference.com/leagues/NBA_";
		String urlString = null;
		String after = "_games.html";
		for (int m = 2015; m >= 1950; m--) {
			urlString = before + String.valueOf(m) + after;
			MatchMapHtml schedule = new MatchMapHtml(urlString);
			ArrayList<GeneralMatch> generalMatchList = schedule.getGeneralMatchList();
			ArrayList<String> detailMatchUrlList = schedule.getDetailMatchUrlList();
			for (int i = 0; i < detailMatchUrlList.size(); i++) {
				db.update(generalMatchList.get(i).getInsertTableStr());
				System.out.println("-------------------------" + detailMatchUrlList.get(i) + "-------------------------------------------");
				System.out.println("------" + generalMatchList.get(i) + "-----------------------------------------------");
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
			}
			ArrayList<String> detailPlayerUrlList = playerMap.getDetailPlayerUrlList();
			for (int i = 0; i < detailPlayerUrlList.size(); i++) {
				System.out.println(detailPlayerUrlList.get(i));
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
		String url = "http://www.basketball-reference.com/leagues";
		SeasonMapHtml seasonMapHtml = new SeasonMapHtml(url);
		ArrayList<String> seasonUrlList = seasonMapHtml.getSeasonUrlList();
		ArrayList<String> seasonList = seasonMapHtml.getSeasonList();
		for (int i = 0; i < seasonUrlList.size(); i++) {
			SeasonHtml seasonHtml = new SeasonHtml(seasonUrlList.get(i), seasonList.get(i));
			System.out.println(seasonUrlList.get(i));
			HashMap<String, SeasonTeam> seasonTeamMap = seasonHtml.getSeasonTeamMap();
			for (Entry<String, SeasonTeam> temp : seasonTeamMap.entrySet()) {
				db.update(temp.getValue().getInsertTableStr());
			}
		}
	}// 得到球队常规赛的每个赛季比赛信息

	public void getPlayOff() {
		String urlString = "http://www.basketball-reference.com/playoffs";
		PlayOffMapHtml playOffHtml = new PlayOffMapHtml(urlString);
		ArrayList<PlayOffSeries> playOffSeriesList = playOffHtml.getPlayOffSeriesList();
		for (int i = 0; i < playOffSeriesList.size(); i++) {
			db.update(playOffSeriesList.get(i).getInsertTableStr());
		}
	}

	public void getPlayerImage() {
		String UrlString = "http://www.basketball-reference.com/players/a";
		for (int k = 0; k < 26; k++) {
			PlayerMapHtml playerMap = new PlayerMapHtml(UrlString);
			ArrayList<GeneralPlayer> generalPlayerList = playerMap.getGeneralPlayerList();
			ArrayList<String> detailPlayerUrlList = playerMap.getDetailPlayerUrlList();
			for (int i = 0; i < detailPlayerUrlList.size(); i++) {
				String htmlUrl = detailPlayerUrlList.get(i);
				String playerName = generalPlayerList.get(i).getPlayerName();
				PlayerImageHtml playerImage = new PlayerImageHtml(htmlUrl, playerName);
				playerImage.getImagePNG();
			}
			String before = UrlString;
			UrlString = before.substring(0, UrlString.length() - 1) + String.valueOf(((char) (before.charAt(before.length() - 1) + 1)));
		}
	}

	public void createDB() {
		DB db = DB.getInstance();
		db.update(new GamePlayer().getCreateTableStr());
		db.update(new GameTeam().getCreateTableStr());
		db.update(new GeneralMatch().getCreateTableStr());
		db.update(new GeneralPlayer().getCreateTableStr());
		db.update(new SeasonPlayer().getCreateTableStr());
		db.update(new SeasonTeam().getCreateTableStr());
		db.update(new PlayOffSeries().getCreateTableStr());
	}// 建立数据库表格
}
