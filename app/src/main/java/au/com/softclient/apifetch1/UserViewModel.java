package au.com.softclient.apifetch1;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.drafts.Draft_6455;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<User> userLiveData;
    private static final String TAG = "APIURL";
    private WebSocketClient webSocketClient;

    public UserViewModel(Application application) {
        super(application);
        userLiveData = new MutableLiveData<>();
        connectWebSocket(); // Connect to WebSocket
        fetchUser(); // Fetch initial user data
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void fetchUser() {
        new FetchUserTask().execute();
    }

    private void connectWebSocket() {
        try {
            URI uri = new URI("ws://192.168.8.168:3000");
            Draft_6455 draft = new Draft_6455();

            webSocketClient = new WebSocketClient(uri, draft) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.d("WS", "WebSocket connected");
                }

                @Override
                public void onMessage(String s) {
                    handleWebSocketMessage(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.d("WS", "WebSocket closed: " + i + " - " + s);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("WS", "WebSocket failure: " + e.getMessage());
                    // Handle reconnection or notify the user of the failure
                }
            };

            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void handleWebSocketMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            int userId = jsonObject.getInt("id");
            if (userId == 3) { // Check if the message is for user with id 3
                JSONObject userObject = jsonObject.getJSONObject("user");
                User user = new User();
                user.setName(userObject.getString("name"));
                user.setEmail(userObject.getString("email"));
                Log.d("WS", "WebSocket For ID=: 3");
                userLiveData.postValue(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("WS", "Error WebSocket For ID=: 3");
        }
    }

    private class FetchUserTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://192.168.8.168:3000/api/users/3") // Replace with your API endpoint
                        .build();

                Log.d(TAG, "API URL: " + request.url().toString());

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject userObject = jsonObject.getJSONObject("user");

                    User user = new User();
                    user.setName(userObject.getString("name"));
                    user.setEmail(userObject.getString("email"));

                    Log.d(TAG, "Name: " + user.getName());
                    Log.d(TAG, "Email: " + user.getEmail());

                    return user;
                }
            } catch (Exception e) {
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}



//package au.com.softclient.apifetch1;
//
//import android.app.Application;
//import android.os.AsyncTask;
//import android.util.Log;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.WebSocket;
//import okhttp3.WebSocketListener;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//public class UserViewModel extends AndroidViewModel {
//    private MutableLiveData<User> userLiveData;
//    private static final String TAG = "APIURL";
//    private WebSocket webSocket;
//
//    public UserViewModel(Application application) {
//        super(application);
//        userLiveData = new MutableLiveData<>();
//        connectWebSocket(); // Connect to WebSocket
//        fetchUser(); // Fetch initial user data
//    }
//
//    public LiveData<User> getUserLiveData() {
//        return userLiveData;
//    }
//
//    public void fetchUser() {
//        new FetchUserTask().execute();
//    }
//
//    private void connectWebSocket() {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                //.url("ws://192.168.8.168:3000/websocket") // Replace with your WebSocket URL
//                .url("ws://192.168.8.168:3000") // Replace with your WebSocket URL
//                .build();
//
//        WebSocketListener webSocketListener = new WebSocketListener() {
//            @Override
//            public void onOpen(WebSocket webSocket, Response response) {
//                super.onOpen(webSocket, response);
//                Log.d("WS", "WebSocket connected");
//            }
//
//            @Override
//            public void onMessage(WebSocket webSocket, String text) {
//                super.onMessage(webSocket, text);
//                handleWebSocketMessage(text);
//            }
//
//            @Override
//            public void onClosed(WebSocket webSocket, int code, String reason) {
//                super.onClosed(webSocket, code, reason);
//                Log.d("WS", "WebSocket closed: " + code + " - " + reason);
//            }
//
//            @Override
//            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
//                super.onFailure(webSocket, t, response);
//                Log.e("WS", "WebSocket failure: " + t.getMessage());
//                // Handle reconnection or notify the user of the failure
//            }
//        };
//
//        webSocket = client.newWebSocket(request, webSocketListener);
//    }
//
//    private void handleWebSocketMessage(String message) {
//        try {
//            JSONObject jsonObject = new JSONObject(message);
//            int userId = jsonObject.getInt("id");
//            if (userId == 3) { // Check if the message is for user with id 3
//                JSONObject userObject = jsonObject.getJSONObject("user");
//                User user = new User();
//                user.setName(userObject.getString("name"));
//                user.setEmail(userObject.getString("email"));
//                Log.d("WS", "WebSocket For ID=: 3" );
//                userLiveData.postValue(user);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.d("WS", "Error WebSocket For ID=: 3" );
//        }
//    }
//
//    private class FetchUserTask extends AsyncTask<Void, Void, User> {
//        @Override
//        protected User doInBackground(Void... voids) {
//            try {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url("http://192.168.8.168:3000/api/users/3") // Replace with your API endpoint
//                        .build();
//
//                Log.d(TAG, "API URL: " + request.url().toString());
//
//                Response response = client.newCall(request).execute();
//
//                if (response.isSuccessful()) {
//                    String jsonData = response.body().string();
//                    JSONObject jsonObject = new JSONObject(jsonData);
//                    JSONObject userObject = jsonObject.getJSONObject("user");
//
//                    User user = new User();
//                    user.setName(userObject.getString("name"));
//                    user.setEmail(userObject.getString("email"));
//
//                    Log.d(TAG, "Name: " + user.getName());
//                    Log.d(TAG, "Email: " + user.getEmail());
//
//                    return user;
//                }
//            } catch (IOException | JSONException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(User user) {
//            userLiveData.postValue(user);
//        }
//    }
//
//    public static class User {
//        private String name;
//        private String email;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//    }
//
//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        webSocket.cancel(); // Cancel WebSocket when ViewModel is no longer used
//    }
//}


//package au.com.softclient.apifetch1;
//
//import android.app.Application;
//import android.os.AsyncTask;
//import android.util.Log;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.WebSocket;
//import okhttp3.WebSocketListener;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.io.IOException;
//
//public class UserViewModel extends AndroidViewModel {
//    private MutableLiveData<User> userLiveData;
//    private static final String TAG = "APIURL";
//    private WebSocket webSocket;
//
//    public UserViewModel(Application application) {
//        super(application);
//        userLiveData = new MutableLiveData<>();
//        fetchUser(); // Fetch initial user data
//        connectWebSocket(); // Connect to WebSocket
//    }
//
//    public LiveData<User> getUserLiveData() {
//        return userLiveData;
//    }
//
//    public void fetchUser() {
//        new FetchUserTask().execute();
//    }
//
//    private void connectWebSocket() {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("ws://192.168.8.168:3000/websocket") // Replace with your WebSocket URL
//                .build();
//
//        WebSocketListener webSocketListener = new WebSocketListener() {
//            @Override
//            public void onOpen(WebSocket webSocket, Response response) {
//                super.onOpen(webSocket, response);
//                Log.d("WS", "WebSocket connected");
//            }
//
//            @Override
//            public void onMessage(WebSocket webSocket, String text) {
//                super.onMessage(webSocket, text);
//                handleWebSocketMessage(text);
//            }
//
//            /*
//            @Override
//            public void onMessage(WebSocket webSocket, ByteString bytes) {
//                super.onMessage(webSocket, bytes);
//                // Handle binary messages if needed
//            }
//            */
//            @Override
//            public void onClosed(WebSocket webSocket, int code, String reason) {
//                super.onClosed(webSocket, code, reason);
//                Log.d("WS", "WebSocket closed: " + code + " - " + reason);
//            }
//
//            @Override
//            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
//                super.onFailure(webSocket, t, response);
//                Log.e("WS", "WebSocket failure: " + t.getMessage());
//            }
//        };
//
//        webSocket = client.newWebSocket(request, webSocketListener);
//    }
//
//    private void handleWebSocketMessage(String message) {
//        try {
//            JSONObject jsonObject = new JSONObject(message);
//            JSONObject userObject = jsonObject.getJSONObject("user");
//
//            User user = new User();
//            user.setName(userObject.getString("name"));
//            user.setEmail(userObject.getString("email"));
//
//            userLiveData.postValue(user);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private class FetchUserTask extends AsyncTask<Void, Void, User> {
//        @Override
//        protected User doInBackground(Void... voids) {
//            try {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url("http://192.168.8.168:3000/api/users/3") // Replace with your API endpoint
//                        .build();
//
//                Log.d(TAG, "API URL: " + request.url().toString());
//
//                Response response = client.newCall(request).execute();
//
//                if (response.isSuccessful()) {
//                    String jsonData = response.body().string();
//                    JSONObject jsonObject = new JSONObject(jsonData);
//                    JSONObject userObject = jsonObject.getJSONObject("user");
//
//                    User user = new User();
//                    user.setName(userObject.getString("name"));
//                    user.setEmail(userObject.getString("email"));
//
//                    Log.d(TAG, "Name: " + user.getName());
//                    Log.d(TAG, "Email: " + user.getEmail());
//
//                    return user;
//                }
//            } catch (IOException | JSONException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(User user) {
//            userLiveData.postValue(user);
//        }
//    }
//
//    public static class User {
//        private String name;
//        private String email;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//    }
//
//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        webSocket.cancel(); // Cancel WebSocket when ViewModel is no longer used
//    }
//}
