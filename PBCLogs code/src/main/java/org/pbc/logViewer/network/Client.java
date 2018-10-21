package org.pbc.logViewer.network;

import com.google.gson.Gson;
import org.pbc.logViewer.model.LogModel;
import org.pbc.logViewer.model.StatisticsModel;
import org.pbc.logViewer.utils.StringConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {

    private static Client client = null;
    private BufferedReader bufferedReader = null;
    private HttpURLConnection con = null;
    private URL obj = null;
    private final Gson gson = new Gson();
    private String TARGET_URL = StringConstants.EMPTY_STRING;

    private Client() {
        //Singleton
    }

    public synchronized static Client getInstance() {
        if (null == client) {
            client = new Client();
        }
        return client;
    }

    public Client setPointerLocation(final long pointerLocation) {
        this.TARGET_URL += pointerLocation;
        return client;
    }

    public Client setUrl(final String url) {
        this.TARGET_URL = url;
        return this;
    }

    public LogModel getLogData() {
        try {
            final String response = call();
            if (!response.isEmpty()) {
                return gson.fromJson(response, LogModel.class);
            } else {
                return null;
            }
        } catch (final Exception e) {
            System.out.println("Still loading log data...");
            return null;
        }
    }

    public StatisticsModel getStatisticsData() {
        try {
            final String response = call();
            if (!response.isEmpty()) {
                return gson.fromJson(response, StatisticsModel.class);
            } else {
                return null;
            }
        } catch (final Exception e) {
            System.out.println("Still Loading statistics data...");
            return null;
        }
    }

    private synchronized String call() {
        try {
            obj = new URL(TARGET_URL);
            con = (HttpURLConnection) obj.openConnection();
            con.addRequestProperty("Content-Type", "text/plain");
            final int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                bufferedReader = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                final StringBuilder response = new StringBuilder();
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            } else {
                return StringConstants.EMPTY_STRING;
            }
        } catch (final Exception e) {
            System.out.println("Error occurred during fetching data : " + e.getMessage());
            return StringConstants.EMPTY_STRING;
        }
    }
}
