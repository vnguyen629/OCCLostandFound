package edu.orangecoastcollege.cs273.vnguyen629.occlostandfound;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

/**
 * Allows the user to create an account. If the user enters specific admin codes,
 * then the user's account becomes an admin account.
 *
 * @author Vu Nguyen
 */
public class UserAccountCreateActivity extends AppCompatActivity {

    private EditText userEditText;
    private EditText passwordEditText;
    private EditText reenterPasswordEditText;
    private EditText emailEditText;
    private EditText idEditText;

    private DBHelper database;
    private List<UserAccount> accounts;

    private boolean nameIsNotSame = true;

    /**
     * Loads up the databases and links up the view widgets.
     * @param savedInstanceState The state of the application saved into a bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_create);

        userEditText = (EditText) findViewById(R.id.userEditText);
        userEditText.addTextChangedListener(userNameTextWatcher);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        reenterPasswordEditText = (EditText) findViewById(R.id.reenterPasswordEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        idEditText = (EditText) findViewById(R.id.idEditText);

        database = new DBHelper(this);
        accounts = database.getAllUserAccount();
    }

    /**
     * Cancels the creation of an account and exits the activity
     * @param view The cancel button
     */
    public void cancel(View view) {
        this.finish();
    }

    /**
     * Creates an account with the user input/data
     * @param view The create account button
     */
    public void createAccount(View view) {
        String username = userEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String rePassword = reenterPasswordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String studentID = idEditText.getText().toString().trim();
        String tempName = userEditText.getText().toString().replace("admin", "");

        if (username.isEmpty() || password.isEmpty() || rePassword.isEmpty() || email.isEmpty()
                || studentID.isEmpty())
            Toast.makeText(this, getString(R.string.all_fields_filled_out_text),
                    Toast.LENGTH_SHORT).show();
        else
        {
            for (UserAccount account : accounts)
                if (account.getStudentUserName().equals(username)) {
                    nameIsNotSame = false;
                    break;
                }
            if (nameIsNotSame == true) {
                if (password.equals(rePassword))
                    if (email.endsWith(getString(R.string.student_cccd_edu))) {

                        UserAccount account = new UserAccount(username, password, email, email, studentID);
                        if(username.startsWith("admin") && username.endsWith("admin"))
                        {
                            account.setmIsAdmin(true);
                            account.setStudentUserName(username.replace("admin", ""));
                        }
                        database.addAccount(account);
                        Toast.makeText(this, getString(R.string.account_created_text),
                                Toast.LENGTH_SHORT).show();
                        this.finish();
                    } else {
                        Toast.makeText(this, getString(R.string.must_be_cccd_email),
                                Toast.LENGTH_SHORT).show();
                    }
                else {
                    Toast.makeText(UserAccountCreateActivity.this, getString(R.string.passwords_do_not_match),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Monitors the username input EditText field
     */
    public TextWatcher userNameTextWatcher = new TextWatcher() {

        /**
         * Unused
         * @param s Unused
         * @param start Unused
         * @param count Unused
         * @param after Unused
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        /**
         * Unused
         * @param s Unused
         * @param start Unused
         * @param before Unused
         * @param count Unused
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            /*String input = s.toString().toLowerCase();
            for(UserAccount account : accounts)
            {
                if(account.getStudentUserName().toLowerCase().startsWith(input))
                {
                    nameIsNotSame = false;
                    userEditText.setTextColor(getResources().getColorStateList(R.color.red));
                }
                else
                {
                    nameIsNotSame = true;
                    userEditText.setTextColor(getResources().getColorStateList(R.color.green));
                }
            }*/
        }

        /**
         * Monitors and waits for the user to finish typing. After they finish typing,
         * it checks to see if the username they typed in has already been taking.
         * @param s Unused
         */
        @Override
        public void afterTextChanged(Editable s) {
            for (UserAccount account : accounts) {
                if (account.getStudentUserName().toLowerCase().equals(s.toString())) {
                    nameIsNotSame = false;
                    Toast.makeText(UserAccountCreateActivity.this, "Username already taken!", Toast.LENGTH_SHORT).show();
                    userEditText.setTextColor(getResources().getColorStateList(R.color.red));
                } else {
                    nameIsNotSame = true;
                    userEditText.setTextColor(getResources().getColorStateList(R.color.green));
                }
            }
        }};
}