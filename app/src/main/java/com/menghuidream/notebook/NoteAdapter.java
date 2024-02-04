package com.menghuidream.notebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<NoteBean> backList;//备份原始数据
    private List<NoteBean> noteList;//更新后的数据
    private MyFilter filter;

    public NoteAdapter(Context context, List<NoteBean> noteList) {
        this.context = context;
        this.noteList = noteList;
        this.backList = noteList;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //context.setTheme(sp.getBoolean("nightMode", false) ? R.style.NightTheme : R.style.DayTheme);
        View view = View.inflate(context, R.layout.item_note, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvTime = view.findViewById(R.id.tv_time);

        tvTitle.setText(noteList.get(position).getTitle());
        tvTime.setText(noteList.get(position).getTime());

        view.setTag(noteList.get(position).getId());
        return view;
    }

    @Override
    public MyFilter getFilter() {
        if (filter == null) {
            filter = new MyFilter();
        }
        return filter;
    }

    class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<NoteBean> list;
            if (constraint == null || constraint.length() == 0) {
                list = backList;
            } else {
                list = new ArrayList<>();
                for (NoteBean noteBean : backList) {
                    if (noteBean.getTitle().contains(constraint) || noteBean.getContent().contains(constraint)) {
                        list.add(noteBean);
                    }
                }
            }
            results.values = list;
            results.count = list.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            noteList = (List<NoteBean>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
