package com.example.weight_watcher.Controllers.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.weight_watcher.Model.Adapters.Adapter;
import com.example.weight_watcher.Model.GridViewRows.Row;
import com.example.weight_watcher.Model.Notification.Notifications;
import com.example.weight_watcher.Model.Toasts.App_Toasts.App_Toast;
import com.example.weight_watcher.Model.User.User;
import com.example.weight_watcher.R;
import com.example.weight_watcher.views.Grid_Display;
import com.example.weight_watcher.views.Measurement_View;
import com.example.weight_watcher.views.Update_Database_Entry;

public class Main_Grid_View {

    public Grid_Display gridDisplay;
    public Adapter adapter;
    public Update_Database_Entry updates;
    public SharedPreferences sharedPref;
    public String email;
    public Intent toUpdateDatabase;
    public Row[] rows;
    private TextView welcomeText;
    private TextView progressText;
    private Button editButton;
    private Button deleteButton;
    private User currentUser;
    private final GridView gridView;
    private Notifications notification;
    private Double poundsLeft;

//Initializer
    public Main_Grid_View(Grid_Display display) {
        this.gridDisplay = display;
        this.gridView = gridDisplay.grid;
        this.gridDisplay.actionButton.setOnClickListener(toAddNewWeight);
        sharedPref = this.gridDisplay.getSharedPreferences(String.valueOf(R.string.userPreference), Context.MODE_PRIVATE);
        this.email = sharedPref.getString("User","");
        Log.v("Shared Preff Email", email);

    }

    //Set the email
    public void setEmail() {

        this.email = this.gridDisplay.usersDb.email;
        Log.v("Set email", email);

    }

    //Sets the grid adapter for the main page
    public void setAdapter() {

        this.adapter = gridDisplay.adapter;
        this.adapter.gridView = this;
        this.gridView.setAdapter(this.adapter);


    }

    //Returns all the users weights and attaches them to the grid view
    public void getAllGridRows() {
        Notifications notifications = new Notifications(gridDisplay.activity);
        this.rows = this.gridDisplay.weightDatabase.getAllUserWeights(email);


    }

    // Finds the authenticated user in the database
    public void findAuthenticatedUser() {
        this.gridDisplay.usersDb.findAuthenticatedUser();

        this.gridDisplay.weightDatabase.getUser();

        this.currentUser = new User(this.gridDisplay.usersDb.results.firstName,
                this.gridDisplay.usersDb.results.lastName,
                this.gridDisplay.usersDb.results.email,
                this.gridDisplay.usersDb.results.password,
                String.valueOf(this.gridDisplay.weightDatabase.results.currentWeight),
                this.gridDisplay.weightDatabase.results.goalWeight,
                this.gridDisplay.usersDb.results.phoneNumber);
        Log.v("Current User Current Email",currentUser.userCredentials.username);
        Log.v("Current User Current Weight",currentUser.weight.currentWeight);


    }

    // Sets all of the UI Values on the Main Page
    public void setUIValues() {
        Log.v("In Set UI Values","In ui values");
        Float current = Float.valueOf(this.currentUser.weight.currentWeight);
        Log.v("Current",String.valueOf(current));
        Log.v("goal",String.valueOf(currentUser.weight.goalWeight));
        Double goal = this.currentUser.weight.goalWeight;

        Notifications notifications = new Notifications(gridDisplay.activity);


        poundsLeft = current - goal;

        this.gridDisplay.welcomeText.setText("Hello, " + currentUser.firstName);
        this.gridDisplay.progressText.setText(poundsLeft+" left to go");

        if (poundsLeft <= 0) {
            Activity thisPage = this.gridDisplay;
            Boolean hasAccess = notifications.hasFilePermissions();
            if(hasAccess.equals(true)) {
                this.gridDisplay.createNotification();
            } else {

                App_Toast newToast = new App_Toast("You have reached your goal",
                        10, gridDisplay.getApplicationContext());
            }
        }

    }

    //Takes the user from the main page to edit and or delete a database entry
    public View.OnClickListener toEditAndDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            Integer rowNumber = Integer.valueOf(tag.substring(0, tag.length() - 1));
            String action = String.valueOf(tag.charAt(tag.length() - 1));
            Row selectedRow = rows[rowNumber];

            gridDisplay.preferences.putString("ID",String.valueOf(selectedRow.ID));
            gridDisplay.preferences.putString("Date",selectedRow.date);
            gridDisplay.preferences.putString("Action",action);
            gridDisplay.preferences.putString("Goal", String.valueOf(selectedRow.goalWeight));
            gridDisplay.preferences.putString("Weight", String.valueOf(selectedRow.weightThatDay));

            gridDisplay.startActivityFromChild(updates, gridDisplay.intent, 1);

        }
    };

    //Takes the user from the main page when they click the floating button to the new measurement sheet
    public View.OnClickListener toAddNewWeight = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(gridDisplay, Measurement_View.class);
            gridDisplay.startActivity(intent);


        }
    };
}
