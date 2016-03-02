package com.hisham.navview;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.hisham.navview.model.Auction;
import com.hisham.navview.model.Realm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GsonStreaming {
    GsonStreamListener listener;

    public interface GsonStreamListener {
        public void onDataReceived(String data);
    }

    public void setStreamListener(MainFragment mainFragment){
        listener = mainFragment;
    }

    public void readJson() {

        JsonReader reader;
        try {
            InputStream source = retrieveStream(new URL("http://auction-api-eu.worldofwarcraft.com/auction-data/258993a3c6b974ef3e6f22ea6f822720/auctions.json"));
            reader = new JsonReader(new InputStreamReader(source));
            reader.beginObject();

            while (reader.hasNext()) {
                //System.out.println(reader.nextString());
                String name = reader.nextName();

                if (name.equals("realm")) {
                        Realm realm = new Gson().fromJson(reader, Realm.class);
                        System.out.println(realm.getName() + " -- " + realm.getSlug());
                    listener.onDataReceived(realm.getName() + " -- " + realm.getSlug());
                } else if (name.equals("alliance")) {
                    reader.beginObject();
                    reader.nextName();
                    reader.beginArray();
                    while (reader.hasNext()){
                        Auction auction = new Gson().fromJson(reader, Auction.class);
                        System.out.println(auction.getItem() + " -- " + auction.getQuantity() + " -- " +auction.getOwner()+ " -- " +auction.getOwnerRealm());
                        listener.onDataReceived(auction.getItem() + " -- " + auction.getQuantity() + " -- " +auction.getOwner()+ " -- " +auction.getOwnerRealm());
                        Thread.sleep(100);
                    }
                } else {// unexpected value, skip it or generate error
                    reader.skipValue();
                }
            }

            reader.endObject();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream retrieveStream(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            return conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void prettyprint(JsonReader reader) throws IOException {
        while (true) {
            JsonToken token = reader.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    reader.beginArray();
                    break;
                case END_ARRAY:
                    reader.endArray();
                    break;
                case BEGIN_OBJECT:
                    reader.beginObject();
                    break;
                case END_OBJECT:
                    reader.endObject();
                    break;
                case NAME:
                    String name = reader.nextName();
                    break;
                case STRING:
                    String s = reader.nextString();
                    break;
                case NUMBER:
                    String n = reader.nextString();
                    break;
                case BOOLEAN:
                    boolean b = reader.nextBoolean();
                    break;
                case NULL:
                    reader.nextNull();
                    break;
                case END_DOCUMENT:
                    return;
            }
        }
    }

}