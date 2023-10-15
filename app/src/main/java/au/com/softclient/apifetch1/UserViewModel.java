package au.com.softclient.apifetch1;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log; // Import Log class
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<User> userLiveData;
    private static final String TAG = "UserViewModel"; // Define a TAG for logging

    public UserViewModel(Application application) {
        super(application);
        userLiveData = new MutableLiveData<>();
        fetchUser();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void fetchUser() {
        new FetchUserTask().execute();
    }

    private class FetchUserTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://192.168.8.168:3000/api/users/3") // Replace with your API endpoint
                        .build();

                // Log the API URL for debugging
                Log.d("TAG", "API URL: " + request.url().toString());

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject userObject = jsonObject.getJSONObject("user");

                    User user = new User();
                    user.setName(userObject.getString("name"));
                    user.setEmail(userObject.getString("email"));

                    // Log the fetched name and email for debugging
                    Log.d("TAG", "Name: " + user.getName());
                    Log.d("TAG", "Email: " + user.getEmail());

                    return user;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            userLiveData.postValue(user);
        }
    }

    public static class User {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
