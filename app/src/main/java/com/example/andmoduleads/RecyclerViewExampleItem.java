package com.example.andmoduleads;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RecyclerViewExampleItem extends FrameLayout {

    TextView tvText;

    public RecyclerViewExampleItem(Context context) {
        super(context);
        inflate(context, R.layout.item_contact, this);
        tvText = findViewById(R.id.contact_name);
    }

    public void bind(String str){
        tvText.setText(str);
    }
}
