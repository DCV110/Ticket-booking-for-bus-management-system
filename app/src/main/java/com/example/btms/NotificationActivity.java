package com.example.btms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvNotifications;
    private View cardNoNotifications;
    private String userEmail;
    private NotificationAdapter adapter;
    private List<NotificationItem> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null);

        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Update greeting
        String userName = sharedPreferences.getString("user_name", "User");
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) {
            tvGreeting.setText("Xin chào " + userName + "!");
        }

        rvNotifications = findViewById(R.id.rvNotifications);
        cardNoNotifications = findViewById(R.id.cardNoNotifications);
        
        TextView btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        if (btnMarkAllRead != null) {
            btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
        }

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        loadNotifications();

        // Setup bottom navigation (Notification is no longer in bottom nav, so no active state)
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, 0); // 0 means no active nav item
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }

    private void loadNotifications() {
        if (userEmail == null) return;

        notifications.clear();
        Cursor cursor = dbHelper.getUserNotifications(userEmail, 100);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                NotificationItem item = new NotificationItem();
                item.id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                item.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                item.message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                item.type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                item.isRead = cursor.getInt(cursor.getColumnIndexOrThrow("is_read")) == 1;
                item.createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
                
                int bookingIdIndex = cursor.getColumnIndex("booking_id");
                if (bookingIdIndex >= 0 && !cursor.isNull(bookingIdIndex)) {
                    item.bookingId = cursor.getLong(bookingIdIndex);
                }
                
                notifications.add(item);
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();

        // Show/hide empty state
        if (notifications.isEmpty()) {
            cardNoNotifications.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            cardNoNotifications.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
        }
    }

    private void markAllAsRead() {
        if (userEmail == null) return;
        
        boolean success = dbHelper.markAllNotificationsAsRead(userEmail);
        if (success) {
            Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
            loadNotifications();
        }
    }

    private void onNotificationClick(NotificationItem item) {
        // Mark as read
        if (!item.isRead) {
            dbHelper.markNotificationAsRead(item.id);
            item.isRead = true;
            adapter.notifyDataSetChanged();
        }

        // Navigate to booking detail if has booking_id
        if (item.bookingId != null && item.bookingId > 0) {
            Intent intent = new Intent(this, BookingDetailActivity.class);
            intent.putExtra("booking_id", item.bookingId);
            startActivity(intent);
        }
    }

    // Notification Item class
    private static class NotificationItem {
        long id;
        String title;
        String message;
        String type;
        boolean isRead;
        String createdAt;
        Long bookingId;
    }

    // Adapter for notifications
    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        private List<NotificationItem> items;

        public NotificationAdapter(List<NotificationItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NotificationItem item = items.get(position);
            
            holder.tvTitle.setText(item.title);
            holder.tvMessage.setText(item.message);
            holder.tvTime.setText(formatTime(item.createdAt));
            
            // Set read/unread style
            if (item.isRead) {
                holder.cardView.setAlpha(0.7f);
                holder.viewUnreadIndicator.setVisibility(View.GONE);
            } else {
                holder.cardView.setAlpha(1.0f);
                holder.viewUnreadIndicator.setVisibility(View.VISIBLE);
            }

            holder.cardView.setOnClickListener(v -> onNotificationClick(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            View viewUnreadIndicator;
            TextView tvTitle;
            TextView tvMessage;
            TextView tvTime;

            ViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardNotification);
                viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvMessage = itemView.findViewById(R.id.tvMessage);
                tvTime = itemView.findViewById(R.id.tvTime);
            }
        }

        private String formatTime(String dateTimeStr) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(dateTimeStr);
                if (date == null) return dateTimeStr;

                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                return dateTimeStr;
            }
        }
    }
}

