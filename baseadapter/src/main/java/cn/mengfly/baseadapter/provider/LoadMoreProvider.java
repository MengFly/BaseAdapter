package cn.mengfly.baseadapter.provider;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.mengfly.baseadapter.BaseViewHolder;
import cn.mengfly.baseadapter.Callback;
import cn.mengfly.baseadapter.R;

public class LoadMoreProvider<T> {
    private static final String TAG = "LoadMoreProvider";
    private static final int VIEW_TYPE_FOOTER = 2;
    private int curPage = 0;
    private LoadMoreListener<T> loadMoreListener;
    private boolean isLoadMoreEnd = false;
    private BaseViewHolder loadMoreVH;
    private volatile boolean isWaitingLoadMore = false;

    public void loadMoreSync(Callback<List<T>> syncCallBack) {
        if (!isWaitingLoadMore) {
            new Thread(() -> {
                isWaitingLoadMore = true;
                Log.i(TAG, "loadMore: start");
                List<T> ts = loadMoreListener.loadMore(curPage);
                if (ts != null && !ts.isEmpty() && !isLoadMoreEnd) {
                    curPage++;
                } else {
                    isLoadMoreEnd = true;
                }
                isWaitingLoadMore = false;
                syncCallBack.callback(ts);
            }).start();

        }
    }


    public RecyclerView.ViewHolder initLoadMore(ViewGroup parent, Context context) {
        if (loadMoreVH == null) {
            loadMoreVH = new LoadMoreViewHolder(context, loadMoreListener.getLoadMoreRes(), parent);
        }
        resetLoad();
        return loadMoreVH;
    }

    public void resetLoad() {
        curPage = 0;
        isLoadMoreEnd = false;
        loadMoreListener.initLoadView(loadMoreVH);
    }

    public void setLoadMoreListener(LoadMoreListener<T> loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        isLoadMoreEnd = false;
    }

    public boolean isLoadMoreEnable() {
        return loadMoreListener != null;
    }

    public boolean isLoadMoreEnd() {
        return isLoadMoreEnd;
    }

    public int type() {
        return VIEW_TYPE_FOOTER;
    }

    public void bindData(RecyclerView.ViewHolder holder) {
        if (holder instanceof LoadMoreViewHolder) {
            if (isLoadMoreEnable() && isLoadMoreEnd) {
                loadMoreListener.loadEndChange(loadMoreVH);
            } else if (isLoadMoreEnd) {
                loadMoreListener.initLoadView(loadMoreVH);
            }
        }
    }

    public void stopLoadMore() {
        isLoadMoreEnd = true;
    }


    public static abstract class DefaultLoadMoreListener<T> implements LoadMoreListener<T> {

        @Override
        public void loadEndChange(BaseViewHolder holder) {
            ((TextView) holder.getView(R.id.tv_status_foot)).setText(R.string.ending);
            holder.getView(R.id.pb_foot).setVisibility(View.GONE);
        }

        @Override
        public void initLoadView(BaseViewHolder holder) {
            ((TextView) holder.getView(R.id.tv_status_foot)).setText(R.string.loading);
            holder.getView(R.id.pb_foot).setVisibility(View.VISIBLE);
        }

        @Override
        public int getLoadMoreRes() {
            return R.layout._item_footer;
        }
    }

    static class LoadMoreViewHolder extends BaseViewHolder {

        LoadMoreViewHolder(Context context, int layoutRes, ViewGroup parent) {
            super(context, layoutRes, parent);
        }
    }

    private class LoadMoreThread implements Runnable {
        @Override
        public void run() {
        }
    }


}
