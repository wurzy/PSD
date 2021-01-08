package client;

import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;
import org.zeromq.ZMQ;

import client.*;
import client.Protocol.*;

public class Reader extends Thread {
	private ClientInfo client;
	private Socket cliSocket;
	private InputStream is;

	Reader(Socket cliSocket, ClientInfo client) throws IOException {
		this.client = client;
		this.cliSocket = cliSocket;
		this.is = cliSocket.getInputStream();
	}

	public void run() {
		String header, content;
		Message m;
		while(((m = readMessage()) != null)) {
			
			header = getStatus(m);
			content = readResponse(m);

			giveMessage(header, content);
		}

		System.out.println("\nConnection ended by the server.");
		System.exit(1);
	}

	private void giveMessage(String header, String content) {
		if (header.equals("EXCEPTION"))
			client.setReply(false, "> " + content);
		else if (header.equals("OK"))
			client.setReply(true, content);
		else
			client.addTransaction(content);
	}

	private String readTransaction(Message m) {
		String response;
		String comp = m.getOrder().getCompany();
		int quant = m.getOrder().getQuantity();
		float price = m.getOrder().getPrice();
		String data = quant + " of " + comp + " shares for " + price + " each.";
		
		switch(m.getResponse().getResult()) {
			case "SELL": response = "You sold " + data; 
						 break;
			case "BUY": response = "You bought " + data;
					    break;
			default: response = "Transaction: " + data;
					 break;
		}
		return response;
	}

	private String getStatus(Message m) {
		return m.getResponse().getResult();
	}


	private String readResponse(Message m) {
		return m.getResponse().getDescription();
	}


	private Message readMessage() {
		Message m = null;

		try {
			byte[] msg = recvMsg(is);
			m = Message.parseFrom(msg);
		} catch(Exception e) {
			System.out.println("Erro ao ler mensagem.");
		}
		return m;
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

}
