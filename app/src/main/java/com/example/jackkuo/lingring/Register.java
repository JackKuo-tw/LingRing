package com.example.jackkuo.lingring;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment {


    public Register() {
        // Required empty public constructor
    }
    private TextView extension;
    private EditText password;
    private static final int MY_PERMISSIONS_REQUEST_USE_SIP = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.register, container, false);
        extension = (TextView) V.findViewById(R.id.extension);
        password = (EditText) V.findViewById(R.id.password);
        Button calcBtn = (Button) V.findViewById(R.id.save);

        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ext =  extension.getText().toString();
                String pass = password.getText().toString();
                if("".equals(ext) || "".equals(pass)){
                    return;
                }

                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.USE_SIP)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        ) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.USE_SIP)||
                            ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    android.Manifest.permission.RECORD_AUDIO)||
                            ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    android.Manifest.permission.CALL_PHONE)
                            ) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.USE_SIP, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_USE_SIP);


                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.USE_SIP, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_USE_SIP);
                    }
                }else {
                    // Permission has already been granted
                }
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.USE_SIP)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        ) {
                    Toast.makeText(getContext() , "該操作需要權限", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(view.getContext(), Dial.class);
                    Bundle bag = new Bundle();
                    bag.putString("ext", ext);
                    bag.putString("pass", pass);
                    intent.putExtras(bag);
                    startActivity(intent);
                }

            }
        });

        // Inflate the layout for this fragment
        return V;
    }



}
