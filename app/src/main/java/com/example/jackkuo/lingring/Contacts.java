package com.example.jackkuo.lingring;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contacts extends Fragment {

    public List<String> ext = new ArrayList<String>();
    public List<String> name = new ArrayList<String>();
    public List<String> content = new ArrayList<String>();
    public ArrayAdapter<String> adapter;

    public Contacts() {
        // Required empty public constructor
    }
    private TextView extension;
    private EditText password;
    private String whom;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.contacts, container, false);
        ListView list = V.findViewById(R.id.contact_list);
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast 快顯功能 第三個參數 Toast.LENGTH_SHORT 2秒  LENGTH_LONG 5秒
                Toast.makeText(getContext(),"長按打給 " + ext.get(content.indexOf(adapter.getItem(position))), Toast.LENGTH_SHORT).show();
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                String stored_ext = getActivity().getSharedPreferences("account", 0)
                        .getString("ext", "");
                String stored_pass = getActivity().getSharedPreferences("account", 0)
                        .getString("pass", "");
                whom = ext.get(content.indexOf(adapter.getItem(position)));
//                if("".equals(stored_ext) || "".equals(stored_pass)){
//                    Toast.makeText(Contact.this,"尚未設定帳密喇吼 QAQ", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
                Toast.makeText(getContext(),"真的要打給 "+ whom + " 囉 QAQ", Toast.LENGTH_SHORT).show();

                //這邊是呼叫 Fragment
                Log.d("whom", whom);
                ((MainActivity) getActivity()).call(whom);
                ((MainActivity) getActivity()).switchContent(new Dial(), R.id.nav_reg);

                // true 表示不再丟給 onItemClick 處理，false 則會再執行 onItemClick，如果有的話
                return true;
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference contacts = database.getReference("contacts");
        contacts.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if( !"".equals((String)dataSnapshot.child("分機號碼").getValue())){
                    String c = (String)dataSnapshot.child("姓名").getValue() +
                            "   " +
                            (String)dataSnapshot.child("單位名稱").getValue() +
                            "   " +
                            (String)dataSnapshot.child("職稱").getValue() +
                            "   " +
                            (String)dataSnapshot.child("分機號碼").getValue();
                    adapter.add(c);
                    ext.add((String)dataSnapshot.child("分機號碼").getValue());
                    name.add((String)dataSnapshot.child("姓名").getValue());
                    content.add(c);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if( !"".equals((String)dataSnapshot.child("分機號碼").getValue())){
                    String c = (String)dataSnapshot.child("姓名").getValue() +
                            "   " +
                            (String)dataSnapshot.child("單位名稱").getValue() +
                            "   " +
                            (String)dataSnapshot.child("職稱").getValue() +
                            "   " +
                            (String)dataSnapshot.child("分機號碼").getValue();
                    adapter.add(c);
                    ext.add((String)dataSnapshot.child("分機號碼").getValue());
                    name.add((String)dataSnapshot.child("姓名").getValue());
                    content.add(c);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        EditText searchEdittext = V.findViewById(R.id.contactSearch);
        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user change the text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                //
            }
        });


        // Inflate the layout for this fragment
        return V;
    }



}
