package com.lh.imbilibili.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.view.adapter.feedbackfragment.FeedbackEpAdapter;

import java.util.List;

/**
 * Created by home on 2016/8/2.
 */
public class EpisodeFragment extends DialogFragment implements FeedbackEpAdapter.onEpClickListener {

    public static final String TAG = "EpisodeFragment";

    public static final String BANGUMI_DETAIL_DATA = "BangumiDetailData";
    RecyclerView recyclerView;

    private FeedbackEpAdapter adapter;
    private List<BangumiDetail.Episode> episodes;
    private AlertDialog dialog;

    private int selectPosition;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(dialog == null) {
            BangumiDetail bangumiDetail = getArguments().getParcelable("data");
            if(bangumiDetail!=null){
                episodes = bangumiDetail.getEpisodes();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("集数选择");
            recyclerView = new RecyclerView(getContext());
            int space = getContext().getResources().getDimensionPixelSize(R.dimen.item_spacing);
            builder.setView(recyclerView, space, space, space, space);
            adapter = new FeedbackEpAdapter(episodes);
            adapter.selectItem(getArguments().getInt("position", 0));
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
            adapter.setOnEpClickListener(this);
            dialog = builder.create();
        }
        return dialog;
    }

    public int getSelectPosition(){
        return selectPosition;
    }

    @Override
    public void onEpClick(int position) {
        selectPosition = position;
        FeedbackFragment fragment= (FeedbackFragment) getFragmentManager().findFragmentByTag(FeedbackFragment.TAG);
        if(fragment!=null){
            fragment.onEpisodeSelect(position);
        }
        dismiss();
    }
}
