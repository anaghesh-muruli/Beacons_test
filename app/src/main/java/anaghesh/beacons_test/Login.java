package anaghesh.beacons_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
private Button continue_btn;
private EditText mobile_num,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUI();


        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    startActivity(new Intent(Login.this,Navigation_home.class));
                }
            }
        });
      
    }
    void setupUI(){
        continue_btn = findViewById(R.id.btn_continue);
        mobile_num = findViewById(R.id.et_mobile);
        pass = findViewById(R.id.et_password);
    }
    Boolean validate(){
        if((mobile_num.getText().toString().isEmpty())||pass.getText().toString().isEmpty()){
            Toast.makeText(this, "Mobile number or password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }


}
