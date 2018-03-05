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
import com.neterbox.jsonpojo.CirclePostadd.CirclePostAddP;
import com.neterbox.jsonpojo.circle.Circlepage;
import com.neterbox.jsonpojo.circle.CircleListDatum;
import com.neterbox.jsonpojo.city.City;
import com.neterbox.jsonpojo.city.CityDatum;
import com.neterbox.jsonpojo.country.Country;
import com.neterbox.jsonpojo.country.CountryDatum;
import com.neterbox.jsonpojo.state.State;
import com.neterbox.jsonpojo.state.StateDatum;
import com.neterbox.retrofit.APIClient;
import com.neterbox.retrofit.APIInterface;
<<<<<<< HEAD
import com.neterbox.utils.Helper;
=======
import com.neterbox.utils.Constants;
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
import com.neterbox.utils.Sessionmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.neterbox.utils.Sessionmanager.user_id;

public class Circles extends Activity {
    GridView gcirclegrid;
    Activity activity;
    ImageView ileft, iright;
    TextView title;
    int i;
    String countrystr = "", statestr = "", citystr = "" ,index="";
    String country_id = "0", state_id = "0";


    private Spinner spinner1, spinner2, spinner3;

<<<<<<< HEAD
=======

>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
    ArrayList<String> country = new ArrayList<>();
    ArrayList<String> state = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();
    ArrayList<CountryDatum> datumcountry = new ArrayList<>();
    ArrayList<StateDatum> datumstate = new ArrayList<>();
    List<CityDatum> datumcity = new ArrayList<>();
    List<CircleListDatum> circleListData = new ArrayList<>();
    Sessionmanager sessionmanager;
    ImageView ichat, icircle, iplay;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    private int firstVisiblePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circles);
        Log.i("TAG", "test");

        activity = this;
        datumstate = new ArrayList<>();
        datumcountry = new ArrayList<>();
<<<<<<< HEAD
        datumcity = new ArrayList<>();
        circleListData = new ArrayList<>();
        sessionmanager = new Sessionmanager(activity);
        idMappings();
        listener();
        country_api();
        Circle(index);
        if (country_id.equals("0")&& state_id.equals("0"))
        {
            gcirclegrid.setEnabled(false);
        }
        else
        {
            gcirclegrid.setEnabled(true);
        }
=======
        sessionmanager = new Sessionmanager(activity);
        idMappings();
        List<CircleListDatum> circleList= new ArrayList<>();
        country_api();
        Circle(index);

        listener();

        String comments = null;
        MultipartBody.Part post_files = null;
        String circle_id = null;
        String countries_id = null;
//        CirclePostAdd(user_id, circle_id, countries_id, state_id, comments, post_files);

    }
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d

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
                Intent i = new Intent(Circles.this, CirclePost.class);
                i.putExtra("circledataextra",(Serializable)circleListData.get(pos));

<<<<<<< HEAD
                if (country_id.equals("0")&& state_id.equals("0"))
                {
                    Toast.makeText(activity, "Please Select Country", Toast.LENGTH_SHORT).show();
                    gcirclegrid.setEnabled(false);
                }
                else
                {
                    gcirclegrid.setEnabled(true);
                }
                sessionmanager.createSession_circledata(circleListData.get(pos));
                Intent it = new Intent(Circles.this, CirclePost.class);
                startActivity(it);
=======
                startActivity(i);
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
                finish();

            }
        });
        ichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Circles.this, ChatModule.class);

                startActivity(i);
                finish();
            }
        });
<<<<<<< HEAD

=======
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
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
                if (position != 0) {
                    countrystr = country.get(position);
                    country_id = datumcountry.get(position - 1).getCountry().getId();
                    if (country_id.equals("0") || state_id.equals("0"))
                    {
                        Toast.makeText(activity, "Please Select State", Toast.LENGTH_SHORT).show();
                        gcirclegrid.setEnabled(false);
                    }
                    else
                    {
                        gcirclegrid.setEnabled(true);
                    }
                    if (Helper.isConnectingToInternet(activity)) {
                        state_api(country_id);
                    } else {
                        Helper.showToastMessage(activity, "No Internet Connection");
                    }
                } else {
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
                if (spinner2 == null) {
                    spinner3.setEnabled(false);
                }
                if (position != 0) {
                    statestr = state.get(position);
                    state_id = datumstate.get(position - 1).getState().getId();
                    if (country_id.equals("0")|| state_id.equals("0"))
                    {
                        gcirclegrid.setEnabled(false);
                    }
                else
                    {
                        gcirclegrid.setEnabled(true);
                    }
                    if (Helper.isConnectingToInternet(activity)) {
                        city_api(state_id);
                    } else {
                        Helper.showToastMessage(activity, "No Internet Connection");
                    }
                } else {
                    spinner3.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                citystr = city.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    int currentFirstVisPos = getFirstVisiblePosition();
    private GridView.OnScrollListener inanswerScrolled = new GridView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollstate) {

            int myLastVisiblePos = 0;
            if (currentFirstVisPos > myLastVisiblePos) {
            }
            if (currentFirstVisPos < myLastVisiblePos) {
            }
            myLastVisiblePos = currentFirstVisPos;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.e("GridView", "firstVisibleItem" + firstVisibleItem + "\nLastVisibleItem" + totalItemCount);
        }

    };
<<<<<<< HEAD
    public void Circle(String index)
=======

    public void Circle(final String index)
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
    {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please Wait...");
        dialog.show();
        Call<Circlepage> circleCall = apiInterface.Circlelistpojo(index);
        circleCall.enqueue(new Callback <Circlepage>() {
            @Override
            public void onResponse(Call<Circlepage> call, Response<Circlepage> res) {
                dialog.dismiss();
                if (res.body().getStatus().equals( "Success")) {

                    circleListData.clear();
                    for(CircleListDatum  circleListDatum :res.body().getData())
                    {
                        circleListData.add(circleListDatum);
                    }
                    Circle_Adapter adapter = new Circle_Adapter(activity, circleListData);
                    gcirclegrid.setAdapter(adapter);
<<<<<<< HEAD
=======
                    gcirclegrid.setOnScrollListener(inanswerScrolled);



//                    Toast.makeText(Circles.this, res.body().getMeesae(), Toast.LENGTH_SHORT).show();
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
                } else {
                    Toast.makeText(Circles.this,"Please try Again", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Circlepage> call, Throwable t) {
<<<<<<< HEAD
                Toast.makeText(activity, "Please try Again", Toast.LENGTH_SHORT).show();
=======
                dialog.dismiss();
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
            }
        });
    }


      /*  TODO CIrcle PostADD  API */

    public void CirclePostAdd(final String user_id , String circle_id, String countries_id, String state_id, String comments, MultipartBody.Part post_files){
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please Wait...");
        dialog.show();

        final Call<CirclePostAddP> circlePostAddPCall = apiInterface.circlepostaddpojocall(user_id , circle_id, countries_id, state_id, comments, post_files);
        circlePostAddPCall.enqueue(new Callback<CirclePostAddP>() {
            @Override
            public void onResponse(Call<CirclePostAddP> call, Response<CirclePostAddP> response) {
                dialog.dismiss();
                if (response.body().getStatus().equals("Success")) {
                    sessionmanager.createSession_circlepostadddata(response.body());
                    Sessionmanager.setPreferenceBoolean(Circles.this, Constants.IS_LOGIN, true);
                    Intent i = new Intent(Circles.this, CirclePost.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(Circles.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onFailure(Call<CirclePostAddP> call, Throwable t) {
                dialog.dismiss();
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
        final Call<Country> countrycall = apiInterface.countrypojo();
        countrycall.enqueue(new Callback<Country>() {
            @Override
            public void onResponse(Call<Country> call, Response<Country> response) {
                dialog.dismiss();
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
                dialog.dismiss();
                Toast.makeText(activity, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void adapter_country() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(aa);
    }

    public void state_api(final String country_id) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please Wait...");
        dialog.show();
        final Call<State> stateCall = apiInterface.statepojo(country_id);
        stateCall.enqueue(new Callback<State>() {
            @Override
            public void onResponse(Call<State> call, Response<State> response) {
                dialog.dismiss();
                datumstate.clear();
                state.clear();
                state.add("State");
                for (i = 0; i < response.body().getData().size(); i++) {
                    datumstate.add(response.body().getData().get(i));
                    state.add(response.body().getData().get(i).getState().getName());
                    adapter_state();
                }
            }

            @Override
            public void onFailure(Call<State> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void adapter_state() {
        ArrayAdapter ss = new ArrayAdapter(this, android.R.layout.simple_spinner_item, state);
        ss.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(ss);
    }

    public void city_api(String state_id) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please Wait...");
        dialog.show();

        final Call<City> stateCall = apiInterface.citypojo(state_id);
        stateCall.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                dialog.dismiss();
                datumcity.clear();
                city.clear();
                city.add("City");

                datumcity = response.body().getData();

                if (datumcity!=null)
                {
                    for(i=0;i<response.body().getData().size();i++)
                    {
//                    datumcity.add(response.body().getData().get(i));
                        city.add(response.body().getData().get(i).getCity().getName());
                        adapter_city();
                    }
                }
                else {
                    Toast.makeText(activity, "No City Found", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<City> call, Throwable t) {
                Toast.makeText(activity, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void adapter_city() {
        ArrayAdapter ss = new ArrayAdapter(this, android.R.layout.simple_spinner_item, city);
        ss.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(ss);
    }

    public int getFirstVisiblePosition() {
        return firstVisiblePosition;
    }
}