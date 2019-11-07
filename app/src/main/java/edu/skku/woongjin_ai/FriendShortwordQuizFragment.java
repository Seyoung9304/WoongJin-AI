package edu.skku.woongjin_ai;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FriendShortwordQuizFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FriendShortwordQuizFragment.OnFragmentInteractionListener mListener;

    String id, scriptnm, question, answer, uid, star, like, hint, key, ans = "", background;
    int cnt, flagAO = 0, flagAX = 0;
    float starFloat;
    ImageView imageViewS2, imageViewS3, imageViewS4, imageViewS5;
    Button imageButtonScript, imageButtonHint, imageButtonCheck;
    EditText editTextAns;

    public FriendShortwordQuizFragment() {

    }

    public static FriendShortwordQuizFragment newInstance(String param1, String param2) {
        FriendShortwordQuizFragment fragment = new FriendShortwordQuizFragment();
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
        final View view = inflater.inflate(R.layout.fragment_friendshortwordquiz, container, false);
        final Context context = container.getContext();

        id = getArguments().getString("id");
        scriptnm = getArguments().getString("scriptnm");
        question = getArguments().getString("question");
        answer = getArguments().getString("answer");
        uid = getArguments().getString("uid");
        star = getArguments().getString("star");
        like = getArguments().getString("like");
        hint = getArguments().getString("desc");
        key = getArguments().getString("key");
        cnt = getArguments().getInt("cnt");
        background = getArguments().getString("background");

        TextView textViewUid = view.findViewById(R.id.uidFriendShortword);
        TextView textViewName = view.findViewById(R.id.nameFriendShortword);
        TextView textViewQuestion = view.findViewById(R.id.questionFriendShortword);
        editTextAns = (EditText) view.findViewById(R.id.ansShortword);
        imageViewS2 = (ImageView) view.findViewById(R.id.star2);
        imageViewS3 = (ImageView) view.findViewById(R.id.star3);
        imageViewS4 = (ImageView) view.findViewById(R.id.star4);
        imageViewS5 = (ImageView) view.findViewById(R.id.star5);
        imageButtonScript = (Button) view.findViewById(R.id.scriptFriendShortword);
        imageButtonHint = (Button) view.findViewById(R.id.hintFriendShortword);
        imageButtonCheck = (Button) view.findViewById(R.id.checkFriendShortword);
        ConstraintLayout backgroundLayout = (ConstraintLayout) view.findViewById(R.id.backgroundshortword);

        starFloat = Float.parseFloat(star);

        textViewUid.setText(uid + " 친구가 낸 질문");
        textViewName.setText(scriptnm);
        textViewQuestion.setText(question);

        if(background.equals("blue")) backgroundLayout.setBackgroundColor(Color.rgb(51, 153, 204));
        else if(background.equals("red")) backgroundLayout.setBackgroundColor(Color.rgb(255, 102, 102));

        imageButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ans = editTextAns.getText().toString();
                if(ans.equals("")) {
                    Toast.makeText(context, "정답을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if(ans.equals(answer)) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.contentShowScript, ((ShowFriendQuizActivity)getActivity()).correctFriendQuizFragment);
                        Bundle bundle = new Bundle(7);
                        bundle.putString("id", id);
                        bundle.putString("scriptnm", scriptnm);
                        bundle.putString("uid", uid);
                        bundle.putString("star", star);
                        bundle.putString("like", like);
                        bundle.putString("key", key);
                        bundle.putInt("cnt", cnt);
                        ((ShowFriendQuizActivity)getActivity()).correctFriendQuizFragment.setArguments(bundle);
//                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.contentShowScript, ((ShowFriendQuizActivity)getActivity()).wrongFriendQuizFragment);
                        Bundle bundle = new Bundle(1);
                        bundle.putString("id", id);
                        ((ShowFriendQuizActivity)getActivity()).wrongFriendQuizFragment.setArguments(bundle);
//                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            }
        });

        imageButtonHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentShowHint, ((ShowFriendQuizActivity)getActivity()).showHintFragment);
                Bundle bundle = new Bundle(1);
                bundle.putString("hint", hint);
                ((ShowFriendQuizActivity)getActivity()).showHintFragment.setArguments(bundle);
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        imageButtonScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentShowScript, ((ShowFriendQuizActivity)getActivity()).showScriptFragment);
                Bundle bundle = new Bundle(2);
                bundle.putString("scriptnm", scriptnm);
                bundle.putString("type", "friend");
                ((ShowFriendQuizActivity)getActivity()).showScriptFragment.setArguments(bundle);
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        if(starFloat >= 1.5) {
            imageViewS2.setImageResource(R.drawable.star_full);
            if(starFloat >= 2.5) {
                imageViewS3.setImageResource(R.drawable.star_full);
                if(starFloat >= 3.5) {
                    imageViewS4.setImageResource(R.drawable.star_full);
                    if(starFloat >= 4.5) {
                        imageViewS5.setImageResource(R.drawable.star_full);
                    }
                }
            }
        }

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
        if (context instanceof FriendShortwordQuizFragment.OnFragmentInteractionListener) {
            mListener = (FriendShortwordQuizFragment.OnFragmentInteractionListener) context;
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
