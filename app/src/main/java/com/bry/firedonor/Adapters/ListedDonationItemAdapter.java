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

import com.bry.firedonor.Constants;
import com.bry.firedonor.Models.DonationImage;
import com.bry.firedonor.Models.DonationItem;
import com.bry.firedonor.R;
import com.bry.firedonor.Variables;

import java.util.List;

public class ListedDonationItemAdapter extends RecyclerView.Adapter<ListedDonationItemAdapter.ViewHolder>{
    private List<DonationItem> selectedDonationItems;
    private Activity mActivity;
    private Context mContext;
    OnBottomReachedListener onBottomReachedListener;

    public ListedDonationItemAdapter(List<DonationItem> itemImages, Activity ac, Context mCont){
        this.selectedDonationItems = itemImages;
        this.mActivity = ac;
        this.mContext = mCont;
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void addDonationItemToList(DonationItem item){
        selectedDonationItems.add(item);
    }

    @NonNull
    @Override
    public ListedDonationItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View recipeView = inflater.inflate(R.layout.listed_donation_item, viewGroup, false);
        return new ListedDonationItemAdapter.ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListedDonationItemAdapter.ViewHolder viewHolder, int i) {
        final DonationItem item = selectedDonationItems.get(i);

        viewHolder.mainImage.setImageBitmap(item.getItemImages().get(0).getImageBitmap());
        viewHolder.usernameTextView.setText(String.format("%s %s", mContext.getResources().getString(R.string.by_user), item.getUploaderName()));
        viewHolder.detailsTextView.setText(item.getItemDetails());
        viewHolder.dateDetails.setText(String.format("%s%s", mContext.getResources().getString(R.string.uploaded_on), item.getTimeOfCreation().getOfficialDate()));
        viewHolder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.selectedDonationItem = item;
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.REQUEST_DONATION_INTENT_FILTER));
            }
        });

        viewHolder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.selectedDonationItem = item;
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.VIEW_DONATION_INTENT_FILTER));
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

        if (i == selectedDonationItems.size() - 1){
            onBottomReachedListener.onBottomReached(i);
        }

    }

    @Override
    public int getItemCount() {
        return selectedDonationItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mainImage;
        TextView usernameTextView;
        TextView detailsTextView;
        RecyclerView otherImagesRecyclerView;
        TextView dateDetails;
        CardView requestButton;
        RelativeLayout viewButton;
        ImageView image1;
        ImageView image2;
        ImageView image3;

        ViewHolder(View itemView) {
            super(itemView);
            mainImage = itemView.findViewById(R.id.mainImage);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            otherImagesRecyclerView = itemView.findViewById(R.id.otherImagesRecyclerView);
            dateDetails = itemView.findViewById(R.id.dateDetails);
            requestButton = itemView.findViewById(R.id.requestButton);
            viewButton = itemView.findViewById(R.id.viewButton);
            image1 = itemView.findViewById(R.id.image1);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
        }
    }

    public interface OnBottomReachedListener {
        void onBottomReached(int position);
    }
}
