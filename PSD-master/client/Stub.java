package client;

import java.util.NoSuchElementException;
import org.zeromq.ZMQ;
import java.lang.*;
import java.io.*;
import java.net.*;
import org.json.JSONArray;
import org.json.JSONObject;

import client.Protocol.*;

public class Stub extends Thread {
	
	private Socket cliSocket;
	private ClientInfo client;
	
	private PrintWriter out;
	
	private InputStream is;
    private OutputStream os;

    private ZMQ.Socket sub;

	private Menu menu;
	private String[] initialMenu;
	private String[] sessionMenu;
	
	private String username;

	Stub(Socket cliSocket, ClientInfo client, ZMQ.Socket sub) throws IOException {
		this.cliSocket = cliSocket;
		this.client = client;
		this.is = cliSocket.getInputStream();
		this.os = cliSocket.getOutputStream();
		this.sub = sub;
		out = new PrintWriter(os, true);
		menu = new Menu();
		setUpMenus();
	}

	public void run() {
		int option;

		while((option = showMenu()) != -1) {
			client.setCommand(option);
			try {
				runCommand(option);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("\nConnection ended!");
		System.exit(0);
	}

	private int showMenu() {
		int option = 0;

		try {
			if (!client.isLogged())
				option = menu.show(initialMenu);
			else {
				sessionMenu[0] = "1) Read notifications " + "(" + client.getNumberOfNotifications() + ")";
				sessionMenu[1] = "2) Check " + "(" + client.getNumberOfTransactions() + ") transactions" ;
				option = menu.show(sessionMenu) + 2;
			}
		} catch (NoSuchElementException e) {
			return -1;
		}

		return option;
	}

	private void runCommand(int option) throws IOException {
		switch(option) {
			case 1: signup();
					break;
			case 2: login();
					break;
			case 3: readNotifications();
					break;
			case 4: readTransactions();
					break;
			case 5: subscribeCompany();
					break;
			case 6: unsubscribeCompany();
					break;
			case 7: sell();
					break;
			case 8: buy();
					break;
			case 9: try {listCompanies();} catch(Exception e) {System.out.println("Error receiving info");}
					break;
			case 10: try {companyInfo();} catch(Exception e) {System.out.println("Error receiving info");}
					break;
			case 11: nop();
					break;
		}
	}

	private static byte[] recvMsg(InputStream inpustream) {
    	try {
            byte len[] = new byte[4096];
            int count = inpustream.read(len); 
            byte[] temp = new byte[count];
            for (int i = 0; i < count; i++) { 
                temp[i] = len[i];
            } 
            return temp;
        } catch (Exception e) {
            System.out.println("recvMsg() occur exception!" + e.toString());
        }
        return null;
	}


	private void signup() throws IOException {
		String username = menu.readString("Username: ");
		String password = menu.readString("Password: ");
		
		User c = User.newBuilder().setUsername(username).setPassword(password).build();
		Message req = Message.newBuilder().setType("REGISTER").setUser(c).build();
		byte[] result = req.toByteArray();
		os.write(result);

		String response = client.getResponse();
		System.out.println("RESPONSE: " +response);
		if (client.getReplyStatus()) {
			
			req = Message.newBuilder().setType("LOGIN").setUser(c).build();
			result = req.toByteArray();
			os.write(result);

			response = client.getResponse();

			if(client.getReplyStatus()) {
				client.setLogged(true);
				this.username = username;
			}
		}
		
		menu.printResponse(response);
	}

	private void login() throws IOException {
		String username = menu.readString("Username: ");
		String password = menu.readString("Password: ");
		
		User c = User.newBuilder().setUsername(username).setPassword(password).build();
		Message req = Message.newBuilder().setType("LOGIN").setUser(c).build();
		byte[] result = req.toByteArray();
		os.write(result);
		String response = client.getResponse();

		if(client.getReplyStatus()) {
			client.setLogged(true);
			this.username = username;
		}else {
			menu.printResponse(response);
		}
	}

	private void readNotifications() {
		int amountNotifications;
		String notifications;

		synchronized (client) {
			amountNotifications = client.getNumberOfNotifications();
			notifications = client.getNotifications();
		}

		if (amountNotifications == 0)
			notifications = "> Still no notifications to present!\n";

		menu.printResponse(notifications);
	}

	private void readTransactions() {
		int amountTransactions;
		String transactions;

		synchronized (client) {
			amountTransactions = client.getNumberOfTransactions();
			transactions = client.getTransactions();
		}

		if (amountTransactions == 0)
			transactions = "> Still no transactions happened!\n";

		menu.printResponse(transactions);
	}

	private void subscribeCompany() {
		String company = menu.readString("Company to subscribe: ");
		
		sub.subscribe(company.getBytes());
		System.out.println("Transactions from " + company + " subscribed.\n");
	}

	private void unsubscribeCompany() {
		String company = menu.readString("Company to subscribe: ");
		sub.unsubscribe(company.getBytes());
		System.out.println("Transactions from " + company + " unsubscribed.\n");
	}

	private void sell() throws IOException {
		String company = menu.readString("Company: ");
		int quantity = menu.readInt("Quantity to buy: ");
		float price = menu.readFloat("Limit price: ");
		
		User c = User.newBuilder().setUsername(username).build();
		Order o = Order.newBuilder().setCompany(company).setQuantity(quantity).setPrice(price).build();
		Message m = Message.newBuilder().setType("SELL").setUser(c).setOrder(o).build();
		byte[] result = m.toByteArray();

		os.write(result);
	}

	private void buy() throws IOException {
		String company = menu.readString("Company: ");
		int quantity = menu.readInt("Quantity to buy: ");
		float price = menu.readFloat("Limit price: ");
		
		System.out.println("Maria " + username);
		User c = User.newBuilder().setUsername(username).build();
		Order o = Order.newBuilder().setCompany(company).setQuantity(quantity).setPrice(price).build();
		Message m = Message.newBuilder().setType("BUY").setUser(c).setOrder(o).build();
		
		byte[] result = m.toByteArray();
		os.write(result);
	}


	private void listCompanies() throws Exception {
		String path = "http://localhost:8080/companies";
		URL url = new URL(path);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		int response = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer reply = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			reply.append(inputLine);
		}
		in.close();
		/*
		JSONArray jsonCompanies = new JSONArray(reply.toString());
		for (int i = 0; i < jsonCompanies.length(); i++) {
			JSONObject object = jsonCompanies.getJSONObject(i);
			String company = object.toString();
			System.out.println(" " + company);
		}
		*/
		System.out.println(reply.toString());
	}

	private void companyInfo() throws Exception {
		String company = menu.readString("Company: ");
		String path = "http://localhost:8080/company/" + company;
		URL url = new URL(path);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		int response = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer reply = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			reply.append(inputLine);
		}
		in.close();
		System.out.println(reply.toString());
			/*
		JSONObject c_info = new JSONObject(reply.toString());
		System.out.println("Company name: " + c_info.getString("name"));
		System.out.println("Exchange: " + c_info.getJSONObject("exchange").getString("name"));
		System.out.println("Yesterday:");
		System.out.println(" minimum: " + c_info.getJSONObject("yesterday").getDouble("minimum"));
		System.out.println(" maximum: " + c_info.getJSONObject("yesterday").getDouble("maximum"));
		System.out.println(" open: " + c_info.getJSONObject("yesterday").getDouble("open"));
		System.out.println(" close: " + c_info.getJSONObject("yesterday").getDouble("close"));
		System.out.println("Today:");
		System.out.println(" minimum: " + c_info.getJSONObject("today").getDouble("minimum"));
		System.out.println(" maximum: " + c_info.getJSONObject("today").getDouble("maximum"));
		System.out.println(" open: " + c_info.getJSONObject("today").getDouble("open"));
	*/
	}

	private void nop() {}


	private void setUpMenus() {
		initialMenu = new String[2];
		sessionMenu = new String[9];

		initialMenu[0] = "1) Register";
		initialMenu[1] = "2) Login";

		sessionMenu[2] = "3) Subscribe company";
		sessionMenu[3] = "4) Unsubscribe company";
		sessionMenu[4] = "5) Sell shares";
		sessionMenu[5] = "6) Buy shares";
		sessionMenu[6] = "7) List companies";
		sessionMenu[7] = "8) Get company info";
		sessionMenu[8] = "9) Nop";
	}
}
