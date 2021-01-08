package exchange;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DirectoryManager {
	
    private Gson gson;

    DirectoryManager() {
        this.gson = new Gson();
    }

	public void sendTransaction(Transaction t) throws Exception {
	    TransactionDir td = new TransactionDir(t);
        String jsonTransaction = gson.toJson(td);
        String path = "http://localhost:8080/company/"+t.getCompany();
        URL url = new URL(path);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonTransaction);
        wr.flush();
        wr.close();
        int response = con.getResponseCode();
    }

    static class TransactionDir {
        private String company;
        private int quantity;
        private float price;
        
        TransactionDir(Transaction t) {
            this.company = t.getCompany();
            this.quantity = t.getQuantity();
            this.price = t.getPrice();
        }

        public String getCompany() {
            return this.company;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public float getPrice() {
            return this.price;
        }
    }

}

