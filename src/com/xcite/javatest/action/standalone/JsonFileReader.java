package com.xcite.javatest.action.standalone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class JsonFileReader implements FileReader {
    @Override
    public Stream<Subscription> getSubscriptionsFromFile() {
        JSONArray ja = new JSONArray(getFile("WebContent/data/newslettersubs.json"));
        List<Subscription> subscriptions = new ArrayList<>();

        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;
            Subscription sub = new Subscription(
                    (Integer) jo.get("id"),
                    (Boolean) jo.get("subscribed"),
                    (Integer) jo.get("listid"),
                    String.valueOf(jo.get("createdate"))
            );
            subscriptions.add(sub);
        }

        return subscriptions.stream();
  }

    @Override
    public Stream<User> getUsersFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<User> users = objectMapper.readValue(getFile("WebContent/data/users.json"), new TypeReference<List<User>>() {});
            return users.stream();
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    private <T> List<T> getObjectsFromFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), new TypeReference<List<T>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static String getFile(String path) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new java.io.FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
        return "";
    }
}
