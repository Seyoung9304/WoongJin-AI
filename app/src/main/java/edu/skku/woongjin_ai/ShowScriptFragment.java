package edu.skku.woongjin_ai;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowScriptFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView textViewScript1, textViewScript2;
    String title, script, type;

    private ShowScriptFragment.OnFragmentInteractionListener mListener;

    public ShowScriptFragment() {

    }

    public static ShowScriptFragment newInstance(String param1, String param2) {
        ShowScriptFragment fragment = new ShowScriptFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_showscript, container, false);
        final Context context = container.getContext();

        title = getArguments().getString("scriptnm");
        type = getArguments().getString("type");

        TextView textViewTitle = (TextView) view.findViewById(R.id.title);
        textViewScript1 = (TextView) view.findViewById(R.id.script1);
        textViewScript2 = (TextView) view.findViewById(R.id.script2);
        ImageButton buttonClose = (ImageButton) view.findViewById(R.id.close);

        textViewTitle.setText(title);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(type.equals("ox")) {
                    fragmentTransaction.remove(((OXTypeActivity)getActivity()).showScriptFragment);
                    fragmentTransaction.commit();
                } else if(type.equals("choice")) {
                    fragmentTransaction.remove(((ChoiceTypeActivity)getActivity()).showScriptFragment);
                    fragmentTransaction.commit();
                } else if(type.equals("shortword")) {
                    fragmentTransaction.remove(((ShortwordTypeActivity)getActivity()).showScriptFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("script_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if(key.equals(title)) {
                        script = snapshot.child("text").getValue().toString();
                        String[] array=script.split("###");
                        textViewScript1.setText(array[0]);
                        textViewScript2.setText(array[1]);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShowScriptFragment.OnFragmentInteractionListener) {
            mListener = (ShowScriptFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}