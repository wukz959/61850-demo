package com.myworld;

import com.myworld.Client.Client;
import com.myworld.listener.EventListener;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.openiec61850.ServiceError;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.util.Map;

/**
 * @ClassName TestConnected
 * @Descripton TODO
 * @Author wkz
 * @Date 2023/12/7 16:38
 * @Version 1.0
 */
@Slf4j
public class TestConnected {
    public static Map<String, String> combinedMap = new HashMap<>();
    public static void main(String[] args) throws IOException, ServiceError {
        TestConnected.readDataFromJson();
        InetAddress remoteAddress = InetAddress.getByName("198.121.0.23");
        InetAddress localAdress = InetAddress.getByName("198.121.0.169");

        EventListener eventListener = new EventListener();
        eventListener.setCallBack(collect -> {
            for (List<String> list : collect) {
                String str = list.get(0);
                int lastIndexOf = str.lastIndexOf(".");
                String key = str.substring(0,lastIndexOf);
                String name = combinedMap.get(key);
                for (int i = 1; i < list.size(); i++) {
                    String f = list.get(i);
                    if (f.contains(".mag.f")){
                        System.out.println(key + " " + name + " " + f);
                        break;
                    }
                }
//                if (name!=null){
//                    appendFile("./need.txt",key + " " + name + " " + f);
//                }else {
//                    appendFile("./notIn.txt", key + " " + name + " " + f);
//                }
            }
        });

        new Client.Builder(remoteAddress, localAdress, eventListener)
                .build()
                .connected();

    }
    public static void readDataFromJson(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File("src/main/resources/transformed_data .json");
            java.util.List<Map<String, String>> listOfMaps = objectMapper.readValue(
                    jsonFile, new TypeReference<java.util.List<Map<String, String>>>() {}
            );

            for (Map<String, String> map : listOfMaps) {
                combinedMap.putAll(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    static void appendFile(String filePath, String textToAppend) {
//        try (FileWriter writer = new FileWriter(filePath, true)) {
//            writer.write(textToAppend + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
