package com.example.btms;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        setupFAQItems();

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void setupFAQItems() {
        LinearLayout container = findViewById(R.id.llFAQContainer);
        if (container == null) return;

        String[][] faqs = {
                {"Làm thế nào để đặt vé?", "Bạn có thể tìm kiếm chuyến đi từ màn hình chính, chọn chuyến phù hợp, chọn ghế và điền thông tin để hoàn tất đặt vé."},
                {"Tôi có thể hủy vé không?", "Có, bạn có thể hủy vé trong phần 'Hành trình của tôi' trước khi chuyến xe khởi hành."},
                {"Các phương thức thanh toán nào được chấp nhận?", "Chúng tôi chấp nhận MoMo, VISA, Credit/Debit Card, PayPal và tiền mặt."},
                {"Làm thế nào để đổi vé?", "Hiện tại tính năng đổi vé đang được phát triển. Vui lòng liên hệ hỗ trợ để được hỗ trợ đổi vé."},
                {"Tôi có thể chọn ghế không?", "Có, bạn có thể chọn ghế theo sơ đồ khi đặt vé. Ghế VIP và ghế thường có giá khác nhau."},
                {"Thông báo nhắc nhở hoạt động như thế nào?", "Ứng dụng sẽ gửi thông báo 30 phút trước giờ khởi hành để nhắc nhở bạn."},
                {"Làm sao để xem lịch sử đặt vé?", "Bạn có thể xem lịch sử đặt vé trong phần 'Hành trình của tôi' hoặc 'Cài đặt' > 'Lịch sử đặt vé'."},
                {"Tôi quên mật khẩu thì làm sao?", "Bạn có thể sử dụng tính năng 'Quên mật khẩu' ở màn hình đăng nhập hoặc liên hệ hỗ trợ."}
        };

        for (String[] faq : faqs) {
            View faqItem = createFAQItem(faq[0], faq[1]);
            container.addView(faqItem);
        }
    }

    private View createFAQItem(String question, String answer) {
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardView.setCardElevation(4);
        cardView.setRadius(12);
        cardView.setPadding(16, 16, 16, 16);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout.LayoutParams) cardView.getLayoutParams()).setMargins(0, 0, 0, 16);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tvQuestion = new TextView(this);
        tvQuestion.setText(question);
        tvQuestion.setTextSize(16);
        tvQuestion.setTextColor(getColor(R.color.text_primary));
        tvQuestion.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvQuestion);

        TextView tvAnswer = new TextView(this);
        tvAnswer.setText(answer);
        tvAnswer.setTextSize(14);
        tvAnswer.setTextColor(getColor(R.color.text_secondary));
        tvAnswer.setPadding(0, 8, 0, 0);
        layout.addView(tvAnswer);

        cardView.addView(layout);
        return cardView;
    }
}

