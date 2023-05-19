package aula9;

import aula9.api.java.Message;
import aula9.mastodon.Mastodon;

public class Test {
	public static void main(String[] args) {
		switch (args[0]) {
			case "create":
				createMessage(args[1]);
				break;
			case "delete":
				deleteMessage(Long.parseLong(args[1]));
				break;
		}

		var res = Mastodon.getInstance().getMessages(null, 0);
		System.out.println(res);
	}

	private static void createMessage(String msg) {
		var res = Mastodon.getInstance().postMessage(null, null, new Message(0, USERNAME, "", msg));
		System.out.println(res);
	}

	private static void deleteMessage(long mid) {
		var res = Mastodon.getInstance().removeFromPersonalFeed(null, mid, null);
		System.out.println(res);
	}
}
