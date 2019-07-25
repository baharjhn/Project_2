package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity{
    SQLiteDatabase commentDatabase;
    CountDownTimer countDownTimers;
    Long t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        commentDatabase = ((SubApp)this.getApplication()).commentDatabase;
        final String url = "https://jsonplaceholder.typicode.com/posts";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            create_posts(response);
                       }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        );

        queue.add(getRequest);

    }

    public void onClick(View v){
        TextView tv = (TextView) v;
        String s = tv.getText().toString();
        String[] temp = s.split(" ");
        String id = temp[1];
        Cursor result = (Cursor) commentDatabase.rawQuery("Select * from COMMENT where postid = '"+id+"' ", null);
        Intent intent = new Intent(this, commentActivity.class);
        intent.putExtra("key", temp[1]);
        result.moveToFirst();
        if (result.getCount() == 0) {
            intent.putExtra("serverordb","0");
            startActivity(intent);
            t = System.currentTimeMillis();
        }
        else if(result.getCount() != 0 && isNetworkConnected() == false)
        {
            intent.putExtra("serverordb", "1");
            startActivity(intent);
        }
        else if(result.getCount() != 0 && isNetworkConnected() == true && (System.currentTimeMillis() - t > 10000)){
            System.out.println("base");
            intent.putExtra("serverordb", "0");
            startActivity(intent);
            t = System.currentTimeMillis();
        }
        else{
            intent.putExtra("serverordb", "1");
            startActivity(intent);
        }
    }
    public void dialog(View v){
        final Context c = this;
        new dialog(c).run();
    }
    public void changeview(View v){
        final View firstview = (View)findViewById(R.id.my_list);
        ViewSwitcher viewSwitcher = findViewById(R.id.viewswitch);
        if(viewSwitcher.getCurrentView() != firstview)
            viewSwitcher.showPrevious();
        else
            viewSwitcher.showNext();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public void create_posts(JSONArray jsonArray) throws JSONException {
        int length = jsonArray.length();
        ArrayList<post> posts = new ArrayList<>();
        for(int i = 0; i < length; ++i){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String userid1 = jsonObject.getString("userId");
            String userid = "userId: " + userid1;
            String id1 = jsonObject.getString("id");
            String id = "id: " + id1;
            String title1 = jsonObject.getString("title");
            String title = "title: " + title1;
            String body1 = jsonObject.getString("body");
            String body = "body: " + body1;
            posts.add(new post(userid, id, title, body));

        }
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for(int i = 0; i < length; ++i){
            HashMap<String, String> item = new HashMap<String,String>();
            item.put("line1", posts.get(i).userid);
            item.put("line2", posts.get(i).id);
            item.put("line3", posts.get(i).title);
            item.put("line4", posts.get(i).body);
            list.add(item);
        }
        SimpleAdapter sa1 = new SimpleAdapter(this, list, R.layout.activity_listview, new String[] {"line1", "line2", "line3", "line4"
        }, new int[]{R.id.userId, R.id.id, R.id.title, R.id.body});
        ListView lv = (ListView) findViewById(R.id.my_list);
        lv.setAdapter(sa1);

        SimpleAdapter sa2 = new SimpleAdapter(this, list, R.layout.activity_gridview, new String[] {"line1", "line2", "line3", "line4"
        }, new int[]{R.id.userId, R.id.id, R.id.title, R.id.body});
        GridView gv = (GridView)findViewById(R.id.my_grid);
        gv.setAdapter(sa2);
    }
}
