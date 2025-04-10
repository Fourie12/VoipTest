package client;

import client.Voip;
import java.util.Scanner;

public class Call {
    public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Who you gonna call?");
		String ip = scanner.nextLine();
		System.out.println("What are the ports?");
		System.out.print("port1: ");
		int port1 = scanner.nextInt();
		System.out.print("port2: ");
		int port2 = scanner.nextInt();

		System.out.println("Calling " + ip + "...");
		Voip voip = new Voip(ip, port1, port2);

		System.out.println("Press Enter to end the call.");
		scanner.nextLine(); // Wait for user to press Enter
		scanner.nextLine(); // Wait for user to press Enter

		voip.stop();
		System.out.println("Call ended.");
		scanner.close();
    }
}

