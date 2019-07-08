package com.bry.firedonor.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bry.firedonor.Activities.MyDonationActivity;
import com.bry.firedonor.Activities.MyDonationsActivity;
import com.bry.firedonor.Constants;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.R;
import com.bry.firedonor.Variables;

import java.util.List;

public class MyDonationsAdapter extends RecyclerView.Adapter<MyDonationsAdapter.ViewHolder>{
    private List<DonationItem> selectedDonationItems;
    private Activity mActivity;
    private Context mContext;

    public MyDonationsAdapter(List<DonationItem> itemImages, Activity ac, Context mCont){
        this.selectedDonationItems = itemImages;
        this.mActivity = ac;
        this.mContext = mCont;
    }


    @NonNull
    @Override
    public MyDonationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View recipeView = inflater.inflate(R.layout.my_donation_item, viewGroup, false);
        return new MyDonationsAdapter.ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDonationsAdapter.ViewHolder viewHolder, int i) {
        final DonationItem item = selectedDonationItems.get(i);

        viewHolder.mainImage.setImageBitmap(item.getItemImages().get(0).getImageBitmap());
        viewHolder.usernameTextView.setText(String.format("%s %s", mContext.getResources().getString(R.string.by_user), item.getUploaderName()));
        viewHolder.dateDetails.setText(String.format("%s%s", mContext.getResources().getString(R.string.uploaded_on), item.getTimeOfCreation().getOfficialDate()));
        viewHolder.informalDateDetails.setText(item.getTimeOfCreation().getCasualDate());

        viewHolder.editCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.selectedDonationItem = item;
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.EDIT_DONATION_INTENT_FILTER));
            }
        });

        viewHolder.requestsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentt = new Intent(mActivity, MyDonationActivity.class);
                mActivity.startActivity(intentt);
            }
        });

        try {
            Bitmap image2 = item.getItemImages().get(1).getImageBitmap();
            Bitmap image3 = item.getItemImages().get(2).getImageBitmap();
            Bitmap image4 = item.getItemImages().get(3).getImageBitmap();

            if(image2!=null) viewHolder.image1.setImageBitmap(image2);
            if(image3!=null) viewHolder.image2.setImageBitmap(image3);
            if(image4!=null) viewHolder.image3.setImageBitmap(image4);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return selectedDonationItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mainImage;
        TextView usernameTextView;
        TextView informalDateDetails;
        TextView dateDetails;
        TextView requestsLink;
        CardView editCard;
        ImageView image1;
        ImageView image2;
        ImageView image3;

        ViewHolder(View itemView) {
            super(itemView);
            mainImage = itemView.findViewById(R.id.mainImage);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            informalDateDetails = itemView.findViewById(R.id.informalDateDetails);
            dateDetails = itemView.findViewById(R.id.dateDetails);
            image1 = itemView.findViewById(R.id.image1);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            requestsLink = itemView.findViewById(R.id.requestsLink);
            editCard = itemView.findViewById(R.id.editCard);
        }
    }
}
