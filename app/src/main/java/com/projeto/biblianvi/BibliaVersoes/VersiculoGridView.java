package com.projeto.biblianvi.BibliaVersoes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Ezequiel on 08/05/2017.
 */

public class VersiculoGridView extends BaseAdapter {
    private Context mContext;
    private int capMax = 0 ;
    private Button button;
    private Biblia biblia;


    public VersiculoGridView(Context c,int capMax, Biblia b) {
        mContext = c;
        this.capMax = capMax;
        biblia = b;

    }

    public int getCount() {
        return capMax;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } else {

            button = (Button) convertView;

        }


        button = new Button(mContext);
        button.setLayoutParams(new GridView.LayoutParams(80,80));
        button.setBackgroundColor(mContext.getResources().getColor(R.color.barrasuperior));
        button.setTextColor(mContext.getResources().getColor(R.color.dark));
       // button.setBackground(mContext.getResources().getDrawable(R.drawable.appthemebuttonazul_btn_default_holo_dark));


        button.setPadding(5,5,5,5);

        if(position < capMax)
            button.setText(Integer.toString(position + 1));


        Log.e("position",Integer.toString(position));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button b = (Button) v;
                Toast.makeText(mContext,b.getText(),Toast.LENGTH_LONG).show();

                biblia.setVerseNum(b.getText().toString());

                Intent intent = new Intent(mContext, Lista_Biblia.class);

                intent.putExtra("livro", biblia.getBooksName());
                intent.putExtra("capitulo", biblia.getChapter());
                intent.putExtra("versiculo", biblia.getVersesNum());
                intent.putExtra("termoBusca", "nada");

               // ActivityOptions options = ActivityOptions.makeCustomAnimation(mContext,v.getWidth(),v.getHeight());

             //   mContext.startActivity(intent,options.toBundle());

                mContext.startActivity(intent);

                Log.e("Biblia", biblia.getBooksName() + " " + biblia.getChapter() + " " + biblia.getVersesNum());
            }
        });

        return button;
    }
}
