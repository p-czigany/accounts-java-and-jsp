package com.xcite.javatest.action.standalone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JsonFileReader implements FileReader {
    @Override
    public Stream<Subscription> getSubscriptionsFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Subscription> subscriptions = objectMapper.readValue(getFile("WebContent/data/newslettersubs.json"), new TypeReference<List<Subscription>>() {});
            return subscriptions.stream();
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    @Override
    public Stream<User> getUsersFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<User> users = objectMapper.readValue(getFile("WebContent/data/newslettersubs.json"), new TypeReference<List<User>>() {});
            return users.stream();
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    private <T> Stream<T> getObjectStreamFromFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<T> objects = objectMapper.readValue(new File(filePath), new TypeReference<List<T>>() {});
            return objects.stream();
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
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
