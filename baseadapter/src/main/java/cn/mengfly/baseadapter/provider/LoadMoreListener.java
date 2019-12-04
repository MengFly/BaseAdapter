package cn.mengfly.baseadapter.provider;

import android.view.View;

import java.util.List;

import cn.mengfly.baseadapter.BaseViewHolder;

public interface LoadMoreListener<T> {

    List<T> loadMore(int page);

    void loadEndChange(BaseViewHolder holder);

    void initLoadView(BaseViewHolder holder);

    int getLoadMoreRes();
}
