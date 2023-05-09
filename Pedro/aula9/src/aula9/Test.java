package aula9;

import aula9.api.java.Message;
import aula9.mastodon.Mastodon;

public class Test {
	private static String USERNAME = "pmmrfernandes";
	private static String PASSWORD = "h7ES2J8dq9THi9UN";

	public static void main(String[] args) {
		switch (args[0]) {
			case "create":
				createMessage(args[1]);
				break;
			case "delete":
				deleteMessage(Long.parseLong(args[1]));
				break;
		}

		var res = Mastodon.getInstance().getMessages(USERNAME, 0);
		System.out.println(res);
	}

	private static void createMessage(String msg) {
		var res = Mastodon.getInstance().postMessage(USERNAME, PASSWORD, new Message(0, USERNAME, "", msg));
		System.out.println(res);
	}

	private static void deleteMessage(long mid) {
		var res = Mastodon.getInstance().removeFromPersonalFeed(USERNAME, mid, PASSWORD);
		System.out.println(res);
	}
}
