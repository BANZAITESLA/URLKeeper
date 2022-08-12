package com.disu.urlkeeper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.disu.urlkeeper.R;
import com.disu.urlkeeper.data.OnboardingData;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 11/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */
public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>{

    private List<OnboardingData> onboardingDataList;

    public OnboardingAdapter(List<OnboardingData> onboardingDataList) {
        this.onboardingDataList = onboardingDataList;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.row_onboarding, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingDataList.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText, descText;
        private ImageView image;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.title_Text);
            descText = itemView.findViewById(R.id.desc_Text);
            image = itemView.findViewById(R.id.image_Onboard);
        }

        void setOnboardingData(OnboardingData onboardingData) {
            titleText.setText(onboardingData.getTitle());
            descText.setText(onboardingData.getDesc());
            image.setImageResource(onboardingData.getImage());
        }
    }
}
