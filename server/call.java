package server;

import server.Voip;
import java.util.Scanner;

public class call {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String ip;
		System.out.println("Who you gonna call?");
		ip = scanner.nextLine();
		Voip voip = new Voip(ip);
	}
}
