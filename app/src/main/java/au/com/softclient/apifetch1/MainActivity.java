package au.com.softclient.apifetch1;


import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);

        // Make the API request
        new FetchDataFromApi().execute();
    }

    private class FetchDataFromApi extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    //.url("http://10.0.2.2:3000/api/users/2") // Use 10.0.2.2 for the Android emulator to reach localhost
                    .url("http://192.168.8.168:3000/api/users/3") // Use 10.0.2.2 for the Android emulator to reach localhost
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            if (jsonData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject userObject = jsonObject.getJSONObject("user");
                    String name = userObject.getString("name");
                    String email = userObject.getString("email");

                    // Update the TextViews with fetched data
                    nameTextView.setText("Name: " + name);
                    emailTextView.setText("Email: " + email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}