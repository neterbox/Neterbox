package com.neterbox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.neterbox.customadapter.Circle_Adapter;
import com.neterbox.jsonpojo.circle.Circlepage;
import com.neterbox.jsonpojo.circle.CircleListDatum;
import com.neterbox.jsonpojo.country.Country;
import com.neterbox.jsonpojo.country.CountryDatum;
import com.neterbox.jsonpojo.state.State;
import com.neterbox.jsonpojo.state.StateDatum;
import com.neterbox.retrofit.APIClient;
import com.neterbox.retrofit.APIInterface;
import com.neterbox.utils.Sessionmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Circles extends Activity {
    GridView gcirclegrid;
    Activity activity;
    ImageView ileft, iright;
    TextView title;
    int i;
    String countrystr = "", statestr = "", citystr = "" ,index="1";
    String country_id = "0", state_id = "0";


    private Spinner spinner1, spinner2, spinner3;


    ArrayList<String> country = new ArrayList<>();
    ArrayList<String> state = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();
    ArrayList<CountryDatum> datumcountry = new ArrayList<>();
    ArrayList<StateDatum> datumstate = new ArrayList<>();
    List<CircleListDatum> circleListData = new ArrayList<>();
    Sessionmanager sessionmanager;
    ImageView ichat, icircle, iplay;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    private int firstVisiblePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circles);
        Log.i("TAG","test");

        activity = this;
        datumstate = new ArrayList<>();
        datumcountry = new ArrayList<>();
        circleListData = new ArrayList<>();
        sessionmanager = new Sessionmanager(activity);
        idMappings();
        List<CircleListDatum> circleList= new ArrayList<>();

        country_api();
        Circle(index);

        listener();


    }

    private void idMappings() {

        gcirclegrid = (GridView) findViewById(R.id.gcirclegrid);
        ichat = (ImageView) findViewById(R.id.ichat);
        icircle = (ImageView) findViewById(R.id.icircle);
        iplay = (ImageView) findViewById(R.id.iplay);
        ileft = (ImageView) findViewById(R.id.ileft);
        iright = (ImageView) findViewById(R.id.iright);
        title = (TextView) findViewById(R.id.title);
        ileft.setImageResource(R.drawable.home);
        iright.setVisibility(View.INVISIBLE);
        title.setText("Channels");

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
    }

    private void listener() {
        gcirclegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                sessionmanager.createSession_circledata(circleListData.get(pos));

                Log.e("data",":"+new Gson().toJson(circleListData.get(pos)));
                Intent i = new Intent(Circles.this, Circle_chat.class);
                i.putExtra("circledataextra",(Serializable)circleListData.get(pos));

                startActivity(i);
                finish();

            }
        });
        ichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =new Intent(Circles.this,ContactsForChatActivityNew.class);

                startActivity(i);
                finish();
            }
        });
        iplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Circles.this, PlayGridview.class);
                startActivity(i);
                finish();
            }
        });
        ileft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Circles.this, HomePage.class);
                startActivity(i);
                finish();
            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinner1 == null) {
                    spinner2.setEnabled(false);
                }
//                Log.e("datumCountry",":"+new Gson().toJson(datumcountry));
//                Log.e("position","=============="+ position);
//
//                Log.e("datumCountry","=========="+new Gson().toJson(datumcountry.get(position)));
                if(position!=0){
                countrystr = country.get(position);
                    country_id = datumcountry.get(position-1).getCountry().getId();
                    state_api(country_id);
            }else{
                    spinner2.setEnabled(true);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statestr = state.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    int currentFirstVisPos = getFirstVisiblePosition();

//    int myLastVisiblePos = gcirclegrid.getFirstVisiblePosition();
    private GridView.OnScrollListener inanswerScrolled = new GridView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollstate) {

            int myLastVisiblePos = 0;
            if (currentFirstVisPos > myLastVisiblePos) {
                //scroll down
            }
            if (currentFirstVisPos < myLastVisiblePos) {
                //scroll up
            }
            myLastVisiblePos = currentFirstVisPos;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.e("GridView", "firstVisibleItem" + firstVisibleItem + "\nLastVisibleItem" + totalItemCount);
        }

    };

    public void Circle(final String index)
    {
        Call<Circlepage> circleCall = apiInterface.Circlelistpojo(index);

        circleCall.enqueue(new Callback <Circlepage>() {
            @Override
            public void onResponse(Call<Circlepage> call, Response<Circlepage> res) {
                if (res.body().getStatus() .equals( "Success")) {
                    for(CircleListDatum  circleListDatum :res.body().getData())
                    {
                        circleListData.add(circleListDatum);
                    }

                    Circle_Adapter adapter = new Circle_Adapter(activity, circleListData);
                    gcirclegrid.setAdapter(adapter);
                    gcirclegrid.setOnScrollListener(inanswerScrolled);



//                    Toast.makeText(Circles.this, res.body().getMeesae(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Circles.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Circlepage> call, Throwable t) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Circles.this, HomePage.class);
        startActivity(i);
        finish();
    }

    public void country_api() {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please Wait...");
        dialog.show();
        dialog.dismiss();
        final Call<Country> countrycall = apiInterface.countrypojo();
        countrycall.enqueue(new Callback<Country>() {
            @Override
            public void onResponse(Call<Country> call, Response<Country> response) {
                datumcountry.clear();
                country.clear();
                country.add("Country");
                for (i = 0; i < response.body().getData().size(); i++) {
                    datumcountry.add(response.body().getData().get(i));
                    country.add(response.body().getData().get(i).getCountry().getName());
                    adapter_country();
                }

            }

            @Override
            public void onFailure(Call<Country> call, Throwable t) {
            }
        });

    }


    public void adapter_country() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(aa);
    }

    public void state_api(String country_id) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please Wait...");
        dialog.show();
        dialog.dismiss();
        final Call<State> stateCall = apiInterface.statepojo(country_id);
        stateCall.enqueue(new Callback<State>() {
            @Override
            public void onResponse(Call<State> call, Response<State> response) {
                datumstate.clear();
                state.clear();
                state.add("State");
                for (i = 0; i < response.body().getData().size(); i++) {
                    datumstate.add(response.body().getData().get(i));
                    state.add(response.body().getData().get(i).getState().getName());
                    //Log.e("state", new Gson().toJson(datumstate));
                    adapter_state();
                }
            }

            @Override
            public void onFailure(Call<State> call, Throwable t) {
            }
        });
    }

    public void adapter_state() {
        ArrayAdapter ss = new ArrayAdapter(this, android.R.layout.simple_spinner_item, state);
        ss.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(ss);
    }

    public int getFirstVisiblePosition() {
        return firstVisiblePosition;
    }
}