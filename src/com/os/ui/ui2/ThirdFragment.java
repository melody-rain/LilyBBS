package com.os.ui.ui2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import com.os.activity.ShowBoardActivity;
import com.os.slidingmenu.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.os.utility.DatabaseDealer;

import java.util.*;

public class ThirdFragment extends Fragment implements SecondFragment.UpdateFavList{

    private View view;
    private List<String> allItems;
    private ArrayList<String> myArrayList;
    private Thread thread;
    private ListView listView;
    private EditText searchBoard;
    private ArrayAdapter arrayAdapter;
    private ListViewAdapterWithFilter lvf;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Collections.sort(allItems, new MyComparator());
//                arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, allItems);
                myArrayList.addAll(allItems);
                lvf = new ListViewAdapterWithFilter(getActivity(), myArrayList);
//                listView.setAdapter(arrayAdapter);
                listView.setAdapter(lvf);
            } else {
                Toast.makeText(getActivity(), "无法获得所有板块列表", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myArrayList = new ArrayList<String>();

        view = inflater.inflate(R.layout.third, null);
        listView = (ListView) view.findViewById(R.id.allBoards);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                allItems = DatabaseDealer.getAllBoardList(getActivity());
                Message msg = new Message();
                if (allItems != null) {
                    msg.what = 0;
                } else {
                    msg.what = -1;
                }

                handler.sendMessage(msg);
            }
        });

        thread.start();

        searchBoard = (EditText) view.findViewById(R.id.search);
        searchBoard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                arrayAdapter.getFilter().filter(s);
                lvf.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                String text = searchBoard.getText().toString();
//                lvf.filter(text);
            }
        });
//        getActivity().getSupportFragmentManager().beginTransaction().add(this, "ThirdFragment").commit();
        return view;
    }

    @Override
    public void updateFav(String favName) {
        SecondFragment secondFragment = (SecondFragment)getActivity().getSupportFragmentManager().findFragmentByTag(SecondFragment.fragmentTag());
        String boardNameEN = favName.substring(0, favName.indexOf("("));
        String boardNameCN = favName.substring(favName.indexOf("(") + 1, favName.indexOf(")"));
        if(!secondFragment.getFavList().contains(favName)){
            secondFragment.getFavList().add(favName);
            if(secondFragment.updateDatabase(boardNameCN, boardNameEN)){
                secondFragment.notifyAdapter();
            }
        }else {
            Toast.makeText(getActivity(), favName + "已经被添加过", Toast.LENGTH_SHORT).show();
        }
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
            view = getActivity().getLayoutInflater().inflate(R.layout.boardlistview, null);

            TextView tv = (TextView)view.findViewById(R.id.boardname);
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

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String boardName = boardFullName.substring(0, boardFullName.indexOf("("));
                    updateFav(boardFullName);
                    return true;
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
}
