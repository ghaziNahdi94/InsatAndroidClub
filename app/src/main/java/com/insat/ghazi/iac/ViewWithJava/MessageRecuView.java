package com.insat.ghazi.iac.ViewWithJava;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.insat.ghazi.iac.R;
import com.rockerhieu.emojicon.EmojiconTextView;


/**
 * Created by ozil_ on 19/02/2017.
 */

public class MessageRecuView extends RelativeLayout {



    private de.hdodenhof.circleimageview.CircleImageView image = null;
    private EmojiconTextView message = null;
    private TextView time = null;






    public MessageRecuView(Context context, Bitmap img, String msg, String dateComplete,boolean afficheComplet) {
        super(context);









            String timeOnly = dateComplete.split("à")[1].trim();


        timeOnly = reglerTime(timeOnly);


        image = new de.hdodenhof.circleimageview.CircleImageView(context); image.setImageBitmap(img);
        message = new EmojiconTextView(context); message.setText(msg); message.setLinksClickable(true); message.setLinkTextColor(Color.BLUE); message.setAutoLinkMask(Linkify.WEB_URLS); message.setMovementMethod(LinkMovementMethod.getInstance()); //les liens
        final int tailleE = 22;
        final float scaleE = getContext().getResources().getDisplayMetrics().density;
        int pixelsE = (int) (tailleE * scaleE + 0.5f);
        message.setEmojiconSize(pixelsE);

        time = new TextView(context); time.setText(timeOnly);



        //color
        message.setTextColor(Color.WHITE);  message.setBackgroundResource(R.drawable.message_recu_back);


        //size
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        time.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);





        //image paramétre
        final int taille = 50;
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (taille * scale + 0.5f);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(pixels,pixels);
        image.setLayoutParams(imageParams);
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) image.getLayoutParams();
        marginLayoutParams.leftMargin = 5;
        marginLayoutParams.topMargin = 10;
        image.setLayoutParams(marginLayoutParams);





        //msg + date layout
        LinearLayout msgDateLayout = new LinearLayout(context);
        msgDateLayout.setOrientation(LinearLayout.VERTICAL);






        //msg layout
        LinearLayout msgLayout = new LinearLayout(context);
        msgLayout.setOrientation(LinearLayout.HORIZONTAL);
        View view = new View(context); view.setBackgroundResource(R.drawable.tchat_dialog_gauche);
        msgLayout.addView(view,20,10);

        LinearLayout linearLayout = new LinearLayout(context); linearLayout.setOrientation(LinearLayout.VERTICAL);
        msgLayout.addView(message);

        //message paramétre
        LinearLayout.LayoutParams msgParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        msgParam.setMargins(0,10,0,0);
        msgLayout.setLayoutParams(msgParam);


        if(afficheComplet)
            msgDateLayout.addView(time);
        msgDateLayout.addView(msgLayout);


        //message + date paramétre
        LinearLayout.LayoutParams msgDateParam = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
        msgDateParam.weight = 8;
        msgDateParam.setMargins(20,0,0,0);
        msgDateLayout.setLayoutParams(msgDateParam);



        //time paramétre
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        timeParams.setMargins(8,10,8,0);
        time.setLayoutParams(timeParams);




        LinearLayout photoTexteLayout = new LinearLayout(context);
        photoTexteLayout.setOrientation(LinearLayout.HORIZONTAL);





        photoTexteLayout.addView(image);

        photoTexteLayout.addView(msgDateLayout);




















        this.addView(photoTexteLayout,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    }




//methode utiles
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


}
