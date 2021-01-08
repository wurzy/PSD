package client;
import java.util.Scanner;
import client.*;

public class Menu {
	private Scanner in;

	Menu() {
		in = new Scanner(System.in);
		in.useDelimiter("[\r\n]");
	}

	public int show(String[] entries) {
		int option = 0;

		String menu = String.join("\n", entries);
		System.out.println(menu + "\n");

		while(option <= 0 || option > entries.length) {
	 		option = readInt("Choose one of the options: ");
			if (option <= 0 || option > entries.length)
				System.out.println("\n> Invalid option\n");
		}

		return option;
	}

	public void printResponse(String response) {
		if (response.length() > 0)
			response += "\n";

		System.out.print("\n" + response);
	}

	public String readString(String msg) {
		System.out.print(msg);
		return in.next();
	}

	public int readInt(String msg) {
		int num;

		try {
			System.out.print(msg);
			num = Integer.parseInt(in.next());
		} catch (NumberFormatException e) {
			System.out.println("\n> The value introduced is not valid\n");
			num = readInt(msg);
		}

		return num;
	}

	public float readFloat(String msg) {
		float num;

		try {
			System.out.print(msg);
			num = Float.parseFloat(in.next());
		} catch (NumberFormatException e) {
			System.out.println("\n> The value introduced is not valid\n");
			num = readFloat(msg);
		}
		
		return num;
	}
}
