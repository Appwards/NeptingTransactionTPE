package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.nepting.allpos.controller.AllPosClient;
import com.nepting.common.client.callback.UICallback;
import com.nepting.common.client.callback.UIRequest;
import com.nepting.common.client.controller.NepClient;
import com.nepting.common.client.model.LoadBalancingAlgorithm;
import com.nepting.common.client.model.LoginRequest;
import com.nepting.common.client.model.LoginResponse;
import com.nepting.common.client.model.TerminalInformation;
import com.nepting.common.client.model.TransactionResponse;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements UICallback {
    Logger logger;
    public NepClient nepClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            nepClient = new AllPosClient(this, logger,
                    getApplicationContext(), false);

            String nepwebUrl = "https://qualif.nepting.com:443/nepweb/ws?wsdl";
            String[] nepWebUrlList = {nepwebUrl};
            LoginRequest request = new LoginRequest("72140355490811602"
                    , nepWebUrlList, LoadBalancingAlgorithm.FIRST_ALIVE, null);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            nepClient.login(request);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        

    }

    @Override
    public String postUIRequest(UIRequest uiRequest) {
        return null;
    }

    @Override
    public void loginEnded(LoginResponse loginResponse) {
        Log.i("tryhard",loginResponse.getGlobalStatus().toString());
    }

    @Override
    public void transactionEnded(TransactionResponse transactionResponse) {

    }

    @Override
    public void getTerminalInformationEnded(TerminalInformation terminalInformation) {

    }

    @Override
    public void fetchLocalTransactionListEnded(int i) {

    }
}