package app.kathenas.launcher.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import app.kathenas.launcher.AppObject;
import app.kathenas.launcher.R;

public class FavTabsAdaptor extends RecyclerView.Adapter<FavTabsAdaptor.MyViewHolder> {

    public interface OnItemClickListener {
        void onViewClick(AppObject appObject);
    }
    private  final Context context;
    private final OnItemClickListener onItemClickListener;
    private final ArrayList<AppObject> data ;

    public FavTabsAdaptor(Context context,ArrayList<AppObject> data, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        final View itemView;
        final ImageView appIcon;
        final TextView appName;
        OnItemClickListener mListener;

        MyViewHolder(View v, OnItemClickListener listener) {
            super(v);
            itemView = v.findViewById(R.id.appInfo);
            appIcon = v.findViewById(R.id.appImage);
            appName = v.findViewById(R.id.appName);
            mListener=listener;

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_item));
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.onViewClick(data.get(getAdapterPosition()));
                }
            }, 200);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_item, parent, false);

        return new MyViewHolder(itemView,onItemClickListener);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (data != null) {

            AppObject app = data.get(position);

            holder.appName.setText(app.getAppName());
            holder.appIcon.setImageDrawable(app.getAppImage());

        }
    }




    @Override
    public int getItemCount() {
        return data.size();
    }
}