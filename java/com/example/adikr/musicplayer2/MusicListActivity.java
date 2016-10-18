package com.example.adikr.musicplayer2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

import com.example.adikr.musicplayer2.R;

public class MusicListActivity extends AppCompatActivity {

    ArrayList<Song> songList;
    ListView songView;
    SeekBar seeker;

    float floating_duration;
    long current_song_id;
    int song_tag;
    Song current_song;
    String current_song_name, duration;
    MediaPlayer mediaPlayer;

    Button play, stop, shuffle, prev, next;
    TextView now_playing, t_time, c_time;

    RadioButton rb_loop, rb_shuffle, rb_next;

    Boolean pau = false;
    Boolean willLoop = false, isshuffle = false, isnext=true;

    Uri songUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        //rb_loop = (RadioButton)findViewById(R.id.loopcheck);
        //rb_shuffle = (RadioButton)findViewById(R.id.shufflecheck);
        rb_next = (RadioButton)findViewById(R.id.next_radio);
        rb_next.setChecked(true);
        now_playing = (TextView)findViewById(R.id.now_playing);
        seeker = (SeekBar)findViewById(R.id.seek);
        play = (Button)findViewById(R.id.play_button);
        play.setText("Play");
        //pause = (Button)findViewById(R.id.pause_button);
        stop = (Button)findViewById(R.id.stop_button);
        shuffle = (Button)findViewById(R.id.shuffle);
        prev = (Button)findViewById(R.id.prev);
        next = (Button)findViewById(R.id.next);
        t_time = (TextView)findViewById(R.id.total_time);
        c_time = (TextView)findViewById(R.id.current_time);

        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();


        getSongList();

        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);

        song_tag = 0;
        current_song = songList.get(song_tag);
        current_song_id = current_song.getId();
        current_song_name = current_song.getTitle();
        now_playing.setText(current_song_name);

        songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current_song_id);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
        seeker.setMax(mediaPlayer.getDuration()/1000);
        //floating_duration = mediaPlayer.getDuration()/1000;


        //--------------------------------------------- MUSIC CONTROLLER START ----------------------------------------------



        play.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(pau == false) {
                           mediaPlayer.start();
                            play.setText("Pause");
                            pau = true;
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    pau = false;
                                    playNext();
                                }
                            });
                            seeker.setMax(mediaPlayer.getDuration() / 1000);
                            t_time.setText(getFormattedTime(mediaPlayer.getDuration()));
                            final Handler mHandler = new Handler();

                            MusicListActivity.this.runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mediaPlayer != null) {
                                                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                                c_time.setText(getFormattedTime(mediaPlayer.getCurrentPosition()));
                                                seeker.setProgress(mCurrentPosition);

                                            }
                                            mHandler.postDelayed(this, 100);
                                        }
                                    }
                            );

                        }

                        else{
                            mediaPlayer.pause();
                            play.setText("Play");
                            pau=false;
                        }
                    }
                }
        );
/*
        pause.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.pause();
                    }
                }
        ); */

        stop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.stop();
                        play.setText("Play");
                        pau = false;
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
                    }
                }
        );

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomSongPick();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!willLoop){
                    if(isshuffle){
                        randomSongPick();
                    }
                    else{
                        playNext();
                    }
                }
                else{
                    playNext();
                }
            }
        });



        //---------------------------------------- MUSIC CONTROLLER END -------------------------------------------------

        //---------------------------------------- SEEKER CHANGE LISTENER -----------------------------------------------

        seeker.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        if(mediaPlayer != null && fromUser)
                            mediaPlayer.seekTo(progress*1000);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

    }

    public void songPicked(View view){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        song_tag = Integer.parseInt(view.getTag().toString());
        current_song = songList.get(song_tag);
        current_song_name = current_song.getTitle();
        now_playing.setText(current_song_name);
        current_song_id = current_song.getId();

        songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current_song_id);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
        pau=false;
        play.setText("Play");
        play.performClick();
        //mediaPlayer.start();
    }

    public void randomSongPick()
    {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        Random random = new Random();
        song_tag = random.nextInt(songList.size());
        current_song = songList.get(song_tag);
        current_song_name = current_song.getTitle();
        now_playing.setText(current_song_name);
        current_song_id = current_song.getId();

        songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current_song_id);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);

        play.performClick();
        //mediaPlayer.start();
    }

    public void playNext(){


        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        if(willLoop)
        {

        }
        else {
            if (song_tag >= songList.size() - 1) {
                song_tag = 0;
            } else {
                song_tag++;
            }
        }
        current_song = songList.get(song_tag);
        current_song_name = current_song.getTitle();
        now_playing.setText(current_song_name);
        current_song_id = current_song.getId();

        songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current_song_id);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);

        play.performClick();
    }

    public void playPrev(){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        if(song_tag == 0){
            song_tag = songList.size()-1;
        }
        else{
            song_tag--;
        }
        current_song = songList.get(song_tag);
        current_song_name = current_song.getTitle();
        now_playing.setText(current_song_name);
        current_song_id = current_song.getId();

        songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current_song_id);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);

        play.performClick();
    }

    public void getSongList(){
        //fetch songs from phone
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst())
        {
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public class Song
    {
        private long id;
        String title;
        String artist;

        public Song(long id, String title, String artist) {
            this.artist = artist;
            this.id = id;
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    public class SongAdapter extends BaseAdapter {

        private ArrayList<Song> songs;
        private LayoutInflater songInf;

        public SongAdapter(Context c, ArrayList<Song> theSongs) {
            songInf = LayoutInflater.from(c);
            songs = theSongs;
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            @SuppressLint("ViewHolder") LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.song, parent, false);

            TextView songView = (TextView)songLay.findViewById(R.id.song_title);
            TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);

            Song currSong = songs.get(position);
            songView.setText(currSong.getTitle());
            artistView.setText(currSong.getArtist());

            songLay.setTag(position);

            return songLay;
        }
    }

    public String getFormattedTime(int millis){

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

    }

    public void radioClickEvent(View view)
    {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId())
        {
            case R.id.loop_radio:
                if(checked){
                    willLoop = true;
                    //mediaPlayer.setLooping(true);
                    isshuffle = false;
                    //((RadioButton) R.id.shufflecheck).setChecked(false);
                }
                break;

            case R.id.shuffle_radio:
                if(checked){
                    isshuffle = true;
                    willLoop = false;
                    //mediaPlayer.setLooping(false);
                }
                break;

            case R.id.next_radio:
                if(checked)
                {
                    isshuffle = false;
                    willLoop = false;
                    //mediaPlayer.setLooping(false);
                }
                break;

        }
    }
    public MyNotification(Context ctx){
        super();
        this.ctx=ctx;
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
        CharSequence tickerText = "Shortcuts";
        long when = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(ctx);

        @SuppressWarnings("deprecation")

        Notification notification=builder.getNotification();
        notification.when=when;
        notification.tickerText=tickerText;
        notification.icon=R.drawable.ic_launcher;

        RemoteViews contentView=new RemoteViews(ctx.getPackageName(), R.layout.messageview);

        //set the button listeners
        setListeners(contentView);

        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        mNotificationManager.notify(548853, notification);
    }

    public void setListeners(RemoteViews view){
        //radio listener
        Intent radio=new Intent(ctx,HelperActivity.class);
        radio.putExtra("DO", "radio");
        PendingIntent pRadio = PendingIntent.getActivity(ctx, 0, radio, 0);
        view.setOnClickPendingIntent(R.id.radio, pRadio);

        //volume listener
        Intent volume=new Intent(ctx, HelperActivity.class);
        volume.putExtra("DO", "volume");
        PendingIntent pVolume = PendingIntent.getActivity(ctx, 1, volume, 0);
        view.setOnClickPendingIntent(R.id.volume, pVolume);

        //reboot listener
        Intent reboot=new Intent(ctx, HelperActivity.class);
        reboot.putExtra("DO", "reboot");
        PendingIntent pReboot = PendingIntent.getActivity(ctx, 5, reboot, 0);
        view.setOnClickPendingIntent(R.id.reboot, pReboot);

        //top listener
        Intent top=new Intent(ctx, HelperActivity.class);
        top.putExtra("DO", "top");
        PendingIntent pTop = PendingIntent.getActivity(ctx, 3, top, 0);
        view.setOnClickPendingIntent(R.id.top, pTop);*/

        //app listener
        Intent app=new Intent(ctx, com.example.demo.HelperActivity.class);
        app.putExtra("DO", "app");
        PendingIntent pApp = PendingIntent.getActivity(ctx, 4, app, 0);
        view.setOnClickPendingIntent(R.id.btn1, pApp);
    }

}



}

