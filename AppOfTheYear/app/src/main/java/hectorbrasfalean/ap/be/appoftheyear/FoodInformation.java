package hectorbrasfalean.ap.be.appoftheyear;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class FoodInformation extends AppCompatActivity {
    private TextView foodNameDisplay,dailyAmountDisplay,totalAmountDisplay,notificationDisplay;
    private WordDao mWordDao;
    private double totalAmount;
    private int dailyAmount;
    private int notificationAmount;
    private boolean dagelijksVerbruikActivated;
    private String foodName;
    private Food currentFood;
    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WordRoomDatabase db = WordRoomDatabase.getDatabase(getApplication());
        mWordDao = db.wordDao();
        setContentView(R.layout.activity_food_information);
        Intent i = getIntent();
        foodName = i.getStringExtra("foodName");
        foodNameDisplay = findViewById(R.id.textViewFood);
        dailyAmountDisplay = findViewById(R.id.editTextDagelijksVerbruik);
        totalAmountDisplay = findViewById(R.id.editTextTotalAmount);
        notificationDisplay = findViewById(R.id.editTextNotification);

        // Get data
        currentFood = mWordDao.getFoodByName(foodName);
        dailyAmount = currentFood.getDailyAmount();
        totalAmount = currentFood.getTotalAmount();
        notificationAmount = currentFood.getNotificationAmount();

        foodNameDisplay.setText(foodName);
        dailyAmountDisplay.setText(Integer.toString(dailyAmount));
        totalAmountDisplay.setText(Double.toString(totalAmount));
        notificationDisplay.setText(Integer.toString(notificationAmount));
        dagelijksVerbruikActivated = currentFood.getDailyDecrease();
        mSwitch = findViewById(R.id.switchDagelijksVerbruik);
        mSwitch.setChecked(dagelijksVerbruikActivated);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dagelijksVerbruikActivated = !dagelijksVerbruikActivated;
                currentFood.setDailyDecrease(dagelijksVerbruikActivated);
            }
        });
    }
    @Override
    protected void onDestroy() {
        int checkAll = 0;
        if(totalAmountDisplay.getText().toString().trim().length() > 0){
            currentFood.setTotalAmount(Double.parseDouble(totalAmountDisplay.getText().toString()));
            checkAll++;
        }
        if(dailyAmountDisplay.getText().toString().trim().length() > 0){
            currentFood.setDailyAmount(Integer.parseInt(dailyAmountDisplay.getText().toString()));
            checkAll++;
        }
        if(notificationDisplay.getText().toString().trim().length() > 0){
            currentFood.setNotificationAmount(Integer.parseInt(notificationDisplay.getText().toString()));
            checkAll++;

        }
        if(checkAll != 3){
            Toast.makeText(
                    getApplicationContext(),
                    "Waardes van lege velden werden niet opgeslagen.",
                    Toast.LENGTH_LONG).show();
        }
        else{
            mWordDao.updateFood(currentFood);
        }
        super.onDestroy();
    }

    public void DecreaseTotalAmount(View view) {

        if(totalAmount > 0){
            if(totalAmountDisplay.getText().toString().trim().length() > 0){
                totalAmount = Double.parseDouble(totalAmountDisplay.getText().toString());
                if(totalAmount < 1){
                    totalAmount = 1;
                }
                currentFood.setTotalAmount(--totalAmount);
                mWordDao.updateFood(currentFood);
                totalAmountDisplay.setText(Double.toString(Math.round(totalAmount * 100.0) / 100.0));
            }
            else{
                ErrorMessage(totalAmountDisplay);
            }
        }
    }

    public void IncreaseTotalAmount(View view) {
        if(totalAmountDisplay.getText().toString().trim().length() > 0) {
            totalAmount = Double.parseDouble(totalAmountDisplay.getText().toString());
            currentFood.setTotalAmount(++totalAmount);
            mWordDao.updateFood(currentFood);
            totalAmountDisplay.setText(Double.toString(Math.round(totalAmount * 100.0) / 100.0));
        }
        else{
            ErrorMessage(totalAmountDisplay);
        }
    }

    public void DecreaseDailyAmount(View view) {
        if(dailyAmount > 0){
            if(dailyAmountDisplay.getText().toString().trim().length() > 0){
                dailyAmount = Integer.parseInt(dailyAmountDisplay.getText().toString());
                if(dailyAmount < 50)
                    dailyAmount = 50;
                dailyAmount -= 50;
                currentFood.setTotalAmount(dailyAmount);
                mWordDao.updateFood(currentFood);
                dailyAmountDisplay.setText(Integer.toString(dailyAmount));
            }
            else{
                ErrorMessage(dailyAmountDisplay);
            }
        }
    }

    public void IncreaseDailyAmount(View view) {
        if(dailyAmountDisplay.getText().toString().trim().length() > 0){
            dailyAmount = Integer.parseInt(dailyAmountDisplay.getText().toString());
            dailyAmount += 50;
            currentFood.setTotalAmount(dailyAmount);
            mWordDao.updateFood(currentFood);
            dailyAmountDisplay.setText(Integer.toString(dailyAmount));
        }
        else{
            ErrorMessage(dailyAmountDisplay);
        }
    }

    public void DecreaseNotificationAmount(View view) {
        if(notificationAmount > 0){
            if(notificationDisplay.getText().toString().trim().length() > 0){
                notificationAmount = Integer.parseInt(notificationDisplay.getText().toString());
                currentFood.setTotalAmount(--notificationAmount);
                mWordDao.updateFood(currentFood);
                notificationDisplay.setText(Integer.toString(notificationAmount));
            }
            else{
                ErrorMessage(notificationDisplay);
            }
        }
    }

    public void IncreaseNotificationAmount(View view) {
        if(notificationDisplay.getText().toString().trim().length() > 0){
            notificationAmount = Integer.parseInt(notificationDisplay.getText().toString());
            currentFood.setTotalAmount(++notificationAmount);
            mWordDao.updateFood(currentFood);
            notificationDisplay.setText(Integer.toString(notificationAmount));
        }
        else{
            ErrorMessage(notificationDisplay);
        }
    }

    private void ErrorMessage(TextView currentTextView){
        currentTextView.setError("Het veld mag niet leeg zijn, vul een waarde in.");
    }

}
