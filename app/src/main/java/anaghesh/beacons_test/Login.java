package anaghesh.beacons_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

      //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

     //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_login);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    startActivity(new Intent(this,Home.class));
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
