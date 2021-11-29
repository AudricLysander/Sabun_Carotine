package id.ac.umn.carotine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import id.ac.umn.carotine.Adapter.ToDoAdapter;

public class TaskDetail extends AppCompatActivity implements DialogCloseListener, View.OnClickListener {

    private ToDoAdapter adapter;

    private int position, id;
    private String task;

    public Button editTaskBtn, startTimerBtn;
    public TextView taskName;

    // Count Down Timer Initialize
    private long timeCountInMilliSeconds = 1 * 60000;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private ProgressBar progressBarCircle;
    private EditText editTextMinute;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;

    // Notification
    private NotificationManagerCompat notificationManager;
    public void sendOnChannel1(){
        String title = "Time's up!!!";
        String message = "Let's check your next work!";

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.icon_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        notificationManager.notify(1, notification);
    }

    // Membuat status waktu, apakah sudah mulai atau berhenti
    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        getSupportActionBar().setTitle("Focus"); //set title di bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //biar ada tombol kembali

        adapter = new ToDoAdapter(this);

        startTimerBtn = findViewById(R.id.startTimerBtn);
        editTaskBtn = findViewById(R.id.editTaskBtn);

        taskName = findViewById(R.id.taskName);
        Intent mainIntent = getIntent();
        taskName.setText(mainIntent.getStringExtra("taskName"));

        id = mainIntent.getIntExtra("taskId", 0);
        task = mainIntent.getStringExtra("taskName");

        editTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTask(id, task);
            }
        });

        // Method untuk memanggil inisialisasi view yang akan dipakai
        initViews();
        // Method untuk memanggil inisialisasi listener yang akan dipakai
        initListeners();
        // Notification
        notificationManager = NotificationManagerCompat.from(this);
    }

    // Method inisialisasi view
    private void initViews() {
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        editTextMinute = (EditText) findViewById(R.id.editTextMinute);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
        imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
    }

    // Method inisialisasi listener
    private void initListeners() {
        imageViewReset.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
    }

    // Implementasi method listener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                reset();
                break;
            case R.id.imageViewStartStop:
                startStop();
                break;
        }
    }

    // Method untuk restart waktu
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    // Method untuk mulai dan stop waktu
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            // Memanggil fungsi inisialisasi values dari timer
            setTimerValues();

            // Memanggil fungsi inisialisasi progress bar
            setProgressBarValues();

            // Menampilkan icon
            imageViewReset.setVisibility(View.VISIBLE);
            imageViewStartStop.setImageResource(R.drawable.timer_stop);

            // Buat edit text disable
            editTextMinute.setEnabled(false);

            // Ubah status waktu jadi mulai
            timerStatus = TimerStatus.STARTED;

            // Memanggil fungsi startcountdown biar waktunya jalan
            startCountDownTimer();

        } else {

            // Sembunyiin ikon reset
            imageViewReset.setVisibility(View.GONE);

            // Ubah ikon stop jadi start
            imageViewStartStop.setImageResource(R.drawable.timer_play_arrow);

            // Buat teks input waktu jadi able
            editTextMinute.setEnabled(true);

            // Ubah status waktu jadi stop
            timerStatus = TimerStatus.STOPPED;

            // Memanggil fungsi stopcountdown biar waktunya berhenti
            stopCountDownTimer();
        }

    }

    // Method untuk inisialisasi value untuk count down
    private void setTimerValues() {
        int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // Ambil data number dari edit text
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            // Membuat toast agar terisi di edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }
        // convert waktu
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    // Method untuk mulai count down
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Ubah text view
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                // Ubah progress barnya biar menyesuaikan waktu
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

                // Notification
                sendOnChannel1();

                // Ubah text view biar kembali jadi awal input
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));

                // Ubah progress bar biar kembali jadi awal input
                setProgressBarValues();

                // Menyembunyikan ikon reset
                imageViewReset.setVisibility(View.GONE);

                // Ubah ikon stop jadi play
                imageViewStartStop.setImageResource(R.drawable.timer_play_arrow);

                // Ubah text view jadi able
                editTextMinute.setEnabled(true);

                // Ubah status waktu jadi stop
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    // Method untuk stop count down, jadi ulang waktu dari awal kalau pencet start
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    // Method untuk set progress bar, biar sesuai dengan waktu
    private void setProgressBarValues() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    // Method untuk convert milisecond ke format waktu hour : minute : second
    private String hmsTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    public void editTask(int id, String task) {
        adapter.editTask(id, task);
//        AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {

    }
}