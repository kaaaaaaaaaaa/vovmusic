package com.vovmusic.uit.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Comment;
import com.vovmusic.uit.models.Status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentSongAdapter extends RecyclerView.Adapter<CommentSongAdapter.ViewHolder> {
    private static final String TAG = "CommentSongAdapter";

    private ScaleAnimation scaleAnimation;
    private AlertDialog alertDialog;
    private Dialog dialog_1;

    private Context context;
    private List<Comment> commentList;

    private ArrayList<Status> statusArrayList;

    private final String ACTION_DELETE_COMMENT = "delete";

    public CommentSongAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentSongAdapter.ViewHolder holder, int position) {
        Picasso.get()
                .load(this.commentList.get(position).getUserImage())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(holder.civAvatarComment);
        holder.tvUserComment.setText(this.commentList.get(position).getUserName().trim());
        holder.tvTimeComment.setText(Handle_Date(this.commentList.get(position).getDate()).trim());
        holder.tvContentComment.setText(this.commentList.get(position).getContent().trim());

        if (this.commentList.get(holder.getLayoutPosition()).getIdUser().trim().equals(DataLocalManager.getUserID())) {
            holder.itemView.setOnLongClickListener(v -> {
                Open_Delete_Comment_Dialog(Gravity.CENTER, holder.getLayoutPosition());
                return false;
            });
        }
    }

    private void Open_Delete_Comment_Dialog(int gravity, int position) {
        this.dialog_1 = new Dialog(this.context);

        dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_1.setContentView(R.layout.layout_textview_dialog);

        Window window = (Window) dialog_1.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_1.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        // Ánh xạ các view trong dialog
        TextView tvDialogTitle = dialog_1.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setSelected(true); // Text will be moved
        TextView tvDialogContent = dialog_1.findViewById(R.id.tvDialogContent);
        Button btnDialogCancel = dialog_1.findViewById(R.id.btnDialogCancel);
        Button btnDialogAction = dialog_1.findViewById(R.id.btnDialogAction);

        tvDialogTitle.setText(R.string.tvDialogTitle6);
        tvDialogContent.setText(R.string.tvDialogContent6);
        btnDialogCancel.setText(R.string.btnDialogCancel6);
        btnDialogAction.setText(R.string.btnDialogAction6);

        this.scaleAnimation = new ScaleAnimation(context, btnDialogCancel);
        this.scaleAnimation.Event_Button();
        btnDialogCancel.setOnClickListener(v -> {
            dialog_1.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(context, btnDialogAction);
        this.scaleAnimation.Event_Button();
        btnDialogAction.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.WrapContentDialog);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
            alertBuilder.setView(view);
            alertBuilder.setCancelable(false);
            this.alertDialog = alertBuilder.create();
            this.alertDialog.show();

            Handle_Delete_Comment(ACTION_DELETE_COMMENT, commentList.get(position).getIdComment(), commentList.get(position).getIdSong(), DataLocalManager.getUserID(), commentList.get(position).getContent(), commentList.get(position).getDate(), position);
        });

        dialog_1.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Handle_Delete_Comment(String action, int commentID, int songID, String userID, String content, String date, int position) {
        DataService dataService = APIService.getService();
        Call<List<Status>> callBack = dataService.addUpdateDeleteCommentSong(action, commentID, songID, userID, content, date);
        callBack.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                statusArrayList = new ArrayList<>();
                statusArrayList = (ArrayList<Status>) response.body();

                if (statusArrayList != null) {
                    if (statusArrayList.get(0).getStatus() == 1) {
                        commentList.remove(position);
                        notifyItemRemoved(position);
//                        notifyDataSetChanged();

                        alertDialog.dismiss();
                        dialog_1.dismiss();
                        Toast.makeText(context, R.string.toast29, Toast.LENGTH_SHORT).show();
                    } else if (statusArrayList.get(0).getStatus() == 2) {
                        alertDialog.dismiss();
                        dialog_1.dismiss();
                        Toast.makeText(context, R.string.toast30, Toast.LENGTH_SHORT).show();
                    } else {
                        dialog_1.dismiss();
                        Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                    }
                }
                alertDialog.dismiss();
                dialog_1.dismiss();
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                alertDialog.dismiss();
                dialog_1.dismiss();

                Log.d(TAG, "Handle_Add_Comment(Error): " + t.getMessage());
            }
        });
    }

    private String Handle_Date(String date) {
        // Code lấy từ link này nè: https://vnsharebox.com/blog/convert-string-to-datetime-android/
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date datetime_1 = simpleDateFormat.parse(date); // Chuyển String ngày nhập vào thành Date

            String currentTime = simpleDateFormat.format(calendar.getTime()); // calendar.getTime(): Trả về đối tượng Date dựa trên giá trị của Calendar.
            Date datetime_2 = simpleDateFormat.parse(currentTime); // Chuyển String ngày nhập vào thành Date

            long diff = datetime_2.getTime() - datetime_1.getTime();
            int hours = (int) (diff / (60 * 60 * 1000));
            int minutes = (int) (diff / (1000 * 60)) % 60;
            int days = (int) (diff / (24 * 60 * 60 * 1000));

            if (days > 0) {
                return " - " + days + " " + context.getString(R.string.days);
            } else {
                if (hours > 0) {
                    return " - " + hours + context.getString(R.string.hours);
                } else {
                    return " - " + minutes + " " + context.getString(R.string.minutes);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return " - " + context.getString(R.string.today);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civAvatarComment;
        private TextView tvUserComment;
        private TextView tvTimeComment;
        private TextView tvContentComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civAvatarComment = itemView.findViewById(R.id.civAvatarComment);
            tvUserComment = itemView.findViewById(R.id.tvUserComment);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
            tvContentComment = itemView.findViewById(R.id.tvContentComment);
        }
    }
}
