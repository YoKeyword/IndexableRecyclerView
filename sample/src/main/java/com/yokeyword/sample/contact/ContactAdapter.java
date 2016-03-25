package com.yokeyword.sample.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yokeyword.indexablelistview.IndexBarAdapter;
import com.yokeyword.sample.R;

/**
 * Created by YoKeyword on 16/3/24.
 */
public class ContactAdapter extends IndexBarAdapter<ContactEntity> {
    private Context mContext;

    public ContactAdapter(Context context) {
        mContext = context;
    }

    @Override
    protected TextView onCreateTitleViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tv_title_contact, parent, false);
        return (TextView) view.findViewById(R.id.tv_title);
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, ContactEntity cityEntity) {
        ContactViewHolder myHolder = (ContactViewHolder) holder;
        myHolder.tvName.setText(cityEntity.getName());
        myHolder.tvMobile.setText(cityEntity.getMobile());
    }

    class ContactViewHolder extends ViewHolder {
        private ImageView imgAvatar;
        private TextView tvMobile, tvName;

        public ContactViewHolder(View view) {
            super(view);
            imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
            tvMobile = (TextView) view.findViewById(R.id.tv_mobile);
            tvName = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}