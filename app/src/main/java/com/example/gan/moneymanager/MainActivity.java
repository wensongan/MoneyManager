package com.example.gan.moneymanager;

import android.app.Activity;
import android.os.Bundle;
// Import our Widget classes
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
// Import java utilites
import java.text.NumberFormat;
import java.util.ArrayList;
// Import Event handling classes for Button and EditText widgets
import android.view.View.OnClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//Import Array list for listview
import android.widget.ListView;
import android.widget.ArrayAdapter;





public class MainActivity extends Activity {
    //Declare reference variables for our widgets
    private EditText billAmountEditText;
    private EditText paymentPurposeEditText;
    private ListView recentTransactions;
    private ArrayList<String> recentTransactionsList;
    private ArrayAdapter<String> adapter;
    private Button cashButton;
    private Button debitButton;
    private Button creditButton;
    private Button resetButton;
    private Button undoButton;
    private TextView totalCashAmountTextView;
    private TextView totalCreditAmountTextView;
    private TextView totalDebitAmountTextView;
    private double tempValue =0.00;
    private int tempValueType;
    // Declare reference for a SharedPreferences object
    private SharedPreferences savedPrefs;


    // Our event handlers will be sharing these
    private String billAmountString = "0.00";
    private String cashAmountString ="0.00";
    private String debitAmountString ="0.00";
    private String creditAmountString ="0.00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize widgets reference variables
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        paymentPurposeEditText = (EditText) findViewById(R.id.PaymentPurposeEditText);
        recentTransactions = (ListView) findViewById(R.id.purpose_listview);

        recentTransactionsList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,recentTransactionsList);
        recentTransactions.setAdapter(adapter);

        cashButton = (Button) findViewById(R.id.cashButton);
        debitButton = (Button) findViewById(R.id.debitButton);
        creditButton = (Button) findViewById(R.id.creditButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        undoButton = (Button) findViewById(R.id.undoButton);
        totalCashAmountTextView = (TextView) findViewById(R.id.totalCashAmountTextView);
        totalCreditAmountTextView = (TextView) findViewById(R.id.totalCreditAmountTextView);
        totalDebitAmountTextView = (TextView) findViewById(R.id.totalDebitAmountTextView);
        calculateAndDisplay();
        // Set the event listeners for our interactive widgets
        billAmountEditText.setOnEditorActionListener( new BillAmountListener() );
        OnClickListener buttonEventListener = new ButtonListener();
        cashButton.setOnClickListener( buttonEventListener );
        creditButton.setOnClickListener( buttonEventListener );
        debitButton.setOnClickListener( buttonEventListener );
        resetButton.setOnClickListener( buttonEventListener );
        undoButton.setOnClickListener( buttonEventListener );
        // get SharedPreferences object
        savedPrefs = getSharedPreferences( "moneyManagerPrefs", MODE_PRIVATE );


    }
    private void calculateAndDisplay() {
        // We will use locale-specific formatting for currency and percentage
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        billAmountString = billAmountEditText.getText().toString();


        totalCashAmountTextView.setText(cashAmountString);
        totalCreditAmountTextView.setText(creditAmountString);
        totalDebitAmountTextView.setText(debitAmountString);
    }
    class ButtonListener implements OnClickListener {
        @Override
        public void onClick( View v ) {
            billAmountString = billAmountEditText.getText().toString();

            if (v.getId() == R.id.cashButton) {
                cashAmountString = new Double(Double.parseDouble(billAmountString) + Double.parseDouble(cashAmountString)).toString();
                tempValue = Double.parseDouble(billAmountString);
                tempValueType = 0;
                updateListView(tempValueType);

            } else if (v.getId() == R.id.creditButton) {
                creditAmountString = new Double(Double.parseDouble(billAmountString) + Double.parseDouble(creditAmountString)).toString();
                tempValue = Double.parseDouble(billAmountString);
                tempValueType = 1;
                updateListView(tempValueType);

            } else if (v.getId() == R.id.debitButton) {
                debitAmountString = new Double(Double.parseDouble(billAmountString) + Double.parseDouble(debitAmountString)).toString();
                tempValue = Double.parseDouble(billAmountString);
                tempValueType = 2;
                updateListView(tempValueType);
            }
            else if (v.getId() == R.id.resetButton) {
                cashAmountString = "0.00";
                debitAmountString = "0.00";
                creditAmountString = "0.00";
                recentTransactionsList.clear();
                adapter.notifyDataSetChanged();

            }
            else if (v.getId() ==R.id.undoButton) {
                if (tempValueType ==0) {
                    cashAmountString = new Double ((Double.parseDouble(cashAmountString))- tempValue).toString();
                    tempValue = 0.00;


                }
                else if (tempValueType ==1) {
                    creditAmountString = new Double (Double.parseDouble(creditAmountString)- tempValue).toString();
                    tempValue = 0.00;

                }
                else if (tempValueType ==2) {
                    debitAmountString = new Double (Double.parseDouble(debitAmountString)- tempValue).toString();
                    tempValue = 0.00;

                }
                if (tempValue != 0.00) {
                    recentTransactionsList.remove(0);
                    adapter.notifyDataSetChanged();
                }
            }

            calculateAndDisplay();
        }
    }

    class BillAmountListener implements OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ( actionId == EditorInfo.IME_ACTION_DONE ) {
                calculateAndDisplay();
            }
            return false;
        }
    }

    @Override
    public void onPause() {
        // Save the billAmountString and tipPercentage instance variables
        Editor prefsEditor = savedPrefs.edit();
        prefsEditor.putString( "billAmountString", billAmountString );
        prefsEditor.commit();

        // Calling the parent onPause() must be done LAST
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load the instance variables back (or default values)
        billAmountString = savedPrefs.getString("billAmountString", "");

    }

    public void updateListView(int type) {
        String paymentType = "Cash";
        if (type ==1) {
            paymentType = "Credit";
        }
        else if (type ==2){
            paymentType = "Debit";
        }
        String result = paymentType+": $"+billAmountEditText.getText().toString() +" - "+ paymentPurposeEditText.getText().toString();
        recentTransactionsList.add(result);
        adapter.notifyDataSetChanged();
    }




}
