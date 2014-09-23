package com.os.ui.ui2;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.os.activity.ShowBoardActivity;
import com.os.slidingmenu.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SecondFragment extends Fragment {

	private View viewFragment;
    private ArrayList<String> favList;
    private View view;
    private ListView listView;
    private ListViewAdapterWithFilter lvf;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewFragment=inflater.inflate(R.layout.second, null);

//        getActivity().getSupportFragmentManager().beginTransaction().add(this, "SecondFragment").commit();

        listView = (ListView) viewFragment.findViewById(R.id.allfav);

        favList = new ArrayList<String>();
        favList.add("test");
        favList.add("test2");
        lvf = new ListViewAdapterWithFilter(getActivity(), favList);

        listView.setAdapter(lvf);

        return viewFragment;
	}

    public interface UpdateFavList{
        public void updateFav(String favName);
    }

    public ArrayList<String> getFavList(){
        return favList;
    }

    class MyComparator implements Comparator {
        public MyComparator() {
            super();
        }

        public int compare(Object o1, Object o2) {
            String stringA = (String) o1;
            String stringB = (String) o2;
            return (stringA.toUpperCase()).compareTo(stringB.toUpperCase());
        }

    }

    private class ListViewAdapterWithFilter extends BaseAdapter {

        private Context context;
        private List<String> list = null;
        private ArrayList<String> arrayList;
        private LayoutInflater inflater;

        public ListViewAdapterWithFilter(Context context, List<String> _allItems) {
            this.context = context;
            this.list = _allItems;
            inflater = LayoutInflater.from(this.context);
            arrayList = new ArrayList<String>();
            arrayList.addAll(_allItems); // as a backup of the list items.
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            view = getActivity().getLayoutInflater().inflate(R.layout.favlistview, null);

            TextView tv = (TextView)view.findViewById(R.id.favBoardName);
            final String boardFullName = list.get(position);

            tv.setText(boardFullName);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String boardName = boardFullName.substring(0, boardFullName.indexOf("("));
                    Intent intent = new Intent(getActivity(), ShowBoardActivity.class);
                    intent.putExtra("boardName", boardName);
                    intent.putExtra("ChineseName", boardFullName.substring(boardFullName.indexOf("(") + 1, boardFullName.indexOf(")")));
                    startActivity(intent);
                }
            });
            return view;
        }

        public void filter(String charText) {
            String tmpCharText = charText.toLowerCase();
            String tmpS;
            list.clear();
            if (charText.length() == 0) {
                list.addAll(arrayList);
            } else {
                for (String s : arrayList) {
                    tmpS = s.toLowerCase();
                    if (tmpS.contains(charText)) {
                        list.add(s);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void notifyAdapter(){
        lvf.notifyDataSetChanged();
    }
}
