package com.example.jackkuo.lingring;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {


    EditText extension;
    EditText password;
    EditText domain;
    Button saveBtn;
    String ext;
    String pass;
    String server;
    View V;

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        V = inflater.inflate(R.layout.register, container, false);
        display();
        saveBtn = (Button) V.findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ext = extension.getText().toString();
                pass = password.getText().toString();
                server = domain.getText().toString();
                SharedPreferences pref = getActivity().getSharedPreferences("account", 0);
                pref.edit()
                        .putString("ext", ext)
                        .putString("pass", pass)
                        .putString("server", server)
                        .apply();
                Toast.makeText(getContext(), "資料儲存成功", Toast.LENGTH_LONG).show();
                // reload SIP connection
                ((MainActivity) getActivity()).initializeManager();
            }

        });

        return V;
    }

    private void display(){
        String stored_ext = getActivity().getSharedPreferences("account", 0)
                .getString("ext", "");
        String stored_pass = getActivity().getSharedPreferences("account", 0)
                .getString("pass", "");
        String stored_domain = getActivity().getSharedPreferences("account", 0)
                .getString("server", "");
        extension = (EditText) V.findViewById(R.id.extension);
        password = (EditText) V.findViewById(R.id.password);
        domain = (EditText) V.findViewById(R.id.domain);
        extension.setText(stored_ext);
        password.setText(stored_pass);
        domain.setText(stored_domain);
    }

}
