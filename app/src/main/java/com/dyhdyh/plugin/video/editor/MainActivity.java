package com.dyhdyh.plugin.video.editor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dyhdyh.plugin.video.editor.ffmpeg.FFmpegCommandBuild;
import com.dyhdyh.plugin.video.editor.ffmpeg.FFmpegResult;
import com.dyhdyh.plugin.video.editor.ffmpeg.RxFFmpeg;
import com.dyhdyh.plugin.video.editor.observer.SimpleProgressDialogObserver;
import com.dyhdyh.plugin.video.editor.utils.FileUtils;
import com.dyhdyh.plugin.video.editor.view.ClickableColorSpan;
import com.dyhdyh.plugin.video.editor.view.TimerEditText;

import java.io.File;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_CODE = 100;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

    TextView tv_info;
    TimerEditText ed_time;
    Button btn_play;
    Button btn_cut;

    private String mInputPath;
    private long mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));

        tv_info = findViewById(R.id.tv_info);
        ed_time = findViewById(R.id.ed_time);
        btn_play = findViewById(R.id.btn_play);
        btn_cut = findViewById(R.id.btn_cut);
        tv_info.setMovementMethod(LinkMovementMethod.getInstance());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if (!checkExternalStart()) {
            clickPickVideo(null);
        }
    }

    private boolean checkExternalStart() {
        Intent mIntent = getIntent();
        Uri uri = mIntent.getData();
        if (uri != null) {
            handleVideoUri(uri);
            return true;
        }
        return false;
    }

    public void clickPickVideo(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                handleVideoUri(uri);
            }
        }
    }


    public void handleVideoUri(Uri uri) {
        Log.d("---------->", uri + "-->");
        ContentResolver cr = this.getContentResolver();
        final String uriString = URLDecoder.decode(uri.toString());
        String beginString = "external_files/";
        if (uriString.contains(beginString)) {
            //文件过来的

        } else {
            //相册过来的
            beginString = "raw/";
        }

        final int beginIndex = uriString.indexOf(beginString) + beginString.length();
        String selectionArg = uriString;
        if (beginIndex >= 0) {
            selectionArg = uriString.substring(beginIndex);
        }

        Log.d("---------->", selectionArg + "-->");
        Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
        }, MediaStore.Video.Media.DATA + " like ?", new String[]{"%" + selectionArg}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                mInputPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));

                handleVideoInfo(size, width, height);

            }
            cursor.close();
        }
    }


    public void clickStartCut(View view) {
        if (TextUtils.isEmpty(mInputPath)) {
            Toast.makeText(this, "请选择视频", Toast.LENGTH_SHORT).show();
            return;
        }

        handleCutVideo(mInputPath, mDuration);
    }

    public void clickStartPlay(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(mInputPath));
            } else {
                uri = Uri.fromFile(new File(mInputPath));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "打开播放器失败", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleVideoInfo(long size, int width, int height) {
        StringBuffer sb = new StringBuffer();
        sb.append("文件路径：");
        sb.append("\n");
        sb.append(mInputPath);
        sb.append("\n");
        sb.append("\n");
        sb.append("分辨率：");
        sb.append(width);
        sb.append("x");
        sb.append(height);
        sb.append("\n");
        sb.append("视频时长：");
        sb.append(format.format(mDuration));
        sb.append("\n");
        sb.append("文件大小：");
        sb.append(Formatter.formatFileSize(this, size));
        sb.append("\n");
        tv_info.setText(sb);

        btn_play.setVisibility(View.VISIBLE);
        btn_cut.setVisibility(View.VISIBLE);
        ed_time.setVisibility(View.VISIBLE);
    }

    public void handleCutVideo(String input, long duration) {
        File inputFile = new File(input);
        final long startTime = ed_time.getStartTime();
        final long cutDurationMillis = ed_time.getEndTime() - startTime;

        tv_info.append("\n开始剪切\n");
        tv_info.append(format.format(startTime));
        tv_info.append("-");
        tv_info.append(format.format(startTime + cutDurationMillis));

        String outputName = String.format("%s_%s_%s.mp4", FileUtils.getFileNameNoEx(inputFile.getName()), ed_time.getFormatStartTime().replace(":", ""), ed_time.getFormatEndTime().replace(":", ""));
        File outputFile = new File(inputFile.getParentFile(), outputName);
        RxFFmpeg.execute(this,
                FFmpegCommandBuild.cut(startTime, cutDurationMillis, input, outputFile.getAbsolutePath()),
                outputFile, duration
        ).subscribe(new SimpleProgressDialogObserver(this) {
            @Override
            public void onNext(FFmpegResult result) {
                super.onNext(result);
                if (result.isSuccess()) {
                    final String output = result.getOutputFile().getAbsolutePath();
                    MediaScannerConnection.scanFile(MainActivity.this, new String[]{output}, new String[]{"video/mp4"}, null);
                    tv_info.append("\n\n");
                    tv_info.append("剪切成功!\n");

                    ClickableColorSpan spannable = new ClickableColorSpan() {
                        @Override
                        public void onClick(View widget) {
                            startTargetFileDir(output);
                        }
                    };

                    SpannableStringBuilder builder = new SpannableStringBuilder(output);
                    builder.setSpan(spannable,0, output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_info.append(builder);
                    tv_info.append("\n");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                tv_info.append("剪切失败\n");
            }
        });
    }

    private void startTargetFileDir(String output) {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            File parentFlie = new File(output).getParentFile();
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider",parentFlie);
            } else {
                uri = Uri.fromFile(parentFlie);
            }
            intent.setDataAndType(uri, "*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "跳转失败", Toast.LENGTH_SHORT).show();
        }
    }


}
