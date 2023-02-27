package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EstimateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstimateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EstimateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstimateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EstimateFragment newInstance(String param1, String param2) {
        EstimateFragment fragment = new EstimateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    EditText enterSource,enterDestination;
    TextView fare;
    Button calFare,direction;
    private static double[] address;
    float minfare=10;
    float totalfare;
    private static DecimalFormat df=new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_estimate, container, false);
        enterSource = view.findViewById(R.id.editText);
        enterDestination = view.findViewById(R.id.editText4);
        fare = view.findViewById(R.id.textView10);
        calFare = view.findViewById(R.id.button);
        direction = view.findViewById(R.id.gmap);



        //code for google map
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = enterSource.getText().toString().trim();
                String dest = enterDestination.getText().toString().trim();

                if (start.equals("") && dest.equals("")) {
                    Toast.makeText(getActivity(), "Enter Both Location", Toast.LENGTH_SHORT).show();
                } else {
                    DisplayTrack(start, dest);
                }
            }
        });


        calFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterSource.getText().toString().trim().equals("") || enterDestination.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Enter the Source/Destination", Toast.LENGTH_SHORT).show();

                } else {
                    findlatlong();
                }
                if (address == null) {
                    Toast.makeText(getActivity(), "Could Not Find Latititude And Longitude", Toast.LENGTH_SHORT).show();
                } else {
                    double dis = distance(address);
                    float distance = (float) (dis/1000);
                    Toast.makeText(getActivity(), "Distance:"+distance, Toast.LENGTH_SHORT).show();
                    if(distance<1)
                        totalfare=minfare;
                    else if(distance>1)
                        totalfare=minfare+(distance*10);
                    fare.setText("Rs."+df.format(totalfare));
                }
            }
        });
        return view;
    }

    private double distance(double[] p1) {
        //Toast.makeText(getActivity(),""+p1[0]+" "+p1[1]+" "+p1[2]+" "+p1[3],Toast.LENGTH_LONG).show();
        double R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(p1[2]-p1[0]);
        double dLong = rad(p1[3]-p1[1]);
        double a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)) +
                (Math.cos(rad(p1[0])) * Math.cos(rad(p1[3])) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    private double rad(double x) {
        return x * Math.PI / 180;
    }


    private void findlatlong() {
        String[] address=new String[2];
        address[0]= enterSource.getText().toString().trim();
        address[1]= enterDestination.getText().toString().trim();
        GeoLocation geolocation = new GeoLocation();
        geolocation.getAddress(address,getActivity(),new GeoHandler());
    }

    private void DisplayTrack(String start, String dest) {
        Uri uri=Uri.parse("https://www.google.co.in/maps/dir/"+ start + "/" + dest);

        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class GeoHandler extends Handler {
        @Override
        public void handleMessage( Message msg) {
            //ArrayList address=new ArrayList<Double>();
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    address = bundle.getDoubleArray("address");
                    break;
            }
        }
    }

}