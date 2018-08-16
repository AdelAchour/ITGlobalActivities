package com.production.achour_ar.gshglobalactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.DataModel.TicketModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListTickets extends AppCompatActivity {

    ArrayList<TicketModel> TicketModels;
    ListView listView;
    private static TicketAdapter adapter;
    String session_token, nameUser, idUser, firstnameUser, nbTicket;
    RequestQueue queue;
    String titreTicket, slaTicket, urgenceTicket, idTicket,
    demandeurTicket, categorieTicket, etatTicket, dateDebutTicket, statutTicket,
            dateEchanceTicket, dateClotureTicket, descriptionTicket, lieuTicket;
    boolean ticketEnretard;


    String nbClos;

    public int nbTicketTab = 8;

    public String[][] ticketTab ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tickets);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Tickets en cours");
        //actionBar.setIcon(R.drawable.ic_action_search);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.setupactionbar, null);

        actionBar.setCustomView(v);


        queue = Volley.newRequestQueue(this);

        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nbTicket = i.getStringExtra("nb");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        listView=(ListView)findViewById(R.id.list);


        TicketModels = new ArrayList<>();
        ticketTab = new String[Integer.valueOf(nbTicket)][nbTicketTab];


        getTicketsHTTP();

        final Handler handlerRefresh = new Handler();

       /* Runnable refresh = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Actualisation", Toast.LENGTH_SHORT).show();
                adapter.clear();
                getTicketsHTTP();
                handlerRefresh.postDelayed(this, 120 * 1000);
            }
        };

        handlerRefresh.postDelayed(refresh, 120 * 1000);*/


        ImageView refreshIcon ;
        refreshIcon = (ImageView)findViewById(R.id.refreshIconID);
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                getTicketsHTTP();
            }
        });


    }

    private void getTicketsHTTP() {
        String url = FirstEverActivity.GLPI_URL+"search/Ticket";

        List<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("criteria[0][field]","5"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));
        params.add(new KeyValuePair("forcedisplay[0]","4"));
        params.add(new KeyValuePair("forcedisplay[1]","10"));
        params.add(new KeyValuePair("forcedisplay[2]","7"));
        params.add(new KeyValuePair("forcedisplay[3]","12"));
        params.add(new KeyValuePair("forcedisplay[4]","15"));
        params.add(new KeyValuePair("forcedisplay[5]","30"));
        params.add(new KeyValuePair("forcedisplay[6]","18"));
        params.add(new KeyValuePair("forcedisplay[7]","21"));
        params.add(new KeyValuePair("forcedisplay[8]","83"));
        params.add(new KeyValuePair("forcedisplay[9]","82"));
        params.add(new KeyValuePair("forcedisplay[10]","16"));
        params.add(new KeyValuePair("forcedisplay[11]","2"));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                try {
                                    JSONObject oneTicket = Jdata.getJSONObject(i);
                                    // Récupération des items pour le row_item
                                    titreTicket = oneTicket.getString("1");
                                    slaTicket = oneTicket.getString("30");
                                    dateDebutTicket = oneTicket.getString("15");
                                    urgenceTicket = oneTicket.getString("10");
                                    statutTicket = oneTicket.getString("12");
                                    idTicket = oneTicket.getString("2");

                                    //Récupération du reste
                                    demandeurTicket = oneTicket.getString("4");
                                    categorieTicket = oneTicket.getString("7");
                                    etatTicket = oneTicket.getString("12");
                                    dateEchanceTicket = oneTicket.getString("18");
                                    descriptionTicket = oneTicket.getString("21");

                                    lieuTicket = oneTicket.getString("83");
                                    dateClotureTicket = oneTicket.getString("16");
                                    ticketEnretard = getBooleanFromSt(oneTicket.getString("82"));

                                } catch (JSONException e) {
                                    Log.e("Nb of data: "+Jdata.length()+" || "+"Error JSONArray at "+i+" : ", e.getMessage());
                                }
                                // ------------------------


                                /* Remplissage du tableau des tickets pour le row item */
                                ticketTab[i][0] = titreTicket;
                                ticketTab[i][1] = slaTicket;
                                ticketTab[i][2] = dateDebutTicket;
                                ticketTab[i][3] = urgenceText(urgenceTicket);
                                ticketTab[i][4] = calculTempsRestant(dateDebutTicket, slaTicket, dateEchanceTicket);
                                ticketTab[i][5] = String.valueOf(ticketEnretard);
                                ticketTab[i][6] = statutTicket;
                                ticketTab[i][7] = idTicket;

                                // -----------------------------

                            }

                            addModelsFromTab(ticketTab);

                            adapter = new TicketAdapter(TicketModels,getApplicationContext());

                            listView.setAdapter(adapter);


                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                    TicketModel TicketModel= TicketModels.get(position);

                                    Snackbar.make(view, "id = "+TicketModel.getIdTicket(), Snackbar.LENGTH_LONG)
                                            .setAction("No action", null).show();

                                    Intent i = new Intent(getApplicationContext(), InfoTicket.class);
                                    i.putExtra("session",session_token);
                                    i.putExtra("nom",nameUser);
                                    i.putExtra("prenom",firstnameUser);
                                    i.putExtra("id",idUser);
                                    i.putExtra("idTicket", TicketModel.getIdTicket());

                                    startActivity(i);


                                        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),  "")
                                                .setSmallIcon(R.drawable.refreshicon)
                                                .setContentTitle("Urgent")
                                                .setContentText("Faut faire vite tu sais")
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                        notificationManager.notify(0, mBuilder.build());*/


                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        Log.e("Error.Response", error.toString());
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token",FirstEverActivity.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };

        // add it to the RequestQueue
        queue.add(getRequest);

    }


    public void notifyUser(String Nom, String timeLeftText, Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,  "")
                .setSmallIcon(R.drawable.refreshicon)
                .setContentTitle("Urgent")
                .setContentText("Ticket "+Nom+" expire dans : "+timeLeftText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(0, mBuilder.build());
    }

    private boolean getBooleanFromSt(String string) {
        boolean bool = false;
        if(string.equals("0")){
            bool = false;
        }
        else if (string.equals("1")){
            bool = true;
        }
        return bool;
    }

    private String getBetweenBrackets(String slaTicket) {
        String between = "";

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(slaTicket);
        while (matcher.find()){
            between = matcher.group();
        }

        return between;
    }

    private String getDigit(String text) {
        String digit = "";

        Pattern pattern = Pattern.compile("([\\d]+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            digit = matcher.group();
        }

        return digit;
    }

    private String getMinTemps(String slaTicket) {
        String between = getBetweenBrackets(slaTicket);
        String minTemps = "";

        Pattern pattern = Pattern.compile("^(.*?)\\/");
        Matcher matcher = pattern.matcher(between);
        while (matcher.find()){
            minTemps = matcher.group();
        }

        return getDigit(minTemps);
    }

    private String getMaxTemps(String slaTicket) {
        String between = getBetweenBrackets(slaTicket);
        String maxTemps = "";

        Pattern pattern = Pattern.compile("([\\d]+)(?=[^\\/]*$)");
        Matcher matcher = pattern.matcher(between);
        while (matcher.find()){
            maxTemps = matcher.group();
        }


        return maxTemps;
    }

    private String calculTempsRestant(String dateDebutTicket, String slaTicket, String dateEcheance) {
        String minTemps = getMinTemps(slaTicket);
        String maxTemps = getMaxTemps(slaTicket);

        long dateDebutMS = getDateDebutMS(dateDebutTicket);
        long dateEcheanceMS = getDateDebutMS(dateEcheance);

        long currentTimeMS = CurrentTimeMS();

        long differenceEcheanceCurrent = dateEcheanceMS - currentTimeMS;

        long minTempsMS = hourToMSConvert(minTemps);
        long maxTempsMS = hourToMSConvert(maxTemps);

        long differenceCurrentDebut = currentTimeMS - dateDebutMS;

        long tempsRestant = maxTempsMS - differenceCurrentDebut;

        return String.valueOf(differenceEcheanceCurrent);

    }

    private long hourToMSConvert(String minTemps) {
        long time = Long.valueOf(minTemps)*3600000;
        return time;
    }

    private long CurrentTimeMS() {
        long time = System.currentTimeMillis();
        return time;
    }

    private long getDateDebutMS(String dateDebutTicket) {
        long dateDebutMS = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //"2018-07-17 11:58:47
        formatter.setLenient(false);

        String oldTime = dateDebutTicket;
        Date oldDate = null;
        try {
            oldDate = formatter.parse(oldTime);
        } catch (ParseException e) { e.printStackTrace(); }
        dateDebutMS = oldDate.getTime();

        return dateDebutMS;
    }

    private void addModelsFromTab(String[][] ticketTab) {
        for (int i = 0; i < ticketTab.length; i++){
            if (!ticketTab[i][6].equals("6")) {
                TicketModel ticket = new TicketModel(ticketTab[i][0], ticketTab[i][1], ticketTab[i][2], ticketTab[i][4], ticketTab[i][7], ticketTab[i][6]);
                ticket.setUrgenceTicket(ticketTab[i][3]);
                ticket.setTicketEnRetard(Boolean.parseBoolean(ticketTab[i][5]));
                //ticket.setTempsRestantTicket(ticketTab[i][4]);

                TicketModels.add(ticket);
            }
        }
    }

    private void AfficheTab(String[][] ticketTab) {
        System.out.println("\n --- Tableau de ticket --- \n");
        for (int i = 0; i < ticketTab.length; i++){
            for(int j = 0; j<ticketTab[0].length; j++){
                System.out.print(ticketTab[i][j]+" ");
            }
            System.out.println("\n");
        }
    }

    private String urgenceText(String urgenceTicket) {
        String urgence = "";
        int urg = Integer.valueOf(urgenceTicket);
        switch (urg){
            case 1:
                urgence = "Très basse";
            break;
            case 2:
                urgence = "Basse";
                break;
            case 3:
                urgence = "Moyenne";
                break;
            case 4:
                urgence = "Haute";
                break;
            case 5:
                urgence = "Très haute";
                break;
        }

        return urgence;
    }

    private String etatText(String etatTicket) {
        String etat = "";
        int et = Integer.valueOf(etatTicket);
        switch (et){
            case 1:
                etat = "Nouveau";
                break;
            case 2:
                etat = "En cours (Attribué)";
                break;
            case 3:
                etat = "En cours (Planifié)";
                break;
            case 4:
                etat = "En attente";
                break;
            case 5:
                etat = "Résolu";
                break;
            case 6:
                etat = "Clos";
                break;
        }

        return etat;
    }


    public static String generateUrl(String baseUrl, List<KeyValuePair> params) {
        if (params.size() > 0) {
            int cpt = 1 ;
            for (KeyValuePair parameter: params) {
                if (cpt==1){
                    baseUrl += "?" + parameter.getKey() + "=" + parameter.getValue();
                }
                else{
                    baseUrl += "&" + parameter.getKey() + "=" + parameter.getValue();
                }
                cpt++;
            }
        }
        return baseUrl;
    }

    public static void triTableauTicketParUrgence(String tableau[][]) {
        int longueur = tableau.length;
        boolean foundHaute = false;
        boolean foundMoyenne = false;

            // ---- Tri des urgences hautes/très hautes ---
            for (int i = 0; i < longueur; i++) {
                if ((!tableau[i][3].equals("Haute"))||(!tableau[i][3].equals("Très haute"))) {
                    for (int k = i+1; k< longueur; k++){
                        if (((tableau[k][3].equals("Haute"))||(tableau[k][3].equals("Très haute")))&&(!foundHaute)){
                            foundHaute = true;
                            permuter(k,i,tableau);
                        }
                    }
                    foundHaute = false;
                }
            }

            // ---- Tri des urgences moyennes ---
        for (int i = nbHauteUrgences(tableau); i < longueur; i++) {
            if (!tableau[i][3].equals("Moyenne")) {
                for (int k = i+1; k< longueur; k++){
                    if ((tableau[k][3].equals("Moyenne"))&&(!foundMoyenne)){
                        foundMoyenne = true;
                        permuter(k,i,tableau);
                    }
                }
                foundMoyenne = false;
            }
        }
    }

    public static void triInfoTicketParUrgence(String tableau[][]) {
        int longueur = tableau.length;
        boolean foundHaute = false;
        boolean foundMoyenne = false;

        // ---- Tri des urgences hautes/très hautes ---
        for (int i = 0; i < longueur; i++) {
            if ((!tableau[i][1].equals("Haute"))||(!tableau[i][1].equals("Très haute"))) {
                for (int k = i+1; k< longueur; k++){
                    if (((tableau[k][1].equals("Haute"))||(tableau[k][1].equals("Très haute")))&&(!foundHaute)){
                        foundHaute = true;
                        permuter(k,i,tableau);
                    }
                }
                foundHaute = false;
            }
        }

        // ---- Tri des urgences moyennes ---
        for (int i = nbHauteUrgences(tableau); i < longueur; i++) {
            if (!tableau[i][1].equals("Moyenne")) {
                for (int k = i+1; k< longueur; k++){
                    if ((tableau[k][1].equals("Moyenne"))&&(!foundMoyenne)){
                        foundMoyenne = true;
                        permuter(k,i,tableau);
                    }
                }
                foundMoyenne = false;
            }
        }
    }

    private static int nbHauteUrgences(String[][] tableau) {
        int nbTickets = 0;
        for(int i = 0; i < tableau.length; i++){
            if ((tableau[i][3].equals("Haute"))||(tableau[i][3].equals("Très haute"))){
                nbTickets++;
            }
        }

        return nbTickets;
    }

    private static void permuter(int k, int i, String[][] tableau) {
        String[] tampon = new String[4] ;

        tampon[0] = tableau[k][0]; tampon[1] = tableau[k][1]; tampon[2] = tableau[k][2]; tampon[3] = tableau[k][3];

        tableau[k][0] = tableau[i][0]; tableau[k][1] = tableau[i][1]; tableau[k][2] = tableau[i][2]; tableau[k][3] = tableau[i][3];

        tableau[i][0] = tampon[0]; tableau[i][1] = tampon[1]; tableau[i][2] = tampon[2]; tableau[i][3] = tampon[3];
    }

    private static void permuterInfo(int k, int i, String[][] tableau) {
        String[] tampon = new String[10] ;

        tampon[0] = tableau[k][0]; tampon[1] = tableau[k][1]; tampon[2] = tableau[k][2]; tampon[3] = tableau[k][3]; tampon[4] = tableau[k][4]; tampon[5] = tableau[k][5]; tampon[6] = tableau[k][6]; tampon[7] = tableau[k][7]; tampon[8] = tableau[k][8]; tampon[9] = tableau[k][9];

        tableau[k][0] = tableau[i][0]; tableau[k][1] = tableau[i][1]; tableau[k][2] = tableau[i][2]; tableau[k][3] = tableau[i][3]; //ajouter until 9

        tableau[i][0] = tampon[0]; tableau[i][1] = tampon[1]; tableau[i][2] = tampon[2]; tableau[i][3] = tampon[3]; //ajouter till 9
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemRetard:
                //Toast.makeText(getApplicationContext(), "Retard Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemClos:
                //Toast.makeText(getApplicationContext(), "Clos Selected", Toast.LENGTH_SHORT).show();
                getnbClos(session_token, idUser);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ToTicketsClos(String session_token, String idUser, String nbClos) {
        Intent i = new Intent(getApplicationContext(), ListTicketsClos.class);
        i.putExtra("session",session_token);
        i.putExtra("id",idUser);
        i.putExtra("nb",nbClos);
        startActivity(i);
    }

    private void getnbClos(final String session_token, final String idUser) {

        RequestQueue queuenb = Volley.newRequestQueue(this);

        String url = FirstEverActivity.GLPI_URL+"search/Ticket";

        List<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("criteria[0][field]","5"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));
        params.add(new KeyValuePair("criteria[1][link]","AND"));
        params.add(new KeyValuePair("criteria[1][field]","12"));
        params.add(new KeyValuePair("criteria[1][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[1][value]","6"));
        params.add(new KeyValuePair("forcedisplay[0]","4"));

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                           String ns = response.getString("totalcount");
                           ToTicketsClos(session_token, idUser, ns);

                        } catch (JSONException e) {
                            Log.e("No response ",e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        Log.e("Error.Response", error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token",FirstEverActivity.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };

        // add it to the RequestQueue
        queuenb.add(getRequest);

    }

    public String getNbClos() {
        return nbClos;
    }

    public void setNbClos(String nbClos) {
        this.nbClos = nbClos;
    }
}

