package edu.skku.woongjin_ai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
from LoginActivity
메인 페이지
 */

public class MainActivity extends AppCompatActivity implements NewHoonjangFragment.OnFragmentInteractionListener, MainQuizTypeFragment.OnFragmentInteractionListener{

    public DatabaseReference mPostReference;
    Intent intent, intentBook, intentGamelist, intentMyPage;
    String id, nickname="";
    String school, mygrade, profile, myname;
    LinearLayout bookButton, quizButton, gameButton;
    Button myPageButton;
    TextView userNickname;
    MainQuizTypeFragment mainQuizTypeFragment;
    UserInfo me;
    NewHoonjangFragment hoonjangFragment_attend, hoonjangFragment_read;

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    String nomore_atd, nomore_read;
    int thisWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = getSharedPreferences("nomore", MODE_PRIVATE);
        nomore_atd = setting.getString("main_attend", "keepgoing");
        nomore_read = setting.getString("main_read", "keepgoing");

        bookButton = (LinearLayout) findViewById(R.id.ReadActivity);
        quizButton = (LinearLayout) findViewById(R.id.QuizActivity);
        gameButton = (LinearLayout) findViewById(R.id.GameActivity);
        myPageButton = (Button) findViewById(R.id.myPage);
        userNickname = (TextView) findViewById(R.id.main);
        mainQuizTypeFragment = new MainQuizTypeFragment();

        intent = getIntent();
        id = intent.getStringExtra("id");

        mPostReference = FirebaseDatabase.getInstance().getReference();

        getFirebaseDatabaseUserInfo();
        postFirebaseDatabaseAttend();

        // 독서 나라 버튼 이벤트
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBook = new Intent(MainActivity.this, NationBookActivity.class);
                intentBook.putExtra("id", id);
                intentBook.putExtra("nickname", nickname);
                intentBook.putExtra("thisWeek", Integer.toString(thisWeek));
                startActivity(intentBook);
            }
        });

        // 질문 나라 버튼 이벤트
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.mainQuizSelect, mainQuizTypeFragment);
                Bundle bundle = new Bundle(3);
                bundle.putString("id", id);
                bundle.putString("nickname", nickname);
                bundle.putString("thisWeek", Integer.toString(thisWeek));
                mainQuizTypeFragment.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // 게임 나라 버튼 이벤트
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentGamelist = new Intent(MainActivity.this, GameListActivity.class);
                intentGamelist.putExtra("id", id);
                intentGamelist.putExtra("nickname", nickname);
                startActivity(intentGamelist);
            }
        });

        // 마이페이지 버튼 이벤트
        myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentMyPage = new Intent(MainActivity.this, MyPageActivity.class);
                intentMyPage.putExtra("id", id);
                intentMyPage.putExtra("nickname", nickname);
                intentMyPage.putExtra("grade", mygrade);
                intentMyPage.putExtra("profile", profile);
                intentMyPage.putExtra("school", school);
                intentMyPage.putExtra("name", myname );
                startActivity(intentMyPage);
            }
        });
    }

    // 출석 체크
    private void postFirebaseDatabaseAttend() {
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int weekNum = 0;
                for(DataSnapshot snapshot : dataSnapshot.child("user_list/" + id + "/my_week_list").getChildren()) weekNum++;

                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                String dayOfWeekS = "";
                switch(dayOfWeek) {
                    case 1:
                        dayOfWeekS = "일"; calendar.add(Calendar.DAY_OF_MONTH, -6); break;
                    case 2:
                        dayOfWeekS = "월"; break;
                    case 3:
                        dayOfWeekS = "화"; calendar.add(Calendar.DAY_OF_MONTH, -1); break;
                    case 4:
                        dayOfWeekS = "수"; calendar.add(Calendar.DAY_OF_MONTH, -2); break;
                    case 5:
                        dayOfWeekS = "목"; calendar.add(Calendar.DAY_OF_MONTH, -3); break;
                    case 6:
                        dayOfWeekS = "금"; calendar.add(Calendar.DAY_OF_MONTH, -4); break;
                    case 7:
                        dayOfWeekS = "토"; calendar.add(Calendar.DAY_OF_MONTH, -5); break;
                }

                String startDate = "";
                Date dateS = calendar.getTime();

                if(weekNum == 0) {
                    weekNum++;
                    String startDate2 = new SimpleDateFormat("yyyy-MM-dd").format(dateS);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/cnt").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/correct").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/level").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/like").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/made").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/solvebomb").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/firstDateOfWeek").setValue(startDate2);
                } else startDate = dataSnapshot.child("user_list/" + id + "/my_week_list/week" + weekNum + "/firstDateOfWeek").getValue().toString();

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = new Date();
                Date date2 = new Date();
                try {
                    date1 = format.parse(todayDate);
                    date2 = format.parse(startDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(Math.abs((date1.getTime() - date2.getTime()) / (24*60*60*1000)) > 6) {
                    weekNum++;
                    String startDate2 = new SimpleDateFormat("yyyy-MM-dd").format(dateS);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/cnt").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/correct").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/level").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/like").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/made").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/solvebomb").setValue(0);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/firstDateOfWeek").setValue(startDate2);
                    mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/attend_list/" + dayOfWeekS).setValue(today);
                } else {
                    if(!dataSnapshot.child("user_list/" + id + "/my_week_list/week" + weekNum + "/attend_list/" + dayOfWeekS).exists()) {
                        mPostReference.child("user_list/" + id + "/my_week_list/week" + weekNum + "/attend_list/" + dayOfWeekS).setValue(today);
                    }
                }

                thisWeek = weekNum;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });
    }

    // 데이터베이스에서 유저 정보 가져오기
    private void getFirebaseDatabaseUserInfo() {
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                me = dataSnapshot.child("user_list/" + id).getValue(UserInfo.class);
                nickname = me.nickname;
                mygrade=me.grade;
                myname=me.name;
                school=me.school;
                profile=me.profile;
                userNickname.setText("안녕 " + nickname + "!\n여행하고 싶은 나라를 골라보자!");
                int AttendCount=0;
                long ReadCount=0;
                ReadCount=dataSnapshot.child("user_list/"+id+"/my_script_list").getChildrenCount();
                DataSnapshot dataSnapshot1=dataSnapshot.child("user_list/"+id+"/my_week_list");
                for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){ //week 껍데기
                    AttendCount+=dataSnapshot2.child("attend_list").getChildrenCount();
                }
                Calendar calendar = Calendar.getInstance();
                Date dateS = calendar.getTime();
                String MedalUpdate = new SimpleDateFormat("yyyy-MM-dd").format(dateS);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                hoonjangFragment_attend=new NewHoonjangFragment();
                hoonjangFragment_read=new NewHoonjangFragment();

                Log.d("nomore", nomore_atd);
                String atdcnt=Integer.toString(AttendCount);
                Log.d("AttendCount", atdcnt);

                if(AttendCount==365 && nomore_atd.equals("stop2")) {
                    uploadFirebaseUserCoinInfo_H("출석왕", 3);
                    mPostReference.child("user_list/" + id + "/my_medal_list/출석왕").setValue("Lev3##"+MedalUpdate);
                    SharedPreferences sf = getSharedPreferences("nomore", MODE_PRIVATE);
                    editor=sf.edit();
                    editor.putString("main_attend", "stop3");
                    editor.commit();
                    transaction.replace(R.id.Mainframe, hoonjangFragment_attend);
                    Bundle bundle = new Bundle(3);
                    bundle.putString("what", "attend");
                    bundle.putString("from", "main_attend");
                    bundle.putInt("level", 3);
                    hoonjangFragment_attend.setArguments(bundle);
                    transaction.commit();
                }else if(AttendCount==100 && nomore_atd.equals("stop1")){
                    uploadFirebaseUserCoinInfo_H("출석왕", 2);
                    mPostReference.child("user_list/" + id + "/my_medal_list/출석왕").setValue("Lev2##"+MedalUpdate);
                    SharedPreferences sf = getSharedPreferences("nomore", MODE_PRIVATE);
                    editor=sf.edit();
                    editor.putString("main_attend", "stop2");
                    editor.commit();
                    transaction.replace(R.id.Mainframe, hoonjangFragment_attend);
                    Bundle bundle = new Bundle(3);
                    bundle.putString("what", "attend");
                    bundle.putString("from", "main_attend");
                    bundle.putInt("level", 2);
                    hoonjangFragment_attend.setArguments(bundle);
                    transaction.commit();
                }else if(AttendCount==30 && nomore_atd.equals("keepgoing")){
                    uploadFirebaseUserCoinInfo_H("출석왕", 1);
                    mPostReference.child("user_list/" + id + "/my_medal_list/출석왕").setValue("Lev1##"+MedalUpdate);
                    SharedPreferences sf = getSharedPreferences("nomore", MODE_PRIVATE);
                    editor=sf.edit();
                    editor.putString("main_attend", "stop1");
                    editor.commit();
                    transaction.replace(R.id.Mainframe, hoonjangFragment_attend);
                    Bundle bundle = new Bundle(3);
                    bundle.putString("what", "attend");
                    bundle.putString("from", "main_attend");
                    bundle.putInt("level", 1);
                    hoonjangFragment_attend.setArguments(bundle);
                    transaction.commit();
                }
                if(ReadCount==150 && nomore_read.equals("stop2")) {
                    uploadFirebaseUserCoinInfo_H("다독왕", 3);
                    mPostReference.child("user_list/" + id + "/my_medal_list/다독왕").setValue("Lev3##"+MedalUpdate);
                    SharedPreferences sf = getSharedPreferences("nomore", MODE_PRIVATE);
                    editor=sf.edit();
                    editor.putString("main_read", "stop3");
                    editor.commit();
                    transaction.replace(R.id.Mainframe, hoonjangFragment_read);
                    Bundle bundle = new Bundle(3);
                    bundle.putString("what", "read");
                    bundle.putString("from", "main_read");
                    bundle.putInt("level", 3);
                    hoonjangFragment_read.setArguments(bundle);
                    transaction.commit();
                }else if(ReadCount==100 && nomore_read.equals("stop1")){
                    uploadFirebaseUserCoinInfo_H("다독왕", 2);
                    mPostReference.child("user_list/" + id + "/my_medal_list/다독왕").setValue("Lev2##"+MedalUpdate);
                    SharedPreferences sf = getSharedPreferences("nomore", MODE_PRIVATE);
                    editor=sf.edit();
                    editor.putString("main_read", "stop2");
                    editor.commit();
                    transaction.replace(R.id.Mainframe, hoonjangFragment_read);
                    Bundle bundle = new Bundle(3);
                    bundle.putString("what", "attend");
                    bundle.putString("from", "main_read");
                    bundle.putInt("level", 2);
                    hoonjangFragment_read.setArguments(bundle);
                    transaction.commit();
                }else if(ReadCount==50 && nomore_read.equals("keepgoing")){
                    uploadFirebaseUserCoinInfo_H("다독왕", 1);
                    mPostReference.child("user_list/" + id + "/my_medal_list/다독왕").setValue("Lev1##"+MedalUpdate);
                    SharedPreferences sf = getSharedPreferences("nomore", MODE_PRIVATE);
                    editor=sf.edit();
                    editor.putString("main_read", "stop1");
                    editor.commit();
                    transaction.replace(R.id.Mainframe, hoonjangFragment_read);
                    Bundle bundle = new Bundle(3);
                    bundle.putString("what", "attend");
                    bundle.putString("from", "main_read");
                    bundle.putInt("level", 1);
                    hoonjangFragment_read.setArguments(bundle);
                    transaction.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });
    }

    // 훈장 수여 자격 여부 확인 및 수여, 데이터베이스에 저장
    private void uploadFirebaseUserCoinInfo_H(String hoonjangname, int level){
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                String today = new SimpleDateFormat("yyMMddHHmm").format(date);
                mPostReference.child("user_list/" + id + "/my_coin_list/" + today + "/get").setValue(Integer.toString(level*100));
                mPostReference.child("user_list/" + id + "/my_coin_list/" + today + "/why").setValue(hoonjangname+" 레벨 "+Integer.toString(level)+"달성!");

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String key=dataSnapshot1.getKey();
                    if(key.equals("user_list")){
                        String mycoin=dataSnapshot1.child(id).child("coin").getValue().toString();
                        int coin = Integer.parseInt(mycoin) + level*100;
                        String coin_convert = Integer.toString(coin);
                        mPostReference.child("user_list/" + id).child("coin").setValue(coin_convert);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
