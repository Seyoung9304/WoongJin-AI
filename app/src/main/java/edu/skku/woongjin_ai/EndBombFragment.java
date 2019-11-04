package edu.skku.woongjin_ai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EndBombFragment extends Fragment {
    private Context context;
    private DatabaseReference mPostReference;
    Button end;
    String id_key, nickname_key, user1_key, user2_key;
    int check1, check2;
    public EndBombFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_bombend, container, false);

        context = container.getContext();
        mPostReference = FirebaseDatabase.getInstance().getReference().child("user_list");
        end = (Button) view.findViewById(R.id.end);
        id_key = getArguments().getString("id");
        nickname_key = getArguments().getString("nickname");
        user1_key = getArguments().getString("user1");
        user2_key = getArguments().getString("user2");
        check1 = 0;
        check2 = 0;

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ValueEventListener findgamers = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String key = postSnapshot.getKey();
                            String gamer_nickname = postSnapshot.child("nickname").getValue().toString();
                            String gamer_coin = postSnapshot.child("coin").getValue().toString();
                            if (check1 == 0 && gamer_nickname.equals(user1_key)) {
                                int coin = Integer.parseInt(gamer_coin) + 150;
                                String coin_convert = Integer.toString(coin);
                                mPostReference.child(key).child("coin").setValue(coin_convert);
                                check1 = 1;
                            }
                            if (check2 == 0 && gamer_nickname.equals(user2_key)) {
                                int coin = Integer.parseInt(gamer_coin) + 150;
                                String coin_convert = Integer.toString(coin);
                                mPostReference.child(key).child("coin").setValue(coin_convert);
                                check2 = 1;
                            }
                            if (check1 == 1 && check2 == 1) {
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                mPostReference.addListenerForSingleValueEvent(findgamers);

                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().remove(EndBombFragment.this).commit();
                manager.popBackStack();
                Intent intent_gamelist = new Intent(getActivity(), GameListActivity.class);
                intent_gamelist.putExtra("id", id_key);
                intent_gamelist.putExtra("nickname", nickname_key);
                startActivity(intent_gamelist);
            }
        });
        return view;
    }
}