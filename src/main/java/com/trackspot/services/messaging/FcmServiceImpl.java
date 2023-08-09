package com.trackspot.services.messaging;

import org.springframework.stereotype.Service;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;


/**
 * Created by Saurabh on 18/07/23.
 */
@Service
public class FcmServiceImpl implements FcmService {

    private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/fcm/send";
    private static final String AUTHORIZATION_KEY = "AAAA80oZcs4:APA91bHjx0_uMP7t4ZQY2WTrh6tLNiR7Rc82fkySOp1Y7Jdfe0oKb_e3qFSns-kfZUtFhzJD0DQLcbAk-68b8M_g6Z6KY7fThG3Ls1JkQF2lHfvt2TG9v21ELgwcWLlv-ZZnrSK2YAPP";

    public void sendSimpleMessage(String to, boolean isCustomLocation, boolean isNewTrip, double latitude, double longitude) throws Exception {
        try {
            URL url = new URL(FCM_ENDPOINT);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "key=" + AUTHORIZATION_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String body = "{ \"data\": { \"data\": { \"latitude\": \"" + latitude + "\", \"longitude\": \"" + longitude + "\", \"isCustomLocation\": " + isCustomLocation + ", \"isNewTrip\": " + isNewTrip + " } }, \"to\": \"" + to + "\" }";
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();

            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                System.out.println("Error sending notification: " + connection.getResponseMessage());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            System.out.println("Response: " + response);
            connection.disconnect();
        } catch (IOException e) {
            throw new Exception("ERROR SENDING NOTIFICATION : " + e);
        }
    }
}
