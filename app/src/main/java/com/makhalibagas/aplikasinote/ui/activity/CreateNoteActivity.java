package com.makhalibagas.aplikasinote.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makhalibagas.aplikasinote.R;
import com.makhalibagas.aplikasinote.database.NoteDatabase;
import com.makhalibagas.aplikasinote.entities.Note;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextTitle, editTextSubtitle, editTextText;
    private TextView textViewTime;
    private View colorView;
    private String selectColor;
    private ImageView imageNote;
    private TextView tvUrl;
    private AlertDialog alertDialog;
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_SELECT = 2;
    private String selectImagePath;
    private Note noteExtra;
    private String fileName = null;
    private MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_note);


        editTextTitle = findViewById(R.id.editTextTitle);
        editTextSubtitle = findViewById(R.id.editTextSubTitle);
        editTextText = findViewById(R.id.editTextText);
        textViewTime = findViewById(R.id.textViewTime);
        colorView = findViewById(R.id.colorView);
        imageNote = findViewById(R.id.imageNote);
        tvUrl = findViewById(R.id.tvUrlNote);


        //EEEE, dd MMMM yyyy HH:mm a
        //Hari, Tanggal bulan tahun, jam a = malam m = pagi
        textViewTime.setText("Last modified : " + new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date()));



        selectImagePath = "";
        selectColor = "#ffffff";

        if (getIntent().getBooleanExtra("EXTRA", false)){
            noteExtra = (Note) getIntent().getSerializableExtra("EXTRA_NOTE");
            setViewOrUpdateNote();
        }


        if (noteExtra != null){
            findViewById(R.id.linearDelete).setVisibility(View.VISIBLE);
            findViewById(R.id.addDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
                }
            });
        }
        setViewColor();

        final Button btHapusUrl = findViewById(R.id.btHapusUrl);
        final FloatingActionButton fabHapusImage = findViewById(R.id.fabdeleteimg);
        btHapusUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvUrl.setText(null);
                tvUrl.setVisibility(View.GONE);
                btHapusUrl.setVisibility(View.GONE);
            }
        });

        fabHapusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                fabHapusImage.setVisibility(View.GONE);
                selectImagePath = "";
            }
        });
    }
    private void setViewOrUpdateNote(){
        editTextTitle.setText(noteExtra.getTitle());
        editTextSubtitle.setText(noteExtra.getSubTitle());
        editTextText.setText(noteExtra.getNoteText());

        if (noteExtra.getImagePath() != null && !noteExtra.getImagePath().trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(noteExtra.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            selectImagePath = noteExtra.getImagePath();
            findViewById(R.id.fabdeleteimg).setVisibility(View.VISIBLE);
        }

        if (noteExtra.getUrl() != null && !noteExtra.getUrl().trim().isEmpty()){
            tvUrl.setText(noteExtra.getUrl());
            tvUrl.setVisibility(View.VISIBLE);
            findViewById(R.id.btHapusUrl).setVisibility(View.VISIBLE);
        }
    }
    private void saveNotes(){

        if (editTextTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Note Title Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            return;
        }else if (editTextSubtitle.getText().toString().isEmpty() && editTextText.getText().toString().isEmpty()){
            Toast.makeText(this, "Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();

        note.setTitle(editTextTitle.getText().toString());
        note.setSubTitle(editTextSubtitle.getText().toString());
        note.setNoteText(editTextText.getText().toString());
        note.setDateTime(textViewTime.getText().toString());
        note.setColor(selectColor);
        note.setImagePath(selectImagePath);

        if (tvUrl.getVisibility() == View.VISIBLE){
            note.setUrl(tvUrl.getText().toString());
            findViewById(R.id.btHapusUrl).setVisibility(View.VISIBLE);
        }

        if (noteExtra != null){
            note.setId(noteExtra.getId());
        }


        @SuppressLint("StaticFieldLeak")
        class saveNoteAsyncTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getInstance(getApplicationContext()).noteDao().insert(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new saveNoteAsyncTask().execute();
    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvSave:
                saveNotes();
                break;
            case R.id.addVoice:
                Dialog dialogVoice = new Dialog(CreateNoteActivity.this);
                dialogVoice.setContentView(R.layout.layout_voice);
                dialogVoice.show();
                startVoiceNote();
                break;
            case R.id.addImage:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }else {
                    selectImage();
                }
                break;
            case R.id.addUrl:
                showDialogUrl();
                break;
            case R.id.addColor:
                showColorDialog();
                break;

        }
    }

    private void showColorDialog() {
        Dialog dialog = new Dialog(CreateNoteActivity.this);
        dialog.setContentView(R.layout.layout_color);

        final ImageView imageView1 = dialog.findViewById(R.id.img_1);
        final ImageView imageView2 = dialog.findViewById(R.id.img_2);
        final ImageView imageView3 = dialog.findViewById(R.id.img_3);
        final ImageView imageView4 = dialog.findViewById(R.id.img_4);
        final ImageView imageView5 = dialog.findViewById(R.id.img_5);
        final ImageView imageView6 = dialog.findViewById(R.id.img_6);
        final ImageView imageView7 = dialog.findViewById(R.id.img_7);
        final ImageView imageView8 = dialog.findViewById(R.id.img_8);
        final ImageView imageView9 = dialog.findViewById(R.id.img_9);
        final ImageView imageView10 = dialog.findViewById(R.id.img_10);


        if (noteExtra != null && noteExtra.getColor() != null && !noteExtra.getColor().trim().isEmpty()){
            switch (noteExtra.getColor()){
                case "#E91E63":
                    dialog.findViewById(R.id.view2).performClick();
                    break;
                case "#2196F3":
                    dialog.findViewById(R.id.view3).performClick();
                    break;
                case "#FFEB3B":
                    dialog.findViewById(R.id.view4).performClick();
                    break;
                case "#FF5722":
                    dialog.findViewById(R.id.view5).performClick();
                    break;
                case "#64FFDA":
                    dialog.findViewById(R.id.view6).performClick();
                    break;
                case "#EA80FC":
                    dialog.findViewById(R.id.view7).performClick();
                    break;
                case "#00ACC1":
                    dialog.findViewById(R.id.view8).performClick();
                    break;
                case "#F57F17":
                    dialog.findViewById(R.id.view9).performClick();
                    break;
                case "#D252FF22":
                    dialog.findViewById(R.id.view10).performClick();
                    break;


            }
        }
        dialog.findViewById(R.id.view1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#FF00BCD4";
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });

        dialog.findViewById(R.id.view2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#E91E63";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });


        dialog.findViewById(R.id.view3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#2196F3";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });

        dialog.findViewById(R.id.view4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#FFEB3B";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.VISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });

        dialog.findViewById(R.id.view5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#FF5722";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.VISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });


        dialog.findViewById(R.id.view6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#64FFDA";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.VISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });

        dialog.findViewById(R.id.view7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#EA80FC";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.VISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });


        dialog.findViewById(R.id.view8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#00ACC1";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.VISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });

        dialog.findViewById(R.id.view9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#F57F17";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.VISIBLE);
                imageView10.setVisibility(View.INVISIBLE);
                setViewColor();
            }
        });

        dialog.findViewById(R.id.view10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor = "#D252FF22";
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
                imageView7.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                imageView9.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.VISIBLE);
                setViewColor();
            }
        });
        dialog.show();
    }
    private void setViewColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) colorView.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectColor));
    }
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_SELECT);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT && resultCode == RESULT_OK){
            if (data != null){
                Uri selectImgUri = data.getData();
                if (selectImgUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectImgUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);

                        findViewById(R.id.fabdeleteimg).setVisibility(View.VISIBLE);
                        selectImagePath = getPathFromUri(selectImgUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null){
            filePath = contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
    private void showDeleteDialog(){
        final Dialog dialog = new Dialog(CreateNoteActivity.this);
        dialog.setContentView(R.layout.layout_delete);
        dialog.findViewById(R.id.tvOKEHAPUS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak")
                class HapusNoteAsyncTask extends AsyncTask<Void, Void, Void>{
                    @Override
                    protected Void doInBackground(Void... voids) {

                        NoteDatabase.getInstance(getApplicationContext()).noteDao().delete(noteExtra);

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent intent = new Intent();
                        intent.putExtra("NoteDelete", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                new HapusNoteAsyncTask().execute();
            }
        });
        dialog.findViewById(R.id.tvBATALHAPUS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showDialogUrl(){
        if (alertDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_url, (ViewGroup) findViewById(R.id.layout_url));
            builder.setView(view);

            alertDialog = builder.create();

            if (alertDialog.getWindow() != null){
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText etUrl = view.findViewById(R.id.editTextAddUrl);
            etUrl.requestFocus();

            view.findViewById(R.id.tvOKE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etUrl.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoteActivity.this, "Enter Url", Toast.LENGTH_SHORT).show();
                    }else if (!Patterns.WEB_URL.matcher(etUrl.getText().toString()).matches()){

                        Toast.makeText(CreateNoteActivity.this, "Enter Invalid Url", Toast.LENGTH_SHORT).show();
                    }else {
                        tvUrl.setText(etUrl.getText().toString());
                        tvUrl.setVisibility(View.VISIBLE);
                        findViewById(R.id.btHapusUrl).setVisibility(View.VISIBLE);
                        alertDialog.dismiss();
                    }
                }
            });

            view.findViewById(R.id.tvBATAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }

        alertDialog.show();
    }
    private void startVoiceNote(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/notesApp/voice/" + System.currentTimeMillis() );

        if (!file.exists()){
            file.mkdirs();
        }

        fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/notesApp/voice/" + System.currentTimeMillis();
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {

            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopVoiceNote(){
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder = null;
    }


}
