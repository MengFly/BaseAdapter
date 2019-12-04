package cn.mengfly.baseadaptersimple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.mengfly.baseadapter.BaseAdapter;
import cn.mengfly.baseadapter.provider.LoadMoreProvider;
import cn.mengfly.baseadaptersimple.adapter.ItemAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    private void initAdapter() {
        adapter = new ItemAdapter(this, R.layout.item_layout);
        recyclerView.setAdapter(adapter);
        // set itemClickListener
        adapter.setOnItemClickListener((itemView, position) ->
                Toast.makeText(MainActivity.this,
                        "onItemClick " + position + adapter.getItem(position), Toast.LENGTH_SHORT).show());

        // set itemLongClickListener
        adapter.setOnItemLongClickListener((itemView, position) -> {
            Toast.makeText(MainActivity.this,
                    "onItemLongClick " + position + adapter.getItem(position), Toast.LENGTH_SHORT).show();
            return true;
        });

        // enable loadMore logic
        adapter.setLoadMoreProvider(new LoadMoreProvider.DefaultLoadMoreListener<String>() {
            @Override
            public List<String> loadMore(int page) {
//                long start = System.currentTimeMillis();
//                //Simulate time-consuming operations
//                while (true) {
//                    if (System.currentTimeMillis() - start > 10 * 1000) {
//                        break;
//                    }
//                }
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    list.add(String.valueOf(page * 10 + i));
                }
                if (Integer.parseInt(list.get(0)) > 100) {
                    adapter.stopLoadMore();
                }
                return list;
            }
        });
        // add emptyView
        adapter.setEmptyView(R.layout.item_empty);
    }
}
