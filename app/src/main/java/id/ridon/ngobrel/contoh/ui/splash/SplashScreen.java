package id.ridon.ngobrel.contoh.ui.splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qiscus.sdk.Qiscus;

import android.os.Handler;

import id.ridon.ngobrel.contoh.ui.homepagetab.HomePageTabActivity;
import id.ridon.ngobrel.contoh.ui.login.LoginActivity;

public class SplashScreen extends AppCompatActivity {
    private static int splashInterval = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!Qiscus.hasSetupUser()) {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
                else {
                    //startActivity(new Intent(SplashScreen.this, RecentConversationsActivity.class));
                    startActivity(new Intent(SplashScreen.this, HomePageTabActivity.class));
                    this.finish();
                }
                this.finish();
            }

            private void finish() {
                // TODO Auto-generated method stub

            }
        }, splashInterval);

    };

}
