package com.insat.ghazi.iac.ViewWithJava;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insat.ghazi.iac.R;
import com.rockerhieu.emojicon.EmojiconTextView;


/**
 * Created by ozil_ on 23/02/2017.
 */

public class MessageEnvoyerView extends RelativeLayout {


    private Context context = null;

    private EmojiconTextView message = null;

        private TextView time = null;


        private TextView vu = null;

    private boolean isVu = false;



        public MessageEnvoyerView(Context context,String msg, String dateComplete,boolean afficheComplet,boolean isVu) {
            super(context);

            this.context = context;
            this.isVu = isVu;



        String timeOnly = dateComplete.split("à")[1].trim();


            timeOnly = reglerTime(timeOnly);


        message = new EmojiconTextView(context); message.setText(msg); message.setLinksClickable(true); message.setLinkTextColor(Color.BLUE); message.setAutoLinkMask(Linkify.WEB_URLS); message.setMovementMethod(LinkMovementMethod.getInstance()); //les liens
            final int tailleE = 22;
            final float scaleE = getContext().getResources().getDisplayMetrics().density;
            int pixelsE = (int) (tailleE * scaleE + 0.5f);
        message.setEmojiconSize(pixelsE); message.setOnClickListener(new ClickMessageListener());


            time = new TextView(context); time.setText(timeOnly);
            vu = new TextView(context); vu.setText("Vu");  vu.setVisibility(GONE);


        //color
        message.setTextColor(Color.WHITE);  message.setBackgroundResource(R.drawable.message_envoyer_back);


        //size
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            time.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);



        LinearLayout allViewLayout = new LinearLayout(context);
        allViewLayout.setOrientation(LinearLayout.HORIZONTAL);



        //time params
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        timeParams.setMargins(8,10,8,0);
        time.setLayoutParams(timeParams);



            if(afficheComplet)
        allViewLayout.addView(new View(context),20,20);



        //msg + date layout
        LinearLayout msgDateLayout = new LinearLayout(context);
        msgDateLayout.setOrientation(LinearLayout.VERTICAL);






        RelativeLayout msgRelLayout = new RelativeLayout(context);


        //msg  params
        RelativeLayout.LayoutParams msgDateParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        msgDateParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        msgDateParams.setMargins(0,10,10,0);


            LinearLayout msgLl = new LinearLayout(context);msgLl.setOrientation(LinearLayout.VERTICAL);
            msgLl.addView(time);
            msgLl.addView(message);
            //msgLl.addView(vu);


        msgRelLayout.addView(msgLl,msgDateParams);





        msgDateLayout.addView(msgRelLayout);








        allViewLayout.addView(msgDateLayout,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);


        this.addView(allViewLayout,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);







    }





    //methode utiles

    public void showVu(){

        if (isVu) { //////////////////////////////////////////////// a modifié
            if(isInternetAvaible())
            vu.setVisibility(GONE);
        }

    }

    private String correctNumber(int s){

        if(s < 10)
            return "0"+s;
        else
            return ""+s;
    }
    private String reglerTime(String time){

        int hour = Integer.parseInt(time.split(":")[0]);  String hourString = correctNumber(hour);
        int minute = Integer.parseInt(time.split(":")[1]);    String minuteString = correctNumber(minute);



        return hourString+":"+minuteString;


    }


    private boolean isInternetAvaible(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;

    }


    //listeners
    class ClickMessageListener implements OnClickListener{
        @Override
        public void onClick(View view) {

            if(vu.getVisibility() == GONE) {

                if (isVu) {
                    if(isInternetAvaible())
                    vu.setVisibility(VISIBLE);
                }


            } else {
                vu.setVisibility(GONE);
                Log.e("hhh","gone");
            }
        }
    }

}
