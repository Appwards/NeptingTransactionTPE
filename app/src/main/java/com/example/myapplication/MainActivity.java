package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.nepting.allpos.controller.AllPosClient;
import com.nepting.common.client.callback.UICallback;
import com.nepting.common.client.callback.UIRequest;
import com.nepting.common.client.controller.NepClient;
import com.nepting.common.client.model.Currency;
import com.nepting.common.client.model.LoadBalancingAlgorithm;
import com.nepting.common.client.model.LoginRequest;
import com.nepting.common.client.model.LoginResponse;
import com.nepting.common.client.model.TerminalInformation;
import com.nepting.common.client.model.TransactionRequest;
import com.nepting.common.client.model.TransactionResponse;
import com.nepting.common.client.model.TransactionType;

import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements UICallback {
    Logger logger;
    public NepClient nepClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            login();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login() {
        nepClient = new AllPosClient(this, logger, getApplicationContext(), false);
        String nepwebUrl = "https://qualif.nepting.com:443/nepweb/ws?wsdl";
        String[] nepWebUrlList = {nepwebUrl};
        LoginRequest request = new LoginRequest("72140355490811602", nepWebUrlList, LoadBalancingAlgorithm.FIRST_ALIVE, null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        nepClient.login(request);
    }

    @Override
    public String postUIRequest(UIRequest uiRequest) {
        return null;
    }

    @Override
    public void loginEnded(LoginResponse loginResponse) {
        loginResponse.getGlobalStatus();
        List<Integer> ex = loginResponse.getExtendedResultList();
        for (int i = 0; i < ex.size(); i++) {
            // A new login is necessary to update the device
            // send another login request after 1 minute nop
            if (ex.get(i) == 9) {
                // wait 1 min
                login();
            }
        }
        Log.i("tryhard",loginResponse.getGlobalStatus().toString());

        // Success
        if (loginResponse.getGlobalStatus().toString().equalsIgnoreCase("2")) {
            // 2 nombre de décimale
            // 978 code de la monnaie
            Currency defaultCurrency = new Currency("EUR", 2, 978);
            TransactionRequest transactionRequest = new
                    TransactionRequest(TransactionType.DEBIT, 500, defaultCurrency, false,
                    Long.toString(System.currentTimeMillis()));

            transactionRequest.setPrivateData("DATA123");

            nepClient.startTransaction(transactionRequest);
        } else {
            // Display error message
        }
    }

    @Override
    public void transactionEnded(TransactionResponse transactionResponse) {
        if (transactionResponse != null) {
            // Serialiser la réponse et la logger

            if (transactionResponse.getGlobalStatus().equals("2")) {
                // Display transaction status
                // Display ticket and enable buttons

                // Imprimer tel quel
                // Imprimer le ticket de paiement immédiatement si demandé par le client
                String ticket = transactionResponse.getCustomerTicket();

                // Vérifier si le montant est le même. Si non refaire une requête avec la différence
                long amount = transactionResponse.getAmount();

                // Vérifie si la transaction est en mode dégradée
                boolean isLocal = transactionResponse.isLocalMode();

                // Si contient 9 refaire un login
                List<Integer> ex = transactionResponse.getExtendedResultList();
                for (int i = 0; i < ex.size(); i++) {
                    // A new login is necessary to update the device
                    // send another login request after 1 minute nop
                    if (ex.get(i) == 9) {
                        // wait 1 min
                        login();
                    }
                }

                // Retreive the private data sent in the request
                String data = transactionResponse.getPrivateData();

                // Vérifier que la carte utilisée est une test ou non
                boolean isTest = transactionResponse.isTestCard();

                String authNo = transactionResponse.getAuthorizationNumber();
                String authCode = transactionResponse.getAuthorizationCode();

                // Nombre de transactions degradées dans le terminal
                int c = transactionResponse.getLocalTransactionsCount();
                if (c > 0) {

                }
            } else {
                // Display transaction status
                // Display ticket
            }

            // close
            if (nepClient != null) {
                nepClient.fetchLocalTransactionList();

                // Tuer le client
                nepClient.destroy();
            }
        }
    }

    @Override
    public void getTerminalInformationEnded(TerminalInformation terminalInformation) {

    }

    @Override
    public void fetchLocalTransactionListEnded(int i) {

    }
}